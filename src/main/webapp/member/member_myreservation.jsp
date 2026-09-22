<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>MY RESERVATION | 인천공항 주차예약</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_myinfo.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_menu.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_myreservation.css">
</head>
<body>
	<div class="wrap">
		<%@ include file="../common_header.jsp"%>
		<main class="main">
			<section class="member_section">
				<div class="member_container">
					<aside class="member_left">
						<%@ include file="member_menu.jsp"%>
					</aside>
					<div class="member_right">
						<div class="page_title">
							<span class="page_eyebrow">MY RESERVATION</span>
							<h1>내 예약 내역</h1>
							<p>예약 내역과 주차 이용 정보를 확인할 수 있습니다.</p>
						</div>
						<div class="reservation_list_head">
							<h2>예약 내역</h2>
							<span>최신 예약순</span>
						</div>
						<div class="reservation_list">
							<%-- 최신순으로 반복 출력하고 첫 항목에만 open을 지정하세요. --%>
							<details class="reservation_item" name="my-reservations" open>
								<summary class="reservation_summary">
									<span class="reservation_status">예약완료</span> <span
										class="reservation_overview"> <strong>주차 구역 ·
											좌석 번호</strong> <span>입차 예정 일시 ~ 출차 예정 일시</span>
									</span> <span class="reservation_number">예약번호</span> <span
										class="reservation_arrow" aria-hidden="true"></span>
								</summary>
								<div class="reservation_detail">
									<dl class="reservation_info">
										<div>
											<dt>예약번호</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>예약 상태</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>주차 위치</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>예약 유형</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>입차 예정</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>출차 예정</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>항공편</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>실제 출차</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>예상 요금</dt>
											<dd>-</dd>
										</div>
										<div>
											<dt>예약금</dt>
											<dd>-</dd>
										</div>
									</dl>
									<%-- 예약 변경/취소 버튼이 필요하면 이곳에 추가하세요. --%>
								</div>
							</details>
							<details class="reservation_item" name="my-reservations">
								<summary class="reservation_summary">
									<span class="reservation_status is_done">출차완료</span> <span
										class="reservation_overview"> <strong>이전 예약 ·
											주차 위치</strong> <span>입차 일시 ~ 출차 일시</span>
									</span> <span class="reservation_number">예약번호</span> <span
										class="reservation_arrow" aria-hidden="true"></span>
								</summary>
								<div class="reservation_detail">
									<p class="reservation_placeholder">예약 상세 정보를 넣어주세요.</p>
								</div>
							</details>
						</div>
						<%-- 조회 결과가 없을 때 reservation_list 대신 표시하세요.
					<div class="reservation_empty">예약 내역이 없습니다.</div>
					--%>
					</div>
				</div>
			</section>
		</main>
	</div>
</body>
</html>
