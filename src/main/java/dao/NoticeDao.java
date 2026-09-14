package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import common.DBConnection;
import dto.NoticeDto;

public class NoticeDao {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	
	

	public int getTotalCount(String select, String search) {
	      int count=0;
	      String sql="select count(*) as count from icn_notice where "+select+" like '%"+search+"%'";
	      try {
	         con=DBConnection.getConnection();
	         ps=con.prepareStatement(sql);
	         rs=ps.executeQuery();
	         if(rs.next()) {
	            count=rs.getInt("count");
	         }
	      }catch(Exception e) {
	         e.printStackTrace();
	         System.out.println("getTotalCount: "+sql);
	      }finally {
	         DBConnection.closeDB(con, ps, rs);
	      }      
	      return count;
	   }




	public List<NoticeDto> getNoticeList(String select, String search, int start, int end) {

	    List<NoticeDto> dtos = new ArrayList<>();

	    String sql =
	          "select * "
	        + "from ( "
	        + "    select rownum as rnum, tbl.* "
	        + "    from ( "
	        + "        select "
	        + "            n.no, "
	        + "            n.title, "
	        + "            n.attach, "
	        + "            n.important, "
	        + "            m.name, "
	        + "            to_char(n.reg_date,'yyyy-MM-dd') as reg_date, "
	        + "            n.hit "
	        + "        from icn_notice n, "
	        + "             icn_notice m "
	        + "        where n.reg_id = m.id "
	        + "		   and n."+select+" like '%"+search+"%'\r\n"
	        + "        order by case when n.important = 'Y' then 0 else 1 end, n.no desc "
	        + "    ) tbl "
	        + ") "
	        + "where rnum >= " + start
	        + "and rnum <= " + end;

	    try {

	        con = DBConnection.getConnection();
	        ps = con.prepareStatement(sql);
	        rs = ps.executeQuery();

	        while(rs.next()) {

	            String no = rs.getString("no");
	            String title = rs.getString("title");
	            String attach = rs.getString("attach");
	            String important = rs.getString("important");
	            String reg_name = rs.getString("name");
	            String reg_date = rs.getString("reg_date");
	            int hit = rs.getInt("hit");

	            NoticeDto dto = new NoticeDto(no,title,"",important,attach,hit,reg_name,reg_date);

	            dtos.add(dto);
	        }

	    } catch(Exception e) {

	        System.out.println("getNoticeList() 오류:" + sql);
	        e.printStackTrace();

	    } finally {

	        DBConnection.closeDB(con, ps, rs);
	    }

	    return dtos;
	}

}
