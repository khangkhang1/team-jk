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
	  
//오라클에 접속 하기 위해 만드는 클래스 Connection 이것이 들어가야한다
	public static Connection getConnenction(){
		Connection con =null;
		try {//이걸 사용 하려면 반드시 try catch를 사용 해야 한다
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//드라이버 설치 같은 느낌 오라클 을 사용 하려면 오라클 드라이버를 읽어야한다 이것이 그것
			//일종의 기초 작업
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String db_url ="jdbc:oracle:thin:@1.245.91.227:1523/xe";
		String db_user ="icn_parking";
		String db_password ="1234";
		//일단 오라클의 데이터 베이스의 uid 아이디 비번 사용
		//그다음 커넥션 클래스를 만들어야한다
		try {
			con = DriverManager.getConnection(db_url, db_user, db_password);
		} catch (SQLException e) {//트라이케치에만 사용 가능
			System.out.println("DB 접속 오류!");
			e.printStackTrace();
		}
		
		
		return con;
		
	}
	//DB연결종료 연결과 반대로 rs,ps,con 순으로 연결을 종료 한다
	public static void closeDB1(Connection con, 
			PreparedStatement ps,
			ResultSet rs) {
		if(null!=rs) {try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		}
		if(null!=ps) {try {
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		}
		if(null!=con) {
			try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
		
		}

	// 학원 내부망의 DB 서버 이름. 개인프로젝트(track27_11g)와 같은 서버를 쓰되
	// 계정은 팀 전용(icn_parking)으로 분리했습니다 - 개인 데이터와 안 섞이게.
	//
	// ※ jsl-704는 인터넷 도메인이 아니라 학원 랜(192.168.0.48)에서만 풀리는 컴퓨터
	//   이름입니다. 집이나 핫스팟에서는 이름 자체가 해석되지 않아 접속이 안 됩니다.
	//   계정 문제가 아니므로 아이디를 바꿔봐야 소용없습니다.
	//   집에서 작업해야 한다면 이 줄을 공인 IP로 바꾸면 됩니다(학원/집 양쪽 다 됨).
	//   다만 공인 IP는 바뀔 수 있으니 평소에는 jsl-704로 두는 걸 권합니다.
	private static final String DB_URL = "jdbc:oracle:thin:@jsl-704:1523/xe";
	private static final String DB_USER = "icn_parking";
	private static final String DB_PASSWORD = "1234";

	public static Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			// 여기서 걸리면 계정 문제가 아니라 ojdbc8.jar이 빌드패스에 없는 겁니다.
			System.out.println("오라클 드라이버를 찾지 못했습니다. ojdbc8.jar 빌드패스를 확인하세요.");
			e.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			System.out.println("DB 접속 오류 (ORA-" + e.getErrorCode() + ") : " + e.getMessage());
			System.out.println("-> common/DBTest.java 를 실행하면 원인을 단계별로 알려줍니다.");
			e.printStackTrace();
		}
		return con;
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

