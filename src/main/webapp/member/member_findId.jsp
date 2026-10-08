<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><fmt:message key="findid.001"/></title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_login.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_menu.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/index1.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_recovery.css">
<script src="${pageContext.request.contextPath}/js/member_recovery.js" defer></script>
</head>
<body>
<div class="wrap">
	<%@ include file="../common_header.jsp"%>
	<main class="login_page" id="recoveryPage" data-mode="id"
		data-endpoint="${pageContext.request.contextPath}/MemberRecovery">
		<div class="member_page_layout">
			<aside class="member_menu_sidebar"><%@ include file="member_menu.jsp"%></aside>
			<div class="login_container">
				<div class="page_title">
					<span class="title_eng">FIND ID</span>
					<h1><fmt:message key="login.009"/></h1>
					<p><fmt:message key="findid.002"/></p>
				</div>
				<section class="login_card recovery_card">
					<h2><fmt:message key="findid.003"/></h2>
					<form id="recoveryForm" autocomplete="off">
						<div class="input_group">
							<label for="recoveryEmail"><fmt:message key="findid.004"/></label>
							<input id="recoveryEmail" name="email" type="email" maxlength="100" required autocomplete="email">
						</div>
						<button type="button" id="sendRecoveryCode" class="recovery_button"><fmt:message key="join.030"/></button>
						<p class="recovery_hint"><fmt:message key="findid.005"/></p>
						<div id="recoveryCodeArea" hidden>
							<div class="input_group">
								<label for="recoveryCode"><fmt:message key="findid.006"/></label>
								<input id="recoveryCode" name="code" type="text" maxlength="6" inputmode="numeric" autocomplete="one-time-code">
							</div>
							<button type="button" id="verifyRecoveryCode" class="recovery_button"><fmt:message key="join.031"/></button>
						</div>
						<div id="recoveryResult" class="recovery_result" role="status" aria-live="polite"></div>
					</form>
					<div class="login_menu recovery_links">
						<a href="${pageContext.request.contextPath}/Member?t_gubun=login"><fmt:message key="hdr.017"/></a>
						<span>|</span>
						<a href="${pageContext.request.contextPath}/Member?t_gubun=findPassword"><fmt:message key="login.010"/></a>
					</div>
				</section>
			</div>
		</div>
	</main>
</div>
</body>
</html>
