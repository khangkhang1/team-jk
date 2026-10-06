document.addEventListener('DOMContentLoaded', function () {
	var page = document.getElementById('recoveryPage');
	if (!page) return;

	var form = document.getElementById('recoveryForm');
	var mode = page.getAttribute('data-mode');
	var endpoint = page.getAttribute('data-endpoint');
	var email = document.getElementById('recoveryEmail');
	var memberId = document.getElementById('recoveryMemberId');
	var code = document.getElementById('recoveryCode');
	var result = document.getElementById('recoveryResult');
	var codeArea = document.getElementById('recoveryCodeArea');
	var resetArea = document.getElementById('resetPasswordArea');
	var sendButton = document.getElementById('sendRecoveryCode');
	var verifyButton = document.getElementById('verifyRecoveryCode');
	var resetButton = document.getElementById('resetPassword');

	form.addEventListener('submit', function (event) {
		event.preventDefault();
	});

	function message(text, success) {
		result.textContent = text;
		result.className = success ? 'recovery_result success' : 'recovery_result';
	}

	function post(action, button, callback) {
		var data = 'action=' + encodeURIComponent(action)
			+ '&mode=' + encodeURIComponent(mode)
			+ '&email=' + encodeURIComponent(email.value);
		if (memberId) data += '&memberId=' + encodeURIComponent(memberId.value);
		if (action === 'verify') data += '&code=' + encodeURIComponent(code.value);
		if (action === 'reset') {
			data += '&password=' + encodeURIComponent(document.getElementById('newPassword').value);
			data += '&confirm=' + encodeURIComponent(document.getElementById('confirmPassword').value);
		}

		var xhr = new XMLHttpRequest();
		var finished = false;
		if (button) button.disabled = true;

		function finish(answer) {
			if (finished) return;
			finished = true;
			if (button) button.disabled = false;
			callback(answer);
		}

		xhr.onreadystatechange = function () {
			if (xhr.readyState !== 4) return;
			if (xhr.status === 200) finish(xhr.responseText.replace(/^\s+|\s+$/g, ''));
			else finish('server_error');
		};
		xhr.onerror = function () { finish('server_error'); };
		xhr.ontimeout = function () { finish('server_error'); };
		xhr.open('POST', endpoint, true);
		xhr.timeout = 30000;
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
		xhr.send(data);
	}

	function errorMessage(answer) {
		var errors = {
			invalid: '입력한 정보를 확인해주세요.',
			wait: '인증번호 재발송은 마지막 요청 후 30초가 지나면 가능합니다.',
			mail_error: '인증 메일을 보내지 못했습니다. 잠시 후 다시 시도해주세요.',
			send_first: '인증번호를 먼저 발송해주세요.',
			expired: '인증번호가 만료되었습니다. 다시 발송해주세요.',
			too_many: '인증 횟수를 초과했습니다. 인증번호를 다시 발송해주세요.',
			wrong_code: '인증번호가 일치하지 않습니다.',
			verify_first: '이메일 인증을 먼저 완료해주세요.',
			invalid_password: '새 비밀번호는 영문 소문자를 포함한 6~16자여야 합니다.',
			password_mismatch: '새 비밀번호와 확인 값이 다릅니다.',
			not_found: '계정 정보를 확인할 수 없습니다.',
			server_error: '처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
		};
		return errors[answer] || errors.server_error;
	}

	function restartVerification() {
		email.readOnly = false;
		if (memberId) memberId.readOnly = false;
		sendButton.hidden = false;
		codeArea.hidden = true;
		if (resetArea) resetArea.hidden = true;
	}

	sendButton.addEventListener('click', function () {
		if (!email.value || email.value.indexOf('@') < 1 || (memberId && !memberId.value)) {
			message('아이디와 이메일을 확인해주세요.', false);
			return;
		}
		post('send', sendButton, function (answer) {
			if (answer === 'sent') {
				codeArea.hidden = false;
				if (resetArea) resetArea.hidden = true;
				code.value = '';
				message('해당 이메일로 등록된 계정이 있으면 인증번호가 발송됩니다. 인증번호는 5분 동안 유효합니다.', true);
			} else {
				message(errorMessage(answer), false);
			}
		});
	});

	verifyButton.addEventListener('click', function () {
		if (!/^\d{6}$/.test(code.value.replace(/^\s+|\s+$/g, ''))) {
			message('인증번호 6자리를 입력해주세요.', false);
			return;
		}
		post('verify', verifyButton, function (answer) {
			if (answer !== 'verified') {
				if (answer === 'expired' || answer === 'too_many') restartVerification();
				message(errorMessage(answer), false);
				return;
			}
			email.readOnly = true;
			if (memberId) memberId.readOnly = true;
			codeArea.hidden = true;
			sendButton.hidden = true;

			if (mode === 'id') {
				post('findId', null, function (idAnswer) {
					if (idAnswer.indexOf('id:') === 0) {
						message('가입 아이디: ' + idAnswer.substring(3), true);
					} else {
						message(errorMessage(idAnswer), false);
					}
				});
			} else {
				resetArea.hidden = false;
				message('이메일 인증이 완료되었습니다. 새 비밀번호를 입력해주세요.', true);
			}
		});
	});

	if (resetButton) resetButton.addEventListener('click', function () {
		var password = document.getElementById('newPassword');
		var confirm = document.getElementById('confirmPassword');
		if (!/^(?=.*[a-z])[A-Za-z0-9!@#$%^&*_+?.-]{6,16}$/.test(password.value)) {
			message(errorMessage('invalid_password'), false);
			return;
		}
		if (password.value !== confirm.value) {
			message(errorMessage('password_mismatch'), false);
			return;
		}
		post('reset', resetButton, function (answer) {
			if (answer === 'updated') {
				resetArea.hidden = true;
				message('비밀번호를 변경했습니다. 새 비밀번호로 로그인해주세요.', true);
			} else {
				if (answer === 'verify_first') restartVerification();
				message(errorMessage(answer), false);
			}
		});
	});
});
