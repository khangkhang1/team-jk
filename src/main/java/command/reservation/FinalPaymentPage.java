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
        
        if (dto == null || dto.getReservation_parking_start_time() == null) {
            request.setAttribute("t_msg", "예약 정보 또는 입차 기록을 찾을 수 없습니다.");
            request.setAttribute("t_url", "ParkingStatus");
            return;
        }

        try {
            // 1. 날짜 변환 도구 (포맷 지정)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // 2. 시간 2개 가져와서 밀리초(숫자)로 변환
            long startMs = sdf.parse(dto.getReservation_parking_start_time()).getTime();
            long endMs = sdf.parse(paymentTime).getTime();
            
            // ================= [ 수정된 로직 위치 ] =================
            
            // 3. 차이 시간 계산 (초 단위)
            long diffSeconds = (endMs - startMs) / 1000;

            // 4. 음수 체크: 결제 시간이 입차 시간보다 이전인 경우 막기
            if (diffSeconds < 0) {
                request.setAttribute("t_msg", "결제 시간이 입차 시간보다 빠릅니다. 시스템 시간을 확인해주세요.");
                request.setAttribute("t_url", "ParkingStatus");
                return; 
            }

            // 5. 30분 단위(1800초) 올림 계산 (1초라도 지나면 1단위 증가)
            long units = (long) Math.ceil((double) diffSeconds / 1800);

            // 6. 0초(입차 직후 결제)인 경우 최소 1단위(기본 30분) 적용
            if (units == 0) {
                units = 1;
            }

            long totalPrice = units * 4500;
            
            // ========================================================

            // 7. JSP로 값 전달
            request.setAttribute("dto", dto);
            request.setAttribute("paymentTime", paymentTime);
            request.setAttribute("totalPrice", totalPrice);
            request.setAttribute("t_url", "Payment");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}