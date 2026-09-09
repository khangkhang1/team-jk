package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReservationDao {
	//DB연결 관련 변수
	Connection con 			= null;
	PreparedStatement ps 	= null;
	ResultSet rs 			= null;
		
	//Dao 싱글톤
	private ReservationDao() {};
	private static ReservationDao dao = new ReservationDao();

	public static ReservationDao getDao() {
		return dao;
	}
}