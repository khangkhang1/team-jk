package command.member;

import common.I18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import common.CommonExecute;

public class MemberLogout implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String name = (String)session.getAttribute("sessionName");
		String msg=I18n.msg(request, "msg.logoutName", name);
		if(name==null)msg=I18n.msg(request, "msg.logout");
		
		session.invalidate();
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "ParkingStatus");
		
	}

}
