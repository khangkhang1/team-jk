package dao;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	// ============================================================
	// 단기주차장 구역(01~04) 단위 실시간 현황 - ParkLocationData를 구역별로 집계
	//
	// [왜 API를 두 개 섞어 쓰나] 2026-09-08 임정규 제안, 팀 채택
	//   위의 StatusOfParking은 단기주차장을 "층" 단위로만 준다 - 지상층이 1,052면 한 덩어리다.
	//   그런데 우리 메인 주차맵은 단기주차장을 구역으로 쪼개 놨기 때문에 층 단위로는 못 붙인다.
	//   반면 ParkLocationData는 개별 주차면마다 parkzoneno(01~04)를 달고 오므로,
	//   그걸 구역별로 세면 "구역 단위 실시간 점유"가 나온다.
	//   => 장기주차장은 StatusOfParking, 단기 4구역은 ParkLocationData 집계로 간다.
	//
	// [주의 - 2026-09-08 실측] 두 API는 서로 다른 기준으로 센다. 숫자를 합치면 안 된다.
	//   ParkLocationData 합계        : 4,614면
	//   StatusOfParking 단기 4개층 합 : 3,403면  (1,211면 차이)
	//   또 parkzoneno 01~04는 "지상층을 넷으로 쪼갠 것"이 아니다.
	//   (지상층 전체가 1,052면인데 구역 하나가 988~1,338면이라 크기 자체가 안 맞음)
	//   따라서 이 값은 화면에서 혼잡도/점유율 표현에만 쓰고,
	//   장기 쪽 면수와 나란히 더해서 "총 주차면 O면" 같은 합계를 내면 안 된다.
	//
	// [성능] 이 API는 한 번 부르면 4,614행이 통째로 온다. 화면 열 때마다 부르면 느리다.
	//   그래서 60초 캐시를 둔다. 주차 현황이 1분 단위로 갱신되는 건 실무적으로 충분하고,
	//   공공 API의 일일 호출 제한도 아낄 수 있다.
	//   메서드에 synchronized를 건 이유: 캐시가 만료된 순간 여러 요청이 동시에 들어오면
	//   4,614행을 동시에 여러 번 받아오게 된다(thundering herd). 한 명만 받아오고
	//   나머지는 그 결과를 쓰게 막는 것이다.
	// ============================================================
	private static final long SHORT_CACHE_MS = 60 * 1000L;
	private static List<ParkingStatusDto> shortZoneCache = null;
	private static long shortZoneCacheTime = 0L;

	public List<ParkingStatusDto> getShortTermZoneStatusList() {
		// 캐시는 static(클래스 공용)이라 인스턴스 락(synchronized 메서드)으로는 못 지킨다.
		// 서블릿은 요청마다 new ParkingApiDao()를 하므로 인스턴스가 매번 다르기 때문.
		// 그래서 클래스 자체를 락으로 잡는다.
		synchronized (ParkingApiDao.class) {
			long now = System.currentTimeMillis();
			if (shortZoneCache != null && (now - shortZoneCacheTime) < SHORT_CACHE_MS) {
				return shortZoneCache;
			}

			List<ParkingSeatDto> seats = getParkingStatus("T1", 5000, 1);
			List<ParkingStatusDto> result = aggregateByZone(seats);

			// 호출 실패(빈 리스트)면 캐시를 갈아엎지 않는다 - 직전에 받아둔 값이라도 보여주는 게 낫다.
			if (!result.isEmpty()) {
				shortZoneCache = result;
				shortZoneCacheTime = now;
			} else if (shortZoneCache != null) {
				return shortZoneCache;
			}
			return result;
		}
	}

	// 개별 주차면 리스트를 구역별로 접어서 ParkingStatusDto로 변환.
	// floor 값은 StatusOfParking 쪽과 형식을 맞춰 "T1 단기주차장 01구역"으로 만든다
	// (화면에서 두 API 결과를 같은 모양으로 다루기 위함).
	private List<ParkingStatusDto> aggregateByZone(List<ParkingSeatDto> seats) {
		// TreeMap을 쓰는 이유: 01,02,03,04 순서를 보장하려고. API 응답 순서에 의존하지 않는다.
		Map<String, int[]> agg = new TreeMap<>();   // 구역번호 -> [총면수, 점유수]
		for (ParkingSeatDto s : seats) {
			String zone = s.getParkZoneNo();
			if (zone == null || zone.trim().isEmpty()) continue;
			zone = zone.trim();

			int[] c = agg.get(zone);
			if (c == null) {
				c = new int[2];
				agg.put(zone, c);
			}
			c[0]++;                          // 총 면수
			if (s.isOccupied()) c[1]++;      // 점유(carstatus = Y)
		}

		// ParkLocationData에는 "집계 시각" 필드가 없다. 그래서 우리가 조회한 시각을 넣는다.
		// StatusOfParking의 datetm과 의미가 미묘하게 다르므로(공항측 집계시각 vs 우리 조회시각)
		// 화면에 그대로 노출할 때는 "OO시 기준"처럼 조회시각임을 알 수 있게 쓸 것.
		String nowText = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

		List<ParkingStatusDto> list = new ArrayList<>();
		for (Map.Entry<String, int[]> e : agg.entrySet()) {
			ParkingStatusDto dto = new ParkingStatusDto();
			dto.setFloor("T1 단기주차장 " + e.getKey() + "구역");
			dto.setParkingArea(e.getValue()[0]);
			dto.setParking(e.getValue()[1]);
			dto.setDateTm(nowText);
			list.add(dto);
		}
		return list;
	}

	// 구역번호("01"~"04")로 한 건만 찾기
	public ParkingStatusDto getShortTermZoneStatus(String zoneNo) {
		for (ParkingStatusDto d : getShortTermZoneStatusList()) {
			if (d.getFloor() != null && d.getFloor().endsWith(" " + zoneNo + "구역")) {
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
