<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="member_menu">

	<p class="member_menu_title">MEMBER</p>

	<ul>

		<!-- 로그아웃 상태 -->
		<c:if test="${empty sessionId}">

			<li class="${apple_gubun eq 'login' ? 'active' : ''}">
				<a href="javascript:movePage('Member','login')">LOGIN</a>
			</li>

			<li class="${apple_gubun eq 'join' ? 'active' : ''}">
				<a href="javascript:movePage('Member','join')">JOIN</a>
			</li>

			<li class="${apple_gubun eq 'findPassword' ? 'active' : ''}">
				<a href="javascript:movePage('Member','findPassword')">FIND PASSWORD</a>
			</li>

		</c:if>


		<!-- 로그인 상태 -->
		<c:if test="${not empty sessionId}">

			<li class="${(apple_gubun eq 'myinfo' or apple_gubun eq 'memberUpdateForm' or apple_gubun eq 'passwordUpdateForm') ? 'active' : ''}">
				<a href="javascript:movePage('Member','myinfo')">MY INFORMATION</a>
			</li>

			<li class="${apple_gubun eq 'myreservation' ? 'active' : ''}">
				<a href="javascript:movePage('Member','myreservation')">MY RESERVATION</a>
			</li>

		</c:if>

	</ul>

</div>
