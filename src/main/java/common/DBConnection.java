package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 팀 DB 접속 공통 클래스.
 *
 * ─────────────────────────────────────────────────────────────────────
 * [2026-09-09 정리] DB 접속이 전원 안 되던 문제를 여기서 고쳤습니다.
 *
 * 원인이 두 개 겹쳐 있었습니다.
 *
 *  (1) 계정 이름이 틀려 있었음
 *      db_user가 "team27_jk"로 박혀 있었는데, 이건 팀 계정이 만들어지기 전에
 *      임시로 적어둔 자리표시자였습니다. 실제로 생성된 계정은 "icn_parking"입니다.
 *      존재하지 않는 계정이라 ORA-01017(로그인 거부)이 났습니다.
 *      main 포함 모든 브랜치가 같은 값이어서 누가 받아도 똑같이 실패했습니다.
 *
 *  (2) 병합 충돌이 잘못 해결된 채로 커밋돼 있었음
 *      충돌 표시(<<<<<<<, ======, >>>>>>>)를 지우지 않고 주석 처리해서
 *      접속 메서드가 두 개 살아 있었습니다.
 *        - getConnenction()  <- 오타. 개인프로젝트 계정(track27_11g)을 봄
 *        - getConnection()   <- 팀 계정
 *      메서드 이름 철자가 달라서 컴파일은 통과했고, 그래서 아무도 눈치채지 못했습니다.
 *      개인프로젝트 DB를 보던 getConnenction()과 closeDB1()은 삭제했습니다.
 *      혹시 본인 코드에서 그 이름을 쓰고 있었다면 getConnection() / closeDB()로
 *      바꿔주세요. (팀 코드가 개인프로젝트 DB를 건드리면 안 됩니다)
 *
 * [접속 안 될 때] 추측하지 말고 common/DBTest.java를 먼저 돌려보세요.
 *   우클릭 -> Run As -> Java Application. 서버 안 켜도 됩니다.
 *   어느 단계에서 멈추는지 알려주면 원인이 바로 나옵니다.
 * ─────────────────────────────────────────────────────────────────────
 */
public class DBConnection {

	// 학원 내부망의 DB 서버 이름. 개인프로젝트(track27_11g)와 같은 서버를 쓰되
	// 계정은 팀 전용(icn_parking)으로 분리했습니다 - 개인 데이터와 안 섞이게.
	//
	// ※ jsl-704는 인터넷 도메인이 아니라 학원 랜(192.168.0.48)에서만 풀리는 컴퓨터
	//   이름입니다. 집이나 핫스팟에서는 이름 자체가 해석되지 않아 접속이 안 됩니다.
	//   계정 문제가 아니므로 아이디를 바꿔봐야 소용없습니다.
	//
	// [2026-09-13] 학원 밖에서도 붙도록 자동 전환을 넣었습니다.
	//   1) jsl-704 로 먼저 시도 (학원에서는 여기서 끝 - 기존과 동일)
	//   2) 안 되면 src/main/java/db_local.properties 의 db.host 로 시도
	//   3) 성공한 호스트를 기억해두고 다음 호출부터는 바로 그쪽으로 감
	//
	//   db_local.properties 는 .gitignore 되어 있어 각자 PC에만 있습니다.
	//   공인 IP를 소스에 박지 않는 이유: 이 저장소가 public 이라 "인터넷에서 붙는
	//   DB 주소 + 계정 + 비번"이 그대로 노출됩니다. 학원 전체가 쓰는 DB 서버라 위험합니다.
	//   집에서 쓰려면 db_local.properties.example 을 복사해 db_local.properties 로 만들고
	//   db.host 한 줄만 채우면 됩니다. 학원에서는 이 파일이 없어도 됩니다.
	private static final String HOST_ACADEMY = "jsl-704";
	private static final String PORT_SERVICE = "1523/xe";
	// 계정/비번은 소스에 두지 않고 secret.properties 에서 읽는다 (common/SecretConfig 참고).

	// 마지막으로 접속에 성공한 호스트. 집에서 매번 jsl-704 실패를 기다리지 않으려고 기억해둠.
	private static String cachedHost = null;

	public static Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			// 여기서 걸리면 계정 문제가 아니라 ojdbc8.jar이 빌드패스에 없는 겁니다.
			System.out.println("오라클 드라이버를 찾지 못했습니다. ojdbc8.jar 빌드패스를 확인하세요.");
			e.printStackTrace();
			return null;
		}

		// 시도 순서: 지난번 성공한 곳 -> 학원 -> db_local.properties  (LinkedHashSet: 순서 유지 + 중복 제거)
		java.util.LinkedHashSet<String> hosts = new java.util.LinkedHashSet<>();
		if (cachedHost != null) hosts.add(cachedHost);
		hosts.add(HOST_ACADEMY);
		String localHost = readLocalHost();
		if (localHost != null) hosts.add(localHost);

		SQLException last = null;
		for (String host : hosts) {
			String url = "jdbc:oracle:thin:@" + host + ":" + PORT_SERVICE;
			try {
				Connection con = DriverManager.getConnection(url, SecretConfig.get("db.user"), SecretConfig.get("db.password"));
				if (!host.equals(cachedHost)) {
					System.out.println("DB 접속 : " + host
							+ (host.equals(HOST_ACADEMY) ? " (학원)" : " (db_local.properties)"));
					cachedHost = host;
				}
				return con;
			} catch (SQLException e) {
				last = e;   // 다음 후보로. 전부 실패하면 아래에서 한 번에 안내.
			}
		}

		System.out.println("DB 접속 오류 - 시도한 호스트 : " + hosts);
		if (last != null) {
			System.out.println("마지막 오류 (ORA-" + last.getErrorCode() + ") : " + last.getMessage());
		}
		if (localHost == null) {
			System.out.println("-> 학원 밖이라면 src/main/java/db_local.properties.example 을 복사해"
					+ " db_local.properties 를 만들고 db.host 를 채우세요.");
		}
		System.out.println("-> common/DBTest.java 를 실행하면 원인을 단계별로 알려줍니다.");
		if (last != null) last.printStackTrace();
		return null;
	}

	// src/main/java/db_local.properties 의 db.host 를 읽는다. 파일이 없으면 null.
	// 이클립스가 src/main/java 의 .properties 를 build/classes 로 복사해주므로 클래스패스
	// 루트("/")에서 읽힌다. Tomcat 에서는 WEB-INF/classes 가 그 자리다.
	private static String readLocalHost() {
		try (java.io.InputStream in = DBConnection.class.getResourceAsStream("/db_local.properties")) {
			if (in == null) return null;
			java.util.Properties p = new java.util.Properties();
			p.load(in);
			String host = p.getProperty("db.host");
			if (host == null || host.trim().isEmpty()) return null;
			return host.trim();
		} catch (java.io.IOException e) {
			return null;
		}
	}

	// DB 연결 종료. 연결과 반대 순서(rs -> ps -> con)로 닫습니다.
	public static void closeDB(Connection con, PreparedStatement ps, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
