package controller;

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

    private ParkingService parkingService =
            new ParkingService();


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
        // 새로고침 요청인지 확인
        // ================================

        String refresh =
                request.getParameter("refresh");


        if ("true".equals(refresh)) {

            // ================================
            // JSON 응답
            // ================================

            response.setContentType(
                    "application/json; charset=UTF-8"
            );

            response.setCharacterEncoding("UTF-8");


            StringBuilder json =
                    new StringBuilder();

            json.append("{");

            json.append("\"longTerm\":[");


            // ================================
            // 장기주차장 데이터
            // ================================

            for (int i = 0;
                 i < longTermList.size();
                 i++) {

                LongTermParkingDto dto =
                        longTermList.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{");

                json.append("\"parkLotNo\":\"")
                    .append(dto.getParkLotNo())
                    .append("\",");

                json.append("\"totalCount\":")
                    .append(dto.getTotalCount())
                    .append(",");

                json.append("\"occupiedCount\":")
                    .append(dto.getOccupiedCount())
                    .append(",");

                json.append("\"availableCount\":")
                    .append(dto.getAvailableCount())
                    .append(",");

                json.append("\"occupancyRate\":")
                    .append(dto.getOccupancyRate())
                    .append(",");

                json.append("\"congestion\":\"")
                    .append(dto.getCongestion())
                    .append("\"");

                json.append("}");
            }


            json.append("],");


            // ================================
            // 단기주차장 데이터
            // ================================

            json.append("\"shortTerm\":[");


            for (int i = 0;
                 i < shortTermList.size();
                 i++) {

                ShortTermParkingDto dto =
                        shortTermList.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{");

                json.append("\"parkZoneNo\":\"")
                    .append(dto.getParkZoneNo())
                    .append("\",");

                json.append("\"totalCount\":")
                    .append(dto.getTotalCount())
                    .append(",");

                json.append("\"occupiedCount\":")
                    .append(dto.getOccupiedCount())
                    .append(",");

                json.append("\"availableCount\":")
                    .append(dto.getAvailableCount())
                    .append(",");

                json.append("\"occupancyRate\":")
                    .append(dto.getOccupancyRate())
                    .append(",");

                json.append("\"congestion\":\"")
                    .append(dto.getCongestion())
                    .append("\"");

                json.append("}");
            }


            json.append("]");

            json.append("}");


            // ================================
            // JSON 전송
            // ================================

            response.getWriter()
                    .write(json.toString());

            return;
        }


        // ================================
        // 일반 접속
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