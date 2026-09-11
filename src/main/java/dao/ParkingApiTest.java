package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.ParkingSeatDto1;

public class ParkingApiTest {

	public static void main(String[] args) {

		// 병합 정리(2026-09-10): getAllParkingStatus()는 ShortTermParkingDao 쪽에
		// 같은 시그니처로 이미 있어서 그쪽을 쓴다(ParkingApiDao에 중복 생성 안 함).
		ShortTermParkingDao dao = new ShortTermParkingDao();

		List<ParkingSeatDto1> list =
				dao.getAllParkingStatus("T1");

		Map<String,Integer> totalMap =
				new HashMap<>();

		Map<String,Integer> occupiedMap =
				new HashMap<>();

		for (ParkingSeatDto1 dto : list) {

			String zone =
					dto.getParkZoneNo();

			if (zone == null) {
				continue;
			}

			// 전체 주차면
			totalMap.put(
					zone,
					totalMap.getOrDefault(zone,0) + 1
			);

			// 사용 중인 주차면
			if (dto.isOccupied()) {

				occupiedMap.put(
						zone,
						occupiedMap.getOrDefault(zone,0) + 1
				);
			}
		}

		System.out.println();
		System.out.println("===== 구역별 주차 현황 =====");

		for (String zone : totalMap.keySet()) {

			int total =
					totalMap.get(zone);

			int occupied =
					occupiedMap.getOrDefault(zone,0);

			int available =
					total - occupied;

			double rate =
					(double) occupied / total * 100;

			System.out.println(
					"구역 " + zone
					+ " → 전체=" + total
					+ " / 사용=" + occupied
					+ " / 빈자리=" + available
					+ " / 점유율="
					+ String.format("%.1f",rate)
					+ "%"
			);
		}
	}
}