package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.PaymentDao;
import dto.ReservationInfoDto;

public class FinalPaymentPage implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		PaymentDao dao = PaymentDao.getDao();
		String reservation_id = request.getParameter("reservation_id");
		ReservationInfoDto dto = dao.getReservationDto(reservation_id);
		request.setAttribute("dto", dto);

	}

}
