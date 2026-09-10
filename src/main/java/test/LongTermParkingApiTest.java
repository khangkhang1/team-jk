package test;

import dao.LongTermParkingDao;
import dto.ParkingStatusDto;

public class LongTermParkingApiTest {

    public static void main(String[] args) {

        LongTermParkingDao dao =
                new LongTermParkingDao();

        System.out.println("=================================");
        System.out.println("장기주차장 API 조회 결과");
        System.out.println("=================================");

        // P1
        printParkingStatus(
                "P1",
                dao.getP1Status()
        );

        // P2
        printParkingStatus(
                "P2",
                dao.getP2Status()
        );

        // P3
        printParkingStatus(
                "P3",
                dao.getP3Status()
        );

        // P4
        // 실제 P4 데이터가 없기 때문에
        // P1 주차타워 데이터를 임시로 사용
        printParkingStatus(
                "P4",
                dao.getP4Status()
        );

        // P5
        printParkingStatus(
                "P5",
                dao.getP5Status()
        );

        System.out.println("=================================");
        System.out.println("조회 완료");
        System.out.println("=================================");
    }


    // ------------------------------------------------------------
    // 장기주차장 주차 현황 출력
    // ------------------------------------------------------------

    private static void printParkingStatus(
            String parkingLot,
            ParkingStatusDto dto) {

        if (dto == null) {

            System.out.println(
                    parkingLot
                    + " → 데이터 없음"
            );

            return;
        }

        int occupied =
                dto.getParking();

        int total =
                dto.getParkingArea();

        int available =
                total - occupied;

        double occupancyRate = 0;

        if (total > 0) {

            occupancyRate =
                    (double) occupied
                    / total
                    * 100;
        }

        String congestion =
                getCongestion(
                        occupancyRate
                );

        System.out.println();

        System.out.println(
                parkingLot
                + " / API 구역명 : "
                + dto.getFloor()
        );

        System.out.println(
                "전체 주차면 : "
                + total
        );

        System.out.println(
                "사용 중 : "
                + occupied
        );

        System.out.println(
                "빈자리 : "
                + available
        );

        System.out.println(
                "점유율 : "
                + String.format(
                        "%.2f",
                        occupancyRate
                )
                + "%"
        );

        System.out.println(
                "혼잡도 : "
                + congestion
        );

        System.out.println(
                "조회시간 : "
                + dto.getDateTm()
        );

        System.out.println(
                "---------------------------------"
        );
    }


    // ------------------------------------------------------------
    // 혼잡도 계산
    // ------------------------------------------------------------

    private static String getCongestion(
            double occupancyRate) {

        if (occupancyRate >= 95) {

            return "매우 혼잡";

        } else if (occupancyRate >= 80) {

            return "혼잡";

        } else if (occupancyRate >= 50) {

            return "보통";

        } else {

            return "여유";
        }
    }
}