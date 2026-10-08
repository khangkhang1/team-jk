<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link href="${pageContext.request.contextPath}/css/common/common_header.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/common.js"></script>

<form name="go">
	<input type="hidden" name="t_gubun">
</form>


<header class="header">

	<div class="header_inner">

		<a href="${pageContext.request.contextPath}/ParkingStatus" class="logo">
			<fmt:message key="hdr.001"/>
			<small>INCHEON AIRPORT PARKING</small>
		</a>

		<ul class="header_menu">

			<!-- 교통 · 주차 -->
			<li>
				<a href="${pageContext.request.contextPath}/index2.html#parking"><fmt:message key="hdr.002"/></a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/index2.html#guide"><fmt:message key="hdr.003"/></a>
					<a href="${pageContext.request.contextPath}/index2.html#parking"><fmt:message key="hdr.004"/></a>
					<a href="${pageContext.request.contextPath}/index2.html#parking"><fmt:message key="hdr.005"/></a>
				</div>
			</li>

			<!-- 주차 예약 조회 -->
			<li>
				<a href="${pageContext.request.contextPath}/reservation.html"><fmt:message key="hdr.006"/></a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/mypage.html"><fmt:message key="hdr.007"/></a>
					<a href="${pageContext.request.contextPath}/mypage.html"><fmt:message key="hdr.008"/></a>
					<a href="${pageContext.request.contextPath}/mypage.html"><fmt:message key="hdr.009"/></a>
					<a href="${pageContext.request.contextPath}/mypage.html"><fmt:message key="hdr.010"/></a>
				</div>
			</li>

			<!-- 공지 사항 -->
			<li>
				<a href="${pageContext.request.contextPath}/Notice"><fmt:message key="hdr.011"/></a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/Notice"><fmt:message key="hdr.011"/></a>
					<a href="${pageContext.request.contextPath}/Faq"><fmt:message key="hdr.012"/></a>
					<a href="${pageContext.request.contextPath}/Report"><fmt:message key="hdr.013"/></a>
				</div>
			</li>

		</ul>

		<div class="header_right">
			<%-- 언어 전환 (기본 한국어). 선택하면 /Lang 이 세션에 저장하고 보던 화면으로 돌아온다 --%>
			<select class="lang_select" aria-label="Language" onchange="location.href='${pageContext.request.contextPath}/Lang?t_lang=' + this.value + '&t_back=' + encodeURIComponent(location.pathname + location.search)">
				<option value="ko" ${sessionScope.lang ne 'ja' ? 'selected' : ''}>한국어</option>
				<option value="ja" ${sessionScope.lang eq 'ja' ? 'selected' : ''}>日本語</option>
			</select>


			<%-- 관리자(sessionLevel = top)로 로그인했을 때만 관리자 콘솔 버튼 --%>
			<c:if test="${sessionLevel eq 'top'}">
				<a href="${pageContext.request.contextPath}/Manager" class="header_admin"><fmt:message key="hdr.014"/></a>
			</c:if>

			<c:if test="${not empty sessionName }">
				<a href="javascript:movePage('Member','myinfo')"><fmt:message key="hdr.015"><fmt:param value="${sessionName }"/></fmt:message></a>
				<span>|</span>
				<a href="javascript:movePage('Member','logout')"><fmt:message key="hdr.016"/></a>

			</c:if>

			<c:if test="${empty sessionName }">
				<a href="javascript:movePage('Member','login')"><fmt:message key="hdr.017"/></a>
				<span>|</span>
				<a href="javascript:movePage('Member','join')"><fmt:message key="hdr.018"/></a>
			</c:if>
		</div>

		<button class="menu_btn" aria-label="<fmt:message key='hdr.019'/>">☰</button>

	</div>

</header>
<%@ include file="/common_jsmsg.jsp" %>

