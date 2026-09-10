package command;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dto.LongTermParkingDto;
import dto.ShortTermParkingDto;
import service.ParkingService;

@WebServlet("/parkingStatus")
public class ParkingStatus extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ParkingService parkingService = new ParkingService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // ================================
        // 실시간 주차 현황 조회
        // ================================

        List<LongTermParkingDto> longTermList =
                parkingService.getLongTermParkingStatus();

        List<ShortTermParkingDto> shortTermList =
                parkingService.getShortTermParkingStatus();


        // ================================
        // JSP로 데이터 전달
        // ================================

        request.setAttribute(
                "longTermList",
                longTermList
        );

        request.setAttribute(
                "shortTermList",
                shortTermList
        );


        // ================================
        // 메인 화면으로 이동
        // ================================

        request.getRequestDispatcher(
                "/index.jsp"
        ).forward(
                request,
                response
        );
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
}