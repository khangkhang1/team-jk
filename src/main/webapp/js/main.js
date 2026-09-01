// 메인 화면(Index.html) - airport.kr 페이지 구조를 그대로 복제한 안내형 화면으로 개편.
// 지도/사이드바는 제거하고 표+안내문 형태로 바꿈. 실제 좌석 선택은 각 구역의 [선택] 버튼을 눌러
// reservation.html(주차맵 페이지)로 들어가서 진행한다.
// 화면 초안 단계 - 아직 안 만든 기능은 전부 안내 alert만 뜸.

// 헤더/본문의 아직 안 만든 링크·버튼 - 공통 안내 처리
document.querySelectorAll('.acStubLink').forEach(function (el) {
	el.addEventListener('click', function (e) {
		e.preventDefault();
		var label = (el.title || el.textContent).trim();
		alert(label + ' 화면은 다음 단계에서 연결됩니다.');
	});
});

// 터미널 탭 - 지금은 1터미널만 서비스. 2터미널은 안내만.
document.querySelectorAll('.acTermTab').forEach(function (tab) {
	tab.addEventListener('click', function () {
		if (tab.dataset.term === '2') {
			alert('제2 여객터미널 서비스는 준비중입니다. 현재는 제1 여객터미널만 이용 가능합니다.');
			return;
		}
		document.querySelectorAll('.acTermTab').forEach(function (t) { t.classList.remove('active'); });
		tab.classList.add('active');
	});
});
