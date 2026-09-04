package dto;

import java.util.List;

public class reservationDto {
	
	
	    private Integer parkingLotId;      // 주차장ID
	    private String parkingLotName;      // 주차장 이름
	    private Integer hourlyRate;         // 시간당요금	
	    private Integer floorId;       // 층ID
	    private Integer floorNumber;   // 층번호
	    private String zoneType;       // 구역구분
	    private Integer seatId;       // 좌석ID
	    private String seatNumber;    // 좌석번호
	    private Integer row;          // 행 (맵 타일 배치용)
	    private Integer column;       // 열 (맵 타일 배치용)
	    private String type;          // 타입 (예: 경차, 장애인, 전기차 등)
	    private Boolean isReserved;   // 예약 가능 여부/상태 표현용
		public Integer getParkingLotId() {
			return parkingLotId;
		}
		public void setParkingLotId(Integer parkingLotId) {
			this.parkingLotId = parkingLotId;
		}
		public String getParkingLotName() {
			return parkingLotName;
		}
		public void setParkingLotName(String parkingLotName) {
			this.parkingLotName = parkingLotName;
		}
		public Integer getHourlyRate() {
			return hourlyRate;
		}
		public void setHourlyRate(Integer hourlyRate) {
			this.hourlyRate = hourlyRate;
		}
		public Integer getFloorId() {
			return floorId;
		}
		public void setFloorId(Integer floorId) {
			this.floorId = floorId;
		}
		public Integer getFloorNumber() {
			return floorNumber;
		}
		public void setFloorNumber(Integer floorNumber) {
			this.floorNumber = floorNumber;
		}
		public String getZoneType() {
			return zoneType;
		}
		public void setZoneType(String zoneType) {
			this.zoneType = zoneType;
		}
		public Integer getSeatId() {
			return seatId;
		}
		public void setSeatId(Integer seatId) {
			this.seatId = seatId;
		}
		public String getSeatNumber() {
			return seatNumber;
		}
		public void setSeatNumber(String seatNumber) {
			this.seatNumber = seatNumber;
		}
		public Integer getRow() {
			return row;
		}
		public void setRow(Integer row) {
			this.row = row;
		}
		public Integer getColumn() {
			return column;
		}
		public void setColumn(Integer column) {
			this.column = column;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Boolean getIsReserved() {
			return isReserved;
		}
		public void setIsReserved(Boolean isReserved) {
			this.isReserved = isReserved;
		}
	
	
	
}
