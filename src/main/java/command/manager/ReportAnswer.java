package command.manager;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.ReportDao;

/**
 * 신고 처리 : 처리 상태 변경 + 처리 내용 저장. Manager?t_gubun=reportAnswer (POST)
 *
 * 규칙을 하나 넣어 두었다 : 「처리 완료」・「반려」로 바꿀 때는 처리 내용을 반드시 적게 한다.
 *   나중에 "이 신고 왜 반려됐냐"는 문의가 오면 근거가 없으면 답할 수 없다.
 *   화면에서만 막으면 우회할 수 있으므로 서버에서도 한 번 더 본다 (입력 검증은 양쪽 모두).
 */
public class ReportAnswer implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String reportId = CommonUtil.getCheckNull(request.getParameter("t_report_id")).trim();
		String status   = CommonUtil.getCheckNull(request.getParameter("t_report_status")).trim();
		String answer   = CommonUtil.getCheckNull(request.getParameter("t_answer_content")).trim();

		String adminId = (String) request.getSession().getAttribute("sessionId");
		if (adminId == null) adminId = "manager";

		// 신고번호가 숫자가 아니면 목록으로 돌려보낸다 (주소창에 직접 친 경우)
		if (!reportId.matches("[0-9]+")) {
			request.setAttribute("t_msg", "잘못된 신고번호입니다.");
			request.setAttribute("t_url", "Manager?t_gubun=report");
			return;
		}
		String viewUrl = "Manager?t_gubun=reportView&t_report_id=" + reportId;

		if (!status.matches("[1-4]")) {
			request.setAttribute("t_msg", "처리 상태를 선택하세요.");
			request.setAttribute("t_url", viewUrl);
			return;
		}
		if ((status.equals("3") || status.equals("4")) && answer.equals("")) {
			request.setAttribute("t_msg", "처리 완료·반려로 바꿀 때는 처리 내용을 남겨야 합니다.");
			request.setAttribute("t_url", viewUrl);
			return;
		}

		// 저장은 입력 그대로 한다. 따옴표·부등호를 여기서 바꾸지 않는 이유는
		// SQL 은 ? 바인딩이라 안전하고, 화면 출력은 JSP 의 <c:out> 이 이스케이프하기 때문이다 (出力時エスケープ).
		int result = new ReportDao().updateAnswer(Integer.parseInt(reportId), status, answer, adminId);

		String msg = result == 1
				? "신고 " + reportId + "번을 '" + statusLabel(status) + "' 로 처리했습니다."
				: "저장하지 못했습니다. 신고번호를 확인하세요.";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", viewUrl);
	}

	private String statusLabel(String status) {
		if (status.equals("1")) return "접수";
		if (status.equals("2")) return "처리 중";
		if (status.equals("3")) return "처리 완료";
		if (status.equals("4")) return "반려";
		return status;
	}

}
