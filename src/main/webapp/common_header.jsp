<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
=========================================================================
 공통 헤더 (강선구, 2026-09-10)
=========================================================================
 [쓰는 법] 헤더를 붙이고 싶은 페이지의 <body> 안에 이 한 줄만 넣으면 된다.

     <jsp:include page="/common_header.jsp" />

   ※ 앞에 "/"를 꼭 붙일 것. 그래야 faq/faq.jsp 처럼 하위 폴더에 있는
     페이지에서도 같은 곳을 찾는다. "/"가 없으면 faq/common_header.jsp를
     찾다가 404가 난다.

 [이 파일에 <!DOCTYPE>, <html>, <head>, <body>가 없는 이유]
   이건 "완성된 문서"가 아니라 다른 페이지에 끼워 넣는 조각이기 때문이다.
   여기에 문서 뼈대가 들어있으면 include하는 순간 <html>이 두 개,
   <body>가 두 개인 문서가 된다.
   -> 문서 뼈대와 <title>, CSS 링크는 각 페이지가 직접 쓴다.
      (제목은 페이지마다 달라야 하므로 여기 두면 안 된다)

 [각 페이지에서 이 CSS를 걸어줘야 헤더 모양이 나온다]
     <link rel="stylesheet"
           href="${pageContext.request.contextPath}/css/index1.css">

 [★중요★ 히어로 배너가 없는 페이지는 solid=true 를 넘길 것]
     <jsp:include page="/common_header.jsp">
         <jsp:param name="solid" value="true" />
     </jsp:include>

   이유 : .header 의 기본 상태는 background:transparent + color:#fff 다.
     index2.html처럼 뒤에 어두운 히어로 배너가 깔린 페이지를 전제로 만들어졌기
     때문이다. 배너가 없는 흰 배경 페이지에 그대로 쓰면 흰 글씨가 흰 배경 위에
     얹혀서 아무것도 안 보인다.
     solid=true 를 주면 처음부터 스크롤된 상태(흰 배경 + 검은 글씨)로 시작한다.
     메인처럼 배너가 있는 페이지에서는 그냥 빼면 된다.
=========================================================================
--%>

<%-- solid=true 면 처음부터 .scrolled 를 달아 불투명 상태로 시작 --%>
<header class="header${param.solid == 'true' ? ' scrolled' : ''}"
        data-solid="${param.solid == 'true'}">

	<div class="header_inner">

		<a href="${pageContext.request.contextPath}/index2.html" class="logo">
			인천공항 주차예약
			<small>INCHEON AIRPORT PARKING</small>
		</a>

		<%--
		  <ul>인 이유 : <li>는 <ul>/<ol>/<menu>의 자식이어야 한다(HTML 규칙).
		  원래 <nav class="header_menu">였는데, 그러면 <li>가 목록 밖에 뜬 꼴이라
		  유효하지 않은 마크업이 된다.
		  <nav>를 <ul>로 "바꾼" 이유(<nav> 안에 <ul>을 새로 넣지 않은 이유) :
		  CSS가 .header_menu>li>a 처럼 직계 자식 셀렉터를 쓰고 있어서,
		  중간에 <ul>을 한 겹 더 끼우면 그 셀렉터가 전부 어긋난다.
		--%>
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

<%--
  스크롤하면 헤더가 작아지는 동작(.scrolled 클래스 토글).
  헤더가 자기 동작을 같이 들고 다녀야 어느 페이지에 붙여도 똑같이 동작한다.
  ※ index2.html에는 같은 스크립트가 이미 안에 들어있다. 나중에 index2.html도
    이 공통 헤더를 쓰게 바꿀 때는 그쪽 스크립트를 지워야 중복 등록이 안 된다.
--%>
<script>
(function(){
	var header = document.querySelector(".header");
	if (!header) return;

	// solid 페이지는 스크롤 위치와 상관없이 계속 불투명하게 유지한다.
	// (이게 없으면 맨 위로 올렸을 때 다시 투명해져서 글씨가 사라진다)
	var solid = header.dataset.solid === "true";

	function updateHeader(){
		header.classList.toggle("scrolled", solid || window.scrollY > 40);
	}

	window.addEventListener("scroll", updateHeader);
	updateHeader();
})();
</script>
