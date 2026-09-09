package command.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import common.CommonExecute;
import dao.MemberDao;
import dto.MemberDto;

public class MemberMyInfo implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		String id= (String)request.getSession().getAttribute("sessionId");
		
		MemberDto dto = dao.getMemberInfo(id);
		request.setAttribute("t_dto", dto);
	}

}
