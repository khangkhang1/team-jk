package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaymentDao {
	
	//DB연결 관련 변수
	Connection con 			= null;
	PreparedStatement ps 	= null;
	ResultSet rs 			= null;
	
	//Dao 싱글톤
	private PaymentDao() {};
	private static PaymentDao dao = new PaymentDao();
	
	public static PaymentDao getDao() {
		return dao;
	}
	
	
}
