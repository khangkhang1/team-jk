<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>회원가입 | 인천공항 주차예약</title>

    <link rel="stylesheet" href="../css/member_join.css">
</head>

<body>

<div class="wrap">

    <!-- HEADER -->
    <header class="header">

        <div class="header_inner">

            <a href="index3.html" class="logo">
                인천공항 주차예약
                <small>INCHEON AIRPORT PARKING</small>
            </a>

            <nav class="header_menu">

                <li>
                    <a href="#">교통 · 주차</a>

                    <div class="header_dropdown">
                        <a href="#">주차장 이용 안내</a>
                        <a href="#">주차 요금</a>
                        <a href="#">주차장 혼잡도</a>
                    </div>
                </li>

                <li>
                    <a href="#">주차 예약 조회</a>

                    <div class="header_dropdown">
                        <a href="#">예약 내역</a>
                        <a href="#">예약 확인</a>
                        <a href="#">예약 취소</a>
                        <a href="#">이용 내역</a>
                    </div>
                </li>

                <li>
                    <a href="#">공지 사항</a>

                    <div class="header_dropdown">
                        <a href="#">공지 사항</a>
                        <a href="#">자주 하는 질문</a>
                    </div>
                </li>

            </nav>

            <div class="header_right">
                <a href="#">로그인</a>
                <span>|</span>
                <a href="#" class="join_link">회원가입</a>
            </div>

            <button class="menu_btn">☰</button>

        </div>

    </header>


    <!-- MAIN -->
    <main class="main">

        <section class="join_section">

            <div class="join_container">

                <!-- PAGE TITLE -->
                <div class="page_title">

                    <span class="page_eyebrow">
                        MEMBER JOIN
                    </span>

                    <h1>회원가입</h1>

                    <p>
                        인천공항 주차예약 서비스를 이용하기 위해<br>
                        회원정보를 입력해주세요.
                    </p>

                </div>


                <!-- JOIN CARD -->
                <div class="join_card">

                    <div class="join_card_head">

                        <h2>회원정보 입력</h2>

                        <span>
                            <em>*</em> 필수 입력사항
                        </span>

                    </div>


                    <form class="join_form">


                        <!-- 아이디 -->
                        <div class="form_row">

                            <label for="member_id">
                                아이디
                                <em>*</em>
                            </label>

                            <div class="input_area">

                                <div class="input_button">

                                    <input
                                        type="text"
                                        id="member_id"
                                        name="member_id"
                                        maxlength="20"
                                        placeholder="아이디를 입력해주세요"
                                    >

                                    <button type="button">
                                        중복확인
                                    </button>

                                </div>

                                <span class="form_hint">
                                    영문 소문자, 숫자를 포함하여 4~20자로 입력해주세요.
                                </span>

                            </div>

                        </div>


                        <!-- 비밀번호 -->
                        <div class="form_row">

                            <label for="password">
                                비밀번호
                                <em>*</em>
                            </label>

                            <div class="input_area">

                                <input
                                    type="password"
                                    id="password"
                                    name="password"
                                    maxlength="70"
                                    placeholder="비밀번호를 입력해주세요"
                                >

                                <span class="form_hint">
                                    안전한 비밀번호를 입력해주세요.
                                </span>

                            </div>

                        </div>


                        <!-- 비밀번호 확인 -->
                        <div class="form_row">

                            <label for="password_check">
                                비밀번호 확인
                                <em>*</em>
                            </label>

                            <div class="input_area">

                                <input
                                    type="password"
                                    id="password_check"
                                    name="password_check"
                                    maxlength="70"
                                    placeholder="비밀번호를 다시 입력해주세요"
                                >

                            </div>

                        </div>


                        <!-- 이름 -->
                        <div class="form_row">

                            <label for="name">
                                이름
                                <em>*</em>
                            </label>

                            <div class="input_area">

                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    maxlength="20"
                                    placeholder="이름을 입력해주세요"
                                >

                            </div>

                        </div>


                        <!-- 전화번호 -->
                        <div class="form_row">

                            <label for="phone_number">
                                휴대전화
                                <em>*</em>
                            </label>

                            <div class="input_area">

                                <input
                                    type="tel"
                                    id="phone_number"
                                    name="phone_number"
                                    maxlength="20"
                                    placeholder="010-0000-0000"
                                >

                            </div>

                        </div>


                        <!-- 이메일 -->
                        <div class="form_row">

                            <label for="email">
                                이메일
                                <em>*</em>
                            </label>

                            <div class="input_area">

                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    maxlength="100"
                                    placeholder="이메일을 입력해주세요"
                                >

                            </div>

                        </div>


                        <!-- 차량번호 -->
                        <div class="form_row">

                            <label for="vehicle_number">
                                차량번호
                                <em>*</em>
                            </label>

                            <div class="input_area">

                                <input
                                    type="text"
                                    id="vehicle_number"
                                    name="vehicle_number"
                                    maxlength="20"
                                    placeholder="차량번호를 입력해주세요"
                                >

                                <span class="form_hint">
                                    예) 12가 3456
                                </span>

                            </div>

                        </div>


                        <!-- 차량 종류 -->
                        <div class="form_row">

                            <label for="vehicle_type">
                                차량 종류
                                <em>*</em>
                            </label>

                            <div class="input_area">

								<input type="radio" name="vehicle_type" class="vehicle_type" value="N">일반 차량
								<input type="radio" name="vehicle_type" class="vehicle_type" value="E">전기차
								<input type="radio" name="vehicle_type" class="vehicle_type" value="D">장애인
                            </div>

                        </div>


                        <!-- 약관 -->
                        <div class="agree_area">

                            <div class="agree_all">

                                <label>

                                    <input
                                        type="checkbox"
                                        id="agreeAll"
                                    >

                                    <span class="check_box"></span>

                                    <strong>
                                        전체 약관에 동의합니다.
                                    </strong>

                                </label>

                            </div>


                            <div class="agree_list">

                                <label>

                                    <input
                                        type="checkbox"
                                        class="agree required"
                                    >

                                    <span class="check_box"></span>

                                    <span>
                                        이용약관 동의
                                        <em>(필수)</em>
                                    </span>

                                    <a href="#">보기</a>

                                </label>


                                <label>

                                    <input
                                        type="checkbox"
                                        class="agree required"
                                    >

                                    <span class="check_box"></span>

                                    <span>
                                        개인정보 수집 및 이용 동의
                                        <em>(필수)</em>
                                    </span>

                                    <a href="#">보기</a>

                                </label>


                                <label>

                                    <input
                                        type="checkbox"
                                        class="agree"
                                    >

                                    <span class="check_box"></span>

                                    <span>
                                        마케팅 정보 수신 동의
                                        <small>(선택)</small>
                                    </span>

                                    <a href="#">보기</a>

                                </label>

                            </div>

                        </div>


                        <!-- BUTTON -->
                        <div class="form_buttons">

                            <button
                                type="button"
                                class="cancel_btn"
                            >
                                취소
                            </button>

                            <button
                                type="submit"
                                class="join_btn"
                            >
                                회원가입
                            </button>

                        </div>

                    </form>

                </div>


                <!-- LOGIN LINK -->
                <div class="login_link">

                    이미 회원이신가요?

                    <a href="#">
                        로그인
                    </a>

                </div>

            </div>

        </section>

    </main>


    <!-- FOOTER -->
    <footer class="footer">

        <div class="footer_inner">

            <div class="footer_top">

                <div class="footer_logo">

                    인천공항 주차예약

                    <small>
                        INCHEON AIRPORT PARKING
                    </small>

                </div>


                <div class="footer_links">

                    <a href="#">이용약관</a>
                    <a href="#">개인정보처리방침</a>
                    <a href="#">사이트맵</a>

                </div>

            </div>


            <div class="footer_info">

                <p>
                    제1여객터미널 주차예약 서비스 · 본 사이트는 팀프로젝트 목적으로 제작되었습니다.
                </p>

                <p>
                    문의 : 제1여객터미널 주차상황실
                </p>

                <p class="copyright">
                    Copyright © Parking Reservation Project. All rights reserved.
                </p>

            </div>

        </div>

    </footer>

</div>


<script>

    /* =====================================================
       전체 약관 동의
    ===================================================== */

    const agreeAll =
        document.getElementById("agreeAll");

    const agrees =
        document.querySelectorAll(".agree");


    agreeAll.addEventListener("change", function(){

        agrees.forEach(function(checkbox){

            checkbox.checked =
                agreeAll.checked;

        });

    });


    /* =====================================================
       개별 약관 변경
    ===================================================== */

    agrees.forEach(function(checkbox){

        checkbox.addEventListener("change", function(){

            const checkedCount =
                document.querySelectorAll(".agree:checked").length;

            agreeAll.checked =
                checkedCount === agrees.length;

        });

    });

</script>

</body>

</html>