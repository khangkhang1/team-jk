<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 아직 만들지 않은 관리자 메뉴의 자리표시 화면. Manager 서블릿이 pageTitle 을 넘긴다. --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${pageTitle} | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">
		<section class="card soon_card">
			<p class="soon_icon">🛠</p>
			<h2>${pageTitle}</h2>
			<p>이 메뉴는 준비 중입니다. 대시보드에서 현황을 확인하실 수 있습니다.</p>
			<a href="${pageContext.request.contextPath}/Manager" class="adm_btn">대시보드로</a>
		</section>
	</main>
</div>

</body>
</html>
