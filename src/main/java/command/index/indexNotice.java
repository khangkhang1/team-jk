package command.index;

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
		
		
		
		request.setAttribute("t_dtos", dtos);

	}
	
}