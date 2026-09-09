package service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dao.ParkingApiDao;
import dto.ParkingLotStatusDto;
import dto.ParkingSeatDto1;

public class ParkingService {

    private ParkingApiDao parkingApiDao = new ParkingApiDao();

    public List<ParkingLotStatusDto> getParkingLotStatus() {

        List<ParkingSeatDto1> seats =
                parkingApiDao.getParkingStatus("T1", 1000, 1);

        Map<String, Integer> totalMap = new LinkedHashMap<>();
        Map<String, Integer> occupiedMap = new LinkedHashMap<>();

        for (ParkingSeatDto1 seat : seats) {

            String parkLotNo = seat.getParkLotNo();

            if (parkLotNo == null || parkLotNo.trim().isEmpty()) {
                continue;
            }

            totalMap.put(
                parkLotNo,
                totalMap.getOrDefault(parkLotNo, 0) + 1
            );

            if (seat.isOccupied()) {
                occupiedMap.put(
                    parkLotNo,
                    occupiedMap.getOrDefault(parkLotNo, 0) + 1
                );
            }
        }

        List<ParkingLotStatusDto> result = new ArrayList<>();

        for (String parkLotNo : totalMap.keySet()) {

            int totalCount = totalMap.get(parkLotNo);
            int occupiedCount =
                    occupiedMap.getOrDefault(parkLotNo, 0);

            result.add(
                new ParkingLotStatusDto(
                    parkLotNo,
                    totalCount,
                    occupiedCount
                )
            );
        }

        return result;
    }
}