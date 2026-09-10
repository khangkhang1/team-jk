<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="dto.LongTermParkingDto" %>
<%@ page import="dto.ShortTermParkingDto" %>

<!DOCTYPE html>
<html lang="ko">
<link rel="stylesheet" href="css/index1.css">

<head>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>인천공항 주차예약</title>

<style>

</style>

</head>


<body>

<div class="wrap">

<!-- HEADER -->
<header class="header">

    <div class="header_inner">

        <a href="#" class="logo">
            인천공항 주차예약
            <small>INCHEON AIRPORT PARKING</small>
        </a>

        <nav class="header_menu">

            <!-- 교통 · 주차 -->
            <li>
                <a href="#parking">교통 · 주차</a>

                <div class="header_dropdown">
                    <a href="#guide">주차장 이용 안내</a>
                    <a href="#parking">주차 요금</a>
                    <a href="#parking">주차장 혼잡도</a>
                </div>
            </li>

            <!-- 주차 예약 조회 -->
            <li>
                <a href="#reserve">주차 예약 조회</a>

                <div class="header_dropdown">
                    <a href="#reserve">예약 내역</a>
                    <a href="#reserve">예약 확인</a>
                    <a href="#reserve">예약 취소</a>
                    <a href="#reserve">이용 내역</a>
                </div>
            </li>

            <!-- 공지 사항 -->
            <li>
                <a href="#notice">공지 사항</a>

                <div class="header_dropdown">
                    <a href="#notice">공지 사항</a>
                    <a href="#notice">자주 하는 질문</a>
                </div>
            </li>

        </nav>

        <div class="header_right">
            <a href="#">로그인</a>
            <span>|</span>
            <a href="#">회원가입</a>
        </div>

        <button class="menu_btn" aria-label="메뉴">☰</button>

    </div>

</header>



<!-- MAIN -->

<main class="main">


<!-- HERO -->

<section class="hero">

<div class="hero_inner">

<div class="hero_eyebrow">
INCHEON AIRPORT PARKING SERVICE
</div>

<h1>
편리한 여행의 시작,
<br>
주차부터 간편하게
</h1>

<p>
인천공항 주차장의 실시간 현황을 확인하고
<br>
원하는 날짜와 시간에 주차 공간을 미리 예약하세요.
</p>

<span class="hero_badge">
실시간 주차 현황 제공 · 간편 예약 서비스
</span>

</div>

</section>


<!-- QUICK MENU -->

<div class="quick_wrap">

<div class="quick_menu">


<a href="#reserve" class="quick_item">

<div class="quick_icon">P</div>

<div class="quick_text">

<strong>주차 예약</strong>

<span>원하는 날짜와 시간으로 예약</span>

</div>

</a>


<a href="#notice" class="quick_item">

<div class="quick_icon">✓</div>

<div class="quick_text">

<strong>예약 조회</strong>

<span>예약 내역을 간편하게 확인</span>

</div>

</a>


<a href="#parking" class="quick_item">

<div class="quick_icon">⌖</div>

<div class="quick_text">

<strong>주차장 현황</strong>

<span>실시간 주차 가능 공간 확인</span>

</div>

</a>


<a href="#guide" class="quick_item">

<div class="quick_icon">i</div>

<div class="quick_text">

<strong>이용 안내</strong>

<span>주차장 이용 방법 안내</span>

</div>

</a>


</div>

</div>


<div class="container">


<!-- RESERVATION -->

<section class="section" id="reserve">

<div class="section_head">

<div>

<h2>주차 예약 · 실시간 주차맵</h2>

<p>
입차 시간을 선택하고 조회하면 해당 시간대의 주차 현황을 주차맵에 표시합니다.
</p>

</div>

</div>


<div class="parking_control_box">


<div class="reserve_control">


<div class="reserve_tabs">

<button class="active" type="button">
제1여객터미널
</button>

<button type="button">
제2여객터미널
</button>

</div>


<div class="control_grid">


<div class="field">

<label>주차장</label>

<select id="parkingTerminal">

<option>제1여객터미널 주차장</option>
<option>제2여객터미널 주차장</option>

</select>

</div>


<div class="field">

<label>입차일</label>

<input type="date" id="startDate">

</div>


<div class="field">

<label>입차 시간</label>

<input type="time" id="entryTime" value="09:00">

</div>


<div class="field">

<label>출차일</label>

<input type="date" id="endDate">

</div>


<div class="search_button_wrap">

        <button type="button" class="realtime_btn" onclick="returnToRealtime()">
            실시간 주차 현황 조회
        </button>
        <button type="button" class="search_btn" onclick="updateParkingByTime()">
            주차 현황 조회
        </button>


    </div>


</div>


<div class="selected_time">

<span>조회 기준</span>

<strong id="selectedTime">
오늘 09:00 기준
</strong>

<small>
선택한 시간대에 맞는 주차 현황을 조회합니다.
</small>

</div>


</div>


<!-- MAP HEADER -->

<div class="map_header">

<div>

<h3>제1여객터미널 전체 주차맵</h3>

<p>
구역별 색상으로 주차 가능 상태를 확인할 수 있습니다.
</p>

</div>


<div class="map_legend">

<span>
<i class="legend available"></i>
여유
</span>

<span>
<i class="legend normal"></i>
보통
</span>

<span>
<i class="legend busy"></i>
혼잡
</span>

</div>

</div>


<!-- PARKING MAP -->
<div class="parking_map_real">

    <!-- 주차장 실제 이미지 -->
    <img
        src="images/parking_map.png"
        alt="인천공항 제1여객터미널 주차장"
        class="parking_map_image"
    >


    <!-- 클릭 가능한 SVG -->
<svg
    class="parking_svg"
    viewBox="0 0 1600 900"
    preserveAspectRatio="none">

    <!-- ================================================= -->
    <!-- P2 -->
    <!-- ================================================= -->
    <path
        id="parkingP2"
        class="parking_area"
        d="
            M430 445
            Q490 440 500 425
            L510 415
            Q520 390 535 375
            L545 370
            Q565 360 590 355
            L760 355
            Q765 355 765 355
            L765 455
            Q765 515 765 515
            L420 515
            Q390 515 395 505
            Z
        "
        onclick="selectParkingZone('P2')"
    >

    </path>

    <text
        class="parking_area_text"
        x="625"
        y="450">
        P2
    </text>


    <!-- ================================================= -->
    <!-- P1 -->
    <!-- ================================================= -->
    <path
        id="parkingP1"
        class="parking_area"
        d="
            M810 380
            Q810 355 835 355
            L1010 355
            Q1045 370 1065 390
            L1075 420
            Q1090 430 1115 440
            L1150 445
            Q1170 460 1178 490
            L1175 510
            Q1165 515 1140 515
            L845 515
            Q810 515 810 515
            Z
        "
        onclick="selectParkingZone('P1')"
    >

    </path>

    <text
        class="parking_area_text"
        x="960"
        y="450">
        P1
    </text>


    <!-- ================================================= -->
    <!-- P4 -->
    <!-- ================================================= -->
    <path
        id="parkingP4"
        class="parking_area"
        d="
            M400 570
            Q430 545 435 545
            L755 545
            Q765 550 765 575
            L765 635
            Q760 640 755 640
            L435 640
            Q415 640 415 640
            Z
        "
        onclick="selectParkingZone('P4')"
    >

    </path>

    <text
        class="parking_area_text"
        x="600"
        y="605">
        P4
    </text>


    <!-- ================================================= -->
    <!-- P3 -->
    <!-- ================================================= -->
    <path
        id="parkingP3"
        class="parking_area"
        d="
            M815 545
            L1140 545
            Q1175 560 1175 580
            L1175 615
            Q1140 640 1130 640
            L815 640
            Z
        "
        onclick="selectParkingZone('P3')"
    >

    </path>

    <text
        class="parking_area_text"
        x="960"
        y="600">
        P3
    </text>


    <!-- ================================================= -->
    <!-- P5 -->
    <!-- ================================================= -->
    <path
        id="parkingP5"
        class="parking_area"
        d="
            M440 690
            Q460 690 475 690
            L735 690
            Q760 690 760 690
            L760 765
            Q760 780 760 780
            L475 780
            Q440 780 440 780
            Z
        "
        onclick="selectParkingZone('P5')"
    >

    </path>

    <text
        class="parking_area_text"
        x="600"
        y="740">
        P5
    </text>


    <!-- ================================================= -->
    <!-- P9 -->
    <!-- ================================================= -->
    <path
        id="parkingP9"
        class="parking_area"
        d="
            M600 235
            L700 195
            L725 240
            L620 245
            Z
        "
        onclick="selectParkingZone('P9')"
    >

    </path>

    <text
        class="parking_area_text"
        x="680"
        y="225">
        P9
    </text>


    <!-- ================================================= -->
    <!-- P8 -->
    <!-- ================================================= -->
    <path
        id="parkingP8"
        class="parking_area"
        d="
            M890 195
            L990 240
            L860 240
            Z
        "
        onclick="selectParkingZone('P8')"
    >

    </path>

    <text
        class="parking_area_text"
        x="900"
        y="225">
        P8
    </text>


    <!-- ================================================= -->
    <!-- P7 -->
    <!-- ================================================= -->
    <path
        id="parkingP7"
        class="parking_area"
        d="
            M608 262
            L738 284
            L735 310
            L625 310
            Z
        "
        onclick="selectParkingZone('P7')"
    >

    </path>

    <text
        class="parking_area_text"
        x="665"
        y="295">
        P7
    </text>


    <!-- ================================================= -->
    <!-- P6 -->
    <!-- ================================================= -->
    <path
        id="parkingP6"
        class="parking_area"
        d="
            M850 285
            L980 265
            L970 312
            L845 312
            Z
        "
        onclick="selectParkingZone('P6')"
    >

    </path>

    <text
        class="parking_area_text"
        x="910"
        y="295">
        P6
    </text>


    <!-- ================================================= -->
    <!-- 선택 정보 연결선 + 정보 박스 -->
    <!-- ================================================= -->
    <g
        id="parkingInfoLayer"
        class="parking_info_layer"
        style="display:none;">

        <!-- 점선 연결선 -->
        <polyline
            id="parkingInfoLine"
            class="parking_info_line"
            points=""
        />


        <!-- 정보 박스 -->
        <g id="parkingInfoBox">

            <rect
                class="parking_info_box_bg"
                x="0"
                y="0"
                width="260"
                height="220"
                rx="16"
            />

            <!-- 구역 번호 -->
            <text
                id="parkingInfoName"
                class="parking_info_name"
                x="20"
                y="38">
                P9
            </text>


            <!-- 주차장 종류 -->
            <text
                id="parkingInfoType"
                class="parking_info_type"
                x="20"
                y="64">
                단기주차장
            </text>


            <!-- 혼잡도 -->
            <circle
                id="parkingInfoStatusCircle"
                class="parking_info_status_circle"
                cx="29"
                cy="94"
                r="7"
            />

            <text
                id="parkingInfoStatus"
                class="parking_info_status"
                x="45"
                y="100">
                여유
            </text>


            <!-- 주차 가능 -->
            <text
                class="parking_info_count_label"
                x="20"
                y="132">
                현재 주차 가능
            </text>

            <text
                id="parkingInfoCount"
                class="parking_info_count"
                x="20"
                y="160">
                600대
            </text>


            <!-- 예약 버튼 -->
            <g
                class="parking_reserve_button"
                onclick="reserveParking()">

                <rect
                    x="20"
                    y="177"
                    width="220"
                    height="30"
                    rx="8"
                />

                <text
                    x="130"
                    y="198"
                    text-anchor="middle">
                    예약하기
                </text>

            </g>

        </g>

    </g>

</svg>

</div>


<!-- 선택된 주차장 상세정보 -->

<div
    class="parking_selected_info"
    id="parkingSelectedInfo"
>

    <div class="parking_selected_top">

        <div>
            <div
                class="parking_selected_name"
                id="parkingSelectedName"
            >
                P1
            </div>

            <div
                class="parking_selected_type"
                id="parkingSelectedType"
            >
                장기주차장
            </div>
        </div>

    </div>


    <div class="parking_selected_data">

        <div class="parking_selected_item">

            <span>
                현재 주차 가능
            </span>

            <strong id="parkingSelectedCount">
                2,700대
            </strong>

        </div>


        <div class="parking_selected_item">

            <span>
                주차 상태
            </span>

            <strong
                id="parkingSelectedStatus"
                class="parking_status_available"
            >
                여유
            </strong>

        </div>

    </div>

</div>

<!-- MAP BOTTOM -->

<div class="map_bottom">


<div>

<span class="pin_icon">⌖</span>

<span id="mapSelectedText">
주차 구역을 클릭하면 상세 현황을 확인할 수 있습니다.
</span>

</div>


<div class="map_total">
    전체 가능
    <strong id="totalParking">0</strong>
    <b>대</b>
</div>


<button
class="refresh_btn"
type="button"
onclick="refreshParking()">

<span class="refresh_icon">↻</span>

새로고침

</button>


</div>



<!-- PARKING CONGESTION -->
<div class="parking_congestion">

    <div class="parking_congestion_head">
        <div>
            <h3>주차장별 실시간 혼잡도</h3>
            <p id="parkingCongestionTime">
                현재 실시간 기준
            </p>
        </div>
    </div>

    <div class="parking_congestion_grid">

        <!-- P1 -->
        <div class="parking_congestion_card" id="congestionCardP1">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P1</span>
                    <strong class="congestion_type">장기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP1">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP1">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP1"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP1">-</strong>
            </div>
        </div>


        <!-- P2 -->
        <div class="parking_congestion_card" id="congestionCardP2">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P2</span>
                    <strong class="congestion_type">장기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP2">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP2">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP2"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP2">-</strong>
            </div>
        </div>


        <!-- P3 -->
        <div class="parking_congestion_card" id="congestionCardP3">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P3</span>
                    <strong class="congestion_type">장기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP3">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP3">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP3"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP3">-</strong>
            </div>
        </div>


        <!-- P4 -->
        <div class="parking_congestion_card" id="congestionCardP4">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P4</span>
                    <strong class="congestion_type">장기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP4">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP4">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP4"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP4">-</strong>
            </div>
        </div>


        <!-- P5 -->
        <div class="parking_congestion_card" id="congestionCardP5">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P5</span>
                    <strong class="congestion_type">장기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP5">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP5">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP5"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP5">-</strong>
            </div>
        </div>


        <!-- P6 -->
        <div class="parking_congestion_card" id="congestionCardP6">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P6</span>
                    <strong class="congestion_type">단기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP6">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP6">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP6"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP6">-</strong>
            </div>
        </div>


        <!-- P7 -->
        <div class="parking_congestion_card" id="congestionCardP7">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P7</span>
                    <strong class="congestion_type">단기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP7">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP7">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP7"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP7">-</strong>
            </div>
        </div>


        <!-- P8 -->
        <div class="parking_congestion_card" id="congestionCardP8">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P8</span>
                    <strong class="congestion_type">단기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP8">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP8">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP8"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP8">-</strong>
            </div>
        </div>


        <!-- P9 -->
        <div class="parking_congestion_card" id="congestionCardP9">
            <div class="congestion_card_top">
                <div>
                    <span class="congestion_zone">P9</span>
                    <strong class="congestion_type">단기주차장</strong>
                </div>
                <span class="congestion_status" id="congestionStatusP9">
                    -
                </span>
            </div>

            <div class="congestion_count">
                <strong id="congestionCountP9">-</strong>
                <span>대 가능</span>
            </div>

            <div class="congestion_bar">
                <div class="congestion_fill" id="congestionFillP9"></div>
            </div>

            <div class="congestion_percent">
                <span>주차면</span>
                <strong id="congestionPercentP9">-</strong>
            </div>
        </div>

    </div>

</div>







</div>

</section>


<!-- NOTICE -->

<section class="section" id="notice">

<div class="info_grid">


<div class="panel">


<div class="section_head">

<div>

<h2>공지사항</h2>

<p>
주차 서비스의 새로운 소식을 알려드립니다.
</p>

</div>

<a href="#" class="more">
더보기 →
</a>

</div>


<ul class="notice_list">


<li>

<span class="notice_tag">
공지
</span>

<strong>
주차예약 서비스 이용 안내
</strong>

<span class="notice_date">
2026.09.02
</span>

</li>


<li>

<span class="notice_tag">
안내
</span>

<strong>
실시간 주차 현황 업데이트 안내
</strong>

<span class="notice_date">
2026.09.01
</span>

</li>


<li>

<span class="notice_tag">
공지
</span>

<strong>
제1여객터미널 주차장 이용 안내
</strong>

<span class="notice_date">
2026.08.28
</span>

</li>


<li>

<span class="notice_tag">
안내
</span>

<strong>
주차예약 시스템 점검 안내
</strong>

<span class="notice_date">
2026.08.25
</span>

</li>


</ul>

</div>


<!-- GUIDE -->

<div class="panel" id="guide">


<div class="section_head">

<div>

<h2>이용 안내</h2>

<p>
처음 이용하셔도 쉽게 예약할 수 있습니다.
</p>

</div>

</div>


<div class="guide_list">


<div class="guide_item">

<span class="guide_num">
01
</span>

<strong>
주차장 선택
</strong>

<span>
원하는 터미널과 주차구역을 선택하세요.
</span>

</div>


<div class="guide_item">

<span class="guide_num">
02
</span>

<strong>
예약 정보 입력
</strong>

<span>
입·출차 날짜와 시간을 입력하세요.
</span>

</div>


<div class="guide_item">

<span class="guide_num">
03
</span>

<strong>
예약 확인
</strong>

<span>
예약 가능 여부를 확인하고 신청하세요.
</span>

</div>


<div class="guide_item">

<span class="guide_num">
04
</span>

<strong>
주차 이용
</strong>

<span>
예약 시간에 맞춰 편리하게 이용하세요.
</span>

</div>


</div>

</div>


</div>

</section>


</div>

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

<a href="#">
이용약관
</a>

<a href="#">
개인정보처리방침
</a>

<a href="#">
사이트맵
</a>

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

<%
	List<LongTermParkingDto> longTermList =
	        (List<LongTermParkingDto>) request.getAttribute("longTermList");
	
	List<ShortTermParkingDto> shortTermList =
	        (List<ShortTermParkingDto>) request.getAttribute("shortTermList");
%>

const parkingData={
	    "09":{
	        P1:50,
	        P2:750,
	        P3:850,
	        P4:250,
	        P5:1000,
	        P6:200,
	        P7:30,
	        P8:800,
	        P9:600
	    },

	    "12":{
	        P1:46,
	        P2:799,
	        P3:1450,
	        P4:80,
	        P5:850,
	        P6:180,
	        P7:180,
	        P8:520,
	        P9:244
	    },

	    "15":{
	        P1:41,
	        P2:755,
	        P3:1200,
	        P4:150,
	        P5:888,
	        P6:333,
	        P7:254,
	        P8:32,
	        P9:467
	    },

	    "18":{
	        P1:56,
	        P2:900,
	        P3:800,
	        P4:25,
	        P5:500,
	        P6:250,
	        P7:280,
	        P8:170,
	        P9:320
	    },

	    "21":{
	        P1:380,
	        P2:804,
	        P3:1400,
	        P4:350,
	        P5:800,
	        P6:400,
	        P7:420,
	        P8:500,
	        P9:500
	    }
	};


	function setParking(data){

	    Object.keys(data).forEach(function(zone){

	        const countElement=
	            document.getElementById("parkingCount"+zone);

	        if(countElement){
	            countElement.textContent=
	                data[zone].toLocaleString()+"대";
	        }

	    });

	    const total=
	        Object.values(data).reduce(
	            (sum,count)=>sum+count,
	            0
	        );

	    document.getElementById("totalParking").textContent=
	        total.toLocaleString();
	}


	function updateParkingByTime(){

		realtimeMode=false;

	    const time=
	        document.getElementById("entryTime").value||"09:00";

	        
	    const hour=
	        parseInt(time.split(":")[0],10);

	    const key=
	        hour<=10
	            ?"09"
	            :hour<=13
	                ?"12"
	                :hour<=16
	                    ?"15"
	                    :hour<=19
	                        ?"18"
	                        :"21";

	    setParking(parkingData[key]);

	    document.getElementById("selectedTime").textContent=
	        "선택 시간 "+time+" 기준";

	    /*
	     * 현재 선택되어 있는 주차구역도
	     * 새로운 시간의 데이터로 갱신
	     */
	    const selected=
	        document.querySelector(".parking_area.active");

	    if(selected){
	        const zone=
	            selected.id.replace("parking","");
	        renderParkingZoneInfo(zone);
	    }
	}
	
	function returnToRealtime(){

	    /*
	     * 실시간 모드로 변경
	     */
	    realtimeMode=true;

	    /*
	     * 선택 시간 표시 초기화
	     */
	    document.getElementById("selectedTime").textContent=
	        "현재 실시간 기준";

	    /*
	     * 최신 API 데이터 다시 조회
	     */
	     
	    updateParkingCongestion();
	     
	    refreshParking();
	}
	
	


	function refreshParking(){

	    const btn=
	        document.querySelector(".refresh_btn");

	    if(!btn){
	        return;
	    }

	    btn.classList.add("loading");
	    btn.disabled=true;

	    /*
	     * 미래 시간 조회 상태라면
	     * API를 다시 호출하지 않고
	     * 현재 더미 데이터를 그대로 유지한다.
	     */
	    if(!realtimeMode){

	        setTimeout(function(){

	            const selected=
	                document.querySelector(
	                    ".parking_area.active"
	                );

	            if(selected){

	                const zone=
	                    selected.id.replace(
	                        "parking",
	                        ""
	                    );

	                renderParkingZoneInfo(zone);
	            }

	            btn.classList.remove("loading");
	            btn.disabled=false;

	        },600);

	        return;
	    }

	    /*
	     * 현재 시간 상태라면
	     * 서버에서 최신 API 데이터를 가져온다.
	     */
	    fetch("parkingStatus?refresh=true")

	        .then(function(response){

	            if(!response.ok){

	                throw new Error(
	                    "주차 정보를 불러오지 못했습니다."
	                );
	            }

	            return response.json();
	        })

	        .then(function(result){

	            /*
	             * 기존 parkingZoneData를
	             * 최신 API 데이터로 교체
	             */
	            const newData={};

	            /*
	             * 장기주차장 P1~P5
	             */
	            result.longTerm.forEach(
	                function(dto){

	                    const zone=
	                        dto.parkLotNo;

	                    newData[zone]={

	                        type:"장기주차장",

	                        totalCount:
	                            dto.totalCount,

	                        occupiedCount:
	                            dto.occupiedCount,

	                        availableCount:
	                            dto.availableCount,

	                        occupancyRate:
	                            dto.occupancyRate,

	                        status:
	                            dto.congestion
	                    };
	                }
	            );

	            /*
	             * 단기주차장 P6~P9
	             *
	             * 중요:
	             * ParkingService에서 이미
	             * P6~P9로 변환해서 보내므로
	             * 별도의 zoneMap이 필요하지 않다.
	             */
	            result.shortTerm.forEach(
	                function(dto){

	                    const zone=
	                        dto.parkZoneNo;

	                    newData[zone]={

	                        type:"단기주차장",

	                        totalCount:
	                            dto.totalCount,

	                        occupiedCount:
	                            dto.occupiedCount,

	                        availableCount:
	                            dto.availableCount,

	                        occupancyRate:
	                            dto.occupancyRate,

	                        status:
	                            dto.congestion
	                    };
	                }
	            );

	            /*
	             * 최신 데이터로 교체
	             */
	            parkingZoneData=
	                newData;

	            /*
	             * 전체 주차 가능 대수 갱신
	             */
	            setRealtimeParking();

	            /*
	             * 현재 선택된 구역 정보 갱신
	             */
	            const selected=
	                document.querySelector(
	                    ".parking_area.active"
	                );

	            if(selected){

	                const zone=
	                    selected.id.replace(
	                        "parking",
	                        ""
	                    );

	                renderParkingZoneInfo(zone);
	            }

	        })

	        .catch(function(error){

	            console.error(
	                "주차 정보 새로고침 오류:",
	                error
	            );

	            alert(
	                "주차 정보를 새로고침하지 못했습니다."
	            );

	        })

	        .finally(function(){

	            btn.classList.remove("loading");
	            btn.disabled=false;

	        });
	}


	/* =========================================================
	   주차구역 데이터
	========================================================= */

	
	<%
    longTermList =
            (List<LongTermParkingDto>) request.getAttribute("longTermList");

    shortTermList =
            (List<ShortTermParkingDto>) request.getAttribute("shortTermList");
%>

let parkingZoneData={

<%
    if(longTermList != null){
        for(LongTermParkingDto dto : longTermList){
%>

    "<%= dto.getParkLotNo() %>":{
        type:"장기주차장",
        totalCount:<%= dto.getTotalCount() %>,
        occupiedCount:<%= dto.getOccupiedCount() %>,
        availableCount:<%= dto.getAvailableCount() %>,
        occupancyRate:<%= dto.getOccupancyRate() %>,
        status:"<%= dto.getCongestion() %>"
    },

<%
        }
    }

    if(shortTermList != null){
        for(ShortTermParkingDto dto : shortTermList){
%>

    "<%= dto.getParkZoneNo() %>":{
        type:"단기주차장",
        totalCount:<%= dto.getTotalCount() %>,
        occupiedCount:<%= dto.getOccupiedCount() %>,
        availableCount:<%= dto.getAvailableCount() %>,
        occupancyRate:<%= dto.getOccupancyRate() %>,
        status:"<%= dto.getCongestion() %>"
    },

<%
        }
    }
%>

};

let realtimeMode=true;

	function setRealtimeParking(){
	
	    let total=0;
	
	    Object.keys(parkingZoneData).forEach(function(zone){
	
	        total+=
	            parkingZoneData[zone].availableCount;
	    });
	
	    document.getElementById(
	        "totalParking"
	    ).textContent=
	        total.toLocaleString();
	    
	    updateParkingCongestion();
	}


	
	/*
	const parkingZoneData={

	    P1:{
	        type:"장기주차장",
	        count:2700,
	        status:"여유"
	    },

	    P2:{
	        type:"장기주차장",
	        count:2500,
	        status:"여유"
	    },

	    P3:{
	        type:"장기주차장",
	        count:1700,
	        status:"혼잡"
	    },

	    P4:{
	        type:"장기주차장",
	        count:1800,
	        status:"보통"
	    },

	    P5:{
	        type:"장기주차장",
	        count:1000,
	        status:"여유"
	    },

	    P6:{
	        type:"단기주차장",
	        count:500,
	        status:"혼잡"
	    },

	    P7:{
	        type:"단기주차장",
	        count:500,
	        status:"여유"
	    },

	    P8:{
	        type:"단기주차장",
	        count:600,
	        status:"보통"
	    },

	    P9:{
	        type:"단기주차장",
	        count:600,
	        status:"여유"
	    }

	};
	*/

	/* =========================================================
	   정보창 위치
	========================================================= */

	const infoPosition={

	    P1:{
	        x:1200,
	        y:370,
	        line:[
	            [1140,445],
	            [1190,445],
	            [1200,410]
	        ]
	    },

	    P2:{
	        x:130,
	        y:365,
	        line:[
	            [430,445],
	            [370,445],
	            [330,410]
	        ]
	    },

	    P3:{
	        x:1200,
	        y:540,
	        line:[
	            [1140,590],
	            [1190,590],
	            [1200,580]
	        ]
	    },

	    P4:{
	        x:130,
	        y:540,
	        line:[
	            [435,590],
	            [370,590],
	            [330,580]
	        ]
	    },

	    P5:{
	        x:130,
	        y:675,
	        line:[
	            [440,735],
	            [370,735],
	            [330,715]
	        ]
	    },

	    P6:{
	        x:1010,
	        y:245,
	        line:[
	            [970,290],
	            [1000,290],
	            [1010,285]
	        ]
	    },

	    P7:{
	        x:330,
	        y:245,
	        line:[
	            [625,290],
	            [560,290],
	            [590,285]
	        ]
	    },

	    P8:{
	        x:1010,
	        y:120,
	        line:[
	            [950,220],
	            [1000,220],
	            [1010,190]
	        ]
	    },

	    P9:{
	        x:330,
	        y:120,
	        line:[
	            [650,225],
	            [560,225],
	            [590,190]
	        ]
	    }

	};


	/* =========================================================
	   현재 시간대 주차 데이터 가져오기
	========================================================= */

	function getCurrentParkingData(){

	    const timeElement=
	        document.getElementById("entryTime");

	    const time=
	        timeElement && timeElement.value
	            ?timeElement.value
	            :"09:00";

	    const hour=
	        parseInt(time.split(":")[0],10);

	    const key=
	        hour<=10
	            ?"09"
	            :hour<=13
	                ?"12"
	                :hour<=16
	                    ?"15"
	                    :hour<=19
	                        ?"18"
	                        :"21";

	    return parkingData[key];

	}
	
	
	/* =========================================================
	   주차장별 혼잡도 카드 갱신
	========================================================= */

	function updateParkingCongestion(){

	    const zones=[
	        "P1",
	        "P2",
	        "P3",
	        "P4",
	        "P5",
	        "P6",
	        "P7",
	        "P8",
	        "P9"
	    ];

	    zones.forEach(function(zone){

	        const data=parkingZoneData[zone];

	        const countElement=
	            document.getElementById(
	                "congestionCount"+zone
	            );

	        const statusElement=
	            document.getElementById(
	                "congestionStatus"+zone
	            );

	        const fillElement=
	            document.getElementById(
	                "congestionFill"+zone
	            );

	        const percentElement=
	            document.getElementById(
	                "congestionPercent"+zone
	            );

	        const cardElement=
	            document.getElementById(
	                "congestionCard"+zone
	            );

	        if(
	            !countElement||
	            !statusElement||
	            !fillElement||
	            !percentElement||
	            !cardElement
	        ){
	            return;
	        }


	        /* =================================================
	           실시간
	        ================================================= */

	        if(realtimeMode){

	            if(!data){

	                countElement.textContent="-";
	                statusElement.textContent="정보 없음";
	                percentElement.textContent="-";

	                statusElement.className=
	                    "congestion_status no_data";

	                fillElement.className=
	                    "congestion_fill no_data";

	                fillElement.style.width="0%";

	                cardElement.classList.add("no_data");

	                return;
	            }


	            const available=
	                Number(data.availableCount)||0;

	            const total=
	                Number(data.totalCount)||0;

	            const occupancy=
	                total>0
	                    ?Math.min(
	                        100,
	                        Math.max(
	                            0,
	                            Number(data.occupancyRate)||0
	                        )
	                    )
	                    :0;

	            const status=
	                data.status||"정보 없음";


	            countElement.textContent=
	                available.toLocaleString();


	            statusElement.textContent=
	                status;


	            percentElement.textContent=
	                occupancy.toFixed(1)+"%";


	            const statusClass=
	                getCongestionClass(status);


	            statusElement.className=
	                "congestion_status "+
	                statusClass;


	            fillElement.className=
	                "congestion_fill "+
	                statusClass;


	            fillElement.style.width=
	                occupancy+"%";


	            cardElement.classList.remove("no_data");

	            return;
	        }


	        /* =================================================
	           미래 시간 더미 데이터
	        ================================================= */

	        const currentData=
	            getCurrentParkingData();

	        if(!currentData||currentData[zone]===undefined){

	            countElement.textContent="-";
	            statusElement.textContent="정보 없음";
	            percentElement.textContent="-";

	            statusElement.className=
	                "congestion_status no_data";

	            fillElement.className=
	                "congestion_fill no_data";

	            fillElement.style.width="0%";

	            return;
	        }


	        const available=
	            Number(currentData[zone])||0;


	        /*
	         * 미래 데이터는 현재 API의 전체 주차면을 기준으로
	         * 예상 점유율을 계산
	         */
	        const total=
	            data&&Number(data.totalCount)
	                ?Number(data.totalCount)
	                :0;


	        let occupancy=0;

	        if(total>0){

	            occupancy=
	                Math.min(
	                    100,
	                    Math.max(
	                        0,
	                        ((total-available)/total)*100
	                    )
	                );

	        }


	        const status=
	            getCongestionStatus(
	                available,
	                total
	            );


	        countElement.textContent=
	            available.toLocaleString();


	        statusElement.textContent=
	            status;


	        percentElement.textContent=
	            occupancy.toFixed(1)+"%";


	        const statusClass=
	            getCongestionClass(status);


	        statusElement.className=
	            "congestion_status "+
	            statusClass;


	        fillElement.className=
	            "congestion_fill "+
	            statusClass;


	        fillElement.style.width=
	            occupancy+"%";

	    });


	    /*
	     * 조회 기준 시간 표시
	     */

	    const timeElement=
	        document.getElementById("entryTime");

	    const time=
	        timeElement&&timeElement.value
	            ?timeElement.value
	            :"09:00";

	    document.getElementById(
	        "parkingCongestionTime"
	    ).textContent=
	        realtimeMode
	            ?"현재 실시간 기준"
	            :"선택 시간 "+time+" 기준";
	}


	/* =========================================================
	   혼잡도 상태 클래스
	========================================================= */

	function getCongestionClass(status){

	    if(status==="여유"){
	        return "available";
	    }

	    if(status==="보통"){
	        return "normal";
	    }

	    if(status==="혼잡"){
	        return "busy";
	    }

	    if(status==="매우 혼잡"){
	        return "very_busy";
	    }

	    return "no_data";
	}


	/* =========================================================
	   미래 시간 혼잡도 계산
	========================================================= */

	function getCongestionStatus(available,total){

	    if(total<=0){
	        return "정보 없음";
	    }

	    const rate=
	        available/total;

	    if(rate>=0.40){
	        return "여유";
	    }

	    if(rate>=0.20){
	        return "보통";
	    }

	    if(rate>=0.05){
	        return "혼잡";
	    }

	    return "매우 혼잡";
	}
	


	/* =========================================================
	   주차 상태 원 색상
	========================================================= */

	function getStatusColor(status){

	    if(status==="여유"){
	        return "#22c55e";
	    }

	    if(status==="보통"){
	        return "#facc15";
	    }

	    if(status==="혼잡"){
	        return "#ef4444";
	    }

	    if(status==="매우 혼잡"){
	        return "#b91c1c";
	    }

	    return "#94a3b8";
	}
	
	function getDummyStatus(zone,key){

	    const totalCount=
	        parkingZoneData[zone]?.totalCount||0;

	    const currentCount=
	        parkingData[key]?.[zone]||0;

	    if(totalCount===0){
	        return "보통";
	    }

	    const ratio=
	        currentCount/totalCount;

	    if(ratio>=0.30){
	        return "여유";
	    }

	    if(ratio>=0.15){
	        return "보통";
	    }

	    if(ratio>=0.05){
	        return "혼잡";
	    }

	    return "매우 혼잡";
	}


	/* =========================================================
	   미래 시간대 혼잡도
	========================================================= */

	/*
	function getDummyStatus(zone,key){

	    const dummyStatus={

	        "09":{
	            P1:"여유",
	            P2:"여유",
	            P3:"보통",
	            P4:"보통",
	            P5:"여유",
	            P6:"혼잡",
	            P7:"여유",
	            P8:"보통",
	            P9:"여유"
	        },

	        "12":{
	            P1:"보통",
	            P2:"보통",
	            P3:"혼잡",
	            P4:"혼잡",
	            P5:"보통",
	            P6:"혼잡",
	            P7:"보통",
	            P8:"혼잡",
	            P9:"보통"
	        },

	        "15":{
	            P1:"혼잡",
	            P2:"혼잡",
	            P3:"혼잡",
	            P4:"혼잡",
	            P5:"혼잡",
	            P6:"매우 혼잡",
	            P7:"혼잡",
	            P8:"매우 혼잡",
	            P9:"혼잡"
	        },

	        "18":{
	            P1:"매우 혼잡",
	            P2:"매우 혼잡",
	            P3:"매우 혼잡",
	            P4:"매우 혼잡",
	            P5:"혼잡",
	            P6:"매우 혼잡",
	            P7:"혼잡",
	            P8:"매우 혼잡",
	            P9:"혼잡"
	        },

	        "21":{
	            P1:"보통",
	            P2:"보통",
	            P3:"혼잡",
	            P4:"혼잡",
	            P5:"보통",
	            P6:"혼잡",
	            P7:"보통",
	            P8:"혼잡",
	            P9:"보통"
	        }

	    };

	    return dummyStatus[key] && dummyStatus[key][zone]
	        ?dummyStatus[key][zone]
	        :"보통";
	}
	*/
	
	
	/* =========================================================
	   P1~P9 상태 원 갱신
	========================================================= */

	function updateParkingByTime(){
		
		realtimeMode=false;
		
	    const time=
	        document.getElementById("entryTime").value||
	        "09:00";

	    const hour=
	        parseInt(
	            time.split(":")[0],
	            10
	        );

	    const key=
	        hour<=10
	            ?"09"
	            :hour<=13
	                ?"12"
	                :hour<=16
	                    ?"15"
	                    :hour<=19
	                        ?"18"
	                        :"21";


	    setParking(parkingData[key]);


	    document.getElementById(
	        "selectedTime"
	    ).textContent=
	        "선택 시간 "+
	        time+
	        " 기준";


	    /*
	     * 현재 선택되어 있는 주차구역도
	     * 새로운 시간의 데이터로 갱신
	     */

	    const selected=
	        document.querySelector(
	            ".parking_area.active"
	        );

	    if(selected){

	        const zone=
	            selected.id.replace(
	                "parking",
	                ""
	            );

	        /*
	         * selectParkingZone()이 아니라
	         * 정보만 다시 그린다.
	         *
	         * 같은 구역을 다시 클릭해서
	         * 선택이 해제되는 문제 방지
	         */

	        renderParkingZoneInfo(zone);

	    }
	    updateParkingCongestion();

	}


	/* =========================================================
	   주차구역 클릭
	========================================================= */

	function selectParkingZone(zone){

	    const selectedArea=
	        document.getElementById("parking"+zone);

	    if(!selectedArea){
	        return;
	    }


	    /*
	     * 이미 선택된 구역을 다시 클릭하면 해제
	     */

	    if(selectedArea.classList.contains("active")){

	        selectedArea.classList.remove("active");

	        const infoLayer=
	            document.getElementById(
	                "parkingInfoLayer"
	            );

	        if(infoLayer){
	            infoLayer.style.display="none";
	        }

	        const mapSelectedText=
	            document.getElementById(
	                "mapSelectedText"
	            );

	        if(mapSelectedText){

	            mapSelectedText.textContent=
	                "주차구역을 선택해주세요.";
	            
	        }

	        return;
	    }


	    /*
	     * 기존 선택 해제
	     */

	    document.querySelectorAll(
	        ".parking_area"
	    ).forEach(function(area){

	        area.classList.remove("active");

	    });


	    /*
	     * 새로운 구역 선택
	     */

	    selectedArea.classList.add("active");


	    /*
	     * 정보창 표시
	     */

	    renderParkingZoneInfo(zone);

	}


	/* =========================================================
	   선택된 주차구역 정보 표시
	========================================================= */

	function renderParkingZoneInfo(zone){

	    const data=
	        parkingZoneData[zone];

	    if(!data){
	        return;
	    }


	    const position=
	        infoPosition[zone];

	    if(!position){
	        return;
	    }


	    /*
	     * 현재 선택 시간의 실제 주차 가능 대수
	     */

	    let currentCount;

	    if(realtimeMode){

	        currentCount=
	            data.availableCount;

	    }else{

	        const currentData=
	            getCurrentParkingData();

	        currentCount=
	            currentData &&
	            currentData[zone] !== undefined
	                ?currentData[zone]
	                :data.availableCount;
	    }


	    /*
	     * 정보창 요소
	     */

	    const infoLayer=
	        document.getElementById(
	            "parkingInfoLayer"
	        );

	    const infoBox=
	        document.getElementById(
	            "parkingInfoBox"
	        );

	    const infoLine=
	        document.getElementById(
	            "parkingInfoLine"
	        );


	    /*
	     * 정보창 위치
	     */

	    infoBox.setAttribute(
	        "transform",
	        "translate("+
	        position.x+
	        " "+
	        position.y+
	        ")"
	    );


	    /*
	     * 점선 연결
	     */

	    infoLine.setAttribute(
	        "points",
	        position.line
	            .map(function(point){

	                return point[0]+","+point[1];

	            })
	            .join(" ")
	    );


	    /*
	     * 구역 번호
	     */

	    document.getElementById(
	        "parkingInfoName"
	    ).textContent=zone;


	    /*
	     * 주차장 종류
	     */

	    document.getElementById(
	        "parkingInfoType"
	    ).textContent=data.type;


	    /*
	     * 주차 가능 대수
	     */

	    document.getElementById(
	        "parkingInfoCount"
	    ).textContent=
	        currentCount.toLocaleString()+
	        "대";


	    /*
	     * 혼잡도
	     */
	    let currentStatus;

	    if(realtimeMode){

	        currentStatus=
	            data.status;

	    }else{

	        const timeElement=
	            document.getElementById("entryTime");

	        const time=
	            timeElement&&timeElement.value
	                ?timeElement.value
	                :"09:00";

	        const hour=
	            parseInt(
	                time.split(":")[0],
	                10
	            );

	        const key=
	            hour<=10
	                ?"09"
	                :hour<=13
	                    ?"12"
	                    :hour<=16
	                        ?"15"
	                        :hour<=19
	                            ?"18"
	                            :"21";

	        currentStatus=
	            getDummyStatus(zone,key);
	    }


	    /*
	     * 혼잡도 텍스트
	     */
	    const statusElement=
	        document.getElementById(
	            "parkingInfoStatus"
	        );

	    statusElement.textContent=
	        currentStatus;


	    /*
	     * 혼잡도 동그라미 색상
	     */
	    const statusCircle=
	        document.getElementById(
	            "parkingInfoStatusCircle"
	        );

	    statusCircle.style.fill=
	        getStatusColor(currentStatus);


	    /*
	     * 정보창 표시
	     */

	    infoLayer.style.display="block";


	    /*
	     * 지도 아래 선택 안내문이 있다면 갱신
	     */

	    const mapSelectedText=
	        document.getElementById(
	            "mapSelectedText"
	        );

	    if(mapSelectedText){

	        mapSelectedText.textContent=
	            zone+
	            " · 현재 주차 가능 "+
	            currentCount.toLocaleString()+
	            "대";

	    }

	}


	/* =========================================================
	   예약하기
	========================================================= */

	function reserveParking(){
	    const selected=
	        document.querySelector(
	            ".parking_area.active"
	        );

	    if(!selected){
	        return;
	    }

	    const zone=
	        selected.id.replace(
	            "parking",
	            ""
	        );

	    alert(
	        zone+
	        " 주차구역 예약 페이지로 이동합니다."
	    );
	}


	/* =================================================
	   HEADER SCROLL
	================================================= */

	const header=document.querySelector(".header");

	function updateHeader(){
	    if(!header){
	        return;
	    }

	    header.classList.toggle(
	        "scrolled",
	        window.scrollY>40
	    );
	}

	window.addEventListener("scroll",updateHeader);

	updateHeader();



	const today=new Date();

	const todayStr=
	    today.getFullYear()
	    +"-"
	    +String(today.getMonth()+1).padStart(2,"0")
	    +"-"
	    +String(today.getDate()).padStart(2,"0");

	document.getElementById("startDate").value=todayStr;

	const tomorrow=
	    new Date(today.getTime()+86400000);

	document.getElementById("endDate").value=
	    tomorrow.getFullYear()
	    +"-"
	    +String(tomorrow.getMonth()+1).padStart(2,"0")
	    +"-"
	    +String(tomorrow.getDate()).padStart(2,"0");

	setRealtimeParking();

</script>


</body>

</html>