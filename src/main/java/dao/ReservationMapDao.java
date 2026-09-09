package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.DBConnection;

public class reservationDao {
	Connection 		  con=null;
	PreparedStatement  ps=null;
	ResultSet		   rs=null;
	private reservationDao(){}
	private static reservationDao dao = new reservationDao();
	public static reservationDao getDao(){
		return dao;
	}
	
	
	
}
