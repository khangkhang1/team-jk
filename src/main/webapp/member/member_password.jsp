<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><fmt:message key="pw.001"/></title>

<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-1.8.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/member.js"></script>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_myinfo.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_menu.css">
</head>

<body>
	<div class="wrap">
		<%@ include file="../common_header.jsp" %>

		<main class="main">
			<section class="member_section">
				<div class="member_container">
					<aside class="member_left">
						<%@ include file="member_menu.jsp" %>
					</aside>

					<div class="member_right">
						<div class="page_title">
							<span class="page_eyebrow">PASSWORD UPDATE</span>
							<h1><fmt:message key="myinfo.009"/></h1>
							<p><fmt:message key="pw.002"/></p>
						</div>

						<div class="join_card">
							<div class="join_card_head">
								<h2><fmt:message key="pw.003"/></h2>
							</div>

							<form class="join_form" name="mem">
								<input type="hidden" name="t_gubun" value="passwordUpdate">

								<div class="form_row">
									<label for="current_password"><fmt:message key="pw.004"/> <em>*</em></label>
									<div class="input_area">
										<input type="password" id="current_password"
											name="t_current_password" maxlength="70"
											autocomplete="current-password">
									</div>
								</div>

								<div class="form_row">
									<label for="new_password"><fmt:message key="findpw.003"/> <em>*</em></label>
									<div class="input_area">
										<input type="password" id="new_password"
											name="t_new_password" maxlength="70"
											autocomplete="new-password">
										<span class="form_hint"><fmt:message key="pw.005"/></span>
									</div>
								</div>

								<div class="form_row">
									<label for="new_password_confirm"><fmt:message key="findpw.004"/> <em>*</em></label>
									<div class="input_area">
										<input type="password" id="new_password_confirm"
											name="t_new_password_confirm" maxlength="70"
											autocomplete="new-password">
									</div>
								</div>

								<div class="form_buttons">
									<button type="button" class="withdraw_btn"
										onclick="movePage('Member','myinfo')"><fmt:message key="join.022"/></button>
									<button type="button" class="join_btn"
										onclick="goPasswordUpdate()"><fmt:message key="myinfo.009"/></button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</section>
		</main>
	</div>
</body>
</html>
