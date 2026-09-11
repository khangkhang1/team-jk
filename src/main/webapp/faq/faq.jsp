<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>자주 묻는 질문 | 인천공항 주차예약</title>
<link href="${pageContext.request.contextPath}/css/index1.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/faq.css" rel="stylesheet">

</head>

<body>

<div class="wrap">

	<%@ include file="../common_header.jsp" %>

	<main class="main faq_page">
	<div class="container">

		<section class="section" style="padding-top:0">

			<div class="section_head">
				<div>
					<h2>자주 묻는 질문</h2>
					<p>예약부터 출차까지, 자주 들어오는 문의를 모았습니다.</p>
				</div>
			</div>

			<%-- TODO: 지금은 하드코딩. 나중에 공지사항처럼 DB(게시판 CRUD)로 교체 --%>
			<div class="faq_cate">
				<a href="#" class="on">전체</a>
				<a href="#">예약</a>
				<a href="#">요금·결제</a>
				<a href="#">입·출차</a>
				<a href="#">항공편</a>
			</div>

			<div class="faq_list">

				<details class="faq_item" open>
					<summary>
						<span class="faq_q">Q</span>
						주차 예약은 언제까지 할 수 있나요?
						<span class="faq_tag">예약</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							입차 예정 시각 <strong>1시간 전</strong>까지 예약할 수 있습니다.
							예약 없이 오셔도 현장 주차는 가능하지만, 성수기에는 만차인 경우가 많아
							미리 예약하시는 것을 권장합니다.
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						예약한 주차 구역을 나중에 바꿀 수 있나요?
						<span class="faq_tag">예약</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							입차 전이라면 예약 내역에서 <strong>예약 변경</strong>으로 구역과 자리를 다시 고를 수 있습니다.
							이미 입차하신 뒤에는 자리 변경은 안 되고 <strong>이용 시간 연장</strong>만 가능합니다.
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						주차 요금은 어떻게 계산되나요?
						<span class="faq_tag">요금·결제</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							구역에 따라 요금이 다릅니다.
							<ul>
								<li>장기주차장 : 1일 2,000원</li>
								<li>단기주차장 : 1일 3,000원</li>
							</ul>
							예약 시 결제하는 금액은 <strong>예약금</strong>이며, 실제 이용 시간에 따른 최종 요금은
							출차할 때 정산합니다.
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						예약을 취소하면 예약금은 환불되나요?
						<span class="faq_tag">요금·결제</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							입차 예정 시각 전에 취소하시면 전액 환불됩니다.
							입차 예정 시각이 지나도록 오지 않으시면 <strong>30분 뒤 자동 취소</strong>되며,
							이 경우 예약금은 환불되지 않습니다.
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						입차할 때 무엇이 필요한가요?
						<span class="faq_tag">입·출차</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							예약 완료 시 발급된 <strong>예약번호</strong>를 입구 단말기에 입력하시면 됩니다.
							차량번호로도 조회할 수 있으니 예약 시 차량번호를 정확히 입력해 주세요.
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						예약한 자리에 다른 차가 서 있으면 어떻게 하나요?
						<span class="faq_tag">입·출차</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							앱 또는 웹에서 <strong>방치 차량 신고</strong>를 해주시면 관리자가 확인 후
							같은 등급의 다른 자리로 재배정해 드립니다. 추가 요금은 발생하지 않습니다.
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						비행기가 결항되면 예약은 어떻게 되나요?
						<span class="faq_tag">항공편</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							예약 시 입력하신 항공편의 운항 정보를 <strong>실시간으로 확인</strong>합니다.
							결항이 확인되면 예약 유형이 자동으로 전환되고 알림을 보내드리므로,
							따로 취소 처리를 하지 않으셔도 됩니다.
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						항공편이 지연되어 늦게 출차하면 추가 요금이 나오나요?
						<span class="faq_tag">항공편</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							지연으로 인한 초과 시간은 정상 요금으로 계산됩니다.
							다만 예약 시 항공편을 입력해 두시면 지연 정보가 자동 반영되어
							<strong>연체료는 부과되지 않습니다.</strong>
						</div>
					</div>
				</details>

				<details class="faq_item">
					<summary>
						<span class="faq_q">Q</span>
						실시간 주차 현황은 얼마나 정확한가요?
						<span class="faq_tag">예약</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div>
							인천국제공항공사에서 제공하는 <strong>공공데이터 API</strong>를 통해
							구역별 주차 현황을 실시간으로 받아옵니다.
							다만 집계 시점과 실제 상황에는 약간의 차이가 있을 수 있습니다.
						</div>
					</div>
				</details>

			</div>

		</section>

	</div>
	</main>

</div>

</body>
</html>
