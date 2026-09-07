// 마이페이지 - 화면 초안, 예약 내역은 전부 고정 mock 데이터. DB 연동은 이후 단계.

document.querySelectorAll('.secondaryBtn').forEach(function (btn) {
	btn.addEventListener('click', function () {
		var label = btn.textContent.trim();
		if (label.indexOf('연장') !== -1) {
			alert('예약 연장 팝업은 다음 단계에서 연결됩니다. (몇 분까지 연장 가능한지 계산해서 보여줄 예정)');
		} else {
			window.location.href = 'reservation.html';
		}
	});
});
