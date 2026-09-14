package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.PaymentDao;
import dto.PaymentDto;
import dto.ReservationInfoDto;

public class Payment implements CommonExecute {
//현재 결제 성공 메세지를 reservationOys.jsp에서 출력 후 넘어옴 -> dao에서 문제 발생 시 성공 메세지 출력 이후 실패 가능성 존재
//	==> jsp가 아닌 여기에서 성공 여부 출력하도록 수정 예정
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
		
		//1유형 2유형 구분하여 예약 dto생성, dao에 넘김(예약 저장)
		if(plan.equals("1")) {
			r_dto = new ReservationInfoDto(reservation_id, flight_no, "1", start_date, start_time, end_date, end_time, plan, member_id, seat,
											estimate_amount, deposit_amount);
		} else {
			r_dto = new ReservationInfoDto(reservation_id, "1", start_date, start_time, plan, member_id, seat, deposit_amount);
		}
		
		//결제 dto생성, dao에 넘김(결제 저장)
		PaymentDto p_dto = new PaymentDto(payment_id, reservation_id, deposit_amount, payment_method, "1", CommonUtil.getTodayTime());
		
		//dao가 모두 정상 실행될 경우 result == 2 / 이후 오토커밋 수정 예정(하나만 오류인 경우 둘 중 하나는 DB에 저장되기 때문)
		int result = dao.saveReservation(r_dto);
		result += dao.savePayment(p_dto);
		
		//dao 비정상인 경우를 기본값으로 설정
		String msg = "예약 실패(dao처리 과정에서 오류)";
		
		//result == 2인 경우
		if(result == 2) {
			msg = "예약 성공(dao 정상 처리)";
		}
		
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Reservation");
	}

}
