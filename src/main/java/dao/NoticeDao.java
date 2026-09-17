package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
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
	          "select *\r\n"
	          + "from (\r\n"
	          + "    select rownum as rnum, tbl.*\r\n"
	          + "    from (\r\n"
	          + "        select\r\n"
	          + "            n.no,\r\n"
	          + "            n.title,\r\n"
	          + "            n.attach,\r\n"
	          + "            n.important,\r\n"
	          + "            m.name,\r\n"
	          + "            to_char(n.reg_date,'yyyy-MM-dd') as reg_date,\r\n"
	          + "            n.hit\r\n"
	          + "        from icn_notice n,\r\n"
	          + "             icn_member m\r\n"
	          + "        where n.reg_id = m.member_id\r\n"
	          + "        and n."+select+" like '%"+search+"%'\r\n"
	          + "        order by case when n.important = 'Y' then 0 else 1 end, n.no desc\r\n"
	          + "    ) tbl\r\n"
	          + ")\r\n"
	          + "where rnum >= "+start+"\r\n"
	          + "and rnum <= "+end;
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




	public String getNoticeNo() {
      String no="";
      String sql="select nvl(max(no),'N000') as no from icn_notice";
      try {
         con=DBConnection.getConnection();
         ps=con.prepareStatement(sql);
         rs=ps.executeQuery();
         if(rs.next()) {
            no=rs.getString("no");
            no=no.substring(1);
            int newNo=Integer.parseInt(no)+1;
            DecimalFormat df=new DecimalFormat("N000");
            no=df.format(newNo);
         }
      }catch(Exception e) {
         e.printStackTrace();
         System.out.println("getNoticeNo: "+sql);
      }finally {
         DBConnection.closeDB(con, ps, rs);
      }
      return no;
   }




	public int noticeSave(NoticeDto dto) {
      int result=0;
      String sql="insert into icn_notice "
      		+ "(no,title,content,important,attach,reg_id,reg_date) "
      		+ "values "
      		+ "('"+dto.getNo()+"','"+dto.getTitle()+"','"+dto.getContent()+"','"+dto.getImportant()+"','"+dto.getAttach()+"','"+dto.getReg_id()+"',to_date('"+dto.getReg_date()+"','yyyy-MM-dd hh24:mi:ss'))";
      try {
         con=DBConnection.getConnection();
         ps=con.prepareStatement(sql);
         result=ps.executeUpdate();
      }catch(Exception e) {
         e.printStackTrace();
         System.out.println("noticeSave: "+sql);
      }finally {
         DBConnection.closeDB(con, ps, rs);
      }
      return result;
   }
	
	
	
	
	

}
