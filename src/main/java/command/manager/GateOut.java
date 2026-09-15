package command.manager;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import common.FeeRule;
import dao.ManagerDao;
import dao.PaymentDao;

/**
 * 출차 처리 + 정산. Manager?t_gubun=gateOut  : 주차 중(2) → 출차 완료(3)
 *
 *   총 요금  = 이용 시간(시작 시각 ~ 지금) × 시간당 요금   (common.FeeRule)
 *   추가 결제 = 총 요금 - 이미 낸 예약금
 *     > 0 이면 결제(payment_type 2), < 0 이면 환불(3), 0 이면 결제 행 없음
 *   예약 갱신 + 결제 저장은 ManagerDao.gateOut 이 한 트랜잭션으로 처리한다.
 *
 * 결제번호는 오윤섭 PaymentDao (싱글턴, getDao()) 의 getPaymentId() 를 그대로 써서 "P26-09-0012" 형식이 이어지게 한다.
 */
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
			String type   = String.valueOf(view.get("reservation_type"));
			double hours  = ((Number) view.get("hours_now")).doubleValue();
			int deposit   = ((Number) view.get("paid_deposit")).intValue();
			int total     = FeeRule.totalFee(type, hours);
			int due       = total - deposit;

			String paymentId = "";
			if (due != 0) paymentId = PaymentDao.getDao().getPaymentId();

			int result = dao.gateOut(rid, paymentId, due, method);

			if (result == 1) {
				String settle = due > 0 ? String.format("추가 결제 %,d원", due)
				              : due < 0 ? String.format("환불 %,d원", -due)
				              : "추가 결제 없음";
				msg = String.format("출차 처리되었습니다. (%s)\n이용 %.1f시간 × %,d원 = 총 %,d원\n예약금 %,d원 차감 → %s",
						rid, FeeRule.billableHours(hours), FeeRule.hourlyRate(type), total, deposit, settle);
			} else {
				msg = "출차 처리에 실패했습니다. 다시 시도하세요.";
			}
		}

		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Manager?t_gubun=gate&t_reservation_id=" + rid);
	}

}
