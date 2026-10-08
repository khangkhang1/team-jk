<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><fmt:message key="myresv.001"/></title>
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
							<h1><fmt:message key="myresv.002"/></h1>
							<p><fmt:message key="myresv.003"/></p>
						</div>
						<div class="reservation_list_head">
							<h2><fmt:message key="hdr.007"/></h2>
							<span><fmt:message key="myresv.004"/></span>
						</div>
						<c:choose>
							<c:when test="${empty reservationList}">
								<div class="reservation_empty"><fmt:message key="myresv.005"/></div>
							</c:when>
							<c:otherwise>
								<div class="reservation_list">
									<c:forEach var="reservation" items="${reservationList}"
										varStatus="loop">
										<c:set var="statusName"><fmt:message key="status.1"/></c:set>
										<c:set var="statusClass" value="" />
										<c:choose>
											<c:when test="${reservation.reservation_status eq '2'}">
												<c:set var="statusName"><fmt:message key="status.2"/></c:set>
												<c:set var="statusClass" value="is_using" />
											</c:when>
											<c:when test="${reservation.reservation_status eq '3'}">
												<c:set var="statusName"><fmt:message key="status.3"/></c:set>
												<c:set var="statusClass" value="is_done" />
											</c:when>
											<c:when test="${reservation.reservation_status eq '4'}">
												<c:set var="statusName"><fmt:message key="status.4"/></c:set>
												<c:set var="statusClass" value="is_cancelled" />
											</c:when>
										</c:choose>
										<details class="reservation_item" name="my-reservations"
											${loop.first ? 'open' : ''}>
											<summary class="reservation_summary">
												<span class="reservation_status ${statusClass}">${statusName}</span>
												<span class="reservation_overview"> <strong><c:out
															value="${reservation.seat_no}" /> · <fmt:message key="${reservation.reservation_type eq '1' ? 'type.1' : 'type.2'}"/></strong> <span><c:out
															value="${reservation.start_at}" /> ~ <c:choose>
															<c:when test="${reservation.reservation_type eq '2'}"><fmt:message key="myresv.007"/></c:when>
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
														<dt><fmt:message key="myresv.008"/></dt>
														<dd>
															<c:out value="${reservation.reserved_at}" default="-" />
														</dd>
													</div>
													<c:if test="${reservation.reservation_type eq '1'}">
														<div>
															<dt><fmt:message key="myresv.009"/></dt>
															<dd>
																<c:out value="${reservation.flight_no}" default="-" />
															</dd>
														</div>
														<div>
															<dt><fmt:message key="myresv.010"/></dt>
															<dd>
																<c:out value="${reservation.end_at}" default="-" />
															</dd>
														</div>
													</c:if>
													<c:set var="reservationAmount"
														value="${reservation.reservation_type eq '1' ? reservation.reservation_estimate_amount : reservation.reservation_deposit_amount}" />
													<div>
														<dt><fmt:message key="${reservation.reservation_type eq '1' ? 'amount.paid' : 'amount.deposit'}"/></dt>
														<dd>
															<c:choose>
																<c:when
																test="${empty reservationAmount}">-</c:when>
																<c:otherwise>
																	<fmt:formatNumber
																		value="${reservationAmount}"
																		pattern="#,###" /><fmt:message key="myresv.012"/></c:otherwise>
															</c:choose>
														</dd>
													</div>
													<c:if test="${not empty reservation.parking_start_at}">
														<div>
															<dt><fmt:message key="myresv.013"/></dt>
															<dd>
																<c:out value="${reservation.parking_start_at}" />
															</dd>
														</div>
													</c:if>
													<c:if test="${not empty reservation.out_at}">
														<div>
															<dt><fmt:message key="myresv.014"/></dt>
															<dd>
																<c:out value="${reservation.out_at}" />
															</dd>
														</div>
													</c:if>
													<c:if test="${reservation.reservation_type eq '2' and reservation.reservation_status eq '2'}">
														<div>
															<dt><fmt:message key="myresv.015"/></dt>
															<dd>
																<c:choose>
																	<c:when test="${empty reservation.estimated_usage_amount}">-</c:when>
																	<c:otherwise>
																		<fmt:formatNumber value="${reservation.estimated_usage_amount}" pattern="#,###" /><fmt:message key="myresv.012"/>
																	</c:otherwise>
																</c:choose>
															</dd>
														</div>
													</c:if>
													<c:if test="${reservation.reservation_type eq '2' and reservation.reservation_status eq '3'}">
														<div>
															<dt><fmt:message key="myresv.016"/></dt>
															<dd>
																<c:choose>
																	<c:when
																		test="${empty reservation.reservation_final_amount}">-</c:when>
																	<c:otherwise>
																		<fmt:formatNumber
																			value="${reservation.reservation_final_amount}"
																			pattern="#,###" /><fmt:message key="myresv.012"/></c:otherwise>
																</c:choose>
															</dd>
														</div>
													</c:if>
												</dl>
												<c:if test="${reservation.reservation_type eq '2' and reservation.reservation_status ne '4'}">
													<p class="reservation_payment_note">
														<c:if test="${reservation.reservation_status eq '1'}"><fmt:message key="myresv.017"/></c:if>
														<c:if test="${reservation.reservation_status eq '2'}"><fmt:message key="myresv.018"/></c:if>
													</p>
												</c:if>
												<c:if test="${reservation.can_cancel eq 1}">
													<div class="reservation_actions">
														<form method="post"
															action="${pageContext.request.contextPath}/Member"
															onsubmit="return confirm('<fmt:message key="myresv.019"/>');">
															<input type="hidden" name="t_gubun"
																value="reservationCancel"> <input type="hidden"
																name="t_reservation_id"
																value="<c:out value='${reservation.reservation_id}'/>">
															<button type="submit" class="reservation_cancel_btn"><fmt:message key="hdr.009"/></button>
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
