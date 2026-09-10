<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<%-- 제목은 페이지마다 다르므로 공통 헤더가 아니라 각 페이지가 직접 쓴다 --%>
<title>자주 묻는 질문 | 인천공항 주차예약</title>

<%--
  헤더 모양을 내는 CSS. contextPath를 붙이는 이유 :
  이 파일은 /faq/ 폴더에 있어서 "css/index1.css"라고 쓰면
  /faq/css/index1.css 를 찾다가 못 찾는다.
--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/index1.css">
</head>

<body>

<div class="wrap">

	<%--
	  공통 헤더. 앞의 "/" 필수.
	  이 페이지는 히어로 배너가 없으므로 solid=true (흰 배경 + 검은 글씨)로 부른다.
	  안 그러면 흰 글씨가 흰 배경에 묻혀서 안 보인다.
	--%>
	<jsp:include page="/common_header.jsp">
		<jsp:param name="solid" value="true" />
	</jsp:include>

	<%-- 헤더가 position:fixed 라 본문이 헤더 밑으로 들어간다. padding-top으로 밀어준다 --%>
	<main style="max-width:1200px; margin:0 auto; padding:110px 20px 80px;">
		<h2>자주 묻는 질문</h2>
		<p>준비 중입니다.</p>
	</main>

</div>

</body>
</html>
