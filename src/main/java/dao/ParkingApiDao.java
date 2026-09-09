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

import dto.*;

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
			// [2026-09-09] ParkingSeatDto가 DB 좌석 엔티티로 재작성되면서(ba9d037)
			// 예전 setter(setParkLaneCode 등)가 사라져 main 빌드가 깨져 있었다.
			// API 필드를 새 DTO 필드에 맞춰 다시 매핑한다.
			//   parklanecode(주차면 코드) -> seatNo
			//   carstatus(Y/N)           -> isOccupied
			//   parklotno(주차장 구분)    -> lotId
			//   parkzoneno(구역 구분)     -> floorId
			// terno(터미널)는 새 DTO에 대응 필드가 없어 버린다 - 현재 T1만 제공되므로 무방.
			dto.setSeatNo(getTagValue(item, "parklanecode"));
			dto.setIsOccupied(getTagValue(item, "carstatus"));
			dto.setCarInDate(getTagValue(item, "carindate"));
			dto.setLotId(toInt(getTagValue(item, "parklotno")));
			dto.setFloorId(toInt(getTagValue(item, "parkzoneno")));
			list.add(dto);
		}
		return list;
	}

	// "01" 같은 문자열을 숫자로. 값이 없거나 숫자가 아니면 0으로 처리한다.
	private int toInt(String s) {
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return 0;
		}
	}

	private String getTagValue(Element item, String tag) {
		NodeList nodes = item.getElementsByTagName(tag);
		if (nodes.getLength() == 0 || nodes.item(0).getFirstChild() == null) {
			return null;
		}
		return nodes.item(0).getFirstChild().getNodeValue();
	}
}
