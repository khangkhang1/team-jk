package command.member;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.MemberDao;
import dto.MemberDto;

public class MemberUpdate implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		String id = (String) request.getSession().getAttribute("sessionId");

		if (id == null) {
			alert(request, "로그인 정보가 만료되었습니다.");
			return;
		}

		MemberDto oldDto = dao.getMemberInfo(id);
		if (oldDto == null) {
			alert(request, "회원 정보를 찾을 수 없습니다.");
			return;
		}

		String name = request.getParameter("t_name");
		String phoneNumber = request.getParameter("t_phone_number");
		String email = request.getParameter("t_email");
		String vehicleNumber = request.getParameter("t_vehicle_number");
		String vehicleType = request.getParameter("t_vehicle_type");

		if (isEmpty(name)) {
			alert(request, "성명을 입력하세요.");
			return;
		}

		String phonePattern = "^010-?\\d{4}-?\\d{4}$";
		if (isEmpty(phoneNumber) || !phoneNumber.matches(phonePattern)) {
			alert(request, "올바른 형식의 전화번호를 입력해주세요.");
			return;
		}

		String emailPattern = "^[a-zA-Z0-9!@#$%^&*_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (isEmpty(email) || !email.matches(emailPattern)) {
			alert(request, "올바른 이메일 형식을 입력해주세요.");
			return;
		}
		email = email.trim();

		if (!oldDto.getEmail().equals(email)) {
			String verifiedEmail = (String) request.getSession().getAttribute("verifiedEmail");
			if (verifiedEmail == null || !verifiedEmail.equals(email)) {
				alert(request, "변경할 이메일의 인증을 완료해주세요.");
				return;
			}
		}

		if (isEmpty(vehicleNumber)) {
			alert(request, "차량 번호를 입력하세요.");
			return;
		}

		if (!"N".equals(vehicleType) && !"E".equals(vehicleType) && !"D".equals(vehicleType)) {
			alert(request, "차량 종류를 선택하세요.");
			return;
		}

		MemberDto dto = new MemberDto();
		dto.setMember_id(id);
		dto.setName(name.trim());
		dto.setPhone_number(phoneNumber.trim());
		dto.setEmail(email);
		dto.setVehicle_number(vehicleNumber.trim());
		dto.setVehicle_type(vehicleType);

		int result = dao.memberUpdate(dto);
		if (result == 1) {
			request.getSession().setAttribute("sessionName", name.trim());
			request.getSession().removeAttribute("emailVerifyCode");
			request.getSession().removeAttribute("emailVerifyEmail");
			request.getSession().removeAttribute("emailVerifyExpire");
			request.getSession().removeAttribute("emailVerifyLastSent");
			request.getSession().removeAttribute("verifiedEmail");
		}

		request.setAttribute("t_msg", result == 1 ? "회원정보가 수정되었습니다." : "회원정보 수정에 실패했습니다.");
		request.setAttribute("t_url", "Member?t_gubun=myinfo");
	}

	private boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}

	private void alert(HttpServletRequest request, String message) {
		request.setAttribute("t_msg", message);
		request.setAttribute("t_url", "Member?t_gubun=myinfo");
	}
}
