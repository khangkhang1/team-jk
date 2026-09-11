<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%--
 공통 헤더 (강선구). 개인프로젝트 common_menu.jsp 와 같은 방식.

 [쓰는 법] 페이지의 <body> 안에서
     같은 폴더 : <%@ include file="common_header.jsp" %>
     하위 폴더 : <%@ include file="../common_header.jsp" %>

 [기본은 불투명 헤더(흰 배경 + 검은 글씨)라 그냥 쓰면 된다]

 [메인처럼 히어로 배너 위에 겹쳐 올리는 투명 헤더가 필요할 때만 앞에 이 줄 추가]
     <% request.setAttribute("headerOverlay", "Y"); %>
   투명 상태로 시작했다가 스크롤을 내리면 불투명해진다.

 [페이지 <head>에 CSS 필수. 없으면 불릿 달린 맨 화면이 나온다]
     <link href="${pageContext.request.contextPath}/css/index1.css" rel="stylesheet">
--%>
<%
    // 정적 include 는 파라미터를 못 넘기므로 request 속성으로 받는다.
    // 기본은 불투명(solid). 페이지 대부분이 흰 배경이라 투명이 기본이면
    // 흰 글씨가 흰 배경에 묻혀서 안 보인다 - 안전한 쪽을 기본값으로 둔다.
    // 메인처럼 배너 위에 겹쳐야 하는 화면만 headerOverlay="Y" 를 넘긴다.
    boolean headerOverlay = "Y".equals(request.getAttribute("headerOverlay"));
%>

<header class="header<%= headerOverlay ? "" : " scrolled" %>"
        data-overlay="<%= headerOverlay ? "Y" : "N" %>">

	<div class="header_inner">

		<a href="${pageContext.request.contextPath}/index2.html" class="logo">
			인천공항 주차예약
			<small>INCHEON AIRPORT PARKING</small>
		</a>

		<%-- <li>는 <ul> 자식이어야 함. CSS가 .header_menu>li>a 라 <ul>을 새로 끼우면 깨져서 태그 자체를 교체 --%>
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
				<a href="${pageContext.request.contextPath}/notice.html">공지 사항</a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/notice.html">공지 사항</a>
					<a href="${pageContext.request.contextPath}/faq/faq.jsp">자주 하는 질문</a>
				</div>
			</li>

		</ul>

		<div class="header_right">
			<a href="${pageContext.request.contextPath}/login.html">로그인</a>
			<span>|</span>
			<%-- TODO: 회원가입 페이지가 아직 없어서 로그인으로 보내둠. 생기면 교체할 것 --%>
			<a href="${pageContext.request.contextPath}/login.html">회원가입</a>
		</div>

		<button class="menu_btn" aria-label="메뉴">☰</button>

	</div>

</header>

<%-- 스크롤 시 헤더 축소. index2.html에 같은 스크립트가 있으니 그 페이지 전환 시 기존 것 삭제할 것 --%>
<script>
(function(){
	var header = document.querySelector(".header");
	if (!header) return;

	// overlay 페이지만 스크롤에 따라 투명<->불투명이 바뀐다.
	// 나머지 페이지는 계속 불투명 유지(맨 위로 올려도 글씨가 사라지지 않게).
	var overlay = header.dataset.overlay === "Y";

	function updateHeader(){
		header.classList.toggle("scrolled", !overlay || window.scrollY > 40);
	}

	window.addEventListener("scroll", updateHeader);
	updateHeader();
})();
</script>
