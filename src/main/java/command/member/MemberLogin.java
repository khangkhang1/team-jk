package command.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import common.CommonExecute;
import dao.MemberDao;

public class MemberLogin implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		String id = request.getParameter("t_id");
		String password = request.getParameter("t_password");
		try {
			password=dao.encryptSHA256(password);
		}catch(Exception e) {
			e.printStackTrace();
		}
		String name= dao.getLoginName(id,password);

		String msg="",url="";
		if(!name.equals("")) {
			msg=name+"님 환영합니다.";
			url="Index";
			HttpSession session = request.getSession();
			session.setAttribute("sessionId", id);
			session.setAttribute("sessionName", name);
			if(id.equals("manager")) {
				session.setAttribute("sessionLevel", "top");
			}
			session.setMaxInactiveInterval(60*60*4);
		}else {
			msg="ID나 비밀번호가 일치하지 않습니다.";
			url="Member";
		}
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", url);
		
	}

}
