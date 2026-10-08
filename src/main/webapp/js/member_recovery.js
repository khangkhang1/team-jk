document.addEventListener('DOMContentLoaded', function () {
	var page = document.getElementById('recoveryPage');
	if (!page) return;
	var form = document.getElementById('recoveryForm');
	var mode = page.dataset.mode;
	var email = document.getElementById('recoveryEmail');
	var memberId = document.getElementById('recoveryMemberId');
	var code = document.getElementById('recoveryCode');
	var result = document.getElementById('recoveryResult');
	var codeArea = document.getElementById('recoveryCodeArea');
	var resetArea = document.getElementById('resetPasswordArea');
	var sendButton = document.getElementById('sendRecoveryCode');
	var verifyButton = document.getElementById('verifyRecoveryCode');
	var resetButton = document.getElementById('resetPassword');

	form.addEventListener('submit', function (event) { event.preventDefault(); });
	function message(text, success) {
		result.textContent = text;
		result.classList.toggle('success', !!success);
	}
	async function post(action, button) {
		var body = new URLSearchParams(new FormData(form));
		body.set('action', action);
		body.set('mode', mode);
		if (button) button.disabled = true;
		try {
			var response = await fetch(page.dataset.endpoint, {
				method: 'POST',
				credentials: 'same-origin',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				body: body.toString()
			});
			if (!response.ok) throw new Error('HTTP ' + response.status);
			return (await response.text()).trim();
		} catch (error) {
			return 'server_error';
		} finally {
			if (button) button.disabled = false;
		}
	}
	function errorMessage(answer) {
		var errors = {
			invalid: jm('rec_invalid', '입력한 정보를 확인해주세요.'),
			wait: jm('rec_wait', '인증번호 재발송은 마지막 요청 후 30초가 지나면 가능합니다.'),
			mail_error: jm('rec_mail_error', '인증 메일을 보내지 못했습니다. 잠시 후 다시 시도해주세요.'),
			send_first: jm('rec_send_first', '인증번호를 먼저 발송해주세요.'),
			expired: jm('rec_expired', '인증번호가 만료되었습니다. 다시 발송해주세요.'),
			too_many: jm('rec_too_many', '인증 횟수를 초과했습니다. 인증번호를 다시 발송해주세요.'),
			wrong_code: jm('rec_wrong_code', '인증번호가 일치하지 않습니다.'),
			verify_first: jm('rec_verify_first', '이메일 인증을 먼저 완료해주세요.'),
			invalid_password: jm('rec_invalid_password', '새 비밀번호는 영문 소문자를 포함한 6~16자여야 합니다.'),
			password_mismatch: jm('rec_password_mismatch', '새 비밀번호와 확인 값이 다릅니다.'),
			not_found: jm('rec_not_found', '계정 정보를 확인할 수 없습니다.'),
			server_error: jm('rec_server_error', '처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.')
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

	sendButton.addEventListener('click', async function () {
		if (!email.reportValidity() || (memberId && !memberId.reportValidity())) return;
		var answer = await post('send', sendButton);
		if (answer === 'sent') {
			codeArea.hidden = false;
			if (resetArea) resetArea.hidden = true;
			code.value = '';
			message(jm('rec_sent', '해당 이메일로 등록된 계정이 있으면 인증번호가 발송됩니다. 인증번호는 5분 동안 유효합니다.'), true);
		} else message(errorMessage(answer), false);
	});

	verifyButton.addEventListener('click', async function () {
		if (!/^\d{6}$/.test(code.value.trim())) {
			message(jm('rec_code6', '인증번호 6자리를 입력해주세요.'), false);
			return;
		}
		var answer = await post('verify', verifyButton);
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
			answer = await post('findId');
			if (answer.indexOf('id:') === 0) message('가입 아이디: ' + answer.substring(3), true);
			else message(errorMessage(answer), false);
		} else {
			resetArea.hidden = false;
			message('이메일 인증이 완료되었습니다. 새 비밀번호를 입력해주세요.', true);
		}
	});

	if (resetButton) resetButton.addEventListener('click', async function () {
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
		var answer = await post('reset', resetButton);
		if (answer === 'updated') {
			resetArea.hidden = true;
			message('비밀번호를 변경했습니다. 새 비밀번호로 로그인해주세요.', true);
		} else {
			if (answer === 'verify_first') restartVerification();
			message(errorMessage(answer), false);
		}
	});
});
