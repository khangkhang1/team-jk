package test;

import java.util.List;

import dao.ShortTermParkingDao;
import dto.ParkingSeatDto1;

public class ParkingApiTest {
	//단기 api test

    public static void main(String[] args) {

        ShortTermParkingDao dao = new ShortTermParkingDao();

        List<ParkingSeatDto1> list =
                dao.getAllParkingStatus("T1");

        System.out.println("=================================");
        System.out.println("단기주차장 API 조회 결과");
        System.out.println("전체 데이터 : " + list.size());
        System.out.println("=================================");

        int zone01 = 0;
        int zone02 = 0;
        int zone03 = 0;
        int zone04 = 0;

        int occupied01 = 0;
        int occupied02 = 0;
        int occupied03 = 0;
        int occupied04 = 0;

        for (ParkingSeatDto1 dto : list) {

            if (!"01".equals(dto.getParkLotNo())) {
                continue;
            }

            String zone = dto.getParkZoneNo();

            if ("01".equals(zone)) {
                zone01++;

                if (dto.isOccupied()) {
                    occupied01++;
                }

            } else if ("02".equals(zone)) {
                zone02++;

                if (dto.isOccupied()) {
                    occupied02++;
                }

            } else if ("03".equals(zone)) {
                zone03++;

                if (dto.isOccupied()) {
                    occupied03++;
                }

            } else if ("04".equals(zone)) {
                zone04++;

                if (dto.isOccupied()) {
                    occupied04++;
                }
            }
        }

        System.out.println();
        System.out.println("----- 단기주차장 구역별 현황 -----");

        printZone("01",zone01,occupied01);
        printZone("02",zone02,occupied02);
        printZone("03",zone03,occupied03);
        printZone("04",zone04,occupied04);
    }

    private static void printZone(
            String zone,
            int total,
            int occupied) {

        int available = total - occupied;

        double rate = 0;

        if (total > 0) {
            rate =
                    (double) occupied / total * 100;
        }

        System.out.println(
                zone + "구역"
                + " / 전체 : " + total
                + " / 사용중 : " + occupied
                + " / 빈자리 : " + available
                + " / 점유율 : "
                + String.format("%.2f",rate)
                + "%"
        );
    }
}