<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link href="${pageContext.request.contextPath}/css/common/common_header.css" rel="stylesheet">


<header class="header">

	<div class="header_inner">

		<a href="ParkingStatus" class="logo">
			인천공항 주차예약
			<small>INCHEON AIRPORT PARKING</small>
		</a>

		<ul class="header_menu">

			<!-- 교통 · 주차 -->
			<li>
				<a href="${pageContext.request.contextPath}/index2.html#parking">교통 · 주차</a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/index2.html#guide">주차장 이용 안내</a>
					<a href="${pageContext.request.contextPath}/index2.html#parking">주차 요금</a>
					<a href="${pageContext.request.contextPath}/index2.html#parking">주차장 혼잡도</a>
				</div>
			</li>

			<!-- 주차 예약 조회 -->
			<li>
				<a href="${pageContext.request.contextPath}/reservation.html">주차 예약 조회</a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/mypage.html">예약 내역</a>
					<a href="${pageContext.request.contextPath}/mypage.html">예약 확인</a>
					<a href="${pageContext.request.contextPath}/mypage.html">예약 취소</a>
					<a href="${pageContext.request.contextPath}/mypage.html">이용 내역</a>
				</div>
			</li>

			<!-- 공지 사항 -->
			<li>
				<a href="Notice">공지 사항</a>

				<div class="header_dropdown">
					<a href="Notice">공지 사항</a>
					<a href="${pageContext.request.contextPath}/faq/faq.jsp">자주 하는 질문</a>
				</div>
			</li>

		</ul>

		<div class="header_right">

			<c:if test="${not empty sessionName }">
				<a href="javascript:movePage('Member','myinfo')">${sessionName }님.</a>
				<span>|</span>
				<a href="javascript:movePage('Member','logout')">Logout</a>

			</c:if>

			<c:if test="${empty sessionName }">
				<a href="javascript:movePage('Member','join')">Join</a>
				<span>|</span>
				<a href="javascript:movePage('Member','login')">Login</a>
			</c:if>
		</div>

		<button class="menu_btn" aria-label="메뉴">☰</button>

	</div>

</header>





