package command.member;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.MemberDao;
import dto.MemberDto;

public class MemberSave implements CommonExecute {

    @Override
    public void execute(HttpServletRequest request) {
        MemberDao dao = MemberDao.getdao();

        String id = request.getParameter("t_id");
        String password = request.getParameter("t_password");
        String passwordConfirm = request.getParameter("t_password_confirm");
        String name = request.getParameter("t_name");
        String phoneNumber = request.getParameter("t_phone_number");
        String email = request.getParameter("t_email");
        String vehicleNumber = request.getParameter("t_vehicle_number");
        String vehicleType = request.getParameter("t_vehicle_type");

        // 아이디:
        // 소문자 최소 1자 포함, 4~20자
        // 영문 대/소문자, 숫자, !@#$%^&*_-. 허용
        String idPattern =
                "^(?=.*[a-z])[a-zA-Z0-9!@#$%^&*_.-]{4,20}$";

        if (isEmpty(id) || !id.matches(idPattern)) {
            alert(request,
                    "영문 소문자를 최소 1자 이상 포함하여 4~20자로 입력해주세요. "
                    + "특수문자는 !@#$%^&*_-.만 가능합니다.");
            return;
        }

        id = id.trim();

        // 가입 직전에 DB 중복 검사
        if (dao.checkId(id) > 0) {
            alert(request, "이미 사용 중인 아이디입니다.");
            return;
        }

        // 비밀번호:
        // 소문자 최소 1자 포함, 6~16자
        // 영문 대/소문자, 숫자, !@#$%^&*_+?.- 허용
        String passwordPattern =
                "^(?=.*[a-z])[A-Za-z0-9!@#$%^&*_+?.-]{6,16}$";

        if (isEmpty(password) || !password.matches(passwordPattern)) {
            alert(request,
                    "비밀번호는 영문 소문자를 최소 1자 이상 포함해 6~16자로 입력해주세요. "
                    + "특수문자는 !@#$%^&*_+?.-만 사용할 수 있습니다.");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            alert(request, "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return;
        }

        if (isEmpty(name)) {
            alert(request, "성명을 입력하세요.");
            return;
        }

        // 01012345678 또는 010-1234-5678
        String phonePattern = "^010-?\\d{4}-?\\d{4}$";

        if (isEmpty(phoneNumber) || !phoneNumber.matches(phonePattern)) {
            alert(request, "올바른 형식의 전화번호를 입력해주세요.");
            return;
        }

        String emailPattern =
                "^[a-zA-Z0-9!@#$%^&*_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (isEmpty(email) || !email.matches(emailPattern)) {
            alert(request, "올바른 이메일 형식을 입력해주세요.");
            return;
        }

        if (isEmpty(vehicleNumber)) {
            alert(request, "차량 번호를 입력하세요.");
            return;
        }

        if (!"N".equals(vehicleType)
                && !"E".equals(vehicleType)
                && !"D".equals(vehicleType)) {
            alert(request, "차량 종류를 선택하세요.");
            return;
        }

//        if (request.getParameter("terms_agree") == null
//                || request.getParameter("privacy_agree") == null) {
//            alert(request, "필수 약관에 동의해주세요.");
//            return;
//        }

        // 모든 서버 검증 통과 후 암호화
        try {
            password = dao.encryptSHA256(password);
        } catch (Exception e) {
            e.printStackTrace();
            alert(request, "비밀번호 암호화 중 오류가 발생했습니다.");
            return;
        }

        MemberDto dto = new MemberDto();
        dto.setMember_id(id);
        dto.setName(name.trim());
        dto.setPassword(password);
        dto.setPhone_number(phoneNumber.trim());
        dto.setEmail(email.trim());
        dto.setVehicle_number(vehicleNumber.trim());
        dto.setVehicle_type(vehicleType);

        int result = dao.memberSave(dto);

        String msg = result == 1
                ? name.trim() + "님 회원가입 되셨습니다."
                : "회원가입 실패!";

        request.setAttribute("t_msg", msg);
        request.setAttribute("t_url", "Member");
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    private void alert(HttpServletRequest request, String message) {
        request.setAttribute("t_msg", message);
        request.setAttribute("t_url", "Member?t_gubun=join");
    }
}