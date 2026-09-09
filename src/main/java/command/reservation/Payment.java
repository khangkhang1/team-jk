package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.reservationDao;

public class Payment implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		reservationDao dao = reservationDao.getDao();
		

	}

}
