package command.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import common.CommonExecute;
import dao.MemberDao;

public class MemberPasswordUpdate implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao =MemberDao.getdao();
		String id=request.getParameter("t_id");
		String password=request.getParameter("t_new_password");
		try {
			password=dao.encryptSHA256(password);
		}catch(Exception e) {
			e.printStackTrace();
		}
		int result= dao.memberPasswordUpdate(id,password);
		String msg="",url="Member",gubun="";
		if(result==1) {
			msg="비밀번호가 수정되었습니다. 다시 로그인해주세요.";
			gubun="login";
			HttpSession session = request.getSession();
			session.invalidate();
		}else {
			msg="비밀번호 변경 실패";
			
			gubun="passwordUpdateForm";
		}
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", url);
		request.setAttribute("t_gubun", gubun);
	}

}
