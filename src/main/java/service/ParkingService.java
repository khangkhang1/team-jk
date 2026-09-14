package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dao.LongTermParkingDao;
import dao.ShortTermParkingDao;
import dto.LongTermParkingDto;
import dto.ParkingSeatDto1;
import dto.ParkingStatusDto;
import dto.ShortTermParkingDto;

public class ParkingService {

    private LongTermParkingDao longTermParkingDao =
            new LongTermParkingDao();

    private ShortTermParkingDao shortTermParkingDao =
            new ShortTermParkingDao();

    // ============================================================
    // 장기주차장 P1~P5 혼잡도 조회
    //
    // P1 → T1 장기 P1 주차장
    // P2 → T1 장기 P2 주차장
    // P3 → T1 장기 P3 주차장
    // P4 → T1 장기 P1 주차타워
    // P5 → T1 P5 예약주차장
    // ============================================================

    public List<LongTermParkingDto> getLongTermParkingStatus() {

        List<LongTermParkingDto> result =
                new ArrayList<>();

        // P1
        addLongTermStatus(
                result,
                "P1",
                longTermParkingDao.getP1Status()
        );

        // P2
        addLongTermStatus(
                result,
                "P2",
                longTermParkingDao.getP2Status()
        );

        // P3
        addLongTermStatus(
                result,
                "P3",
                longTermParkingDao.getP3Status()
        );

        // P4 → 주차타워 임시 매핑
        addLongTermStatus(
                result,
                "P4",
                longTermParkingDao.getP4Status()
        );

        // P5
        addLongTermStatus(
                result,
                "P5",
                longTermParkingDao.getP5Status()
        );

        return result;
    }

    // 장기주차장 데이터 하나를 LongTermParkingDto로 변환
    private void addLongTermStatus(
            List<LongTermParkingDto> result,
            String parkLotNo,
            ParkingStatusDto status) {

        if (status == null) {
            result.add(
                    new LongTermParkingDto(
                            parkLotNo,
                            0,
                            0
                    )
            );
            return;
        }

        int totalCount =
                status.getParkingArea();

        int occupiedCount =
                status.getParking();

        LongTermParkingDto dto =
                new LongTermParkingDto(
                        parkLotNo,
                        totalCount,
                        occupiedCount
                );

        // 화면 "실시간 배지"용 - 실제 구역명과 공항측 집계시각.
        // 우리 라벨(P1~P5)은 임의로 붙인 것이라, 실제로 어느 주차장인지 보여주려면 필요하다.
        dto.setFloor(status.getFloor());
        dto.setDateTm(status.getDateTm());

        result.add(dto);
    }

    // ============================================================
    // 단기주차장 P6~P9 혼잡도 조회
    //
    // API 구역
    // 01 → P6
    // 02 → P7
    // 03 → P8
    // 04 → P9
    // ============================================================

    public List<ShortTermParkingDto> getShortTermParkingStatus() {

        List<ShortTermParkingDto> result =
                new ArrayList<>();

        // T1 전체 주차면 조회
        List<ParkingSeatDto1> seats =
                shortTermParkingDao.getAllParkingStatus("T1");

        String queriedAt =
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // API 구역별 집계
        for (int zoneNo = 1; zoneNo <= 4; zoneNo++) {

            String apiZoneNo =
                    String.format("%02d", zoneNo);

            String svgZoneNo =
                    "P" + (zoneNo + 5);

            int totalCount = 0;
            int occupiedCount = 0;

            for (ParkingSeatDto1 seat : seats) {

                if (seat.getParkZoneNo() == null) {
                    continue;
                }

                if (!apiZoneNo.equals(
                        seat.getParkZoneNo().trim())) {
                    continue;
                }

                totalCount++;

                if (seat.isOccupied()) {
                    occupiedCount++;
                }
            }

            ShortTermParkingDto dto =
                    new ShortTermParkingDto(
                            svgZoneNo,
                            totalCount,
                            occupiedCount
                    );

            // ParkLocationData에는 집계시각 필드가 없다. 그래서 우리가 조회한 시각을 넣는다.
            // 장기 쪽 dateTm(공항측 집계시각)과 의미가 다르므로 화면 문구도 "조회 기준"으로 쓸 것.
            dto.setFloor("T1 단기주차장 " + apiZoneNo + "구역");
            dto.setDateTm(queriedAt);

            result.add(dto);
        }

        return result;
    }
}