package command.reservation;

import javax.servlet.http.HttpServletRequest;
import common.CommonExecute;
import common.CommonUtil;
import dao.PaymentDao;
import dto.ReservationInfoDto;

public class FinalPaymentPage implements CommonExecute {

    @Override
    public void execute(HttpServletRequest request) {
        PaymentDao dao = PaymentDao.getDao();
        String reservation_id = request.getParameter("reservation_id");
        ReservationInfoDto dto = dao.getReservationDto(reservation_id);
        String paymentTime = CommonUtil.getTodayTime();

        try {
            // 1. 날짜 변환 도구 (포맷 지정)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // 2. 시간 2개 가져와서 밀리초(숫자)로 바꾼 뒤 단순 빼기 (-)
            long startMs = sdf.parse(dto.getReservation_parking_start_time()).getTime();
            long endMs = sdf.parse(paymentTime).getTime();
            
            // 3. 빼기 결괏값(밀리초) -> 분(minute) 변환
            long minutes = (endMs - startMs) / 1000 / 60;
            
            // 4. 30분 단위 올림해서 요금 계산 (30분당 4500원)
            long totalPrice = (long) Math.ceil((double) minutes / 30) * 4500;

            // 5. JSP로 값 전달
            request.setAttribute("dto", dto);
            request.setAttribute("paymentTime", paymentTime);
            request.setAttribute("totalPrice", totalPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}