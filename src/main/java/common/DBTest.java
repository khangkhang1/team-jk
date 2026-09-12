package common;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DB 접속 진단 도구 (강선구 작성, 2026-09-09)
 *
 * [쓰는 법]
 *   이 파일을 이클립스에서 우클릭 -> Run As -> Java Application
 *   (서버 실행 필요 없음. 그냥 자바 프로그램으로 돌아간다)
 *
 * [왜 만들었나]
 *   "DB 접속이 안 된다"는 한 문장 안에 원인이 최소 5가지가 섞여 있다.
 *   드라이버 문제 / 네트워크 문제 / 계정 문제 / 서비스명 문제 / 계정잠김.
 *   에러 메시지를 서로 주고받으며 추측하는 것보다, 각자 PC에서 이걸 한 번 돌려서
 *   "몇 번 단계에서 멈췄는지"만 말하는 게 훨씬 빠르다.
 *
 *   단계를 쪼개 놓은 이유가 이것이다. 4단계(로그인)에서 실패한 건 계정 문제지만,
 *   2단계(이름 해석)에서 실패한 건 계정을 백 번 바꿔도 소용이 없다.
 */
public class DBTest {

	// DBConnection.java와 같은 값을 쓴다. 여기만 고치면 안 되고 저기도 같이 고쳐야 함.
	private static final String HOST = "jsl-704";
	private static final int    PORT = 1523;
	private static final String SERVICE = "xe";
	private static final String USER = "icn_parking";
	private static final String PASS = "1234";

	private static final String URL = "jdbc:oracle:thin:@" + HOST + ":" + PORT + "/" + SERVICE;

	public static void main(String[] args) {
		line();
		System.out.println(" DB 접속 진단  ->  " + URL + "  (계정 " + USER + ")");
		line();

		if (!step1_driver())  { return; }
		String ip = step2_host();
		if (ip == null)       { return; }
		if (!step3_port(ip))  { return; }
		if (!step4_login())   { return; }
		step5_tables();

		line();
		System.out.println(" 전부 통과. DB는 정상입니다.");
		System.out.println(" 그래도 화면에서 에러가 나면 DB가 아니라 다른 문제입니다.");
		line();
	}

	// ── 1단계 : ojdbc8.jar이 클래스패스에 잡혀 있나 ─────────────────────
	private static boolean step1_driver() {
		System.out.print("[1/5] 오라클 드라이버 로드 ......... ");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("OK");
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("실패");
			fail("ojdbc8.jar을 못 찾았습니다.",
				"src/main/webapp/WEB-INF/lib/ojdbc8.jar 파일이 있는지 확인하세요.",
				"파일은 있는데 이 에러가 나면 이클립스 빌드패스에 안 잡힌 겁니다:",
				"  프로젝트 우클릭 -> Properties -> Java Build Path -> Libraries",
				"  거기에 ojdbc8.jar이 없으면 Add JARs로 추가.",
				"※ 예전에 .classpath 파일이 지워졌던 사고가 있었으니 그 여파일 수 있습니다.");
			return false;
		}
	}

	// ── 2단계 : jsl-704 라는 이름이 IP로 풀리나 ─────────────────────────
	private static String step2_host() {
		System.out.print("[2/5] 호스트명 해석 (" + HOST + ") ..... ");
		try {
			String ip = InetAddress.getByName(HOST).getHostAddress();
			System.out.println("OK  -> " + ip);
			return ip;
		} catch (Exception e) {
			System.out.println("실패");
			fail("'" + HOST + "' 라는 이름을 IP로 바꾸지 못했습니다.",
				"이건 계정이나 비밀번호 문제가 아닙니다. 바꿔봐야 소용없습니다.",
				"",
				HOST + "는 인터넷 도메인이 아니라 학원 내부망(192.168.0.x)에 있는",
				"컴퓨터 이름입니다. 그래서 같은 랜에 붙어 있을 때만 풀립니다.",
				"",
				"확인할 것:",
				"  - 지금 학원 와이파이/랜에 연결돼 있습니까? (집이면 절대 안 됩니다)",
				"  - 핸드폰 핫스팟을 쓰고 있진 않습니까?",
				"  - 명령프롬프트에서  ping jsl-704  를 쳐보세요. 이름조차 못 찾으면 같은 증상입니다.");
			return null;
		}
	}

	// ── 3단계 : 1523 포트까지 TCP가 닿나 ────────────────────────────────
	private static boolean step3_port(String ip) {
		System.out.print("[3/5] 포트 연결 (" + ip + ":" + PORT + ") ... ");
		Socket s = new Socket();
		try {
			s.connect(new InetSocketAddress(ip, PORT), 5000);
			System.out.println("OK");
			return true;
		} catch (Exception e) {
			System.out.println("실패");
			fail("이름은 풀렸는데 " + PORT + "번 포트로 연결이 안 됩니다.",
				"확인할 것:",
				"  - DB가 올라가 있는 PC(" + ip + ")가 켜져 있습니까?",
				"  - 그 PC의 오라클 리스너(TNSLSNR)가 실행 중입니까?",
				"  - 방화벽이 " + PORT + "번을 막고 있진 않습니까?",
				"※ ping이 안 되는 건 정상입니다. 윈도우가 ping만 따로 막습니다.");
			return false;
		} finally {
			try { s.close(); } catch (Exception ignore) {}
		}
	}

	// ── 4단계 : 실제 로그인 ─────────────────────────────────────────────
	// 주의: 여러 번 반복 실행하면 오라클이 계정을 잠글 수 있습니다(ORA-28000).
	//       실패했으면 원인을 고치고 나서 다시 돌리세요. 연타 금지.
	private static boolean step4_login() {
		System.out.print("[4/5] 로그인 (" + USER + ") ......... ");
		try {
			DriverManager.setLoginTimeout(10);
			Connection c = DriverManager.getConnection(URL, USER, PASS);
			c.close();
			System.out.println("OK");
			return true;
		} catch (SQLException e) {
			System.out.println("실패  (ORA-" + String.format("%05d", e.getErrorCode()) + ")");
			switch (e.getErrorCode()) {
				case 1017:
					fail("계정 이름이나 비밀번호가 틀립니다. (ORA-01017)",
						"현재 코드에 박힌 값 : " + USER + " / " + PASS,
						"",
						"예전 코드에는 team27_jk 라고 적혀 있었는데 그건 계정이 확정되기 전",
						"임시로 써둔 값이라 실제로 존재하지 않습니다. icn_parking 이 맞습니다.",
						"main 브랜치를 받아왔다면 옛날 값일 수 있으니 Pull부터 하세요.");
					break;
				case 28000:
					fail("계정이 잠겼습니다. (ORA-28000)",
						"비밀번호를 여러 번 틀리면 오라클이 계정을 잠급니다.",
						"DB 관리자(강사님 또는 서버 담당)에게 잠금 해제를 요청하세요:",
						"  ALTER USER " + USER + " ACCOUNT UNLOCK;");
					break;
				case 12514:
				case 12505:
					fail("서비스명 '" + SERVICE + "' 을 리스너가 모릅니다. (ORA-" + e.getErrorCode() + ")",
						"서버는 살아있는데 접속할 DB 이름이 다릅니다.",
						"URL 끝의 /" + SERVICE + " 부분이 맞는지 확인하세요.");
					break;
				case 17002:
					fail("통신이 끊겼습니다. (ORA-17002)",
						"포트까지는 닿았는데 오라클과 대화가 안 됩니다.",
						"DB 서버가 재시작 중이거나 리스너가 내려갔을 수 있습니다.");
					break;
				default:
					fail("알 수 없는 오류입니다.",
						e.getMessage(),
						"",
						"위 메시지를 그대로 팀 채팅에 붙여넣어 주세요.");
			}
			return false;
		}
	}

	// ── 5단계 : 테이블이 보이나 ─────────────────────────────────────────
	private static void step5_tables() {
		System.out.print("[5/5] 테이블 조회 .................. ");
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			st = con.createStatement();
			rs = st.executeQuery("SELECT table_name FROM user_tables ORDER BY table_name");
			StringBuilder sb = new StringBuilder();
			int n = 0;
			while (rs.next()) {
				if (n++ > 0) sb.append(", ");
				sb.append(rs.getString(1));
			}
			System.out.println("OK  (" + n + "개)");
			System.out.println("      " + sb);
			if (n == 0) {
				System.out.println("      ※ 접속은 되는데 테이블이 하나도 없습니다.");
				System.out.println("        sql/ 폴더의 CREATE TABLE 스크립트를 돌려야 합니다.");
			}
		} catch (SQLException e) {
			System.out.println("실패");
			System.out.println("      " + e.getMessage());
		} finally {
			DBConnection.closeDB(con, null, rs);
			try { if (st != null) st.close(); } catch (SQLException ignore) {}
		}
	}

	private static void fail(String... lines) {
		System.out.println();
		line();
		System.out.println(" [원인]");
		for (String s : lines) {
			System.out.println(s.isEmpty() ? "" : "   " + s);
		}
		line();
	}

	private static void line() {
		System.out.println("=======================================================================");
	}
}
