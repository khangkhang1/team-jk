package command.member;

import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.SecretConfig;
import dao.MemberDao;
import dto.MemberDto;
import mail.SendMail;

/**
 * 비밀번호 찾기 - 임시 비밀번호 메일 발송 (황희원 작성).
 *
 * [2026-09-18 강선구] test 브랜치가 이 파일 때문에 빌드가 안 돼서 최소한으로 맞춰 놓았습니다.
 *   없는 메서드를 부르고 있었습니다. MemberDao / MemberDto 에 있는 것으로 바꿨습니다.
 *     dao.getMemberEmail(id, 전화 3칸)  -> dao.getMemberInfo(id) + 전화번호 대조
 *     dao.getNewPassword(길이)          -> 이 클래스 안에서 임시 비밀번호 생성 (아래 makeTempPassword)
 *     dto.getEmail_1() + "@" + getEmail_2() -> dto.getEmail()  (DB 는 email 한 칸입니다)
 *     dao.memberPasswordUpdate(id, pw, 길이) -> memberPasswordUpdate(id, pw)  (인자 2개)
 *   기능 의도는 그대로 두었습니다. 설계가 다르면 희원상 방식으로 바꿔주세요.
 *   맨 위 주석에 적혀 있던 Gmail 앱 비밀번호도 지웠습니다 (이 저장소는 public 입니다).
 */
public class MemberSendPassword implements CommonExecute {

	// 임시 비밀번호 길이. 4자리는 너무 짧아 메일을 가로채지 않아도 맞힐 수 있어서 8자리로 둡니다.
	private static final int TEMP_PASSWORD_LENGTH = 8;

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();

		String id = request.getParameter("t_id");
		String mobile_1 = request.getParameter("t_mobile_1");
		String mobile_2 = request.getParameter("t_mobile_2");
		String mobile_3 = request.getParameter("t_mobile_3");

		MemberDto dto = (id == null || id.trim().equals("")) ? null : dao.getMemberInfo(id.trim());

		// 화면에서 전화번호를 3칸으로 받으면 회원 정보와 맞는지 확인한다 (ID 만으로 남의 비밀번호를 바꾸지 못하게)
		if (dto != null && !matchPhone(dto.getPhone_number(), mobile_1, mobile_2, mobile_3)) {
			dto = null;
		}

		String msg = "", url = "Member", gubun = "";

		if (dto == null) {
			msg = "ID나 연락처 정보가 정확하지 않습니다.";
			gubun = "findpassword";
		} else {
			String fromUserEmail    = SecretConfig.get("mail.from");         // secret.properties
			String fromUserPassword = SecretConfig.get("mail.appPassword");  // 구글 앱 비밀번호

			String newPassword = makeTempPassword(TEMP_PASSWORD_LENGTH);
			String toUserEmail = dto.getEmail();
			String mailTitle   = dto.getName() + "님 임시 비밀번호를 발송합니다.";
			String mailContent = "새로운 비밀번호는 " + newPassword + " 입니다. 로그인 후 바로 변경해 주세요.";

			SendMail sm = new SendMail(fromUserEmail, fromUserPassword);
			boolean tf = sm.sendPassword(toUserEmail, mailTitle, mailContent);

			if (tf) {
				try {
					newPassword = dao.encryptSHA256(newPassword);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int result = dao.memberPasswordUpdate(id, newPassword);
				if (result == 1) {
					msg = dto.getName() + "님 임시 비밀번호를 발송했습니다.";
				} else {
					msg = "임시 비밀번호 변경 실패! 관리자에게 문의 바랍니다.";
					System.out.println("임시 비밀번호 메일 발송 후 업데이트 오류");
				}
			} else {
				msg = "메일 발송에 실패했습니다. 잠시 후 다시 시도해 주세요.";
			}
			gubun = "login";
		}

		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", url);
		request.setAttribute("t_gubun", gubun);
	}

	// 전화번호 3칸을 붙여서 회원 정보와 비교. 하이픈이 있든 없든 맞도록 숫자만 남겨 비교한다.
	// 화면에서 전화번호를 안 받으면(파라미터 없음) 검사하지 않는다.
	private boolean matchPhone(String saved, String m1, String m2, String m3) {
		if (m1 == null && m2 == null && m3 == null) return true;
		String input = onlyDigit(m1) + onlyDigit(m2) + onlyDigit(m3);
		if (input.equals("")) return true;
		return onlyDigit(saved).equals(input);
	}

	private String onlyDigit(String s) {
		return s == null ? "" : s.replaceAll("[^0-9]", "");
	}

	// 임시 비밀번호. 헷갈리는 글자(0 O 1 l I)는 빼서 메일을 보고 옮겨 적을 때 틀리지 않게 한다.
	// 예측 가능한 Random 대신 SecureRandom 을 쓴다 (비밀번호라서).
	private String makeTempPassword(int length) {
		String pool = "23456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(pool.charAt(random.nextInt(pool.length())));
		}
		return sb.toString();
	}

}
