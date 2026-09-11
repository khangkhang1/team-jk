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

// 인천공항공사 ParkLocationData API 호출 담당 DAO
// T1 단기주차장 개별 주차면 정보를 조회한다.
public class ShortTermParkingDao {

    private static final String BASE_URL =
            "http://apis.data.go.kr/B551177/ParkLocationData/getParkLocationData";

    private static final String SERVICE_KEY =
            "8A6C6Mp2ylWbir47yE6IJtBplIUUhhvbxRr3CbDEGe4URfJZBRmcEoT5SdFTxhrK%2Bdk8bO1MQY%2BOV7guUPsrDw%3D%3D";

    // 주차면 현황 조회
    public List<ParkingSeatDto1> getParkingStatus(
            String terminalId,
            int numOfRows,
            int pageNo) {

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

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            list = parseXml(response.body());

        } catch (Exception e) {

            System.out.println(
                    "getParkingStatus() 오류 : " + url
            );

            e.printStackTrace();
        }

        return list;
    }

    // ------------------------------------------------------------
    // 60초 캐시 (2026-09-11 추가)
    //
    // 이 API는 개별 주차면 단위라 T1만 4,614행이 통째로 내려온다. 실측 834ms.
    // 화면을 열 때마다, 새로고침할 때마다 부르면 느린 데다 공공데이터포털의
    // 일일 호출 제한도 금방 닳는다. 주차 현황이 1분 단위로 갱신되는 건
    // 실무적으로 충분하므로 60초 동안은 받아둔 값을 그대로 돌려준다.
    //
    // 캐시가 static인 이유 : 서블릿은 요청마다 new ShortTermParkingDao()를 하므로
    //   인스턴스 필드에 두면 캐시가 매번 비어 의미가 없다.
    // 클래스 락을 잡는 이유 : 캐시가 만료된 순간 요청이 여러 개 몰리면
    //   4,614행을 동시에 여러 번 받아오게 된다. 한 명만 받아오고 나머지는
    //   그 결과를 쓰게 막는다.
    // ------------------------------------------------------------
    private static final long CACHE_MS = 60 * 1000L;
    private static List<ParkingSeatDto1> allCache = null;
    private static String allCacheKey = null;
    private static long allCacheTime = 0L;

    // T1 전체 주차면 조회
    // API가 한 번에 최대 1,000건이므로 페이지를 반복해서 조회한다.
    public List<ParkingSeatDto1> getAllParkingStatus(
            String terminalId) {

        synchronized (ShortTermParkingDao.class) {

            long now = System.currentTimeMillis();

            boolean fresh =
                    allCache != null
                    && (now - allCacheTime) < CACHE_MS
                    && allCacheKey != null
                    && allCacheKey.equals(terminalId);

            if (fresh) {
                return allCache;
            }

            List<ParkingSeatDto1> fetched =
                    fetchAllParkingStatus(terminalId);

            // 호출 실패(빈 리스트)면 캐시를 갈아엎지 않는다.
            // 직전에 받아둔 값이라도 보여주는 편이 빈 화면보다 낫다.
            if (!fetched.isEmpty()) {
                allCache = fetched;
                allCacheKey = terminalId;
                allCacheTime = now;
                return fetched;
            }

            if (allCache != null) {
                return allCache;
            }

            return fetched;
        }
    }

    // 실제 호출부 (캐시를 거치지 않는다)
    private List<ParkingSeatDto1> fetchAllParkingStatus(
            String terminalId) {

        List<ParkingSeatDto1> allList =
                new ArrayList<>();

        int pageNo = 1;
        int numOfRows = 1000;

        while (true) {

            List<ParkingSeatDto1> pageList =
                    getParkingStatus(
                            terminalId,
                            numOfRows,
                            pageNo
                    );

            if (pageList == null
                    || pageList.isEmpty()) {

                break;
            }

            allList.addAll(pageList);

            System.out.println(
                    pageNo
                    + "페이지 조회 완료 : "
                    + pageList.size()
                    + "개"
            );

            if (pageList.size() < numOfRows) {
                break;
            }

            pageNo++;
        }

        System.out.println(
                "전체 조회 완료 : "
                + allList.size()
                + "개"
        );

        return allList;
    }

    // XML → ParkingSeatDto1 리스트 변환
    private List<ParkingSeatDto1> parseXml(
            String xml) throws Exception {

        List<ParkingSeatDto1> list =
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

        NodeList totalCountNodes =
                doc.getElementsByTagName(
                        "totalCount"
                );

        if (totalCountNodes.getLength() > 0) {

            System.out.println(
                    "API 전체 주차면 수 = "
                    + totalCountNodes
                            .item(0)
                            .getTextContent()
            );
        }

        NodeList items =
                doc.getElementsByTagName("item");

        for (int i = 0;
             i < items.getLength();
             i++) {

            Element item =
                    (Element) items.item(i);

            ParkingSeatDto1 dto =
                    new ParkingSeatDto1();

            dto.setParkLaneCode(
                    getTagValue(
                            item,
                            "parklanecode"
                    )
            );

            dto.setCarStatus(
                    getTagValue(
                            item,
                            "carstatus"
                    )
            );

            dto.setCarInDate(
                    getTagValue(
                            item,
                            "carindate"
                    )
            );

            dto.setParkLotNo(
                    getTagValue(
                            item,
                            "parklotno"
                    )
            );

            dto.setParkZoneNo(
                    getTagValue(
                            item,
                            "parkzoneno"
                    )
            );

            dto.setTerminalNo(
                    getTagValue(
                            item,
                            "terno"
                    )
            );

            list.add(dto);
        }

        System.out.println(
                "전체 주차면 수 = "
                + list.size()
        );

        return list;
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