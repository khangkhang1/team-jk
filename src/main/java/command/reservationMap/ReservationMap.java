package command.reservationMap;

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
        
        String startTime = request.getParameter("reqStartTime");
        String endTime = request.getParameter("reqEndTime");

        // [수정] 기본값 설정: 테스트 날짜(2026-09-11) 데이터가 조회되도록 범위 지정
        if (parkingLotId == null || parkingLotId.trim().isEmpty()) {
            parkingLotId = "P1";
        }
        if (startTime == null || startTime.trim().isEmpty()) {
            startTime = "2026-09-11 00:00"; // 해당 일자 시작점
        }
        if (endTime == null || endTime.trim().isEmpty()) {
            endTime = "2026-09-13 23:59";   // 해당 일자 종료점 (2099년 대신 해당 날짜 전체 조회)
        }

        ReservationMapDao dao = ReservationMapDao.getDao();
        List<ReservationMapDto> dtos = dao.getPakingMap(parkingLotId.toUpperCase(), startTime, endTime);

        // JSP로 데이터 전달
        request.setAttribute("seatList", dtos);
        request.setAttribute("selectedLotId", parkingLotId.toUpperCase());
        request.setAttribute("reqStartTime", startTime);
        request.setAttribute("reqEndTime", endTime);
    }
}