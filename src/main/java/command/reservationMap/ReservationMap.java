package command.reservationMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.ReservationMapDao;
import dto.ReservationMapDto;

public class ReservationMap implements CommonExecute {

    @Override
    public void execute(HttpServletRequest request) {
        String parkingLotId = request.getParameter("lotId");
        if (parkingLotId == null || parkingLotId.trim().isEmpty()) {
            parkingLotId = request.getParameter("zone");
        }
        if (parkingLotId == null || parkingLotId.trim().isEmpty()) {
            parkingLotId = "P1";
        }
        
        String id = (String) request.getSession().getAttribute("sessionId");
        
        String startTime = request.getParameter("reqStartTime");
        String endTime = request.getParameter("reqEndTime");
        
        String today = LocalDate.now().toString();

       

      
       
        // [변경] 자바에서의 일괄적인 3시간 추가 로직 제거 (SQL에서 구역별로 처리)

        ReservationMapDao dao = ReservationMapDao.getDao();
        
        // 결항 자동 재배정 실행
        try {
            dao.autoChange();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // DB 맵 데이터 조회 (정제된 startTime, endTime 그대로 전달)
        List dtos = dao.getPakingMap(parkingLotId.toUpperCase(), startTime, endTime);
        
        String type = "N";
        if (id != null && !id.trim().isEmpty()) {
            type = dao.getMemberType(id);
        }
        
        // JSP로 데이터 전달
        request.setAttribute("seatList", dtos);
        request.setAttribute("selectedLotId", parkingLotId.toUpperCase());
        request.setAttribute("reqStartTime", startTime);
        request.setAttribute("reqEndTime", endTime);
        request.setAttribute("memberType", type);
    }
}