<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
=========================================================================
 [연습용] 항공편 도착현황 목록 — 강선구
=========================================================================
 목표 : FlightApiDao가 가져온 항공편 리스트를 표로 뿌린다.

 흐름 (이게 이 프로젝트 전체의 기본 패턴이다) :
   브라우저  →  Flight 서블릿  →  FlightApiDao  →  공공데이터 API
                     ↓ request.setAttribute("flightList", list)
                     ↓ forward
              flight_list.jsp  →  JSTL/EL로 반복 출력  →  브라우저

 규칙 : TODO 번호 순서대로 직접 타이핑해서 채운다.
        답을 보고 베끼지 말 것. 막히면 그때만 정답 파일을 연다.
        (정답 위치는 터미널에서 클로드가 알려줌)
=========================================================================
--%>

<%-- ─────────────────────────────────────────────────────────────
 TODO 1. page 지시자  →  ★맨 윗줄에 미리 채워둠. 타이핑 안 해도 됨★

 원래 이것도 네가 칠 TODO였는데, 비워놨더니 이 파일을 열자마자
 한글이 전부 깨져버렸다. 그 이유를 알고 넘어가는 게 이 TODO의 목적이다.

   1) 이클립스는 .jsp 파일을 열 때 파일 안의 pageEncoding 값을 보고
      "이 파일은 UTF-8로 읽어야겠군" 하고 판단한다.
   2) 그런데 그 줄이 없으면 판단 근거가 없으니
      기본값인 ISO-8859-1(서유럽 문자셋)로 읽어버린다.
   3) ISO-8859-1에는 한글이 아예 없다 → 전부 깨짐.

 즉 pageEncoding 은 "브라우저에 한글 잘 보내려고" 쓰는 게 아니라
 그 이전에 "이 소스파일 자체를 뭘로 읽을지"를 정하는 줄이다.
 그래서 JSP는 무조건 맨 첫 줄에 이게 와야 한다.

   contentType  = 브라우저한테 보낼 때의 문자셋 (출력 쪽)
   pageEncoding = 이 .jsp 소스파일 자체의 문자셋 (입력 쪽)
   → 둘 다 UTF-8로 맞춰야 안 깨진다. 하나만 맞추면 어딘가에서 깨진다.

 ※ 앞으로 새 JSP를 만들 때마다 이 한 줄을 제일 먼저 치는 습관을 들일 것.
   팀원들도 똑같이 당할 자리다. (이 프로젝트 첫 JSP가 이 파일이라 아무도 아직 안 겪음)
───────────────────────────────────────────────────────────── --%>


<%-- ─────────────────────────────────────────────────────────────
 TODO 2. JSTL core 라이브러리 선언
   - <c:forEach>, <c:if> 같은 태그를 쓰려면 먼저 선언해야 한다.
   - uri 는 http://java.sun.com/jsp/jstl/core , prefix 는 c
   - 힌트 : <%@ taglib ... %>
   - (jstl.jar / standard.jar 는 WEB-INF/lib 에 이미 들어있음)
───────────────────────────────────────────────────────────── --%>


<html>
<head>
<meta charset="UTF-8">
<title>항공편 도착현황 (연습)</title>
<style>
	body { font-family: "맑은 고딕", sans-serif; margin: 40px; background: #f7f8fa; }
	h2 { margin: 0 0 4px; }
	.sub { color: #888; font-size: 13px; margin-bottom: 20px; }
	.searchBox { background: #fff; padding: 16px 20px; border: 1px solid #e2e5ea;
	             border-radius: 8px; margin-bottom: 20px; }
	.searchBox input[type=text] { padding: 7px 10px; border: 1px solid #ccd0d6;
	                              border-radius: 4px; width: 140px; }
	.searchBox button { padding: 7px 18px; border: 0; border-radius: 4px;
	                    background: #1a5fd0; color: #fff; cursor: pointer; }
	table { width: 100%; border-collapse: collapse; background: #fff;
	        border: 1px solid #e2e5ea; border-radius: 8px; overflow: hidden; }
	th, td { padding: 10px 12px; border-bottom: 1px solid #eef0f3;
	         font-size: 14px; text-align: center; }
	th { background: #f0f3f8; color: #333; font-weight: 600; }
	tr.cancelled { background: #fff2f2; }
	tr.cancelled td { color: #c62828; font-weight: 700; }
	.empty { padding: 50px; text-align: center; color: #999; background: #fff;
	         border: 1px solid #e2e5ea; border-radius: 8px; }
	.cnt { font-size: 13px; color: #666; margin-bottom: 8px; }
</style>
</head>
<body>

	<h2>항공편 도착현황</h2>
	<p class="sub">인천국제공항공사 여객기 운항 현황 API (실데이터)</p>

	<%-- 조회 폼 : GET 으로 자기 자신(Flight 서블릿)을 다시 부른다 --%>
	<div class="searchBox">
		<form action="Flight" method="get">

			<%-- ─────────────────────────────────────────────────
			 TODO 3. 분기 파라미터를 hidden 으로 넣는다.
			   - 우리 팀 규칙 : 분기는 항상 t_gubun
			   - 이 화면은 목록이므로 값은 list
			   - 힌트 : <input type="hidden" ...>
			───────────────────────────────────────────────────── --%>


			<label>조회일자</label>

			<%-- ─────────────────────────────────────────────────
			 TODO 4. 날짜 입력칸.
			   - name 은 t_searchday (파라미터는 전부 t_ 접두어)
			   - placeholder 는 20260908 처럼 YYYYMMDD 형식 안내
			   - ★핵심★ value 에는 방금 조회했던 날짜가 다시 찍혀야 한다.
			     서블릿이 request.setAttribute("searchday", ...) 로 넣어줄 것이므로
			     EL 표기법 ${ ... } 으로 꺼내 쓴다.
			     → 이걸 안 하면 조회할 때마다 입력칸이 비워져서 사용자가 매번
			       다시 타이핑해야 한다. (入力工数の削減 = 업무개선 포인트)
			───────────────────────────────────────────────────── --%>


			<button type="submit">조회</button>
		</form>
	</div>


	<%-- ─────────────────────────────────────────────────────────
	 TODO 5. 결과가 0건일 때 안내문 띄우기.
	   - 서블릿이 넣어준 flightList 가 비어 있으면 아래 div 를 보여준다.
	   - JSTL 의 empty 연산자를 쓴다.
	   - 힌트 : <c:if test="${ ... }"> ... </c:if>
	     감싸야 할 내용 →  <div class="empty">조회 결과가 없습니다.</div>
	─────────────────────────────────────────────────────────── --%>



	<%-- ─────────────────────────────────────────────────────────
	 TODO 6. 결과가 1건 이상일 때만 표 전체를 보여준다.
	   - TODO 5 와 반대 조건. 아래 <p class="cnt"> 부터 </table> 까지를 감싼다.
	   - not empty 를 쓰면 된다.
	─────────────────────────────────────────────────────────── --%>

		<p class="cnt">
			<%-- TODO 7. 총 건수 출력.
			     - EL 에서 리스트 크기는 ${fn:length(...)} 도 있지만
			       taglib 을 하나 더 걸어야 하니, 여기서는 서블릿이 따로 넣어준
			       totalCount 를 그냥 꺼내 쓴다.  → 총 ○건 --%>
			총 건
		</p>

		<table>
			<tr>
				<th>편명</th>
				<th>항공사</th>
				<th>출발지</th>
				<th>예정시각</th>
				<th>변경시각</th>
				<th>현황</th>
			</tr>

			<%-- ─────────────────────────────────────────────────
			 TODO 8. flightList 를 반복해서 <tr> 을 찍는다.
			   - <c:forEach var="f" items="${...}"> ... </c:forEach>
			   - var="f" 로 잡으면 한 건 한 건이 FlightStatusDto 다.

			 TODO 9. 결항인 행은 빨갛게.
			   - <tr> 에 class="cancelled" 를 붙이면 CSS가 이미 준비돼 있다.
			   - FlightStatusDto 에 isCancelled() 가 이미 있다.
			     EL 에서 boolean getter 는 ${f.cancelled} 로 접근한다.
			       (is 를 떼고 앞글자를 소문자로 — getFlightNo() → ${f.flightNo} 와 같은 규칙)
			   - 힌트 : <tr <c:if test="...">class="cancelled"</c:if>>

			 TODO 10. 각 칸 채우기. DTO 의 getter 이름 그대로 EL 로 꺼낸다.
			   getFlightNo()          → ${f.flightNo}
			   getAirline()           → ?
			   getAirport()           → ?
			   getScheduleDateTime()  → ?
			   getEstimatedDateTime() → ?
			   getRemark()            → ?
			───────────────────────────────────────────────────── --%>


		</table>


	<%-- ─────────────────────────────────────────────────────────
	 [보너스] 다 되고 나서 시간 남으면.
	   예정시각이 202609081430 처럼 붙어서 나와 읽기 힘들다.
	   → FlightStatusDto 에 getScheduleTimeText() 같은 메서드를 하나 만들어서
	     "14:30" 으로 잘라 돌려주고, JSP 에서는 ${f.scheduleTimeText} 로 쓴다.
	   ※ 포맷 변환을 JSP 안에서 하지 않고 DTO(자바)에서 하는 이유 :
	     화면은 "보여주기"만, 가공은 자바에서 — 역할 분리(責務の分離).
	     같은 포맷이 다른 화면에서도 필요할 때 재사용된다.
	─────────────────────────────────────────────────────────── --%>

</body>
</html>
