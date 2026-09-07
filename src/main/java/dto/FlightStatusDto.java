package dto;

// 인천공항공사 OpenAPI(StatusOfPassengerFlightsDeOdp - "여객기 운항 현황 상세 조회 서비스") 응답 1건을 담는 DTO.
// 결항 대응 기능(예약 자동연장/알림 + 자리변경 알림)의 핵심 판단 근거가 되는 DTO.
// API 응답을 담는 용도와 icn_flight 테이블 한 행을 담는 용도를 겸함(개인 프로젝트 PlayerDto와 같은 방식 - 폼입력/DB행을 굳이 나누지 않음).
//
// 2026-09-07 강선구: data.go.kr 서비스 명세(Swagger)에서 태그명 직접 확인 완료.
// airline/flightId/airport/scheduleDateTime/estimatedDateTime/gatenumber/remark/terminalid
// 전부 명세 그대로이며, remark(현황) 값은 도착/결항/지연/회항/착륙 중 하나로 내려옴.
//
// 2026-09-07(2) 5차 회의에서 "항공편번호"(편명)와 "항공편ID"(DB PK, 임의생성)를 구분하기로 확정 -
// API의 flightId 태그(=편명)를 그대로 flightId로 쓰면 우리 DB의 flight_id(PK)와 이름이 겹쳐서
// flightNo로 이름을 바꾸고, id를 DB PK 전용으로 새로 둠. API XML 태그명 자체는 안 바뀜(파싱 쪽만 매핑 변경).
public class FlightStatusDto {
    private int id;                  // DB PK(icn_flight.flight_id) - 아직 저장 전이면 0
    private String airline;          // 항공사
    private String flightNo;         // 편명 (예: KE001) - API 태그명은 flightId지만 우리 쪽에서는 flightNo로 부름
    private String airport;          // 상대 공항(출발지) 이름
    private String scheduleDateTime; // 원래 예정 시각 (YYYYMMDDHH24MI)
    private String estimatedDateTime;// 변경된(지연 등) 예정 시각
    private String gateNumber;       // 탑승구/입국장
    private String remark;           // 운항현황 - "도착"/"결항"/"지연"/"회항"/"착륙"
    private String terminalId;       // 터미널 구분
    private String updatedAt;        // DB에 마지막으로 저장/갱신한 시각 (icn_flight.updated_at)

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getAirline() {
        return airline;
    }
    public void setAirline(String airline) {
        this.airline = airline;
    }
    public String getFlightNo() {
        return flightNo;
    }
    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
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
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 화면/로직에서 바로 쓰기 좋게 - 결항 대응 기능의 핵심 판단
    public boolean isCancelled() {
        return "결항".equals(remark);
    }
    public boolean isDelayed() {
        return "지연".equals(remark);
    }
}
