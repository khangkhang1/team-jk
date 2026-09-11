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

        // 기본값 설정
        if (parkingLotId == null || parkingLotId.trim().isEmpty()) {
            parkingLotId = "P1";
        }
        if (startTime == null || startTime.trim().isEmpty()) {
            startTime = "2026-09-11 09:00";
        }
        if (endTime == null || endTime.trim().isEmpty()) {
            endTime = "2026-09-11 18:00";
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