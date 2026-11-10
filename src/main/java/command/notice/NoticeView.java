package command.notice;

import common.I18n;

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
		      if (preDto != null) preDto.setTitle(I18n.content(request, "notice." + preDto.getNo() + ".title", preDto.getTitle()));
		      if (nextDto != null) nextDto.setTitle(I18n.content(request, "notice." + nextDto.getNo() + ".title", nextDto.getTitle()));
		      request.setAttribute("preDto", preDto);
		      request.setAttribute("nextDto", nextDto);
		     
	      }
	      NoticeDto dto = dao.noticeView(no);
	      // 일본어 번역이 있으면 제목·내용 교체 (content_ja.properties).
	      // 수정폼(noticeUpdateForm)은 DB 원문을 그대로 보여준다 - 번역문이 폼에 들어가면 저장할 때 원문을 덮어쓴다.
	      if (dto != null && gubun.equals("noticeView")) {
	    	  dto.setTitle(I18n.content(request, "notice." + no + ".title", dto.getTitle()));
	    	  dto.setContent(I18n.content(request, "notice." + no + ".content", dto.getContent()));
	      }
	      request.setAttribute("dto", dto);
		
		
		
	}

}
