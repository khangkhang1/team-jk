// 공지사항·FAQ 페이지 - 화면 초안, 전부 정적 텍스트/가상 데이터. 실제 공지 등록(관리자 CRUD)은 이후 단계.

document.querySelectorAll('.ntcTab').forEach(function (btn) {
	btn.addEventListener('click', function () {
		document.querySelectorAll('.ntcTab').forEach(function (b) { b.classList.remove('active'); });
		btn.classList.add('active');
		document.querySelectorAll('.ntcPanel').forEach(function (p) { p.classList.add('hidden'); });
		var target = document.getElementById('panel' + btn.dataset.tab.charAt(0).toUpperCase() + btn.dataset.tab.slice(1));
		if (target) target.classList.remove('hidden');
	});
});
