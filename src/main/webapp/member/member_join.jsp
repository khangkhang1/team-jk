<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title><fmt:message key="join.001"/></title>

<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-1.8.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/member.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_join.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_menu.css">

</head>

<body>
	<div class="wrap">
		<!-- HEADER -->
		<header class="header">

			<%@include file="../common_header.jsp"%>
		</header>


		<!-- MAIN -->
		<main class="main">

			<section class="join_section">

				<div class="member_page_layout">

					<aside class="member_menu_sidebar">
						<%@ include file="member_menu.jsp" %>
					</aside>

					<div class="join_container">

					<!-- PAGE TITLE -->
					<div class="page_title">

						<span class="page_eyebrow"> MEMBER JOIN </span>

						<h1><fmt:message key="hdr.018"/></h1>

						<p>
							<fmt:message key="join.002"/><br> <fmt:message key="join.003"/>
						</p>

					</div>


					<!-- JOIN CARD -->
					<div class="join_card">

						<div class="join_card_head">

							<h2><fmt:message key="join.004"/></h2>

							<span> <em>*</em> <fmt:message key="join.005"/>
							</span>

						</div>


						<form id="joinForm" class="join_form" name="mem">
							<input type="hidden" name="t_gubun" value="memberSave">


							<!-- 아이디 -->
							<div class="form_row">
								<label for="member_id"> <fmt:message key="login.005"/> <em>*</em>
								</label>

								<div class="input_area">
									<div class="input_button">
										<input type="text" id="member_id" name="t_id" maxlength="20"
											placeholder="<fmt:message key='login.015'/>" oninput="setEmpty()"> <input
											type="button" onclick="checkId()" id="idCheckBtn"
											value="<fmt:message key='join.029'/>">

									</div>

									<!-- 중복확인 결과 -->
									<div id="idCheckResult" class="verify_result"><fmt:message key="join.006"/></div>

									<span class="form_hint"> <fmt:message key="join.007"/><br> <fmt:message key="join.008"/>
									</span>

									<!-- 서버/JS에서 상태 저장용 -->
									<input type="hidden" name="t_id_check" id="id_check">
								</div>
							</div>


							<!-- 비밀번호 -->
							<div class="form_row">

								<label for="password"> <fmt:message key="login.006"/> <em>*</em>
								</label>

								<div class="input_area">

									<input type="password" id="password" name="t_password"
										maxlength="70" placeholder="<fmt:message key='login.016'/>"> <span
										class="form_hint"><fmt:message key="join.009"/></span>

								</div>

							</div>


							<!-- 비밀번호 확인 -->
							<div class="form_row">

								<label for="password_check"> <fmt:message key="join.010"/> <em>*</em>
								</label>

								<div class="input_area">

									<input type="password" id="password_check"
										name="t_password_confirm" maxlength="70"
										placeholder="<fmt:message key='join.024'/>">

								</div>

							</div>


							<!-- 이름 -->
							<div class="form_row">

								<label for="name"> <fmt:message key="join.011"/> <em>*</em>
								</label>

								<div class="input_area">

									<input type="text" id="name" name="t_name" maxlength="20"
										placeholder="<fmt:message key='join.025'/>">

								</div>

							</div>


							<!-- 전화번호 -->
							<div class="form_row">

								<label for="phone_number"> <fmt:message key="join.012"/> <em>*</em>
								</label>

								<div class="input_area">

									<input type="tel" id="phone_number" name="t_phone_number"
										maxlength="20" placeholder="010-0000-0000">

								</div>

							</div>


							<!-- 이메일 -->
							<div class="form_row">
								<label for="email"> <fmt:message key="join.013"/> <em>*</em>
								</label>

								<div class="input_area email_verify">

									<!-- 이메일 입력 -->
									<div class="verify_input_row">
										<input type="email" id="email" name="t_email" maxlength="100"
											placeholder="<fmt:message key='join.026'/>"> <input type="button"
											id="sendEmailBtn" onclick="sendEmailCode()" value="<fmt:message key='join.030'/>">
									</div>

									<!-- 인증번호 입력 -->
									<div class="verify_code_row">
										<input type="text" name="t_email_code" id="email_code"
											maxlength="6" placeholder="<fmt:message key='join.027'/>"> <input
											type="button" id="emailCheckBtn" onclick="checkEmailCode()"
											value="<fmt:message key='join.031'/>">
									</div>

									<!-- 인증 상태 -->
									<div id="emailVerifyResult" class="verify_result"><fmt:message key="join.014"/></div>

									<span class="form_hint"><fmt:message key="join.015"/></span>

								</div>
							</div>


							<!-- 차량번호 -->
							<div class="form_row">

								<label for="vehicle_number"> <fmt:message key="join.016"/> <em>*</em>
								</label>

								<div class="input_area">

									<input type="text" id="vehicle_number" name="t_vehicle_number"
										maxlength="20" placeholder="<fmt:message key='join.028'/>"> <span
										class="form_hint"> <fmt:message key="join.017"/> </span>

								</div>

							</div>


							<!-- 차량 종류 -->
							<div class="form_row">

								<label> <fmt:message key="join.018"/> <em>*</em>
								</label>

								<div class="input_area vehicle_type">

									<label class="radio_label"> <input type="radio"
										name="t_vehicle_type" value="N"> <span><fmt:message key="join.019"/></span>
									</label> <label class="radio_label"> <input type="radio"
										name="t_vehicle_type" value="E"> <span><fmt:message key="join.020"/></span>
									</label> <label class="radio_label"> <input type="radio"
										name="t_vehicle_type" value="D"> <span><fmt:message key="join.021"/></span>
									</label>

								</div>

							</div>


							<!-- 약관 -->
							<!-- 
							<div class="agree_area">

								<div class="agree_all">

									<label> <input type="checkbox" id="agreeAll"> <span
										class="check_box"></span> <strong> 전체 약관에 동의합니다. </strong>

									</label>

								</div>


								<div class="agree_list">

									<label> <input type="checkbox" class="agree required"
										name="terms_agree"> <span class="check_box"></span> <span>
											이용약관 동의 <em>(필수)</em>
									</span> <a href="#">보기</a>

									</label> <label> <input type="checkbox" class="agree required"
										name="privacy_agree"> <span class="check_box"></span>
										<span> 개인정보 수집 및 이용 동의 <em>(필수)</em>
									</span> <a href="#">보기</a>

									</label> <label> <input type="checkbox" class="agree">

										<span class="check_box"></span> <span> 마케팅 정보 수신 동의 <small>(선택)</small>
									</span> <a href="#">보기</a>

									</label>

								</div>

							</div>
							-->

							<!-- BUTTON -->
							<div class="form_buttons">

								<button type="button" class="cancel_btn" id="cancelBtn">
									<fmt:message key="join.022"/></button>

								<input type="button" onclick="goSave()" value="<fmt:message key='hdr.018'/>"
									class="join_btn">
								</button>

							</div>

						</form>

					</div>


					<!-- LOGIN LINK -->
					<div class="login_link">

						<fmt:message key="join.023"/> <a href="javascript:movePage('Member','login')">
							<fmt:message key="hdr.017"/> </a>

					</div>

					</div>
				</div>

			</section>

		</main>


		<!-- FOOTER -->
		<footer class="footer">

			<div class="footer_inner">

				<div class="footer_top">

					<div class="footer_logo">

						<fmt:message key="hdr.001"/> <small> INCHEON AIRPORT PARKING </small>

					</div>


					<div class="footer_links">

						<a href="#"><fmt:message key="ftr.001"/></a> <a href="#"><fmt:message key="ftr.002"/></a> <a href="#"><fmt:message key="ftr.003"/></a>

					</div>

				</div>


				<div class="footer_info">

					<p><fmt:message key="ftr.004"/></p>

					<p><fmt:message key="ftr.005"/></p>

					<p class="copyright">Copyright © Parking Reservation Project.
						All rights reserved.</p>

				</div>

			</div>

		</footer>

	</div>


</body>

</html>
