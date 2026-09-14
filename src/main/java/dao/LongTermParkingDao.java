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

import dto.ParkingStatusDto;

// 인천공항공사 StatusOfParking API 호출 담당 DAO
// 장기주차장 P1~P5의 구역 단위 실시간 현황을 조회한다.
public class LongTermParkingDao {

    private static final String STATUS_URL =
            "http://apis.data.go.kr/B551177/StatusOfParking/getTrackingParking";

    private static final String SERVICE_KEY =
            "8A6C6Mp2ylWbir47yE6IJtBplIUUhhvbxRr3CbDEGe4URfJZBRmcEoT5SdFTxhrK%2Bdk8bO1MQY%2BOV7guUPsrDw%3D%3D";

    // 전체 구역별 실시간 주차 현황 조회
    public List<ParkingStatusDto> getZoneStatusList() {

        List<ParkingStatusDto> list =
                new ArrayList<>();

        String url = STATUS_URL
                + "?serviceKey=" + SERVICE_KEY
                + "&type=xml"
                + "&numOfRows=50"
                + "&pageNo=1";

        try {

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            list =
                    parseStatusXml(
                            response.body()
                    );

        } catch (Exception e) {

            System.out.println(
                    "getZoneStatusList() 오류 : "
                    + url
            );

            e.printStackTrace();
        }

        return list;
    }

    // 구역명으로 한 건 조회
    public ParkingStatusDto getZoneStatus(
            String floorName) {

        List<ParkingStatusDto> list =
                getZoneStatusList();

        for (ParkingStatusDto dto : list) {

            if (floorName != null
                    && floorName.equals(
                            dto.getFloor())) {

                return dto;
            }
        }

        return null;
    }

    // ------------------------------------------------------------
    // 장기주차장 P1~P5용 API 구역명
    //
    // P1 → T1 장기 P1 주차장
    // P2 → T1 장기 P2 주차장
    // P3 → T1 장기 P3 주차장
    // P4 → T1 장기 P1 주차타워 (시연용 임시 매핑)
    // P5 → T1 P5 예약주차장
    // ------------------------------------------------------------

    public ParkingStatusDto getP1Status() {
        return getZoneStatus(
                "T1 장기 P1 주차장"
        );
    }

    public ParkingStatusDto getP2Status() {
        return getZoneStatus(
                "T1 장기 P2 주차장"
        );
    }

    public ParkingStatusDto getP3Status() {
        return getZoneStatus(
                "T1 장기 P3 주차장"
        );
    }

    // 실제 P4 데이터가 없으므로 주차타워 데이터를 임시 사용
    public ParkingStatusDto getP4Status() {
        return getZoneStatus(
                "T1 장기 P1 주차타워"
        );
    }

    public ParkingStatusDto getP5Status() {
        return getZoneStatus(
                "T1 P5 예약주차장"
        );
    }

    // StatusOfParking XML → ParkingStatusDto 리스트
    private List<ParkingStatusDto> parseStatusXml(
            String xml) throws Exception {

        List<ParkingStatusDto> list =
                new ArrayList<>();

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

        NodeList items =
                doc.getElementsByTagName(
                        "item"
                );

        for (int i = 0;
             i < items.getLength();
             i++) {

            Element item =
                    (Element) items.item(i);

            ParkingStatusDto dto =
                    new ParkingStatusDto();

            dto.setFloor(
                    getTagValue(
                            item,
                            "floor"
                    )
            );

            dto.setParking(
                    toInt(
                            getTagValue(
                                    item,
                                    "parking"
                            )
                    )
            );

            dto.setParkingArea(
                    toInt(
                            getTagValue(
                                    item,
                                    "parkingarea"
                            )
                    )
            );

            dto.setDateTm(
                    getTagValue(
                            item,
                            "datetm"
                    )
            );

            list.add(dto);
        }

        return list;
    }

    // 문자열 → int
    private int toInt(String value) {

        try {

            if (value == null) {
                return 0;
            }

            return Integer.parseInt(
                    value.trim()
            );

        } catch (Exception e) {

            return 0;
        }
    }

    // XML 태그 값 가져오기
    private String getTagValue(
            Element item,
            String tag) {

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