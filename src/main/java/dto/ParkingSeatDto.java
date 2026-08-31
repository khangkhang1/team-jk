package dto;

// 인천공항공사 OpenAPI(ParkLocationData) 응답 1건을 담는 DTO.
// 항목명은 API 문서(주차면 정보_OpenAPI활용가이드) 그대로 매핑.
public class ParkingSeatDto {
    private String parkLaneCode; // 주차면번호 (예: A01001) - 개별 주차면 단위 코드
    private String carStatus;    // 주차면상태 - "Y"=사용중(찬 자리), "N"=빈 자리
    private String carInDate;    // 주차면입차시간 (yyyyMMddHHmmss)
    private String parkLotNo;    // 주차장구분
    private String parkZoneNo;   // 주차구역구분
    private String terminalNo;   // 터미널구분 (현재 T1만 제공)

    public String getParkLaneCode() {
        return parkLaneCode;
    }
    public void setParkLaneCode(String parkLaneCode) {
        this.parkLaneCode = parkLaneCode;
    }
    public String getCarStatus() {
        return carStatus;
    }
    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }
    public String getCarInDate() {
        return carInDate;
    }
    public void setCarInDate(String carInDate) {
        this.carInDate = carInDate;
    }
    public String getParkLotNo() {
        return parkLotNo;
    }
    public void setParkLotNo(String parkLotNo) {
        this.parkLotNo = parkLotNo;
    }
    public String getParkZoneNo() {
        return parkZoneNo;
    }
    public void setParkZoneNo(String parkZoneNo) {
        this.parkZoneNo = parkZoneNo;
    }
    public String getTerminalNo() {
        return terminalNo;
    }
    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    // 화면에서 바로 쓰기 좋게 - "Y"면 사용중(찬 자리)
    public boolean isOccupied() {
        return "Y".equals(carStatus);
    }
}
