package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import common.CommonUtil;
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
		// 관리자 콘솔(command/manager/NoticeSave)은 no 와 reg_date 를 안 채우고 호출한다.
		// 비어 있으면 여기서 채운다 - 정규상 화면은 미리 채워서 오므로 영향 없음. (2026-09-23 병합)
		if (dto.getNo() == null || dto.getNo().trim().isEmpty()) dto.setNo(getNoticeNo());
		String regDateExpr = (dto.getReg_date() == null || dto.getReg_date().trim().isEmpty())
				? "SYSDATE" : "to_date('" + dto.getReg_date() + "','yyyy-MM-dd hh24:mi:ss')";
      int result=0;
      String sql="insert into icn_notice "
      		+ "(no,title,content,important,attach,reg_id,reg_date) "
      		+ "values "
      		+ "('"+dto.getNo()+"','"+dto.getTitle()+"','"+dto.getContent()+"','"+dto.getImportant()+"','"+dto.getAttach()+"','"+dto.getReg_id()+"',"+regDateExpr+")";
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




	//조회수 증가
		public int setHitCount(String no) {
			int result = 0;
			String sql = "update icn_notice\r\n"
		            + "set hit = hit + 1\r\n"
		            + "where no = '"+no+"'";;
			
		            try {
		                con = DBConnection.getConnection();
		                ps = con.prepareStatement(sql);
		                result = ps.executeUpdate();
		             }catch(Exception e){
		                e.printStackTrace();
		                System.out.println("setHitCount 오류:"+sql);
		             }finally {
		                DBConnection.closeDB(con, ps, rs);
		             }
			
			

			return result;
		}




		//상세조회
	public NoticeDto noticeView(String no) {
		NoticeDto dto = null;
		String sql = "select no, title, content, important, attach, reg_id,to_char(reg_date,'yyyy-MM-dd') as reg_date,hit\r\n"
				+ "from icn_notice\r\n"
				+ "where no = '"+no+"'";
		try {
	         con=DBConnection.getConnection();
	         ps=con.prepareStatement(sql);
	         rs=ps.executeQuery();
	         if(rs.next()){
	             String title = rs.getString(CommonUtil.getCheckNull("title"));
	             //CommonUtil.getDoubleQuot(title); //큰 따옴표 html특수기호 문자표로 나오게
	             String content = rs.getString("content");
	             String important = rs.getString("important");
	             String attach = rs.getString("attach");
	             String reg_id = rs.getString("reg_id");
	             String reg_date = rs.getString("reg_date");
	            // String update_date = rs.getString("update_date");
	             int hit = rs.getInt("hit");
	        	 
	             dto = new NoticeDto(no, title, content, important, attach, hit, reg_id, reg_date);
	        	 
	         }
	         
	      }catch(Exception e) {
	         e.printStackTrace();
	         System.out.println("noticeView 오류 : "+sql);
	      }finally {
	         DBConnection.closeDB(con, ps, rs);
	      }
		
		
		return dto;
	}




	//게시글 삭제
	public int noticeDelete(String no) {
		int result = 0;
		String sql = "delete from icn_notice\r\n"
				+ "			where no = '"+no+"'";
		
		try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            result = ps.executeUpdate();
         }catch(Exception e){
            e.printStackTrace();
            System.out.println("noticeDelete 오류:"+sql);
         }finally {
            DBConnection.closeDB(con, ps, rs);
         }
		
		return result;
	}




	//이전글,다음글 (이전글 '+' 다음글 '-')
		public NoticeDto getPreNextNotice(String no, String gubun) {
			NoticeDto dto = null;
			String sql = "select n1.*, n2.no, n2.title from(\r\n"
					+ "select rnum "+gubun+" 1 as rnum\r\n"
					+ "from(\r\n"
					+ "    select rownum rnum, n.no\r\n"
					+ "    from(\r\n"
					+ "        select no\r\n"
					+ "        from icn_notice\r\n"
					+ "        order by important, no desc) n \r\n"
					+ ") where no ='"+no+"') n1,\r\n"
					+ "(select rownum rnum, no, title\r\n"
					+ "    from(\r\n"
					+ "        select no, title\r\n"
					+ "        from icn_notice\r\n"
					+ "        order by important, no desc)) n2\r\n"
					+ "where n1.rnum = n2.rnum        \r\n"
					+ "";
			
			try {
				con = DBConnection.getConnection();
				ps  = con.prepareStatement(sql);
				rs  = ps.executeQuery();	
				if(rs.next()){
					String title = rs.getString("title"); 
					String n_no = rs.getString("no"); 
					
					dto = new NoticeDto(n_no, title);
				}
			}catch(Exception e) {
				System.out.println("getPreNextNotice() 오류:"+sql);
				e.printStackTrace();
			}finally {
				DBConnection.closeDB(con, ps, rs);
			}
			
			return dto;
		
	}
	
	
	
	

	// ================================================================
	// 아래는 강선구 관리자 콘솔(controller/Manager, command/manager/*)용. 2026-09-23 병합 때 test_ksg 에서 접목.
	// 위쪽 정규상 게시판용 메서드와 인자 수가 달라 시그니처가 겹치지 않는다. 값은 전부 ? 바인딩.
	// ================================================================

	/** 목록. 중요 공지를 맨 위로, 그 다음 최신 번호 순. search 가 있으면 제목에서 찾는다. */
	public ArrayList<NoticeDto> getNoticeList(String search) {
		ArrayList<NoticeDto> dtos = new ArrayList<>();
		String sql =
			  "SELECT no, title, NVL(important,'N') AS important, attach, NVL(hit,0) AS hit, reg_id,\r\n"
			+ "       TO_CHAR(reg_date,'YYYY-MM-DD') AS reg_date\r\n"
			+ "FROM   icn_notice\r\n"
			+ "WHERE  UPPER(title) LIKE UPPER('%' || ? || '%')\r\n"
			+ "ORDER BY DECODE(NVL(important,'N'),'Y',0,1), reg_date DESC, no DESC";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setString(1, search == null ? "" : search);
			rs  = ps.executeQuery();
			while (rs.next()) {
				dtos.add(readRow(rs, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getNoticeList() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dtos;
	}

	/** 한 건. 없으면 null */
	public NoticeDto getNoticeView(String no) {
		NoticeDto dto = null;
		String sql =
			  "SELECT no, title, content, NVL(important,'N') AS important, attach, NVL(hit,0) AS hit, reg_id,\r\n"
			+ "       TO_CHAR(reg_date,'YYYY-MM-DD HH24:MI') AS reg_date\r\n"
			+ "FROM   icn_notice\r\n"
			+ "WHERE  no = ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setString(1, no);
			rs  = ps.executeQuery();
			if (rs.next()) {
				dto = readRow(rs, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getNoticeView() 오류 : " + no);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dto;
	}

	public int noticeUpdate(NoticeDto dto) {
		String sql =
			  "UPDATE icn_notice\r\n"
			+ "SET    title = ?, content = ?, important = ?\r\n"
			+ "WHERE  no = ?";
		return executeUpdate(sql, dto.getTitle(), dto.getContent(), dto.getImportant(), dto.getNo());
	}

	// 다음 번호. 같은 커넥션 안에서 구해야 INSERT 직전 값과 어긋나지 않는다.
	private String nextNo(Connection con) throws Exception {
		// 이미 들어 있는 번호가 'N001' 형식(정규상 화면에서 넣은 값)이라 같은 형식으로 맞춘다.
		// 'N' 뒤 숫자만 떼어 가장 큰 값 + 1. 형식이 다른 행은 걸러서 TO_NUMBER 가 터지지 않게 한다.
		String sql =
			  "SELECT 'N' || LPAD(NVL(MAX(TO_NUMBER(SUBSTR(no,2))),0) + 1, 3, '0') AS next_no\r\n"
			+ "FROM   icn_notice\r\n"
			+ "WHERE  REGEXP_LIKE(no, '^N[0-9]+$')";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getString("next_no");
			return "N001";
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) { }
			if (ps != null) try { ps.close(); } catch (Exception ignore) { }
		}
	}

	private NoticeDto readRow(ResultSet rs, boolean withContent) throws Exception {
		NoticeDto dto = new NoticeDto();
		dto.setNo(rs.getString("no"));
		dto.setTitle(rs.getString("title"));
		dto.setImportant(rs.getString("important"));
		dto.setAttach(rs.getString("attach"));
		dto.setHit(rs.getInt("hit"));
		dto.setReg_id(rs.getString("reg_id"));
		dto.setReg_date(rs.getString("reg_date"));
		if (withContent) dto.setContent(rs.getString("content"));
		return dto;
	}

	private int executeUpdate(String sql, Object... params) {
		int result = 0;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("NoticeDao.executeUpdate() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, null);
		}
		return result;
	}

}
