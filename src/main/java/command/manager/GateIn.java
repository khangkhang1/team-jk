package command.manager;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.ManagerDao;

// 입차 처리. Manager?t_gubun=gateIn  : 예약완료(1) → 주차 중(2)
// 차단기·번호판 인식 대신 관리자가 예약번호로 입차를 확인하는 "하드웨어 대체" 입력이다.
public class GateIn implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String rid = CommonUtil.getCheckNull(request.getParameter("t_reservation_id")).trim().toUpperCase();

		int result = 0;
		if (!rid.equals("")) {
			result = new ManagerDao().gateIn(rid);
		}

		String msg = (result == 1)
				? "입차 처리되었습니다. (" + rid + ")"
				: "입차 처리할 수 없습니다. 예약완료 상태인 예약번호인지 확인하세요.";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Manager?t_gubun=gate&t_reservation_id=" + rid);
	}

}
