package command.reservation;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.PaymentDao;

public class FinalPayment implements CommonExecute {

    @Override
    public void execute(HttpServletRequest request) {

        PaymentDao dao = PaymentDao.getDao();
        
        String reservation_id = request.getParameter("t_reservation_id");
        String reservation_pay_method = request.getParameter("t_reservation_pay_method");
        String imp_uid = request.getParameter("t_imp_uid");
        String merchant_uid = request.getParameter("t_merchant_uid");
        
        String amountStr = request.getParameter("t_final_amount");
        int totalPrice = 0;
        
        try {
            if (amountStr != null) {
                amountStr = amountStr.replaceAll("[^0-9]", "");
                totalPrice = Integer.parseInt(amountStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("t_msg", "결제 금액 형식이 올바르지 않습니다.");
            request.setAttribute("t_url", "ParkingStatus");
            return;
        }

        // 3. 결제 PK 생성 및 현재 시간 수집
        String payment_id = dao.getPaymentId();
        String todayTime = CommonUtil.getTodayTime();

        // 4. DB 처리 (int 타입 값 전달)
        int resultR = dao.finalPaymentR(reservation_id, totalPrice, todayTime);
        int resultP = dao.finalPaymentP(payment_id, reservation_id, totalPrice, todayTime, reservation_pay_method);
        
        // 두 작업 모두 정상 실행(1 + 1 = 2)되었는지 확인
        int result = resultR + resultP;

        // 5. 결과 메시지 및 이동 URL 설정
        String msg = "결제 처리에 실패하였습니다.";
        if (result == 2) {
            msg = "결제가 완료되었습니다.\r\n"
                + "결제액: " + String.format("%,d", totalPrice) + "원"; // 천단위 콤마 표기
        }

        request.setAttribute("t_msg", msg);
        request.setAttribute("t_url", "ParkingStatus");
    }
}