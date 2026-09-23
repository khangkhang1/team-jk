package command.report;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.ReportDao;
import dto.ReportDto;

public class ReportSave implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String memberId      = (String) request.getSession().getAttribute("sessionId");
		String type          = CommonUtil.getCheckNull(request.getParameter("t_report_type")).trim();
		String title         = CommonUtil.getCheckNull(request.getParameter("t_title")).trim();
		String content       = CommonUtil.getCheckNull(request.getParameter("t_content")).trim();
		String seatNo        = CommonUtil.getCheckNull(request.getParameter("t_seat_no")).trim().toUpperCase();
		String reservationId = CommonUtil.getCheckNull(request.getParameter("t_reservation_id")).trim().toUpperCase();

		String msg = check(type, title, content, seatNo, reservationId);
		if (msg != null) {
			request.setAttribute("t_msg", msg);
			request.setAttribute("t_url", "Report");
			return;
		}

		ReportDto dto = new ReportDto();
		dto.setMember_id(memberId);
		dto.setReport_type(type);
		dto.setTitle(title);
		dto.setContent(content);
		dto.setSeat_no(seatNo.equals("") ? null : seatNo);
		dto.setReservation_id(reservationId.equals("") ? null : reservationId);

		int result = new ReportDao().reportSave(dto);
		request.setAttribute("t_msg", result == 1
				? "문의가 접수되었습니다. 담당자가 확인 후 연락드리겠습니다."
				: "접수하지 못했습니다. 잠시 후 다시 시도해 주세요.");
		request.setAttribute("t_url", result == 1 ? "Index" : "Report");
	}

	private String check(String type, String title, String content, String seatNo, String reservationId) {
		if (!type.matches("[1-5]"))  return "문의 종류를 선택하세요.";
		if (title.equals(""))        return "제목을 입력하세요.";
		if (content.equals(""))      return "내용을 입력하세요.";
		if (bytes(title) > 200)      return "제목이 너무 깁니다. (한글 기준 약 60자)";
		if (bytes(content) > 2000)   return "내용이 너무 깁니다. (한글 기준 약 600자)";
		if (bytes(seatNo) > 20)      return "좌석 번호를 확인하세요. 예) P1-07";
		if (bytes(reservationId) > 30) return "예약 번호를 확인하세요.";
		return null;
	}

	private int bytes(String s) {
		return s.getBytes(StandardCharsets.UTF_8).length;
	}

}
