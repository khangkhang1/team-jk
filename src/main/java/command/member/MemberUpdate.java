package command.member;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.MemberDao;
import dto.MemberDto;

public class MemberUpdate implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		MemberDto dto = new MemberDto();
		String id = request.getParameter("t_id");
		String name = request.getParameter("t_name");
		dto.setMember_id(id);
		dto.setName(name);
		dto.setPhone_number(request.getParameter("t_phone_number"));
		dto.setEmail(request.getParameter("t_email"));
		dto.setVehicle_number(request.getParameter("t_vehicle_number"));
		dto.setVehicle_type(request.getParameter("t_vehicle_type"));
		;
		int result=dao.memberUpdate(dto);
		String msg=result==1?"수정 성공":"수정 실패";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Member");
		request.setAttribute("t_gubun", "myinfo");
		request.setAttribute("t_id", id);
	}

}
