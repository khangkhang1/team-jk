<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>비밀번호 변경 | 인천공항 주차예약</title>

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
							<h1>비밀번호 변경</h1>
							<p>현재 비밀번호를 확인한 뒤 새 비밀번호로 변경합니다.</p>
						</div>

						<div class="join_card">
							<div class="join_card_head">
								<h2>비밀번호 입력</h2>
							</div>

							<form class="join_form" name="mem">
								<input type="hidden" name="t_gubun" value="passwordUpdate">

								<div class="form_row">
									<label for="current_password">현재 비밀번호 <em>*</em></label>
									<div class="input_area">
										<input type="password" id="current_password"
											name="t_current_password" maxlength="70"
											autocomplete="current-password">
									</div>
								</div>

								<div class="form_row">
									<label for="new_password">새 비밀번호 <em>*</em></label>
									<div class="input_area">
										<input type="password" id="new_password"
											name="t_new_password" maxlength="70"
											autocomplete="new-password">
										<span class="form_hint">영문 소문자를 최소 1자 포함하여 6~16자로 입력해주세요.</span>
									</div>
								</div>

								<div class="form_row">
									<label for="new_password_confirm">새 비밀번호 확인 <em>*</em></label>
									<div class="input_area">
										<input type="password" id="new_password_confirm"
											name="t_new_password_confirm" maxlength="70"
											autocomplete="new-password">
									</div>
								</div>

								<div class="form_buttons">
									<button type="button" class="withdraw_btn"
										onclick="movePage('Member','myinfo')">취소</button>
									<button type="button" class="join_btn"
										onclick="goPasswordUpdate()">비밀번호 변경</button>
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
