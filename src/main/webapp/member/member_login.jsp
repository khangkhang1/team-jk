<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title><fmt:message key="login.001"/></title>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/member.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_login.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_menu.css">
<link href="${pageContext.request.contextPath}/css/index1.css"
	rel="stylesheet">
</head>

<body>


	<!-- HEADER -->
	<header class="header">

		<%@include file="../common_header.jsp"%>
	</header>


	<!-- MAIN -->
	<main class="login_page">

		<div class="member_page_layout">

			<aside class="member_menu_sidebar">
				<%@ include file="member_menu.jsp" %>
			</aside>

			<div class="login_container">

			<!-- PAGE TITLE -->
			<div class="page_title">

				<span class="title_eng">MEMBER LOGIN</span>

				<h1><fmt:message key="hdr.017"/></h1>

				<p>
					<fmt:message key="login.002"/><br> <fmt:message key="login.003"/>
				</p>

			</div>


			<!-- LOGIN CARD -->
			<section class="login_card">

				<h2><fmt:message key="login.004"/></h2>

				<form id="loginForm" name="mem">
					<input type="hidden" name="t_gubun">

					<!-- ID -->
					<div class="input_group">

						<label for="member_id"> <fmt:message key="login.005"/> </label> <input type="text"
							id="member_id" name="t_id" maxlength="20" onkeypress="checkEnter()"
							value="<c:out value='${rememberedId}'/>"
							placeholder="<fmt:message key='login.015'/>" autocomplete="username" autofocus>

						<p class="error_message" id="idError"></p>

					</div>


					<!-- PASSWORD -->
					<div class="input_group">

						<label for="password"> <fmt:message key="login.006"/> </label> <input type="password"
							id="password" name="t_password" maxlength="70" onkeypress="checkEnterPassword()"
							placeholder="<fmt:message key='login.016'/>" autocomplete="current-password">

						<p class="error_message" id="passwordError"></p>

					</div>


					<!-- 아이디 저장 -->
					<div class="login_remember">
						<label class="remember_id_label" for="rememberId">
							<input type="checkbox" id="rememberId" name="t_rememberId" value="Y"
								aria-describedby="rememberIdHint"
								<c:if test="${not empty rememberedId}">checked</c:if>>
							<span><fmt:message key="login.007"/></span>
						</label>
						<p class="remember_id_hint" id="rememberIdHint"><fmt:message key="login.008"/></p>
					</div>

					<!-- LOGIN BUTTON -->
					<input type="button" onclick="memberLogin()" class="login_btn"
						value="<fmt:message key='hdr.017'/>">


					<!-- LOGIN MENU -->
					<div class="login_menu">

						<a href="${pageContext.request.contextPath}/Member?t_gubun=findId"><fmt:message key="login.009"/></a> <span>|</span> <a href="${pageContext.request.contextPath}/Member?t_gubun=findPassword"><fmt:message key="login.010"/></a> <span>|</span>

						<a href="javascript:movePage('Member','join')"><fmt:message key="hdr.018"/></a>

					</div>

				</form>

			</section>


			<!-- INFO -->
			<div class="login_info">

				<div class="info_icon">!</div>

				<div class="info_text">
					<strong><fmt:message key="login.011"/></strong>
					<p>
						<fmt:message key="login.012"/><br> <fmt:message key="login.013"/>
					</p>
				</div>

			</div>

			</div>
		</div>

	</main>


	<!-- FOOTER -->
	<footer class="footer">

		<div class="footer_inner">

			<div class="footer_logo">
				<span class="logo_main"><fmt:message key="hdr.001"/></span> <span class="logo_sub">INCHEON
					AIRPORT PARKING</span>
			</div>

			<div class="footer_info">

				<p><fmt:message key="login.014"/></p>

				<p class="copyright">© INCHEON AIRPORT PARKING. All Rights
					Reserved.</p>

			</div>

		</div>

	</footer>


</body>
</html>

