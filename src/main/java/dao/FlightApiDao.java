package dao;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import dto.FlightStatusDto;

// 인천공항공사 OpenAPI(StatusOfPassengerFlightsDeOdp - "여객기 운항 현황 상세 조회 서비스") 호출 담당 DAO.
// 결항 대응 기능(예약 자동연장/알림 + 자리변경 알림)의 데이터 출처.
// ParkingApiDao와 같은 패턴 - "외부 API 조회해서 DTO 리스트로 돌려준다".
//
// 2026-09-07 강선구: data.go.kr에서 서비스명·필드명 직접 확인 완료.
// - 서비스명이 처음 추정했던 "StatusOfPassengerFlightsOdp"가 아니라
//   "StatusOfPassengerFlightsDeOdp"("De"가 붙음)이 맞음 - 확인 전 URL은 404 났을 것.
// - ParkLocationData(주차면 정보)와 제공기관(B551177)은 같지만 "서비스 단위"가 달라서
//   활용신청도 따로 해야 함 - data.go.kr에서 "인천국제공항공사_여객기 운항 현황 상세 조회 서비스"로
//   검색해서 별도로 활용신청할 것(개발계정은 자동승인, 무료).
// - pageNo/numOfRows는 API 명세상 필수 파라미터라 반드시 넘겨야 함(빠지면 오류 응답).
public class FlightApiDao {

	private static final String BASE_URL = "https://apis.data.go.kr/B551177/StatusOfPassengerFlightsDeOdp/getPassengerArrivalsDeOdp";

	// 공공데이터포털(data.go.kr)에서 활용신청 후 발급받은 서비스키(Encoding 버전).
	// 주의: 이미 URL 인코딩된 값이라 그대로 붙여야 함. 한 번 더 인코딩하면 이중 인코딩 오류남.
	private static final String SERVICE_KEY = "8A6C6Mp2ylWbir47yE6IJtBplIUUhhvbxRr3CbDEGe4URfJZBRmcEoT5SdFTxhrK%2Bdk8bO1MQY%2BOV7guUPsrDw%3D%3D";

	// 귀국 도착편 운항현황 조회 - 결항 여부 확인용.
	// searchday: 조회일자(YYYYMMDD). null이면 API 기본값(오늘)으로 조회됨 - 예약자의 도착 예정일을 넘기는 걸 권장.
	// airport: 상대(출발지) 공항 IATA 코드, 예: "NRT"(나리타). null/빈 문자열이면 전체 조회.
	// fromTime/toTime: 조회 시간 범위 (HHMM 형식, 예: "0900"). 필요 없으면 null로 넘기면 됨.
	// lang: "K"(국문) 기본.
	public List<FlightStatusDto> getArrivalFlights(String searchday, String airport, String fromTime, String toTime, String lang) {
		List<FlightStatusDto> list = new ArrayList<>();

		StringBuilder urlBuilder = new StringBuilder(BASE_URL);
		urlBuilder.append("?serviceKey=").append(SERVICE_KEY);
		urlBuilder.append("&pageNo=1");        // 필수 파라미터
		urlBuilder.append("&numOfRows=100");   // 필수 파라미터 - 한 페이지에 몇 건 받을지
		urlBuilder.append("&type=xml");
		urlBuilder.append("&lang=").append(lang == null ? "K" : lang);
		if (searchday != null && !searchday.isEmpty()) {
			urlBuilder.append("&searchday=").append(searchday);
		}
		if (airport != null && !airport.isEmpty()) {
			urlBuilder.append("&airport_code=").append(airport);
		}
		if (fromTime != null && !fromTime.isEmpty()) {
			urlBuilder.append("&from_time=").append(fromTime);
		}
		if (toTime != null && !toTime.isEmpty()) {
			urlBuilder.append("&to_time=").append(toTime);
		}
		String url = urlBuilder.toString();

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			list = parseXml(response.body());
		} catch (Exception e) {
			System.out.println("getArrivalFlights() 오류 : " + url);
			e.printStackTrace();
		}
		return list;
	}

	// 특정 편명 하나만 결항인지 바로 확인하고 싶을 때 쓰는 편의 메서드.
	// 예약자 본인 항공편의 결항 여부를 좌석 예약 화면 등에서 바로 체크할 때 사용.
	// searchday: 예약자가 입력한 도착 예정일(YYYYMMDD). 같은 편명이 매일 뜨므로 날짜를 넘겨야 정확히 매칭됨.
	public boolean isFlightCancelled(String flightId, String searchday) {
		List<FlightStatusDto> flights = getArrivalFlights(searchday, null, null, null, "K");
		for (FlightStatusDto f : flights) {
			if (flightId != null && flightId.equalsIgnoreCase(f.getFlightId())) {
				return f.isCancelled();
			}
		}
		return false; // 조회 결과에 없으면 결항 아님으로 처리(운항정보 없음과 결항은 구분 필요 - 추후 보완)
	}

	// 응답 XML의 <item> 목록을 FlightStatusDto 리스트로 변환 (JDK 내장 DOM 파서만 사용 - 별도 jar 불필요)
	private List<FlightStatusDto> parseXml(String xml) throws Exception {
		List<FlightStatusDto> list = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
			FlightStatusDto dto = new FlightStatusDto();
			dto.setAirline(getTagValue(item, "airline"));
			dto.setFlightId(getTagValue(item, "flightId"));
			dto.setAirport(getTagValue(item, "airport"));
			dto.setScheduleDateTime(getTagValue(item, "scheduleDateTime"));
			dto.setEstimatedDateTime(getTagValue(item, "estimatedDateTime"));
			dto.setGateNumber(getTagValue(item, "gatenumber"));
			dto.setRemark(getTagValue(item, "remark"));
			dto.setTerminalId(getTagValue(item, "terminalid"));
			list.add(dto);
		}
		return list;
	}

	private String getTagValue(Element item, String tag) {
		NodeList nodes = item.getElementsByTagName(tag);
		if (nodes.getLength() == 0 || nodes.item(0).getFirstChild() == null) {
			return null;
		}
		return nodes.item(0).getFirstChild().getNodeValue();
	}
}
