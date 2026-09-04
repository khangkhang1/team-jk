<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>주차장 안내 지도</title>
  <style>
    .map-container {
      position: relative;
      display: inline-block;
    }
    .map-container img {
      max-width: 100%;
      height: auto;
      display: block;
    }
  </style>
</head>
<body>

  <div class="map-container">
    <!-- src 속성에 실제 이미지 파일 경로 또는 URL을 넣으세요 -->
    <img src="img/주차선택 맵이미지.jpg" usemap="#parking-map" alt="인천공항 주차장 지도">
    
    <map name="parking-map">
      <!-- 1. 제1여객터미널 / 교통센터 상단 -->
      <area shape="poly" coords="250,90, 480,80, 620,130, 600,200, 260,200" href="https://example.com/terminal1" alt="제1여객터미널 및 교통센터" title="제1여객터미널 및 교통센터">
      
      <!-- 2. 단기주차장 H 구역 -->
      <area shape="rect" coords="330,220, 420,265" href="https://example.com/short-term-h" alt="단기주차장 H" title="단기주차장 H">
      
      <!-- 3. 단기주차장 A 구역 -->
      <area shape="rect" coords="500,220, 590,265" href="https://example.com/short-term-a" alt="단기주차장 A" title="단기주차장 A">
      
      <!-- 4. 단기주차장 C 구역 -->
      <area shape="rect" coords="330,265, 420,310" href="https://example.com/short-term-c" alt="단기주차장 C" title="단기주차장 C">
      
      <!-- 5. 단기주차장 D 구역 -->
      <area shape="rect" coords="500,265, 590,310" href="https://example.com/short-term-d" alt="단기주차장 D" title="단기주차장 D">
      
      <!-- 6. 장기주차장 (서측/P2 구역) -->
      <area shape="poly" coords="220,330, 480,330, 480,550, 200,550" href="https://example.com/long-term-west" alt="장기주차장 (서측)" title="장기주차장 (서측)">
      
      <!-- 7. 장기주차장 (동측/P1 구역) -->
      <area shape="poly" coords="490,330, 670,330, 670,550, 490,550" href="https://example.com/long-term-east" alt="장기주차장 (동측)" title="장기주차장 (동측)">
      
      <!-- 8. P3 장기주차장 (카드전용) -->
      <area shape="rect" coords="490,580, 670,670" href="https://example.com/p3-card" alt="P3 장기주차장" title="P3 장기주차장">
      
      <!-- 9. P4 상주직원 주차장 -->
      <area shape="rect" coords="250,580, 480,670" href="https://example.com/p4-staff" alt="P4 상주직원 주차장" title="P4 상주직원 주차장">
      
      <!-- 10. P5 예약주차장 -->
      <area shape="rect" coords="250,730, 480,820" href="https://example.com/p5-reservation" alt="P5 예약주차장" title="P5 예약주차장">
    </map>
  </div>

</body>
</html>