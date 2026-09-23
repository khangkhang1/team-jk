package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;

public class FinalPaymentView implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String reservation_start_time = request.getParameter("reservation_start_time");
		String seat_no = request.getParameter("seat_no");
		String paymentTime = request.getParameter("paymentTime");
		long totalPrice = (long)Integer.parseInt(request.getParameter("totalPrice"));
		
		request.setAttribute("reservation_start_time", reservation_start_time);
        request.setAttribute("seat_no", seat_no);
        request.setAttribute("paymentTime", paymentTime);
        request.setAttribute("totalPrice", totalPrice);

	}

}
