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

import dto.ParkingSeatDto1;

// 인천공항공사 OpenAPI(ParkLocationData) 호출 담당 DAO.
public class ParkingApiDao {

	private static final String BASE_URL = "http://apis.data.go.kr/B551177/ParkLocationData/getParkLocationData";

	private static final String SERVICE_KEY = "8A6C6Mp2ylWbir47yE6IJtBplIUUhhvbxRr3CbDEGe4URfJZBRmcEoT5SdFTxhrK%2Bdk8bO1MQY%2BOV7guUPsrDw%3D%3D";

	// 주차면 현황 조회
	public List<ParkingSeatDto1> getParkingStatus(String terminalId, int numOfRows, int pageNo) {

		List<ParkingSeatDto1> list = new ArrayList<>();

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

			HttpResponse<String> response = client.send(
					request,
					HttpResponse.BodyHandlers.ofString()
			);

			list = parseXml(response.body());

		} catch (Exception e) {

			System.out.println("getParkingStatus() 오류 : " + url);
			e.printStackTrace();

		}

		return list;
	}
	
	//+
	public List<ParkingSeatDto1> getAllParkingStatus(String terminalId) {

		List<ParkingSeatDto1> allList = new ArrayList<>();

		int pageNo = 1;
		int numOfRows = 1000;

		while (true) {

			List<ParkingSeatDto1> pageList =
					getParkingStatus(terminalId,numOfRows,pageNo);

			if (pageList == null || pageList.isEmpty()) {
				break;
			}

			allList.addAll(pageList);

			System.out.println(
					pageNo + "페이지 조회 완료 : "
					+ pageList.size() + "개"
			);

			if (pageList.size() < numOfRows) {
				break;
			}

			pageNo++;
		}

		System.out.println(
				"전체 조회 완료 : "
				+ allList.size() + "개"
		);

		return allList;
	}
	
	
	

	// XML을 ParkingSeatDto 리스트로 변환
	private List<ParkingSeatDto1> parseXml(String xml) throws Exception {

		List<ParkingSeatDto1> list = new ArrayList<>();

		DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();

		DocumentBuilder builder =
				factory.newDocumentBuilder();

		Document doc =
				builder.parse(
						new InputSource(
								new StringReader(xml)
						)
				);

		NodeList totalCountNodes = doc.getElementsByTagName("totalCount");

		if (totalCountNodes.getLength() > 0) {
			System.out.println(
				"API 전체 주차면 수 = "
				+ totalCountNodes.item(0).getTextContent()
			);
		}
		
		
		NodeList items =
				doc.getElementsByTagName("item");

		for (int i = 0; i < items.getLength(); i++) {

			Element item =
					(Element) items.item(i);

			ParkingSeatDto1 dto =
					new ParkingSeatDto1();

			dto.setParkLaneCode(
					getTagValue(item, "parklanecode")
			);

			dto.setCarStatus(
					getTagValue(item, "carstatus")
			);

			dto.setCarInDate(
					getTagValue(item, "carindate")
			);

			dto.setParkLotNo(
					getTagValue(item, "parklotno")
			);

			dto.setParkZoneNo(
					getTagValue(item, "parkzoneno")
			);

			dto.setTerminalNo(
					getTagValue(item, "terno")
			);

			list.add(dto);

			// API에서 실제로 들어오는 값 확인
//			System.out.println(
//					"주차장=" + dto.getParkLotNo()
//					+ " / 구역=" + dto.getParkZoneNo()
//					+ " / 주차면=" + dto.getParkLaneCode()
//					+ " / 상태=" + dto.getCarStatus()
//			);
			
			
			
		}
		System.out.println("전체 주차면 수 = " + list.size());
		
		return list;
	}

	// XML 태그 값 가져오기
	private String getTagValue(Element item, String tag) {

		NodeList nodes =
				item.getElementsByTagName(tag);

		if (nodes.getLength() == 0
				|| nodes.item(0).getFirstChild() == null) {

			return null;
		}

		return nodes.item(0)
				.getFirstChild()
				.getNodeValue();
	}
}