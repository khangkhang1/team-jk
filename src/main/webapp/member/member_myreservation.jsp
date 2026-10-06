<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
					<aside class="member_left"><%@ include
							file="member_menu.jsp"%></aside>
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
						<c:choose>
							<c:when test="${empty reservationList}">
								<div class="reservation_empty">예약 내역이 없습니다.</div>
							</c:when>
							<c:otherwise>
								<div class="reservation_list">
									<c:forEach var="reservation" items="${reservationList}"
										varStatus="loop">
										<c:set var="statusName" value="예약완료" />
										<c:set var="statusClass" value="" />
										<c:choose>
											<c:when test="${reservation.reservation_status eq '2'}">
												<c:set var="statusName" value="주차중" />
												<c:set var="statusClass" value="is_using" />
											</c:when>
											<c:when test="${reservation.reservation_status eq '3'}">
												<c:set var="statusName" value="출차완료" />
												<c:set var="statusClass" value="is_done" />
											</c:when>
											<c:when test="${reservation.reservation_status eq '4'}">
												<c:set var="statusName" value="예약취소" />
												<c:set var="statusClass" value="is_cancelled" />
											</c:when>
										</c:choose>
										<details class="reservation_item" name="my-reservations"
											${loop.first ? 'open' : ''}>
											<summary class="reservation_summary">
												<span class="reservation_status ${statusClass}">${statusName}</span>
												<span class="reservation_overview"> <strong><c:out
															value="${reservation.seat_no}" /> ·
														${reservation.reservation_type eq '1' ? '예약형' : '자유출차형'}</strong> <span><c:out
															value="${reservation.start_at}" /> ~ <c:choose>
															<c:when test="${reservation.reservation_type eq '2'}">자유출차</c:when>
															<c:otherwise>
																<c:out value="${reservation.end_at}" />
															</c:otherwise>
														</c:choose></span>
												</span> <span class="reservation_number"><c:out
														value="${reservation.reservation_id}" /></span> <span
													class="reservation_arrow" aria-hidden="true"></span>
											</summary>
											<div class="reservation_detail">
												<dl class="reservation_info">
													<div>
														<dt>예약 일시</dt>
														<dd>
															<c:out value="${reservation.reserved_at}" default="-" />
														</dd>
													</div>
													<c:if test="${reservation.reservation_type eq '1'}">
														<div>
															<dt>항공편</dt>
															<dd>
																<c:out value="${reservation.flight_no}" default="-" />
															</dd>
														</div>
														<div>
															<dt>도착 예정</dt>
															<dd>
																<c:out value="${reservation.arrive_at}" default="-" />
															</dd>
														</div>
														<div>
															<dt>예상 요금</dt>
															<dd>
																<c:choose>
																	<c:when
																		test="${empty reservation.reservation_estimate_amount}">-</c:when>
																	<c:otherwise>
																		<fmt:formatNumber
																			value="${reservation.reservation_estimate_amount}"
																			pattern="#,###" />원</c:otherwise>
																</c:choose>
															</dd>
														</div>
													</c:if>
													<div>
														<dt>예약금</dt>
														<dd>
															<c:choose>
																<c:when
																	test="${empty reservation.reservation_deposit_amount}">-</c:when>
																<c:otherwise>
																	<fmt:formatNumber
																		value="${reservation.reservation_deposit_amount}"
																		pattern="#,###" />원</c:otherwise>
															</c:choose>
														</dd>
													</div>
													<c:if test="${not empty reservation.parking_start_at}">
														<div>
															<dt>주차 시작</dt>
															<dd>
																<c:out value="${reservation.parking_start_at}" />
															</dd>
														</div>
													</c:if>
													<c:if test="${not empty reservation.out_at}">
														<div>
															<dt>실제 출차</dt>
															<dd>
																<c:out value="${reservation.out_at}" />
															</dd>
														</div>
													</c:if>
													<c:if test="${reservation.reservation_status eq '3'}">
														<div>
															<dt>최종 결제</dt>
															<dd>
																<c:choose>
																	<c:when
																		test="${empty reservation.reservation_final_amount}">-</c:when>
																	<c:otherwise>
																		<fmt:formatNumber
																			value="${reservation.reservation_final_amount}"
																			pattern="#,###" />원</c:otherwise>
																</c:choose>
															</dd>
														</div>
													</c:if>
												</dl>
												<c:if test="${reservation.can_cancel eq 1}">
													<div class="reservation_actions">
														<form method="post"
															action="${pageContext.request.contextPath}/Member"
															onsubmit="return confirm('이 예약을 취소하시겠습니까?');">
															<input type="hidden" name="t_gubun"
																value="reservationCancel"> <input type="hidden"
																name="t_reservation_id"
																value="<c:out value='${reservation.reservation_id}'/>">
															<button type="submit" class="reservation_cancel_btn">예약
																취소</button>
														</form>
													</div>
												</c:if>
											</div>
										</details>
									</c:forEach>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</section>
		</main>
	</div>
</body>
</html>
