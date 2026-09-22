package command.member;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.MemberDao;
import dto.ReservationInfoDto;

public class MemberReservation implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		String id=(String)request.getSession().getAttribute("sessionId");
		List<ReservationInfoDto> dtos=dao.getReservationInfo(id);
		

	}

}
