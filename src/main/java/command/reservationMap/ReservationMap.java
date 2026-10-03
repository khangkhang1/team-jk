package command.reservationMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // 1. 파라미터 정제 (T 제거 및 'yyyy-MM-dd'만 들어온 경우 시:분 보완)
        if (startTime != null && !startTime.trim().isEmpty()) {
            startTime = startTime.replace("T", " ");
            if (startTime.length() == 10) startTime += " 00:00";
        } else {
            startTime = today + " 00:00";
        }

        if (endTime != null && !endTime.trim().isEmpty()) {
            endTime = endTime.replace("T", " ");
            if (endTime.length() == 10) endTime += " 23:59";
        } else {
            endTime = today + " 23:59";
        }
       
        // 2. 안전한 파싱 및 3시간 더하기
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dt = LocalDateTime.parse(endTime, formatter);
            dt = dt.plusHours(3); 
            endTime = dt.format(formatter);
        } catch (Exception e) {
            System.out.println("날짜 포맷 파싱 오류 발생, 기본값으로 대체합니다.");
            e.printStackTrace();
            // 에러 발생 시 안전하게 오늘 날짜 기반으로 재설정
            endTime = today + " 23:59";
        }

        ReservationMapDao dao = ReservationMapDao.getDao();
        
        // 결항 자동 재배정 실행
        try {
            dao.autoChange();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // DB 맵 데이터 조회
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