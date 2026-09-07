package dto;

/**
 * DB(SEAT, PARKING_LOT 테이블) 및 화면 연동용 DTO
 */
public class ParkingSeatDto {
    private int seatId;          // SEAT_ID (좌석 고유 ID, PK)
    private int floorId;         // FLOOR_ID (층 ID, FK)
    private String seatNo;       // SEAT_NO (좌석 번호, 예: A-01 또는 A01001)
    private int seatRow;         // SEAT_ROW (좌석 행)
    private int seatCol;         // SEAT_COL (좌석 열)
    private String seatType;     // SEAT_TYPE (좌석 유형: NORMAL, DISABLED, EV, GATE)
    
    // 주차장 및 터미널 연동용 필드
    private int lotId;           // LOT_ID (주차장 ID, FK)
    private String lotName;      // NAME (주차장 이름)
    private String floorName;    // FLOOR_NAME (층 이름, 예: 1층, 2층)
    
    // 점유/예약 상태 판단용 필드
    private String isOccupied;   // 실시간 사용 여부 ("Y" / "N")
    private String carInDate;    // 입차 시간 (yyyyMMddHHmmss)

    // Getter / Setter
    public int getSeatId() {
        return seatId;
    }
    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getFloorId() {
        return floorId;
    }
    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public String getSeatNo() {
        return seatNo;
    }
    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public int getSeatRow() {
        return seatRow;
    }
    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatCol() {
        return seatCol;
    }
    public void setSeatCol(int seatCol) {
        this.seatCol = seatCol;
    }

    public String getSeatType() {
        return seatType;
    }
    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getLotId() {
        return lotId;
    }
    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public String getLotName() {
        return lotName;
    }
    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public String getFloorName() {
        return floorName;
    }
    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getIsOccupied() {
        return isOccupied;
    }
    public void setIsOccupied(String isOccupied) {
        this.isOccupied = isOccupied;
    }

    public String getCarInDate() {
        return carInDate;
    }
    public void setCarInDate(String carInDate) {
        this.carInDate = carInDate;
    }

    // 화면(JS)에서 예약 불가능한 자리인지 매핑용
    public boolean isTaken() {
        return "Y".equals(isOccupied);
    }
}