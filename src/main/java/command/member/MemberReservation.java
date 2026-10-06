package command.member;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.MemberDao;

public class MemberReservation implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		String id=(String)request.getSession().getAttribute("sessionId");

		List<Map<String, Object>> reservations = dao.getReservationInfo(id);
		request.setAttribute("reservationList", reservations);

		// [2026-09-29] 빌드 복구용으로 잠시 막아둠 - 황희원
		//   MemberDao.getReservationInfo() 가 9/23 에 주석 처리됐는데(작성 중이던 상태)
		//   이 호출만 남아 있어서 프로젝트 전체가 컴파일되지 않았습니다.
		//   그 메서드를 완성하시면 아래 두 줄의 주석을 풀어주세요.
		//   (메서드 안에 dto 변수가 선언되어 있지 않고, reservation_start_date 처럼
		//    icn_reservation 에 없는 컬럼을 조회하고 있어서 그대로는 동작하지 않습니다)
		// List<ReservationInfoDto> dtos = dao.getReservationInfo(id);
		// request.setAttribute("reservationList", dtos);
	}

}
