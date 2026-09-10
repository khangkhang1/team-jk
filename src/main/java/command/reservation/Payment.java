package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.PaymentDao;
import dto.PaymentDto;
import dto.ReservationInfoDto;

public class Payment implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		PaymentDao dao = PaymentDao.getDao();
		String reservation_id = dao.getReservationId();
		String payment_id = dao.getPaymentId();
		String plan = request.getParameter("t_reservation_plan");
		String seat = request.getParameter("t_reservation_seat");
		String start_date = request.getParameter("t_reservation_start_date");
		String start_time = request.getParameter("t_reservation_start_time");
		String end_date = request.getParameter("t_reservation_end_date");
		String end_time = request.getParameter("t_reservation_end_time");
		String flight_no = request.getParameter("t_reservation_flight_no");
		String payment_method = request.getParameter("t_reservation_pay_method");
		int estimate_amount = Integer.parseInt(request.getParameter("t_reservation_estimate_amount"));
		int deposit_amount = Integer.parseInt(request.getParameter("t_reservation_deposit_amount"));
		String member_id = (String)request.getSession().getAttribute("sessionId");
		
		ReservationInfoDto r_dto = null;
		
		if(plan.equals("1")) {
			r_dto = new ReservationInfoDto(reservation_id, flight_no, "1", start_date, start_time, end_date, end_time, plan, member_id, seat);
		} else {
			r_dto = new ReservationInfoDto(reservation_id, "1", start_date, start_time, plan, member_id, seat);
		}
		
		PaymentDto p_dto = new PaymentDto(payment_id, reservation_id, estimate_amount, deposit_amount, payment_method, "1", CommonUtil.getTodayTime());
		
		
		int result = dao.saveReservation(r_dto, p_dto);
	}

}
