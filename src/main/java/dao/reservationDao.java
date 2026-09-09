package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class reservationDao {
	//DB연결 관련 변수
	Connection con 			= null;
	PreparedStatement ps 	= null;
	ResultSet rs 			= null;
		
	//Dao 싱글톤
	private reservationDao() {};
	private static reservationDao dao = new reservationDao();

	public static reservationDao getDao() {
		return dao;
	}
}