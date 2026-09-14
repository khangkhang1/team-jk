package dto;

// 인천공항공사 OpenAPI(StatusOfParking - "주차 현황 조회 서비스") 응답 1건을 담는 DTO.
// 개별 주차면이 아니라 "구역 단위 집계"(주차된 대수 / 총 주차면수)를 준다.
//
// 개별 주차면 단위는 ParkLocationData(=ParkingSeatDto)가 따로 있지만,
// T1만 4,614면이라 그걸 전부 DB에 넣으면 감당이 안 돼서 개별 칸은 우리 임의 데이터로 가고
// 이 구역 단위 집계만 실 API로 연동하기로 함 (2026-09-08 결정).
//
// 항목명은 활용가이드(V7.4) 그대로 매핑.
public class ParkingStatusDto {
	private String floor;       // 주차장 구분 (예: "T1 장기 P1 주차장") - 별첨의 19개 구역명 중 하나
	private int parking;        // 주차구역 주차수 (현재 주차된 대수)
	private int parkingArea;    // 주차구역 총주차면수 (0이면 미운영)
	private String dateTm;      // 주차 현황 업데이트 시각 (yyyyMMddHHmmss.SSS)

	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public int getParking() {
		return parking;
	}
	public void setParking(int parking) {
		this.parking = parking;
	}
	public int getParkingArea() {
		return parkingArea;
	}
	public void setParkingArea(int parkingArea) {
		this.parkingArea = parkingArea;
	}
	public String getDateTm() {
		return dateTm;
	}
	public void setDateTm(String dateTm) {
		this.dateTm = dateTm;
	}

	// 화면에서 바로 쓰기 좋게 - 잔여 대수
	public int getRemain() {
		int r = parkingArea - parking;
		return r < 0 ? 0 : r;   // 실제 API에서 주차수가 총면수를 넘는 경우가 있음(주차대행 등) - 음수 방지
	}

	// 혼잡도 등급 - 인덱스/상세맵 범례(여유/보통/혼잡)와 같은 기준
	public String getStatusLabel() {
		if (parkingArea <= 0) return "미운영";
		double ratio = (double) getRemain() / parkingArea;
		if (ratio > 0.5) return "여유";
		if (ratio > 0.2) return "보통";
		return "혼잡";
	}
}
