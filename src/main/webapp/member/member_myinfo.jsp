<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>

<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">

<head>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title><fmt:message key="myinfo.001"/></title>

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


		<!-- HEADER -->
		<header class="header">

			<%@include file="../common_header.jsp"%>

		</header>


		<!-- MAIN -->
		<main class="main">

			<section class="member_section">

				<div class="member_container">


					<!-- 왼쪽 회원 메뉴 -->
					<div class="member_left">

						<%@ include file="../member/member_menu.jsp"%>

					</div>


					<!-- 오른쪽 본문 -->
					<div class="member_right">


						<!-- PAGE TITLE -->
						<div class="page_title">

							<span class="page_eyebrow"> MEMBER MYINFO </span>

							<h1><fmt:message key="myinfo.002"/></h1>

							<p><fmt:message key="myinfo.003"/></p>

						</div>


						<!-- MEMBER INFO CARD -->
						<div class="join_card">


							<!-- CARD HEADER -->
							<div class="join_card_head">

								<h2><fmt:message key="myinfo.004"/></h2>

								<span> <em>*</em> <fmt:message key="join.005"/>
								</span>

							</div>


							<!-- FORM -->
							<form id="myInfoForm" class="join_form" name="mem">
								<input type="hidden" name="t_gubun"> 
								<input type="hidden" name="t_id" value="${dto.getMember_id()}">

								<!-- 아이디 -->
								<div class="form_row">

									<label for="member_id"> <fmt:message key="login.005"/> </label>

									<div class="input_area">

										<input type="text" id="member_id" name="t_id"
											value="${dto.getMember_id()}" disabled> <span
											class="form_hint"> <fmt:message key="myinfo.005"/> </span>

									</div>

								</div>



								<!-- 이름 -->
								<div class="form_row">

									<label for="name"> <fmt:message key="join.011"/> <em>*</em>
									</label>

									<div class="input_area">

										<input type="text" id="name" name="t_name" maxlength="20"
											value="${dto.getName()}">

									</div>

								</div>


								<!-- 전화번호 -->
								<div class="form_row">

									<label for="phone_number"> <fmt:message key="join.012"/> <em>*</em>
									</label>

									<div class="input_area">

										<input type="tel" id="phone_number" name="t_phone_number"
											maxlength="20" value="${dto.getPhone_number()}">

									</div>

								</div>


								<!-- 이메일 -->
								<div class="form_row">

									<label for="email"> <fmt:message key="join.013"/> <em>*</em>
									</label>

									<div class="input_area email_verify">

										<div class="verify_input_row">
											<input type="email" id="email" name="t_email" maxlength="100"
												value="${dto.getEmail()}"> <input type="button" id="sendEmailBtn"
												value="<fmt:message key='join.030'/>" onclick="sendEmailCode()">
										</div>

										<div class="verify_code_row">
											<input type="text" id="email_code" name="t_email_code"
												maxlength="6" placeholder="<fmt:message key='join.027'/>"> <input
												type="button" id="emailCheckBtn" value="<fmt:message key='join.031'/>" onclick="checkEmailCode()">
										</div>

										<div id="emailVerifyResult" class="verify_result"><fmt:message key="myinfo.006"/></div>

										<span class="form_hint"><fmt:message key="myinfo.007"/></span>
									</div>

								</div>


								<!-- 차량번호 -->
								<div class="form_row">

									<label for="vehicle_number"> <fmt:message key="join.016"/> <em>*</em>
									</label>

									<div class="input_area">

										<input type="text" id="vehicle_number" name="t_vehicle_number"
											maxlength="20" value="${dto.getVehicle_number()}">

									</div>

								</div>


								<!-- 차량 종류 -->
								<div class="form_row">

									<label> <fmt:message key="join.018"/> <em>*</em>
									</label>

									<div class="input_area vehicle_type">

										<label class="radio_label"> <input type="radio"
											name="t_vehicle_type" value="N"
											${dto.getVehicle_type() eq 'N' ? 'checked' : ''}> <span>
												<fmt:message key="join.019"/> </span>

										</label> <label class="radio_label"> <input type="radio"
											name="t_vehicle_type" value="E"
											${dto.getVehicle_type() eq 'E' ? 'checked' : ''}> <span>
												<fmt:message key="join.020"/> </span>

										</label> <label class="radio_label"> <input type="radio"
											name="t_vehicle_type" value="D"
											${dto.getVehicle_type() eq 'D' ? 'checked' : ''}> <span>
												<fmt:message key="join.021"/> </span>

										</label>

									</div>

								</div>


								<!-- BUTTON -->
								<div class="form_buttons">

									<button type="button" class="withdraw_btn" onclick="exitId()"><fmt:message key="myinfo.008"/></button>

									<button type="button" class="withdraw_btn"
										onclick="movePage('Member','passwordUpdateForm')"><fmt:message key="myinfo.009"/></button>

									<button type="button" class="join_btn" onclick="goUpdate()">
										<fmt:message key="myinfo.010"/></button>

								</div>





							</form>

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
