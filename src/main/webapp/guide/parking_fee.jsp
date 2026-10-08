<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>주차요금 안내 | 인천공항 주차예약</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/guide/parking_fee.css">
</head>
<body>

<jsp:include page="../common_header.jsp"/>

<main class="feePage">

    <!-- 상단 배너 -->
    <section class="feeHero">
        <div class="feeHeroInner">
            <span class="feeLabel">PARKING FEE GUIDE</span>
            <h1>주차요금 안내</h1>
            <p>
                주차장 종류별 요금과 예상 주차 비용을 확인하고<br>
                합리적인 주차 계획을 세워보세요.
            </p>

            <div class="feeHeroButtons">
                <a href="#feeTable" class="feeBtn feeBtnPrimary">
                    요금표 확인 <span>↓</span>
                </a>
                <a href="#feeCalculator" class="feeBtn feeBtnOutline">
                    예상 요금 계산
                </a>
            </div>
        </div>

        <div class="feeHeroVisual" aria-hidden="true">
            <div class="feeVisualCircle">
                <span class="feeVisualWon">₩</span>
                <span class="feeVisualCar">🚘</span>
            </div>
            <div class="feeVisualBadge">FEE</div>
        </div>
    </section>


    <!-- 브레드크럼 -->
    <div class="feeBreadcrumb">
        <a href="../index.jsp">홈</a>
        <span>›</span>
        <span>이용 안내</span>
        <span>›</span>
        <strong>주차요금 안내</strong>
    </div>


    <!-- 요금 요약 -->
    <section class="feeSection">
        <div class="feeSectionHeading">
            <span class="feeEyebrow">PARKING FEE</span>
            <h2>주차장별 요금 안내</h2>
            <p>인천공항 제1여객터미널의 일반 승용차 기준 요금입니다.</p>
        </div>

        <div class="feeSummaryGrid">

            <article class="feeSummaryCard shortFeeCard">
                <div class="feeCardTop">
                    <span class="feeCardIcon">◷</span>
                    <span class="feeCardBadge">단시간 이용</span>
                </div>

                <h3>단기주차장</h3>
                <p class="feeCardDescription">
                    배웅, 마중 등 짧은 시간 주차할 때 이용합니다.
                </p>

                <div class="feeMainPrice">
                    <strong>2,400원</strong>
                    <span>/ 시간</span>
                </div>

                <div class="feeCardDetail">
                    <div>
                        <span>기본 요금</span>
                        <strong>30분 1,200원</strong>
                    </div>
                    <div>
                        <span>추가 요금</span>
                        <strong>15분당 600원</strong>
                    </div>
                    <div>
                        <span>1일 최대</span>
                        <strong>24,000원</strong>
                    </div>
                </div>
            </article>


            <article class="feeSummaryCard longFeeCard">
                <div class="feeCardTop">
                    <span class="feeCardIcon">◷</span>
                    <span class="feeCardBadge">장시간 이용</span>
                </div>

                <h3>장기주차장</h3>
                <p class="feeCardDescription">
                    여행이나 출장 등 장시간 주차할 때 이용합니다.
                </p>

                <div class="feeMainPrice">
                    <strong>1,000원</strong>
                    <span>/ 시간</span>
                </div>

                <div class="feeCardDetail">
                    <div>
                        <span>기본 요금</span>
                        <strong>시간당 1,000원</strong>
                    </div>
                    <div>
                        <span>무료 이용</span>
                        <strong>최초 10분</strong>
                    </div>
                    <div>
                        <span>1일 최대</span>
                        <strong>9,000원</strong>
                    </div>
                </div>
            </article>

        </div>

        <div class="feeNotice">
            <span class="feeNoticeIcon">!</span>
            <p>
                <strong>요금 안내</strong>
                위 요금은 일반 승용차 기준이며, 실제 요금은 입출차 시각과
                차량 구분, 할인 적용 여부 등에 따라 달라질 수 있습니다.
            </p>
        </div>
    </section>


    <!-- 상세 요금표 -->
    <section class="feeTableSection" id="feeTable">
        <div class="feeSectionHeading">
            <span class="feeEyebrow">FEE TABLE</span>
            <h2>주차요금 상세표</h2>
            <p>단기주차장과 장기주차장의 요금 기준을 비교해 보세요.</p>
        </div>

        <div class="feeTableWrap">
            <table class="feeTable">
                <thead>
                    <tr>
                        <th>구분</th>
                        <th>단기주차장</th>
                        <th>장기주차장</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <th>최초 10분</th>
                        <td>무료</td>
                        <td>무료</td>
                    </tr>
                    <tr>
                        <th>기본 요금</th>
                        <td>30분 1,200원</td>
                        <td>시간당 1,000원</td>
                    </tr>
                    <tr>
                        <th>추가 요금</th>
                        <td>15분당 600원</td>
                        <td>시간당 1,000원</td>
                    </tr>
                    <tr class="feeTableHighlight">
                        <th>1일 최대 요금</th>
                        <td>24,000원</td>
                        <td>9,000원</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <p class="feeTableCaption">
            ※ 공식 요금 및 할인 기준은 변경될 수 있으므로 이용 전 최신 공지사항을 확인해 주세요.
        </p>

        <a
            href="https://www.airport.kr/ap_ko/969/subview.do"
            target="_blank"
            rel="noopener noreferrer"
            class="officialFeeLink">
            <span>인천국제공항 공식 주차요금 확인</span>
            <span>↗</span>
        </a>
    </section>


    <!-- 예상 주차요금 계산기 -->
    <section class="feeCalculatorSection" id="feeCalculator">
        <div class="feeSectionHeading">
            <span class="feeEyebrow">FEE CALCULATOR</span>
            <h2>예상 주차요금 계산</h2>
            <p>주차장 종류와 예상 이용 시간을 입력해 보세요.</p>
        </div>

        <div class="feeCalculatorBox">

            <div class="feeCalculatorForm">

                <div class="feeFormGroup">
                    <label for="parkingType">주차장 종류</label>
                    <select id="parkingType">
                        <option value="short">단기주차장</option>
                        <option value="long">장기주차장</option>
                    </select>
                </div>

                <div class="feeFormGroup">
                    <label>예상 주차 시간</label>
                    <div class="feeTimeInputs">
                        <div>
                            <input type="number" id="parkingHours"
                                   min="0" max="720" value="2"
                                   inputmode="numeric">
                            <span>시간</span>
                        </div>
                        <div>
                            <input type="number" id="parkingMinutes"
                                   min="0" max="59" value="0"
                                   inputmode="numeric">
                            <span>분</span>
                        </div>
                    </div>
                </div>

                <button type="button" id="calculateFeeBtn" class="feeCalculateBtn">
                    예상 요금 계산하기 <span>→</span>
                </button>

                <p class="feeCalculatorHelp">
                    예상 금액이며 실제 정산 금액과 차이가 발생할 수 있습니다.
                </p>

            </div>


            <div class="feeCalculatorResult">
                <span class="feeResultLabel">ESTIMATED PARKING FEE</span>
                <p class="feeResultTitle">예상 주차요금</p>

                <div class="feeResultPrice">
                    <strong id="estimatedFee">4,800</strong>
                    <span>원</span>
                </div>

                <div class="feeResultDivider"></div>

                <div class="feeResultRow">
                    <span>선택한 주차장</span>
                    <strong id="resultParkingType">단기주차장</strong>
                </div>

                <div class="feeResultRow">
                    <span>이용 시간</span>
                    <strong id="resultParkingTime">2시간 0분</strong>
                </div>

                <p class="feeResultMessage" id="feeResultMessage" aria-live="polite">
                    입력한 이용 시간에 대한 예상 요금입니다.
                </p>
            </div>

        </div>
    </section>


    <!-- 할인 및 정산 안내 -->
    <section class="feeSection feeExtraSection">
        <div class="feeSectionHeading">
            <span class="feeEyebrow">BEFORE YOU PARK</span>
            <h2>요금 정산 전 확인 사항</h2>
            <p>출차 전에 아래 사항을 확인해 주세요.</p>
        </div>

        <div class="feeExtraGrid">

            <article class="feeExtraCard">
                <div class="feeExtraNumber">01</div>
                <h3>주차장 종류 확인</h3>
                <p>
                    단기주차장과 장기주차장은 요금 체계가 다릅니다.
                    이용 목적과 예상 주차 시간에 맞춰 선택해 주세요.
                </p>
            </article>

            <article class="feeExtraCard">
                <div class="feeExtraNumber">02</div>
                <h3>할인 대상 확인</h3>
                <p>
                    경차, 장애인, 국가유공자 등 할인 대상과 적용 조건은
                    공식 안내에서 확인해 주세요.
                </p>
            </article>

            <article class="feeExtraCard">
                <div class="feeExtraNumber">03</div>
                <h3>출차 전 요금 정산</h3>
                <p>
                    사전 무인 정산기나 공항 공식 앱 등 이용 가능한
                    정산 방법을 확인하고 출차해 주세요.
                </p>
            </article>

        </div>
    </section>


    <!-- 하단 이동 영역 -->
    <section class="feeBottom">
        <div>
            <span class="feeEyebrow">READY TO PARK?</span>
            <h2>주차 현황을 확인해 보세요.</h2>
            <p>주차 가능 대수를 확인하고 편리하게 주차 계획을 세워보세요.</p>
        </div>

        <div class="feeBottomButtons">
            <a href="../index.jsp#parking" class="feeBtn feeBtnLight">
                주차 현황 확인 <span>→</span>
            </a>
            <a href="../index.jsp#reserve" class="feeBtn feeBtnDark">
                주차 예약하기 <span>→</span>
            </a>
        </div>
    </section>

</main>

<jsp:include page="../common_footer.jsp"/>


<script>
(function(){

    const parkingType = document.getElementById("parkingType");
    const parkingHours = document.getElementById("parkingHours");
    const parkingMinutes = document.getElementById("parkingMinutes");
    const calculateBtn = document.getElementById("calculateFeeBtn");

    const estimatedFee = document.getElementById("estimatedFee");
    const resultParkingType = document.getElementById("resultParkingType");
    const resultParkingTime = document.getElementById("resultParkingTime");
    const feeResultMessage = document.getElementById("feeResultMessage");

    function calculateParkingFee(type, totalMinutes){

        if(totalMinutes <= 10){
            return 0;
        }

        let fee = 0;

        if(type === "short"){

            if(totalMinutes <= 30){
                fee = 1200;
            }else{
                fee = 1200 + Math.ceil((totalMinutes - 30) / 15) * 600;
            }

            const days = Math.ceil(totalMinutes / 1440);

            fee = Math.min(fee, days * 24000);

        }else{

            const chargeableMinutes = totalMinutes - 10;

            fee = Math.ceil(chargeableMinutes / 60) * 1000;

            const days = Math.ceil(totalMinutes / 1440);

            fee = Math.min(fee, days * 9000);
        }

        return fee;
    }


    function updateFee(){

        const hours = Number(parkingHours.value);
        const minutes = Number(parkingMinutes.value);

        if(
            parkingHours.value === "" ||
            parkingMinutes.value === "" ||
            !Number.isFinite(hours) ||
            !Number.isFinite(minutes) ||
            !Number.isInteger(hours) ||
            !Number.isInteger(minutes) ||
            hours < 0 ||
            hours > 720 ||
            minutes < 0 ||
            minutes > 59
        ){
            alert("주차 시간을 올바르게 입력해 주세요.");
            return;
        }

        const totalMinutes = hours * 60 + minutes;
        const type = parkingType.value;

        const fee = calculateParkingFee(type, totalMinutes);

        estimatedFee.textContent = fee.toLocaleString("ko-KR");

        resultParkingType.textContent =
            type === "short" ? "단기주차장" : "장기주차장";

        resultParkingTime.textContent =
            hours + "시간 " + minutes + "분";

        if(totalMinutes <= 10){
            feeResultMessage.textContent =
                "최초 10분 무료 기준으로 계산한 예상 금액입니다.";
        }else{
            feeResultMessage.textContent =
                "일반 승용차 기준으로 계산한 예상 금액입니다. 할인 및 실제 입출차 시각에 따라 달라질 수 있습니다.";
        }
    }


    calculateBtn.addEventListener("click", updateFee);

    parkingType.addEventListener("change", updateFee);

    parkingHours.addEventListener("input", function(){
        if(parkingHours.value !== "" && parkingMinutes.value !== ""){
            updateFee();
        }
    });

    parkingMinutes.addEventListener("input", function(){
        if(parkingHours.value !== "" && parkingMinutes.value !== ""){
            updateFee();
        }
    });

    updateFee();

})();
</script>

</body>
</html>