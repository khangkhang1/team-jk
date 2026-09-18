<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="ko">

<head>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>MY INFO | 인천공항 주차예약</title>

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

							<h1>마이페이지</h1>

							<p>회원정보를 확인하고 수정할 수 있습니다.</p>

						</div>


						<!-- MEMBER INFO CARD -->
						<div class="join_card">


							<!-- CARD HEADER -->
							<div class="join_card_head">

								<h2>회원정보</h2>

								<span> <em>*</em> 필수 입력사항
								</span>

							</div>


							<!-- FORM -->
							<form id="myInfoForm" class="join_form" name="mem">
								<input type="hidden" name="t_gubun"> 
								<input type="hidden" name="t_id" value="${dto.getMember_id()}">

								<!-- 아이디 -->
								<div class="form_row">

									<label for="member_id"> 아이디 </label>

									<div class="input_area">

										<input type="text" id="member_id" name="t_id"
											value="${dto.getMember_id()}" disabled> <span
											class="form_hint"> 아이디는 변경할 수 없습니다. </span>

									</div>

								</div>



								<!-- 이름 -->
								<div class="form_row">

									<label for="name"> 이름 <em>*</em>
									</label>

									<div class="input_area">

										<input type="text" id="name" name="t_name" maxlength="20"
											value="${dto.getName()}">

									</div>

								</div>


								<!-- 전화번호 -->
								<div class="form_row">

									<label for="phone_number"> 휴대전화 <em>*</em>
									</label>

									<div class="input_area">

										<input type="tel" id="phone_number" name="t_phone_number"
											maxlength="20" value="${dto.getPhone_number()}">

									</div>

								</div>


								<!-- 이메일 -->
								<div class="form_row">

									<label for="email"> 이메일 <em>*</em>
									</label>

									<div class="input_area email_verify">

										<div class="verify_input_row">
											<input type="email" id="email" name="t_email" maxlength="100"
												value="${dto.getEmail()}"> <input type="button" id="sendEmailBtn"
												value="인증번호 발송" onclick="sendEmailCode()">
										</div>

										<div class="verify_code_row">
											<input type="text" id="email_code" name="t_email_code"
												maxlength="6" placeholder="인증번호 6자리 입력"> <input
												type="button" id="emailCheckBtn" value="인증 확인" onclick="checkEmailCode()">
										</div>

										<div id="emailVerifyResult" class="verify_result">이메일을
											변경하면 인증이 필요합니다.</div>

										<span class="form_hint">기존 이메일과 다르게 변경하는 경우에만 인증이
											필요합니다.</span>
									</div>

								</div>


								<!-- 차량번호 -->
								<div class="form_row">

									<label for="vehicle_number"> 차량번호 <em>*</em>
									</label>

									<div class="input_area">

										<input type="text" id="vehicle_number" name="t_vehicle_number"
											maxlength="20" value="${dto.getVehicle_number()}">

									</div>

								</div>


								<!-- 차량 종류 -->
								<div class="form_row">

									<label> 차량 종류 <em>*</em>
									</label>

									<div class="input_area vehicle_type">

										<label class="radio_label"> <input type="radio"
											name="t_vehicle_type" value="N"
											${dto.getVehicle_type() eq 'N' ? 'checked' : ''}> <span>
												일반 차량 </span>

										</label> <label class="radio_label"> <input type="radio"
											name="t_vehicle_type" value="E"
											${dto.getVehicle_type() eq 'E' ? 'checked' : ''}> <span>
												전기차 </span>

										</label> <label class="radio_label"> <input type="radio"
											name="t_vehicle_type" value="D"
											${dto.getVehicle_type() eq 'D' ? 'checked' : ''}> <span>
												장애인 차량 </span>

										</label>

									</div>

								</div>


								<!-- BUTTON -->
								<div class="form_buttons">

									<button type="button" class="withdraw_btn" onclick="exitId()">회원
										탈퇴</button>

									<button type="button" class="withdraw_btn"
										onclick="movePage('Member','passwordUpdateForm')">비밀번호 변경</button>

									<button type="button" class="join_btn" onclick="goUpdate()">
										정보 수정</button>

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

						인천공항 주차예약 <small> INCHEON AIRPORT PARKING </small>

					</div>


					<div class="footer_links">

						<a href="#">이용약관</a> <a href="#">개인정보처리방침</a> <a href="#">사이트맵</a>

					</div>

				</div>


				<div class="footer_info">

					<p>제1여객터미널 주차예약 서비스 · 본 사이트는 팀프로젝트 목적으로 제작되었습니다.</p>

					<p>문의 : 제1여객터미널 주차상황실</p>

					<p class="copyright">Copyright © Parking Reservation Project.
						All rights reserved.</p>

				</div>

			</div>

		</footer>


	</div>

</body>

</html>
