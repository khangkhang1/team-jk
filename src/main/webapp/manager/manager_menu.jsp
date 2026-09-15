<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
 관리자 공통 틀 : 왼쪽 사이드 메뉴 + 상단바. 관리자 화면 <body> 맨 앞에서 include 한다.
   <%@ include file="manager_menu.jsp" %>
 서블릿이 넘기는 값 : activeMenu (현재 메뉴 키), pageTitle (상단바 제목)
--%>
<%-- 관리자 콘솔은 한국어 고정. 클라이언트가 언어 헤더를 안 보내면 fmt 가 포맷을 건너뛰므로 명시한다 --%>
<fmt:setLocale value="ko_KR" />
<jsp:useBean id="now" class="java.util.Date" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<aside class="adm_side">
	<a href="${ctx}/Manager" class="adm_brand">
		<strong>인천공항 주차예약</strong>
		<span>관리자 콘솔</span>
	</a>

	<nav class="adm_nav">
		<a href="${ctx}/Manager" class="${activeMenu == 'dashboard' ? 'on' : ''}">
			<i>▦</i> 대시보드
		</a>

		<p class="adm_nav_group">운영</p>
		<a href="${ctx}/Manager?t_gubun=gate" class="${activeMenu == 'gate' ? 'on' : ''}">
			<i>⇄</i> 입·출차 처리
		</a>
		<a href="${ctx}/Manager?t_gubun=reservation" class="${activeMenu == 'reservation' ? 'on' : ''}">
			<i>▤</i> 예약 관리
		</a>
		<a href="${ctx}/Manager?t_gubun=seat" class="${activeMenu == 'seat' ? 'on' : ''}">
			<i>▣</i> 좌석·구역 현황
		</a>

		<p class="adm_nav_group">통계</p>
		<a href="${ctx}/Manager?t_gubun=sales" class="${activeMenu == 'sales' ? 'on' : ''}">
			<i>◔</i> 매출 통계
		</a>

		<p class="adm_nav_group">콘텐츠</p>
		<a href="${ctx}/Faq" class="${activeMenu == 'faq' ? 'on' : ''}">
			<i>?</i> FAQ 관리
		</a>
		<a href="${ctx}/Manager?t_gubun=notice" class="${activeMenu == 'notice' ? 'on' : ''}">
			<i>!</i> 공지사항 관리 <em>준비 중</em>
		</a>

		<p class="adm_nav_group">도구</p>
		<a href="${ctx}/Manager?t_gubun=ai" class="${activeMenu == 'ai' ? 'on' : ''}">
			<i>✦</i> AI 어시스턴트 <em>준비 중</em>
		</a>
	</nav>

	<div class="adm_side_foot">
		<a href="${ctx}/Index">이용자 사이트</a>
		<a href="${ctx}/Member?t_gubun=logout">로그아웃</a>
	</div>
</aside>

<div class="adm_main">

	<header class="adm_top">
		<h1>${pageTitle}</h1>
		<div class="adm_top_right">
			<span class="adm_date"><fmt:formatDate value="${now}" pattern="yyyy.MM.dd (E)" /></span>
			<span class="adm_user">${sessionName}님</span>
		</div>
	</header>
