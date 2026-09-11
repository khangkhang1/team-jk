<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%--
 공통 헤더 (강선구). 개인프로젝트 common_menu.jsp 와 같은 방식.

 [쓰는 법] 페이지의 <body> 안에서
     같은 폴더 : <%@ include file="common_header.jsp" %>
     하위 폴더 : <%@ include file="../common_header.jsp" %>

 [배너 없는 페이지는 앞에 이 줄을 추가]
     <% request.setAttribute("headerSolid", "Y"); %>
   안 주면 헤더가 투명 + 흰 글씨라 흰 배경 페이지에서 안 보인다.

 [페이지 <head>에 CSS 필수. 없으면 불릿 달린 맨 화면이 나온다]
     <link href="${pageContext.request.contextPath}/css/index1.css" rel="stylesheet">
--%>
<%
// 정적 include 는 파라미터를 못 넘기므로 request 속성으로 받는다.
// 안 넘어오면 "N"(투명 헤더)이 기본 - web_jsl common_header.jsp 의 null 처리와 같은 방식.
String headerSolid = (String) request.getAttribute("headerSolid");
if (headerSolid == null)
	headerSolid = "N";
%>
<script src="js/common.js"></script>

<form name="go">
	<input type="hidden" name="t_gubun">
</form>
<header class="header<%="Y".equals(headerSolid) ? " scrolled" : ""%>"
	data-solid="<%=headerSolid%>">

	<div class="header_inner">


		<a href="javascript:movePage('Index','')" class="logo"> 인천공항 주차예약
			<small>INCHEON AIRPORT PARKING</small>
		</a>

		<%-- <li>는 <ul> 자식이어야 함. CSS가 .header_menu>li>a 라 <ul>을 새로 끼우면 깨져서 태그 자체를 교체 --%>
		<ul class="header_menu">

			<!-- 교통 · 주차 -->

			<li><a
				href="${pageContext.request.contextPath}/index.jsp#parking">교통 ·
					주차</a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/index.jsp#guide">주차장
						이용 안내</a> <a
						href="${pageContext.request.contextPath}/index.jsp#parking">주차
						요금</a> <a href="${pageContext.request.contextPath}/index.jsp#parking">주차장
						혼잡도</a>
				</div></li>


			<!-- 주차 예약 조회 -->
			<li><a
				href="${pageContext.request.contextPath}/reservation.html">주차 예약
					조회</a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/mypage.html">예약 내역</a>
					<a href="${pageContext.request.contextPath}/mypage.html">예약 확인</a>
					<a href="${pageContext.request.contextPath}/mypage.html">예약 취소</a>
					<a href="${pageContext.request.contextPath}/mypage.html">이용 내역</a>
				</div></li>

			<!-- 공지 사항 -->
			<li><a href="${pageContext.request.contextPath}/notice.html">공지
					사항</a>

				<div class="header_dropdown">
					<a href="${pageContext.request.contextPath}/notice.html">공지 사항</a>
					<a href="${pageContext.request.contextPath}/faq/faq.jsp">자주 하는
						질문</a>
				</div></li>

		</ul>

		<div class="header_right">

			<c:if test="${not empty sessionName }">
				<a>${sessionName }님.</a>
				<span>|</span>
				<a href="javascript:movePage('Member','logout')">Logout</a>

			</c:if>

			<c:if test="${empty sessionName }">
				<a href="javascript:movePage('Member','join')">Join</a>
				<span>|</span>
				<a href="javascript:movePage('Member','login')">Login 
			</c:if>
		</div>


		<button class="menu_btn" aria-label="메뉴">☰</button>

	</div>

</header>

<%-- 스크롤 시 헤더 축소. index2.html에 같은 스크립트가 있으니 그 페이지 전환 시 기존 것 삭제할 것 --%>
<script>
	(function() {
		var header = document.querySelector(".header");
		if (!header)
			return;

		// solid 페이지는 맨 위로 올려도 불투명 유지
		var solid = header.dataset.solid === "Y";

		function updateHeader() {
			header.classList.toggle("scrolled", solid || window.scrollY > 40);
		}

		window.addEventListener("scroll", updateHeader);
		updateHeader();
	})();
</script>
