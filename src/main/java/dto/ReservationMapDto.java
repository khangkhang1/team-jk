package dto;

import java.security.Timestamp;
import java.util.List;

public class ReservationMapDto {
	
	
	    private String parkingLotId;      // 주차장ID
	    private String seatId;       // 좌석ID
	    private String type;          // 타입 (예: 경차, 장애인, 전기차 등)
	    private Boolean isReserved;   // 예약 가능 여부/상태 표현용
	    private String entryTime;       // 입차(예약 시작) 시간
	    private String exitTime;        // 출차(예약 종료) 시간
		public ReservationMapDto(String parkingLotId, String seatId, String type, Boolean isReserved) {
			super();
			this.parkingLotId = parkingLotId;
			this.seatId = seatId;
			this.type = type;
			this.isReserved = isReserved;
			
		}
		
		public ReservationMapDto(String parkingLotId, String seatId, String type, Boolean isReserved,
				String startTime, String endTime) {
			super();
			this.parkingLotId = parkingLotId;
			this.seatId = seatId;
			this.type = type;
			this.isReserved = isReserved;
			this.entryTime = startTime;
			this.exitTime = endTime;
		}

		public String getParkingLotId() {
			return parkingLotId;
		}

		public void setParkingLotId(String parkingLotId) {
			this.parkingLotId = parkingLotId;
		}

		public String getSeatId() {
			return seatId;
		}

		public void setSeatId(String seatId) {
			this.seatId = seatId;
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

		public String getEntryTime() {
			return entryTime;
		}

		public void setEntryTime(String entryTime) {
			this.entryTime = entryTime;
		}

		public String getExitTime() {
			return exitTime;
		}

		public void setExitTime(String exitTime) {
			this.exitTime = exitTime;
		}
		
	
	
	
	
}
