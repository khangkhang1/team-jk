package command.member;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.MemberDao;
import dto.MemberDto;

public class MemberSave implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		MemberDto dto = new MemberDto();
		String id = request.getParameter("t_id");
		String name = request.getParameter("t_name");
		String password = request.getParameter("t_password");
		try {
			password = dao.encryptSHA256(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dto.setMember_id(id);
		dto.setName(name);
		dto.setPassword(password);
		dto.setPhone_number(request.getParameter("t_phone_number"));
		dto.setEmail(request.getParameter("t_email"));
		dto.setVehicle_number(request.getParameter("t_vehicle_number"));
		dto.setVehicle_type(request.getParameter("t_vehicle_type"));
		;

		int result = dao.memberSave(dto);
		String msg = result == 1 ? name + "님 회원가입 되셨습니다." : "회원가입 실패!";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Member");

	}

}
