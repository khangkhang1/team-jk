package command.manager;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import common.FeeRule;
import dao.ManagerDao;
import dao.PaymentDao;

public class GateOut implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String rid    = CommonUtil.getCheckNull(request.getParameter("t_reservation_id")).trim().toUpperCase();
		String method = CommonUtil.getCheckNull(request.getParameter("t_pay_method"));
		if (method.equals("")) method = "card";

		String msg;
		ManagerDao dao = new ManagerDao();
		HashMap<String, Object> view = dao.getReservationView(rid);

		if (view.isEmpty()) {
			msg = "존재하지 않는 예약번호입니다.";
		} else if (!"2".equals(String.valueOf(view.get("status")))) {
			msg = "주차 중인 예약만 출차 처리할 수 있습니다. (현재 : " + view.get("status_label") + ")";
		} else {
			FeeRule.Settlement fee = FeeRule.settle(view);
			String paymentId = fee.getDue() > 0 ? PaymentDao.getDao().getPaymentId() : "";

			if (dao.gateOut(rid, paymentId, fee.getTotal(), fee.getDue(), method) == 1) {
				String detail = fee.isPlanFree()
						? String.format("주차 %d분 → 30분 × %d = %,d원", fee.getMinutes(), fee.getUnits(), fee.getTotal())
						: String.format("예약 요금 %,d원 + 초과 %d분(30분 × %d) %,d원", fee.getBase(), fee.getMinutes(), fee.getUnits(), fee.getExtra());
				String settle = fee.getDue() > 0 ? String.format("추가 결제 %,d원", fee.getDue()) : "추가 결제 없음";
				msg = String.format("출차 처리되었습니다. (%s)\n%s\n선결제 %,d원 차감 → %s", rid, detail, fee.getPrepaid(), settle);
			} else {
				msg = "출차 처리에 실패했습니다. 다시 시도하세요.";
			}
		}

		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Manager?t_gubun=gate&t_reservation_id=" + rid);
	}

}
