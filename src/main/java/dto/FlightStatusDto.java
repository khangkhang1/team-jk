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
    private String gateNumber;       // 입국장 출구 (API 태그 exitnumber, 예: "D") - 2026-09-17 정정, 아래 주석 참고
    private String remark;           // 운항현황 - "도착"/"결항"/"지연"/"회항"/"착륙", 아직 안 온 편은 비어 있음(=예정)
    private String terminalId;       // 터미널 구분 - P01/P02 = 제1터미널(본관/탑승동), P03 = 제2터미널
    private String updatedAt;        // DB에 마지막으로 저장/갱신한 시각 (icn_flight.updated_at)

    // 2026-09-17 추가 - 실제 응답 원문(item)을 열어보고 확인한 필드들.
    // 기존에 읽던 "gatenumber" 태그는 이 API에 아예 없어서 gateNumber가 항상 null이었다.
    // 도착편은 탑승구 대신 입국장 출구(exitnumber)가 오므로 그걸 gateNumber에 담는다.
    private String airportCode;      // 출발 공항 IATA 코드 (예: NRT) - "NRT"로도 검색되게 하려고
    private String codeshare;        // "Master"(실제 운항편) / "Slave"(공동운항으로 판 편명)
    private String masterFlightId;   // Slave일 때 실제 운항편의 편명 (예: AA8905 -> CX426)
    private String carousel;         // 수하물 수취대 번호

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

    public String getAirportCode() {
        return airportCode;
    }
    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }
    public String getCodeshare() {
        return codeshare;
    }
    public void setCodeshare(String codeshare) {
        this.codeshare = codeshare;
    }
    public String getMasterFlightId() {
        return masterFlightId;
    }
    public void setMasterFlightId(String masterFlightId) {
        this.masterFlightId = masterFlightId;
    }
    public String getCarousel() {
        return carousel;
    }
    public void setCarousel(String carousel) {
        this.carousel = carousel;
    }

    // 화면/로직에서 바로 쓰기 좋게 - 결항 대응 기능의 핵심 판단
    public boolean isCancelled() {
        return "결항".equals(remark);
    }
    public boolean isDelayed() {
        return "지연".equals(remark);
    }
    // 아직 도착 전 - API가 remark 태그 자체를 안 보낸다
    public boolean isScheduled() {
        return remark == null || remark.trim().isEmpty();
    }
    // 공동운항 편명(Slave)이 아니라 실제로 뜨는 비행기인가.
    // 오늘자 실측(2026-09-17): 1,156건 중 Slave가 622건 - 절반 이상이 같은 비행기의 다른 편명이다.
    public boolean isMaster() {
        return !"Slave".equalsIgnoreCase(codeshare);
    }
    // 우리 시스템은 제1터미널 주차장이라 터미널 구분이 중요하다.
    // 코드 의미는 명세에 없어서 항공사 분포로 확인함: P03에 대한항공/아시아나/델타 -> 제2터미널.
    public String getTerminalName() {
        if ("P01".equals(terminalId)) return "T1";
        if ("P02".equals(terminalId)) return "T1 탑승동";
        if ("P03".equals(terminalId)) return "T2";
        return terminalId == null ? "" : terminalId;
    }
}
