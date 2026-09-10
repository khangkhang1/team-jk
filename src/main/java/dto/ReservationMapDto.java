package dto;

import java.security.Timestamp;
import java.util.List;

public class ReservationMapDto {
	
	
	    private Integer parkingLotId;      // 주차장ID
	    private String parkingLotName;      // 주차장 이름  


	    private String zoneType;       // 구역구분
	    private Integer seatId;       // 좌석ID
	    private String seatNumber;    // 좌석번호
	    private Integer row;          // 행 (맵 타일 배치용)
	    private Integer column;       // 열 (맵 타일 배치용)
	    private String type;          // 타입 (예: 경차, 장애인, 전기차 등)
	    private Boolean isReserved;   // 예약 가능 여부/상태 표현용
	    private Timestamp entryTime;       // 입차(예약 시작) 시간
	    private Timestamp exitTime;        // 출차(예약 종료) 시간
	    
	    
		public Integer getParkingLotId() {
			return parkingLotId;
		}
		public Timestamp getEntryTime() {
			return entryTime;
		}
		public void setEntryTime(Timestamp entryTime) {
			this.entryTime = entryTime;
		}
		public Timestamp getExitTime() {
			return exitTime;
		}
		public void setExitTime(Timestamp exitTime) {
			this.exitTime = exitTime;
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
