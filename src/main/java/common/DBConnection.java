package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

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

}
