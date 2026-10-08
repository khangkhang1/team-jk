package command.index;

import common.I18n;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.NoticeDao;
import dto.NoticeDto;

public class indexNotice implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		NoticeDao dao = new NoticeDao();
		//String no = request.getParameter("t_no");
		
		List<NoticeDto> dtos = dao.indexNotice();
		for (NoticeDto d : dtos) d.setTitle(I18n.content(request, "notice." + d.getNo() + ".title", d.getTitle()));   // 일본어 번역이 있으면 제목 교체
		
		
		
		request.setAttribute("t_dtos", dtos);

	}
	
}