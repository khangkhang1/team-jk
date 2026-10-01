<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>주차장 이용 안내 | 인천공항 주차예약</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/guide/parking_guide.css">
</head>
<body>

<%@include file="/common_header.jsp" %>

<main class="guidePage">

    <!-- 상단 안내 배너 -->
    <section class="guideHero">
        <div class="guideHeroInner">
            <span class="guideLabel">PARKING GUIDE</span>
            <h1>주차장 이용 안내</h1>
            <p>
                인천공항 제1여객터미널 주차장을<br>
                더욱 편리하게 이용하는 방법을 안내합니다.
            </p>

            <div class="guideHeroButtons">
                <a href="ParkingStatus" class="guideBtn guideBtnPrimary">
                    실시간 주차 현황 <span>→</span>
                </a>
                <a href="../index.jsp#parking" class="guideBtn guideBtnOutline">
                    이용 요금 안내 <span>→</span>
                </a>
            </div>
        </div>

        <div class="guideHeroIcon" aria-hidden="true">
            <div class="heroCircle">
                <span class="heroP">P</span>
                <span class="heroCar">🚘</span>
            </div>
            <div class="heroSmallCircle">P</div>
        </div>
    </section>


    <!-- 페이지 위치 -->
    <div class="guideBreadcrumb">
        <a href="../index.jsp">홈</a>
        <span>›</span>
        <span>교통 · 주차</span>
        <span>›</span>
        <strong>주차장 이용 안내</strong>
    </div>


    <!-- 주차장 종류 -->
    <section class="guideSection">
        <div class="sectionHeading">
            <span class="sectionEyebrow">PARKING TYPE</span>
            <h2>어떤 주차장을 이용할까요?</h2>
            <p>이용 목적과 주차 시간에 맞는 주차장을 선택해 보세요.</p>
        </div>

        <div class="parkingTypeGrid">

            <article class="parkingTypeCard shortParking">
                <div class="typeTop">
                    <span class="typeIcon">◷</span>
                    <span class="typeBadge">단시간 이용</span>
                </div>

                <h3>단기주차장</h3>
                <p class="typeDescription">
                    공항 방문이나 승객 배웅 등<br>
                    비교적 짧은 시간 주차할 때 이용합니다.
                </p>

                <div class="typeInfo">
                    <div>
                        <span class="infoLabel">추천 이용</span>
                        <strong>배웅 · 마중 · 단시간 방문</strong>
                    </div>
                    <div>
                        <span class="infoLabel">위치</span>
                        <strong>여객터미널 인근</strong>
                    </div>
                </div>
            </article>


            <article class="parkingTypeCard longParking">
                <div class="typeTop">
                    <span class="typeIcon">◷</span>
                    <span class="typeBadge">장시간 이용</span>
                </div>

                <h3>장기주차장</h3>
                <p class="typeDescription">
                    여행이나 출장 등으로<br>
                    장시간 차량을 주차할 때 이용합니다.
                </p>

                <div class="typeInfo">
                    <div>
                        <span class="infoLabel">추천 이용</span>
                        <strong>여행 · 출장 · 장시간 주차</strong>
                    </div>
                    <div>
                        <span class="infoLabel">위치</span>
                        <strong>여객터미널 외곽 주차 구역</strong>
                    </div>
                </div>
            </article>


        </div>
    </section>


    <!-- 이용 절차 -->
    <section class="guideProcessSection">
        <div class="sectionHeading">
            <span class="sectionEyebrow">HOW TO USE</span>
            <h2>주차장 이용 절차</h2>
            <p>아래 순서에 따라 편리하게 주차장을 이용해 보세요.</p>
        </div>

        <div class="processGrid">

            <article class="processCard">
                <div class="processNumber">01</div>
                <div class="processIcon">⌕</div>
                <h3>주차장 선택</h3>
                <p>
                    실시간 주차 현황을 확인하고<br>
                    목적에 맞는 주차 구역을 선택합니다.
                </p>
            </article>

            <div class="processArrow">→</div>

            <article class="processCard">
                <div class="processNumber">02</div>
                <div class="processIcon">▣</div>
                <h3>예약 및 입차</h3>
                <p>
                    예약주차장은 예약 조건을 확인하고<br>
                    안내된 절차에 따라 입차합니다.
                </p>
            </article>

            <div class="processArrow">→</div>

            <article class="processCard">
                <div class="processNumber">03</div>
                <div class="processIcon">🚘</div>
                <h3>차량 주차</h3>
                <p>
                    지정된 구역에 차량을 주차하고<br>
                    주차 위치를 기억해 둡니다.
                </p>
            </article>

            <div class="processArrow">→</div>

            <article class="processCard">
                <div class="processNumber">04</div>
                <div class="processIcon">₩</div>
                <h3>요금 정산 및 출차</h3>
                <p>
                    이용 요금을 확인하고 정산한 후<br>
                    출차 안내에 따라 이동합니다.
                </p>
            </article>

        </div>
    </section>


    <!-- 이용 전 확인 사항 -->
    <section class="guideSection noticeSection">
        <div class="sectionHeading">
            <span class="sectionEyebrow">CHECK LIST</span>
            <h2>이용 전 확인해 주세요</h2>
            <p>원활한 주차장 이용을 위해 다음 사항을 확인해 주세요.</p>
        </div>

        <div class="noticeGrid">

            <article class="noticeCard">
                <span class="noticeIcon">01</span>
                <div>
                    <h3>실시간 주차 현황 확인</h3>
                    <p>
                        주차 가능 대수는 시간에 따라 달라질 수 있으므로
                        출발 전에 주차 현황을 확인해 주세요.
                    </p>
                </div>
            </article>

            <article class="noticeCard">
                <span class="noticeIcon">02</span>
                <div>
                    <h3>예약 정보 확인</h3>
                    <p>
                        예약주차장을 이용하는 경우 예약 시간,
                        입차 가능 시간 및 이용 조건을 확인해 주세요.
                    </p>
                </div>
            </article>

            <article class="noticeCard">
                <span class="noticeIcon">03</span>
                <div>
                    <h3>주차 요금 확인</h3>
                    <p>
                        주차장 종류와 이용 조건에 따라 요금이 다를 수 있으므로
                        최신 요금 및 할인 조건을 확인해 주세요.
                    </p>
                </div>
            </article>

            <article class="noticeCard">
                <span class="noticeIcon">04</span>
                <div>
                    <h3>주차 위치 기억하기</h3>
                    <p>
                        주차 구역과 층, 주변 표지판을 확인해 두면
                        출차 시 차량을 찾기 편리합니다.
                    </p>
                </div>
            </article>

        </div>
    </section>


    <!-- 하단 바로가기 -->
    <section class="guideBottom">
        <div>
            <span class="sectionEyebrow">READY TO GO?</span>
            <h2>주차 계획을 시작해 보세요.</h2>
            <p>주차 현황을 확인하고 이용 계획에 맞는 주차장을 찾아보세요.</p>
        </div>

        <div class="guideBottomButtons">
            <a href="../index.jsp#reserve" class="guideBtn guideBtnDark">
                주차 예약하기 <span>→</span>
            </a>
        </div>
    </section>

</main>

<%@include file="../common_footer.jsp"%>

</body>
</html>