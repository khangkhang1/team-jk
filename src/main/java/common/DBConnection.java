package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
//<<<<<<<HEAD
	  
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
		String db_user ="track27_11g";
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
		if(null!=con) {try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		}

		
	}
//======

	// DB 접속
	// TODO: 팀 DB 계정 확정되면 db_user / db_passward 교체할 것.
	// 개인프로젝트(track27_11g)와 같은 학원 서버(jsl-704)를 쓰더라도, 계정/테이블 프리픽스는
	// 팀 전용으로 분리해야 함 - 개인프로젝트 데이터와 안 섞이게.

	public static Connection getConnection(){
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String db_url = "jdbc:oracle:thin:@jsl-704:1523/xe";
		String db_user = "team27_jk";      // TODO: 팀 DB 계정 확정되면 교체
		String db_passward = "1234";       // TODO: 팀 DB 비밀번호 확정되면 교체

		try {
			con = DriverManager.getConnection(db_url, db_user, db_passward);
		} catch (SQLException e) {
			System.out.println("DB 접속 오류~~~");
			e.printStackTrace();
		}
		return con;
	}

	// DB 연결 종료

	public static void closeDB(Connection con, PreparedStatement ps, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

//>>>>>>> ijg
}
