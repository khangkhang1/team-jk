package command.report;

import common.I18n;

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
			request.setAttribute("t_msg", I18n.msg(request, msg));
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
				? I18n.msg(request, "msg.reportSaved")
				: I18n.msg(request, "msg.reportFail"));
		request.setAttribute("t_url", result == 1 ? "ParkingStatus" : "Report");
	}

	private String check(String type, String title, String content, String seatNo, String reservationId) {
		if (!type.matches("[1-5]"))  return "msg.reportType";
		if (title.equals(""))        return "msg.reportTitle";
		if (content.equals(""))      return "msg.reportContent";
		if (bytes(title) > 200)      return "msg.reportTitleLong";
		if (bytes(content) > 2000)   return "msg.reportContentLong";
		if (bytes(seatNo) > 20)      return "msg.reportSeat";
		if (bytes(reservationId) > 30) return "msg.reportResv";
		return null;
	}

	private int bytes(String s) {
		return s.getBytes(StandardCharsets.UTF_8).length;
	}

}
