<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>인천공항 주차예약 - 로그인</title>
<script type="text/javascript" src="js/jquery-1.8.1.min.js"></script>
<script src="js/common.js"></script>
<script src="js/member.js"></script>
<link rel="stylesheet" href="css/member_login.css">


</head>

<body>
	<form name="go">
		<input type="hidden" name="t_gubun">
	</form>

	<!-- HEADER -->
	<header class="header">

		<div class="header_inner">

			<a href="index.html" class="logo"> <span class="logo_main">인천공항
					주차예약</span> <span class="logo_sub">INCHEON AIRPORT PARKING</span>
			</a>

			<nav class="gnb">

				<a href="#">교통 · 주차</a> <a href="#">주차 예약 조회</a> <a href="#">공지
					사항</a>

			</nav>

			<div class="header_right">

				<a href="Member" class="active">로그인</a> <span class="divider">|</span>
				<a href="javascript:movePage('Member','join')">회원가입</a>

			</div>

		</div>

	</header>


	<!-- MAIN -->
	<main class="login_page">

		<div class="login_container">

			<!-- PAGE TITLE -->
			<div class="page_title">

				<span class="title_eng">MEMBER LOGIN</span>

				<h1>로그인</h1>

				<p>
					인천공항 주차예약 서비스를 이용하시려면<br> 로그인해주세요.
				</p>

			</div>


			<!-- LOGIN CARD -->
			<section class="login_card">

				<h2>회원 로그인</h2>

				<form id="loginForm" name="mem">
					<input type="hidden" name="t_gubun">

					<!-- ID -->
					<div class="input_group">

						<label for="member_id"> 아이디 </label> <input type="text"
							id="member_id" name="t_id" maxlength="20"
							placeholder="아이디를 입력해주세요" autocomplete="username" autofocus >

						<p class="error_message" id="idError"></p>

					</div>


					<!-- PASSWORD -->
					<div class="input_group">

						<label for="password"> 비밀번호 </label> <input type="password"
							id="password" name="t_password" maxlength="70"
							placeholder="비밀번호를 입력해주세요" autocomplete="current-password">

						<p class="error_message" id="passwordError"></p>

					</div>


					<!-- LOGIN BUTTON -->
					<input type="button" onclick="memberLogin()" class="login_btn" value="로그인">


					<!-- LOGIN MENU -->
					<div class="login_menu">

						<a href="#">아이디 찾기</a> <span>|</span> <a href="#">비밀번호 찾기</a> <span>|</span>

						<a href="javascript:movePage('Member','join')">회원가입</a>

					</div>

				</form>

			</section>


			<!-- INFO -->
			<div class="login_info">

				<div class="info_icon">!</div>

				<div class="info_text">
					<strong>안내</strong>
					<p>
						주차 예약 및 예약 조회 서비스는<br> 로그인 후 이용하실 수 있습니다.
					</p>
				</div>

			</div>

		</div>

	</main>


	<!-- FOOTER -->
	<footer class="footer">

		<div class="footer_inner">

			<div class="footer_logo">
				<span class="logo_main">인천공항 주차예약</span> <span class="logo_sub">INCHEON
					AIRPORT PARKING</span>
			</div>

			<div class="footer_info">

				<p>인천국제공항 주차예약 서비스</p>

				<p class="copyright">© INCHEON AIRPORT PARKING. All Rights
					Reserved.</p>

			</div>

		</div>

	</footer>


	<script src="js/member.js"></script>

</body>
</html>
```
