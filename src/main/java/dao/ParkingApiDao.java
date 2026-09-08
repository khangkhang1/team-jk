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

import dto.ParkingSeatDto;
import dto.ParkingStatusDto;

// 인천공항공사 OpenAPI(ParkLocationData) 호출 담당 DAO.
// DB가 아니라 외부 공공데이터 API를 조회한다는 점만 다르고,
// "조회해서 DTO 리스트로 돌려준다"는 역할은 PlayerDao 등 다른 Dao와 동일하게 맞춤.
public class ParkingApiDao {

	private static final String BASE_URL = "http://apis.data.go.kr/B551177/ParkLocationData/getParkLocationData";

	// 공공데이터포털(data.go.kr)에서 활용신청 후 발급받은 서비스키(Encoding 버전).
	// 주의: 이 키는 이미 URL 인코딩된 값이라 그대로 붙여야 함. URLEncoder로 한 번 더 인코딩하면
	// "이중 인코딩"이 되어 401(Unauthorized)/Forbidden 에러가 난다 - data.go.kr에서 흔한 실수.
	private static final String SERVICE_KEY = "8A6C6Mp2ylWbir47yE6IJtBplIUUhhvbxRr3CbDEGe4URfJZBRmcEoT5SdFTxhrK%2Bdk8bO1MQY%2BOV7guUPsrDw%3D%3D";

	// 주차면 현황 조회. terminalId는 현재 "T1"만 제공됨(API 문서 기준, 2터미널 없음).
	public List<ParkingSeatDto> getParkingStatus(String terminalId, int numOfRows, int pageNo) {
		List<ParkingSeatDto> list = new ArrayList<>();

		String url = BASE_URL
				+ "?serviceKey=" + SERVICE_KEY
				+ "&terminalid=" + terminalId
				+ "&type=xml"
				+ "&numOfRows=" + numOfRows
				+ "&pageNo=" + pageNo;

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			list = parseXml(response.body());
		} catch (Exception e) {
			System.out.println("getParkingStatus() 오류 : " + url);
			e.printStackTrace();
		}
		return list;
	}

	// ============================================================
	// 구역 단위 실시간 주차 현황 (StatusOfParking - "주차 현황 조회 서비스", 가이드 V7.4)
	//
	// 위 getParkingStatus()가 쓰는 ParkLocationData는 "개별 주차면" 단위라 T1만 4,614면이 내려온다.
	// 그걸 전부 DB에 적재하는 건 감당이 안 돼서, 개별 칸은 우리 임의 데이터로 두고
	// 구역별 잔여 대수만 이 API로 실시간 연동하기로 함 (2026-09-08 결정).
	//
	// 별첨 기준 T1 구역명(그대로 내려옴):
	//   T1 단기주차장지상층 / 지하1층 / 지하2층 / 지하3층
	//   T1 장기 P1 주차장 / P2 주차장 / P3 주차장 / P1 주차타워 / P2 주차타워
	//   T1 P5 예약주차장
	//   (P4는 2026-07 폐지, P6~P9는 애초에 존재하지 않음 - 가이드 개정이력 v7.3 참고)
	// ============================================================
	private static final String STATUS_URL = "http://apis.data.go.kr/B551177/StatusOfParking/getTrackingParking";

	public List<ParkingStatusDto> getZoneStatusList() {
		List<ParkingStatusDto> list = new ArrayList<>();

		// 전체가 19건(T1+T2)이라 넉넉히 한 번에 받는다. 페이징 필요 없음.
		String url = STATUS_URL
				+ "?serviceKey=" + SERVICE_KEY
				+ "&type=xml"
				+ "&numOfRows=50"
				+ "&pageNo=1";

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			list = parseStatusXml(response.body());
		} catch (Exception e) {
			System.out.println("getZoneStatusList() 오류 : " + url);
			e.printStackTrace();
		}
		return list;
	}

	// 구역명(floor)으로 한 건만 찾기 - 예: "T1 장기 P1 주차장"
	public ParkingStatusDto getZoneStatus(String floorName) {
		List<ParkingStatusDto> list = getZoneStatusList();
		for (ParkingStatusDto d : list) {
			if (floorName != null && floorName.equals(d.getFloor())) {
				return d;
			}
		}
		return null;
	}

	private List<ParkingStatusDto> parseStatusXml(String xml) throws Exception {
		List<ParkingStatusDto> list = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
			ParkingStatusDto dto = new ParkingStatusDto();
			dto.setFloor(getTagValue(item, "floor"));
			dto.setParking(toInt(getTagValue(item, "parking")));
			dto.setParkingArea(toInt(getTagValue(item, "parkingarea")));
			dto.setDateTm(getTagValue(item, "datetm"));
			list.add(dto);
		}
		return list;
	}

	private int toInt(String s) {
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return 0;
		}
	}

	// 응답 XML의 <item> 목록을 ParkingSeatDto 리스트로 변환 (JDK 내장 DOM 파서만 사용 - 별도 jar 불필요)
	private List<ParkingSeatDto> parseXml(String xml) throws Exception {
		List<ParkingSeatDto> list = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
			ParkingSeatDto dto = new ParkingSeatDto();
			dto.setParkLaneCode(getTagValue(item, "parklanecode"));
			dto.setCarStatus(getTagValue(item, "carstatus"));
			dto.setCarInDate(getTagValue(item, "carindate"));
			dto.setParkLotNo(getTagValue(item, "parklotno"));
			dto.setParkZoneNo(getTagValue(item, "parkzoneno"));
			dto.setTerminalNo(getTagValue(item, "terno"));
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
