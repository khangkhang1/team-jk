package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.ReservationDao;

public class Payment implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		ReservationDao dao = ReservationDao.getDao();
		

	}

}
