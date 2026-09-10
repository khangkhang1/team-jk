<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
 공통 헤더. WEB-INF 안이라 브라우저로 직접 못 연다(404). 정상이다.

 [쓰는 법] 페이지의 <body> 안에
     <jsp:include page="/WEB-INF/inc/common_header.jsp">
         <jsp:param name="solid" value="true" />
     </jsp:include>

   solid=true : 배너 없는 페이지용(흰 배경+검은 글씨). 안 주면 흰 글씨라 안 보임.

 [페이지 <head>에 이게 없으면 CSS 안 걸려서 맨 화면 나옴]
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index1.css">
--%>

<header class="header${param.solid == 'true' ? ' scrolled' : ''}"
        data-solid="${param.solid == 'true'}">

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

	// solid 페이지는 맨 위로 올려도 불투명 유지
	var solid = header.dataset.solid === "true";

	function updateHeader(){
		header.classList.toggle("scrolled", solid || window.scrollY > 40);
	}

	window.addEventListener("scroll", updateHeader);
	updateHeader();
})();
</script>
