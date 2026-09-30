package command.index;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.PaymentDao;
import dao.indexReservationDao;
import dto.ReservationDto;
import dto.ReservationInfoDto;

public class indexReservation implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		indexReservationDao dao = new indexReservationDao();
		String id =(String)request.getSession().getAttribute("sessionId");
		
		ReservationInfoDto dto = dao.getFinalPayment(id);
		
		
		
		
		request.setAttribute("r_dto", dto);
		
		
//		dto=dao.getreser(id);
//		sql+
		
		
				
				

	}

}
