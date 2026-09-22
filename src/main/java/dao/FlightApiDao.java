package dao;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import common.SecretConfig;
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
//
// ─────────────────────────────────────────────────────────────────────
// [2026-09-17 수정] "DAO를 써도 항공편이 안 나온다"는 팀원 문의로 실측해서 고친 것 3가지
//
//  1) secret.properties 가 없을 때 엉뚱한 에러가 나던 문제
//     서비스키를 static final 필드에서 읽고 있었다. static 초기화 중에 예외가 나면
//     JVM이 이 클래스를 "초기화 실패"로 영구 표시해서, 두 번째 호출부터는
//       NoClassDefFoundError: Could not initialize class dao.FlightApiDao
//     라는 (빌드패스가 깨진 것처럼 보이는) 에러만 나오고 "secret.properties 가 없습니다"
//     안내문은 사라진다. -> 키를 메서드 안에서 읽도록 바꿈. 이제 매번 원래 안내문이 나온다.
//
//  2) 오늘 항공편이 안 나오던 문제 (가장 치명적)
//     numOfRows=100 고정 + searchday 생략이었다. 실측 결과
//       - searchday 생략   : "오늘"이 아니라 3일 전부터 전부(11,609건). 첫 100건은 3일 전 새벽편
//       - 오늘 + 100건     : 하루 1,156건 중 새벽 5시까지만
//     그래서 오후 편명은 검색해도 안 나오고, 결항 판정도 "목록에 없음 = 결항 아님"으로 틀리게 나왔다.
//     -> searchday 기본값을 오늘로, 1,000건씩 끝까지 페이지를 넘겨 받도록 수정.
//
//  3) 키가 틀려도 "0건"으로만 보이던 문제
//     인증 실패 시 API는 정상 응답과 다른 모양(<returnAuthMsg>)을 주는데, <item>이 없으니
//     그냥 0건으로 처리됐다. -> 인증 실패/오류코드를 감지해서 원인을 담아 예외로 알린다.
//
// [조회 가능 날짜] 오늘 기준 3일 전 ~ 6일 후 (2026-09-17 실측: 09-14 ~ 09-23만 데이터 있음)
// [캐시] 하루치(약 1,200건)를 받는 데 페이지 2번이 필요하다. 날짜별로 5분 보관.
//   주차현황(60초)보다 길게 잡은 이유: 운항 스케줄은 분 단위로 바뀌지 않고,
//   목록 화면에서 검색어 한 글자마다 API를 다시 부르지 않게 하는 게 목적이라서.
// ─────────────────────────────────────────────────────────────────────
public class FlightApiDao {

	private static final String BASE_URL = "https://apis.data.go.kr/B551177/StatusOfPassengerFlightsDeOdp/getPassengerArrivalsDeOdp";

	public static final int MIN_OFFSET_DAYS = -3;   // 오늘 기준 조회 가능한 가장 이른 날
	public static final int MAX_OFFSET_DAYS = 6;    // 오늘 기준 조회 가능한 가장 늦은 날

	private static final int ROWS_PER_PAGE = 1000;
	private static final int MAX_PAGES = 20;          // 혹시 모를 무한루프 방지 (20,000건)
	private static final long CACHE_MS = 5 * 60 * 1000L;

	private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
	// uuuu + STRICT : 20260231 같은 없는 날짜를 2월 28일로 슬쩍 바꾸지 않고 오류로 처리
	private static final DateTimeFormatter YMD =
			DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

	// HttpClient는 한 번 만들어서 계속 쓰는 게 정석이다(내부에 연결 풀이 있음).
	// 타임아웃이 없으면 API가 응답을 안 줄 때 톰캣 요청 스레드가 무한정 묶인다.
	private static final HttpClient CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(5))
			.build();

	// 날짜(yyyyMMdd) -> 그날 도착편 전체
	private static final Map<String, CacheEntry> CACHE = new ConcurrentHashMap<>();
	// 날짜별 잠금. 같은 날짜를 동시에 여러 명이 요청해도 API는 한 번만 부르게 한다.
	// 다른 날짜 요청까지 줄 세우지 않으려고 날짜마다 따로 잡는다.
	private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();

	// 서비스키는 부를 때마다 읽는다 (위 [수정 1] 참고). SecretConfig가 파일을 캐시하므로 비용 없음.
	private static String serviceKey() {
		return SecretConfig.get("api.serviceKey");
	}

	// ─────────────────────────────────────────────────────────────
	// 날짜 처리
	// ─────────────────────────────────────────────────────────────
	public static String today() {
		return LocalDate.now(SEOUL).format(YMD);
	}

	public static String minDay() {
		return LocalDate.now(SEOUL).plusDays(MIN_OFFSET_DAYS).format(YMD);
	}

	public static String maxDay() {
		return LocalDate.now(SEOUL).plusDays(MAX_OFFSET_DAYS).format(YMD);
	}

	// null/빈값이면 오늘. 형식이 틀리거나 API가 주지 않는 날짜면 이유를 담아 IllegalArgumentException.
	public static String normalizeDay(String searchday) {
		if (searchday == null || searchday.trim().isEmpty()) {
			return today();
		}
		String d = searchday.trim();
		LocalDate date;
		try {
			date = LocalDate.parse(d, YMD);
		} catch (Exception e) {
			throw new IllegalArgumentException("조회일자 형식이 잘못됐습니다 : " + d + " (예: " + today() + ")");
		}
		LocalDate now = LocalDate.now(SEOUL);
		if (date.isBefore(now.plusDays(MIN_OFFSET_DAYS)) || date.isAfter(now.plusDays(MAX_OFFSET_DAYS))) {
			throw new IllegalArgumentException("조회 가능한 날짜는 " + minDay() + " ~ " + maxDay()
					+ " 입니다. (공항 API가 오늘 기준 3일 전 ~ 6일 후만 제공)");
		}
		return d;
	}

	// ─────────────────────────────────────────────────────────────
	// 하루치 도착편 전체 (캐시 사용) - 목록 화면/검색 화면용
	// 실패하면 원인을 담은 예외를 그대로 던진다. 화면에서 그 메시지를 보여줘야 원인을 찾을 수 있다.
	// ─────────────────────────────────────────────────────────────
	public ArrivalsOfDay getArrivalsOfDay(String searchday) {
		String day = normalizeDay(searchday);

		CacheEntry hit = CACHE.get(day);
		if (hit != null && !hit.isExpired()) {
			return new ArrivalsOfDay(day, hit, true, false);
		}

		synchronized (LOCKS.computeIfAbsent(day, k -> new Object())) {
			// 잠금을 기다리는 동안 앞사람이 이미 받아왔을 수 있으니 한 번 더 확인
			hit = CACHE.get(day);
			if (hit != null && !hit.isExpired()) {
				return new ArrivalsOfDay(day, hit, true, false);
			}

			List<FlightStatusDto> flights;
			try {
				flights = fetchAll(day, null, null, null, "K");
			} catch (RuntimeException e) {
				// 통신 실패인데 예전에 받아둔 게 있으면, 오래됐어도 그걸 보여주는 게 빈 화면보다 낫다.
				if (hit != null) {
					System.out.println("[FlightApiDao] API 실패, 이전 캐시로 대체 (" + day + ") : " + e.getMessage());
					return new ArrivalsOfDay(day, hit, true, true);
				}
				throw e;
			}

			// API는 시각순으로 정렬돼 오지 않는다(2026-09-17 실측: 예정시각 기준 1,156건 중 147곳이 역전).
			// 화면이 "예정" 시각을 기준으로 보여주므로 여기서 한 번 정렬해 둔다. 같은 시각이면 편명순.
			flights.sort(Comparator.comparing(FlightStatusDto::getScheduleDateTime, Comparator.nullsLast(Comparator.naturalOrder()))
					.thenComparing(FlightStatusDto::getFlightNo, Comparator.nullsLast(Comparator.naturalOrder())));

			CacheEntry fresh = new CacheEntry(Collections.unmodifiableList(flights), System.currentTimeMillis());
			CACHE.put(day, fresh);
			CACHE.entrySet().removeIf(en -> en.getValue().isExpired());
			return new ArrivalsOfDay(day, fresh, false, false);
		}
	}

	// ─────────────────────────────────────────────────────────────
	// 기존 메서드 (FlightDao 등에서 사용 중 - 이름과 사용법 그대로 유지)
	// ─────────────────────────────────────────────────────────────

	// 귀국 도착편 운항현황 조회 - 결항 여부 확인용.
	// searchday: 조회일자(YYYYMMDD). null이면 오늘. (예전 주석엔 "API 기본값=오늘"이라 돼 있었으나 실제론 아니었음)
	// airport: 상대(출발지) 공항 IATA 코드, 예: "NRT"(나리타). null/빈 문자열이면 전체 조회.
	// fromTime/toTime: 조회 시간 범위 (HHMM 형식, 예: "0900"). 필요 없으면 null로 넘기면 됨.
	// lang: "K"(국문) 기본.
	//
	// 통신 실패 시 빈 리스트를 돌려주는 기존 동작은 유지한다(호출하는 쪽이 그렇게 짜여 있음).
	// 단 secret.properties 가 없거나 날짜가 잘못된 건 조용히 넘기지 않고 예외로 알린다.
	public List<FlightStatusDto> getArrivalFlights(String searchday, String airport, String fromTime, String toTime, String lang) {
		String day = normalizeDay(searchday);
		serviceKey();   // 설정 누락은 여기서 바로 드러나게 (아래 try 안에서 삼켜지지 않도록)

		boolean noFilter = isEmpty(airport) && isEmpty(fromTime) && isEmpty(toTime)
				&& (lang == null || "K".equals(lang));
		try {
			if (noFilter) {
				// 조건 없는 하루치 조회는 캐시를 탄다. 받는 쪽이 리스트를 수정해도 캐시가 안 망가지게 복사본으로.
				return new ArrayList<>(getArrivalsOfDay(day).getFlights());
			}
			return fetchAll(day, airport, fromTime, toTime, lang);
		} catch (RuntimeException e) {
			System.out.println("getArrivalFlights() 오류 (" + day + ") : " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// 특정 편명 하나만 결항인지 바로 확인하고 싶을 때 쓰는 편의 메서드.
	// 예약자 본인 항공편의 결항 여부를 좌석 예약 화면 등에서 바로 체크할 때 사용.
	// searchday: 예약자가 입력한 도착 예정일(YYYYMMDD). 같은 편명이 매일 뜨므로 날짜를 넘겨야 정확히 매칭됨.
	// 공동운항 편명(예: AA8905)으로 물어봐도 된다 - Slave 행도 실제 편과 같은 remark를 가진다(실측 622/622).
	public boolean isFlightCancelled(String flightNo, String searchday) {
		List<FlightStatusDto> flights = getArrivalFlights(searchday, null, null, null, "K");
		for (FlightStatusDto f : flights) {
			if (flightNo != null && flightNo.equalsIgnoreCase(f.getFlightNo())) {
				return f.isCancelled();
			}
		}
		return false; // 조회 결과에 없으면 결항 아님으로 처리(운항정보 없음과 결항은 구분 필요 - 추후 보완)
	}

	// 편명 목록 중 검색어로 시작하는 것만 골라내는 자동완성용 메서드.
	// 예약 페이지의 "항공편 검색" 입력창(주소 검색 UX와 동일한 방식)에서 사용 - 4차 회의에서 강선구가 제안한 방식.
	public List<FlightStatusDto> searchByFlightNoPrefix(String prefix, String searchday) {
		if (prefix == null || prefix.isEmpty()) {
			return new ArrayList<>();
		}
		return filterByPrefix(getArrivalFlights(searchday, null, null, null, "K"), prefix);
	}

	// ─────────────────────────────────────────────────────────────
	// 걸러내기 (서블릿에서도 씀)
	// ─────────────────────────────────────────────────────────────
	public static List<FlightStatusDto> filterByPrefix(List<FlightStatusDto> src, String prefix) {
		List<FlightStatusDto> result = new ArrayList<>();
		if (prefix == null || prefix.trim().isEmpty()) {
			return result;
		}
		String p = prefix.trim().toUpperCase();
		for (FlightStatusDto f : src) {
			if (f.getFlightNo() != null && f.getFlightNo().toUpperCase().startsWith(p)) {
				result.add(f);
			}
		}
		return result;
	}

	// 공동운항 편명(Slave)을 실제 운항편(Master) 아래로 묶는다.
	// 반환 : 실제 운항편 -> 그 비행기의 공동운항 편명들 (순서는 넘겨받은 리스트 순서 그대로)
	// Master를 못 찾은 Slave는 혼자서 한 줄이 된다(실측으로는 0건이지만 방어).
	// Map의 키로 DTO 객체 자체를 쓴다. FlightStatusDto는 equals()를 재정의하지 않았으므로
	// "같은 객체인가"로 구분된다 - 나중에 DTO에 equals()를 추가하면 이 부분을 다시 볼 것.
	public static Map<FlightStatusDto, List<String>> groupCodeshares(List<FlightStatusDto> src) {
		Map<String, FlightStatusDto> masters = new LinkedHashMap<>();
		for (FlightStatusDto f : src) {
			if (f.isMaster() && f.getFlightNo() != null) {
				masters.put(f.getFlightNo(), f);
			}
		}

		Map<FlightStatusDto, List<String>> grouped = new LinkedHashMap<>();
		for (FlightStatusDto f : src) {
			if (f.isMaster()) {
				grouped.computeIfAbsent(f, k -> new ArrayList<>());
				continue;
			}
			FlightStatusDto master = masters.get(f.getMasterFlightId());
			if (master != null) {
				grouped.computeIfAbsent(master, k -> new ArrayList<>()).add(f.getFlightNo());
			} else {
				grouped.computeIfAbsent(f, k -> new ArrayList<>());
			}
		}
		return grouped;
	}

	// ─────────────────────────────────────────────────────────────
	// API 호출
	// ─────────────────────────────────────────────────────────────
	private List<FlightStatusDto> fetchAll(String day, String airport, String fromTime, String toTime, String lang) {
		List<FlightStatusDto> all = new ArrayList<>();
		for (int pageNo = 1; pageNo <= MAX_PAGES; pageNo++) {
			Page page = fetchPage(day, airport, fromTime, toTime, lang, pageNo);
			all.addAll(page.items);
			if (page.items.isEmpty() || all.size() >= page.totalCount) {
				return all;
			}
		}
		System.out.println("[FlightApiDao] " + MAX_PAGES + "페이지를 넘어서 중단함 (" + day + ", " + all.size() + "건)");
		return all;
	}

	private Page fetchPage(String day, String airport, String fromTime, String toTime, String lang, int pageNo) {
		StringBuilder url = new StringBuilder(BASE_URL);
		url.append("?serviceKey=").append(serviceKey());
		url.append("&pageNo=").append(pageNo);          // 필수 파라미터
		url.append("&numOfRows=").append(ROWS_PER_PAGE); // 필수 파라미터
		url.append("&type=xml");
		url.append("&lang=").append(lang == null ? "K" : lang);
		url.append("&searchday=").append(day);
		if (!isEmpty(airport)) {
			url.append("&airport_code=").append(airport);
		}
		if (!isEmpty(fromTime)) {
			url.append("&from_time=").append(fromTime);
		}
		if (!isEmpty(toTime)) {
			url.append("&to_time=").append(toTime);
		}

		// 주의: 로그/에러 메시지에 url을 그대로 찍지 않는다. 서비스키가 들어 있다.
		String xml;
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url.toString()))
					.timeout(Duration.ofSeconds(15))
					.GET()
					.build();
			HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			xml = response.body();
			if (response.statusCode() != 200) {
				// 키가 틀리면 403과 함께 본문에 이유(<returnAuthMsg>)를 담아 준다.
				// 상태코드만 보고 끝내면 "등록되지 않은 서비스키" 같은 진짜 원인이 사라지므로,
				// 본문을 먼저 해석해서 이유가 있으면 그 내용으로 예외를 낸다(parsePage 안에서 던짐).
				parsePage(xml);
				throw new IllegalStateException("항공편 API 응답 오류 (HTTP " + response.statusCode()
						+ ") 응답 앞부분 : " + head(xml));
			}
		} catch (IOException e) {
			throw new IllegalStateException("항공편 API에 연결하지 못했습니다 : " + e.getClass().getSimpleName()
					+ (e.getMessage() == null ? "" : " " + e.getMessage()), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("항공편 API 호출이 중단됐습니다.", e);
		}
		return parsePage(xml);
	}

	// 응답 XML 한 페이지를 해석 (JDK 내장 DOM 파서만 사용 - 별도 jar 불필요)
	private Page parsePage(String xml) {
		Document doc;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// 외부 엔티티(XXE) 차단 - 외부에서 받은 XML을 파싱할 때의 기본 방어
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			throw new IllegalStateException("항공편 API 응답을 해석하지 못했습니다(XML이 아님). 응답 앞부분 : "
					+ head(xml), e);
		}

		// 인증 실패는 정상 응답과 모양이 다르다. 예전 코드는 이걸 0건으로 조용히 넘겼다.
		String authMsg = text(doc, "returnAuthMsg");
		if (authMsg != null) {
			throw new IllegalStateException("공공데이터포털 인증 실패 : " + authMsg + " (" + text(doc, "errMsg") + ")\n"
					+ "  secret.properties 의 api.serviceKey 를 확인하세요. Encoding 키를 그대로 넣어야 합니다.");
		}
		String code = text(doc, "resultCode");
		if (code != null && !"00".equals(code)) {
			throw new IllegalStateException("항공편 API 오류 : " + code + " " + text(doc, "resultMsg"));
		}

		Page page = new Page();
		page.totalCount = toInt(text(doc, "totalCount"));
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
			FlightStatusDto dto = new FlightStatusDto();
			dto.setAirline(getTagValue(item, "airline"));
			dto.setFlightNo(getTagValue(item, "flightId")); // API 태그명은 flightId지만 실제 값은 편명 -> 우리 쪽 필드는 flightNo
			dto.setAirport(getTagValue(item, "airport"));
			dto.setAirportCode(getTagValue(item, "airportCode"));
			dto.setScheduleDateTime(getTagValue(item, "scheduleDateTime"));
			dto.setEstimatedDateTime(getTagValue(item, "estimatedDateTime"));
			dto.setGateNumber(getTagValue(item, "exitnumber"));   // "gatenumber" 태그는 없음 - 도착편은 입국장 출구
			dto.setCarousel(getTagValue(item, "carousel"));
			dto.setRemark(getTagValue(item, "remark"));
			dto.setTerminalId(getTagValue(item, "terminalid"));
			dto.setCodeshare(getTagValue(item, "codeshare"));
			dto.setMasterFlightId(getTagValue(item, "masterflightid"));
			page.items.add(dto);
		}
		return page;
	}

	// ─────────────────────────────────────────────────────────────
	// 작은 도우미들
	// ─────────────────────────────────────────────────────────────
	private String getTagValue(Element item, String tag) {
		NodeList nodes = item.getElementsByTagName(tag);
		if (nodes.getLength() == 0 || nodes.item(0).getFirstChild() == null) {
			return null;
		}
		String v = nodes.item(0).getFirstChild().getNodeValue();
		if (v == null) {
			return null;
		}
		v = v.trim();   // masterflightid 처럼 값이 없을 때 줄바꿈만 들어오는 태그가 있다
		return v.isEmpty() ? null : v;
	}

	private static String text(Document doc, String tag) {
		NodeList nodes = doc.getElementsByTagName(tag);
		if (nodes.getLength() == 0) {
			return null;
		}
		String v = nodes.item(0).getTextContent();
		return v == null ? null : v.trim();
	}

	private static int toInt(String s) {
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return 0;
		}
	}

	private static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	private static String head(String s) {
		if (s == null) {
			return "(빈 응답)";
		}
		String t = s.replaceAll("\\s+", " ").trim();
		return t.length() > 120 ? t.substring(0, 120) + "..." : t;
	}

	private static class Page {
		int totalCount;
		List<FlightStatusDto> items = new ArrayList<>();
	}

	private static class CacheEntry {
		final List<FlightStatusDto> flights;
		final long fetchedAt;

		CacheEntry(List<FlightStatusDto> flights, long fetchedAt) {
			this.flights = flights;
			this.fetchedAt = fetchedAt;
		}

		boolean isExpired() {
			return System.currentTimeMillis() - fetchedAt > CACHE_MS;
		}
	}

	// getArrivalsOfDay() 결과. 목록 화면에 "몇 시에 받아온 데이터인지"를 보여주려고 같이 담는다.
	public static class ArrivalsOfDay {
		private final String searchday;
		private final List<FlightStatusDto> flights;
		private final long fetchedAt;
		private final boolean fromCache;
		private final boolean stale;    // API가 실패해서 유효기간 지난 캐시를 대신 준 경우

		ArrivalsOfDay(String searchday, CacheEntry entry, boolean fromCache, boolean stale) {
			this.searchday = searchday;
			this.flights = entry.flights;
			this.fetchedAt = entry.fetchedAt;
			this.fromCache = fromCache;
			this.stale = stale;
		}

		public String getSearchday() {
			return searchday;
		}
		public List<FlightStatusDto> getFlights() {
			return flights;
		}
		public long getFetchedAt() {
			return fetchedAt;
		}
		public boolean isFromCache() {
			return fromCache;
		}
		public boolean isStale() {
			return stale;
		}
	}
}