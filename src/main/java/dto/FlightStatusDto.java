package dto;

// 인천공항공사 OpenAPI(StatusOfPassengerFlightsDeOdp - "여객기 운항 현황 상세 조회 서비스") 응답 1건을 담는 DTO.
// 결항 대응 기능(예약 자동연장/알림 + 자리변경 알림)의 핵심 판단 근거가 되는 DTO.
//
// 2026-09-07 강선구: data.go.kr 서비스 명세(Swagger)에서 태그명 직접 확인 완료.
// airline/flightId/airport/scheduleDateTime/estimatedDateTime/gatenumber/remark/terminalid
// 전부 명세 그대로이며, remark(현황) 값은 도착/결항/지연/회항/착륙 중 하나로 내려옴.
// 서비스키 발급 후 실제 응답으로 최종 1회 검증만 남음(값 포맷 등).
public class FlightStatusDto {
    private String airline;          // 항공사
    private String flightId;         // 편명 (예: KE001)
    private String airport;          // 상대 공항(출발지) 코드
    private String scheduleDateTime; // 원래 예정 시각
    private String estimatedDateTime;// 변경된(지연 등) 예정 시각
    private String gateNumber;       // 탑승구/입국장
    private String remark;           // 운항현황 - "도착"/"결항"/"지연"/"회항"/"착륙"
    private String terminalId;       // 터미널 구분

    public String getAirline() {
        return airline;
    }
    public void setAirline(String airline) {
        this.airline = airline;
    }
    public String getFlightId() {
        return flightId;
    }
    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }
    public String getAirport() {
        return airport;
    }
    public void setAirport(String airport) {
        this.airport = airport;
    }
    public String getScheduleDateTime() {
        return scheduleDateTime;
    }
    public void setScheduleDateTime(String scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
    }
    public String getEstimatedDateTime() {
        return estimatedDateTime;
    }
    public void setEstimatedDateTime(String estimatedDateTime) {
        this.estimatedDateTime = estimatedDateTime;
    }
    public String getGateNumber() {
        return gateNumber;
    }
    public void setGateNumber(String gateNumber) {
        this.gateNumber = gateNumber;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getTerminalId() {
        return terminalId;
    }
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    // 화면/로직에서 바로 쓰기 좋게 - 결항 대응 기능의 핵심 판단
    public boolean isCancelled() {
        return "결항".equals(remark);
    }
    public boolean isDelayed() {
        return "지연".equals(remark);
    }
}
