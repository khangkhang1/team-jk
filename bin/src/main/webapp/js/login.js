// 로그인·회원가입 페이지 - 화면 초안, 전부 가상 데이터/클라이언트 로직만 존재. 서버 연동(세션·DB)은 이후 단계.

function showTab(tabName) {
	document.querySelectorAll('.memTab').forEach(function (btn) {
		btn.classList.toggle('active', btn.dataset.tab === tabName);
	});
	document.querySelectorAll('.memPanel').forEach(function (panel) {
		panel.classList.add('hidden');
	});
	var target = document.getElementById('panel' + tabName.charAt(0).toUpperCase() + tabName.slice(1));
	if (target) target.classList.remove('hidden');
}

document.querySelectorAll('.memTab').forEach(function (btn) {
	btn.addEventListener('click', function () {
		showTab(btn.dataset.tab);
	});
});

// URL 파라미터로 초기 탭 지정 (메인화면 로그인/회원가입 버튼에서 진입)
var urlParams = new URLSearchParams(window.location.search);
var initialTab = urlParams.get('tab');
if (initialTab && ['login', 'join'].indexOf(initialTab) !== -1) {
	showTab(initialTab);
}

// 편도 항공권 선택 시 안내
document.getElementById('joinRoundtripInput').addEventListener('change', function () {
	if (this.value === 'oneway') {
		alert('편도 항공권은 이 시스템을 이용하실 수 없습니다 (현장 이용을 안내해드립니다). 가입은 계속 진행하실 수 있습니다.');
	}
});

document.getElementById('loginSubmitBtn').addEventListener('click', function () {
	alert('로그인 처리는 다음 단계(서버 연동)에서 연결됩니다.\n지금은 화면 확인용 마이페이지로 이동합니다.');
	window.location.href = 'mypage.html';
});

document.getElementById('joinSubmitBtn').addEventListener('click', function () {
	var pw = document.getElementById('joinPwInput').value;
	var pwCheck = document.getElementById('joinPwCheckInput').value;
	if (pw !== pwCheck) {
		alert('비밀번호가 일치하지 않습니다.');
		return;
	}
	alert('회원가입 처리는 다음 단계(서버 연동)에서 연결됩니다.\n지금은 화면 확인용 로그인 탭으로 이동합니다.');
	showTab('login');
});
