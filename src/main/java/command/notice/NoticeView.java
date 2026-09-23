package command.notice;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.NoticeDao;
import dto.NoticeDto;

public class NoticeView implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		NoticeDao dao = new NoticeDao();
	      String no = request.getParameter("t_no");
	      
	      String gubun = request.getParameter("t_gubun");
		  if(gubun.equals("noticeView")) {
		      int result = dao.setHitCount(no);
		      if(result != 1) System.out.println("공지사항 조회수 증가 오류.");
		    
		      //이전글 '+' 다음글 '-'
		      NoticeDto preDto = dao.getPreNextNotice(no,"+");
		      NoticeDto nextDto = dao.getPreNextNotice(no,"-");
		      request.setAttribute("preDto", preDto);
		      request.setAttribute("nextDto", nextDto);
		     
	      }
	      NoticeDto dto = dao.noticeView(no);
	      request.setAttribute("dto", dto);
		
		
		
	}

}
