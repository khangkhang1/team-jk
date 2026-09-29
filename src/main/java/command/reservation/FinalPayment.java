package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.PaymentDao;

public class FinalPayment implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {

		PaymentDao dao = PaymentDao.getDao();
		String reservation_id = request.getParameter("t_reservation_id");
		String reservation_pay_method = request.getParameter("t_reservation_pay_method");
		String payment_id = dao.getPaymentId();
		long totalPrice = (long)Integer.parseInt(request.getParameter("t_final_amount"));
		int result = dao.finalPaymentR(reservation_id, totalPrice, CommonUtil.getTodayTime())
						+ dao.finalPaymentP(payment_id, reservation_id, totalPrice, CommonUtil.getTodayTime(), reservation_pay_method);
		
		String msg = "결제에 실패하였습니다.";
		if(result == 2) {
			msg = "결제가 완료되었습니다.\r\n"
					+ "결제액:  "+totalPrice+"원 결제";
		}
		
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "ParkingStatus");
	}

}
