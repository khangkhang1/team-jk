package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.DBConnection;
import dto.ReservationMapDto;

public class ReservationMapDao {
	Connection 		  con=null;
	PreparedStatement  ps=null;
	ResultSet		   rs=null;
	private ReservationMapDao(){}
	private static ReservationMapDao dao = new ReservationMapDao();
	public static ReservationMapDao getDao(){
		return dao;
	}
	public List<ReservationMapDto> getPakingMap(String map ,String time) {
		ArrayList<ReservationMapDto> dtos=new ArrayList<>();
		String sql = "";
		
		
		return dtos;
	}
	
	//주차상태 업데이트
 
	
	
}
