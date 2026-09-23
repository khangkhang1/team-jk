<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
=========================================================================
 항공편 도착 현황 (AJAX + 실시간 API, DB 저장 없음) - 강선구 2026-09-17

 [흐름]  이 페이지는 처음 한 번만 뜨고, 데이터는 전부 AJAX로 받는다.
   화면 조작(날짜/검색어/상태)
     -> $.ajax  GET  /Flight?t_gubun=arrivals&...
     -> Flight 서블릿 -> FlightApiDao.getArrivalsOfDay()  (날짜별 5분 캐시)
     -> 인천공항 API  (캐시에 없을 때만)
     <- JSON  -> 표 다시 그리기

 [팀원 참고]
   - 예약 화면에서 팝업으로 띄우려면
       window.open(ctx + '/flight/flight_search.jsp', 'flight', 'width=1000,height=760');
     그리고 부모창에 아래 함수를 만들어 두면, 여기서 [이 항공편으로 선택]을 누를 때 호출된다.
       function onFlightSelected(f) { ... f.flightNo, f.searchday, f.remark ... }
   - 에러가 나면 화면 빨간 상자에 서버가 보낸 원인이 그대로 뜬다.
     "secret.properties 가 없습니다"가 보이면 SecretConfig.java 주석대로 파일을 만들면 된다.
   - 검색어 입력은 300ms 동안 타이핑이 멈췄을 때만 요청한다(디바운스).
     서버 쪽 캐시와 합쳐서, 글자 하나마다 공항 API를 때리지 않게 한 것.
=========================================================================
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>항공편 도착 현황</title>
<script src="${pageContext.request.contextPath}/js/jquery-1.8.1.min.js"></script>
<style>
	[hidden] { display: none !important; }
	* { box-sizing: border-box; }
	body { margin: 0; background: #f4f6f9; color: #1f2937;
	       font-family: "Pretendard", "맑은 고딕", "Malgun Gothic", sans-serif; font-size: 14px; }
	.wrap { max-width: 1100px; margin: 0 auto; padding: 28px 20px 110px; }

	.top h1 { margin: 0; font-size: 22px; }
	.top .sub { margin: 4px 0 18px; color: #6b7280; font-size: 13px; }

	.panel { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 14px 16px; }
	.row { display: flex; flex-wrap: wrap; gap: 10px 16px; align-items: center; }
	.row label { color: #4b5563; font-size: 13px; display: flex; align-items: center; gap: 6px; }
	select, input[type=search] { height: 36px; border: 1px solid #d1d5db; border-radius: 6px;
	                             padding: 0 10px; font: inherit; background: #fff; }
	input[type=search] { flex: 1 1 260px; min-width: 200px; }
	select:focus, input:focus { outline: 2px solid #bfdbfe; border-color: #3b82f6; }

	.tabs { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 12px; }
	.tabs button { border: 1px solid #d1d5db; background: #fff; color: #374151; border-radius: 999px;
	               padding: 6px 12px; font: inherit; cursor: pointer; }
	.tabs button b { font-weight: 600; color: #6b7280; margin-left: 2px; }
	.tabs button.on { background: #1d4ed8; border-color: #1d4ed8; color: #fff; }
	.tabs button.on b { color: #dbeafe; }

	.summary { margin: 14px 2px 8px; color: #374151; }
	.muted { color: #9ca3af; }
	.warn { color: #b45309; font-weight: 600; }

	.error { margin: 10px 0; padding: 12px 14px; border: 1px solid #fecaca; background: #fef2f2;
	         color: #991b1b; border-radius: 8px; white-space: pre-wrap; line-height: 1.55; }
	.error b { display: block; margin-bottom: 4px; }

	.tableWrap { position: relative; background: #fff; border: 1px solid #e5e7eb; border-radius: 10px;
	             overflow-x: auto; }
	table { width: 100%; border-collapse: collapse; min-width: 820px; }
	th, td { padding: 9px 12px; border-bottom: 1px solid #f0f1f3; text-align: left; vertical-align: top;
	         word-break: keep-all; }   /* 한글 단어가 "캐세이퍼시픽항/공"처럼 중간에서 끊기지 않게 */
	td.nowrap { white-space: nowrap; }
	th { position: sticky; top: 0; background: #f9fafb; color: #6b7280; font-size: 12px; font-weight: 600; }
	tbody tr { cursor: pointer; }
	tbody tr:hover { background: #f8fafc; }
	tbody tr.sel { background: #eff6ff; box-shadow: inset 3px 0 0 #2563eb; }
	tbody tr.row-cancel { background: #fff5f5; }
	td.time { white-space: nowrap; font-variant-numeric: tabular-nums; font-weight: 600; }
	td.time small { font-weight: 400; color: #9ca3af; }
	td.changed { color: #c2410c; }
	td.no b { font-size: 15px; }
	.cs { margin-top: 2px; font-size: 12px; color: #6b7280; }
	mark { background: #fde68a; padding: 0 2px; border-radius: 2px; }
	.code { color: #9ca3af; font-size: 12px; }

	.badge { display: inline-block; min-width: 42px; text-align: center; padding: 2px 8px;
	         border-radius: 999px; font-size: 12px; font-weight: 600; }
	.badge.plan   { background: #f3f4f6; color: #4b5563; }
	.badge.ok     { background: #dcfce7; color: #166534; }
	.badge.delay  { background: #ffedd5; color: #9a3412; }
	.badge.cancel { background: #fee2e2; color: #991b1b; }
	.badge.divert { background: #ede9fe; color: #5b21b6; }

	.empty, .loading { padding: 48px 16px; text-align: center; color: #9ca3af; }
	.loading { position: absolute; inset: 0; background: rgba(255,255,255,.8); padding-top: 60px; }

	.pick { position: fixed; left: 0; right: 0; bottom: 0; background: #111827; color: #f9fafb;
	        padding: 14px 20px; display: flex; gap: 12px; align-items: center; justify-content: center;
	        flex-wrap: wrap; box-shadow: 0 -4px 16px rgba(0,0,0,.15); }
	.pick .muted { color: #9ca3af; }
	.pick button { border: 0; border-radius: 6px; background: #3b82f6; color: #fff; padding: 9px 16px;
	               font: inherit; font-weight: 600; cursor: pointer; }
</style>
</head>
<body>
<div class="wrap">

	<header class="top">
		<h1>항공편 도착 현황</h1>
		<p class="sub">인천국제공항공사 여객기 운항현황 실시간 API · DB에 저장하지 않음 · 5분마다 갱신</p>
	</header>

	<div class="panel">
		<div class="row">
			<label>도착일 <select id="day"></select></label>
			<label>터미널
				<select id="terminal">
					<option value="all">전체</option>
					<option value="T1">제1터미널</option>
					<option value="T2">제2터미널</option>
				</select>
			</label>
			<input id="keyword" type="search" autocomplete="off"
			       placeholder="편명 · 항공사 · 출발지 (예: KE704, 나리타, NRT)">
		</div>
		<div class="tabs" id="tabs">
			<button type="button" data-status="all" class="on">전체 <b data-count="all">-</b></button>
			<button type="button" data-status="scheduled">예정 <b data-count="scheduled">-</b></button>
			<button type="button" data-status="arrived">도착 <b data-count="arrived">-</b></button>
			<button type="button" data-status="delayed">지연 <b data-count="delayed">-</b></button>
			<button type="button" data-status="cancelled">결항 <b data-count="cancelled">-</b></button>
		</div>
	</div>

	<div class="summary" id="summary">&nbsp;</div>
	<div class="error" id="error" hidden></div>

	<div class="tableWrap">
		<table>
			<thead>
				<tr>
					<th>예정</th><th>변경</th><th>편명</th><th>항공사</th>
					<th>출발지</th><th>터미널</th><th>출구 · 수취대</th><th>현황</th>
				</tr>
			</thead>
			<tbody id="rows"></tbody>
		</table>
		<div class="empty" id="empty" hidden>조건에 맞는 항공편이 없습니다.</div>
		<div class="loading" id="loading">항공편을 불러오는 중입니다… (그날 첫 조회는 몇 초 걸립니다)</div>
	</div>

	<div class="pick" id="pick" hidden>
		<div id="pickText"></div>
		<button type="button" id="pickBtn">이 항공편으로 선택</button>
	</div>
</div>

<script>
(function ($) {
	var CTX = '${pageContext.request.contextPath}';
	var WEEK = ['일', '월', '화', '수', '목', '금', '토'];
	var REMARK_CLASS = { '도착': 'ok', '착륙': 'ok', '지연': 'delay', '결항': 'cancel', '회항': 'divert' };

	var state = {
		status: 'all',
		seq: 0,          // 요청 번호. 늦게 도착한 옛날 응답을 걸러내는 데 쓴다
		xhr: null,
		timer: null,
		lastKeyword: '',
		list: [],
		day: '',
		selected: null,
		range: ''
	};

	// ── 작은 도우미들 ────────────────────────────────────────────
	function pad(n) { return (n < 10 ? '0' : '') + n; }
	function ymd(d) { return d.getFullYear() + pad(d.getMonth() + 1) + pad(d.getDate()); }
	function parseYmd(s) { return new Date(+s.substr(0, 4), +s.substr(4, 2) - 1, +s.substr(6, 2)); }

	function dayLabel(s, today) {
		var d = parseYmd(s);
		return (d.getMonth() + 1) + '/' + d.getDate() + ' (' + WEEK[d.getDay()] + ')' + (s === today ? ' 오늘' : '');
	}

	// API 데이터를 화면에 넣기 전에 반드시 이스케이프 (항공사명 등에 <, & 가 섞여도 화면이 안 깨지게)
	function esc(s) {
		return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
			return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
		});
	}

	// 202609171430 -> 14:30. 조회일과 날짜가 다르면(자정 넘어가는 편) 전날/다음날 표시
	function hm(dt, day) {
		if (!dt || dt.length < 12) return '';
		var t = dt.substr(8, 2) + ':' + dt.substr(10, 2);
		var d = dt.substr(0, 8);
		if (d < day) return '<small>전날</small> ' + t;
		if (d > day) return '<small>다음날</small> ' + t;
		return t;
	}

	function buildDays(min, max, today, selected) {
		var $s = $('#day').empty();
		var d = parseYmd(min), end = parseYmd(max);
		while (d <= end) {
			var v = ymd(d);
			$s.append($('<option>').val(v).text(dayLabel(v, today)));
			d.setDate(d.getDate() + 1);
		}
		$s.val(selected || today);
	}

	function showError(msg) {
		$('#error').html('<b>항공편을 불러오지 못했습니다</b>' + esc(msg)).prop('hidden', false);
		$('#rows').empty();
		$('#empty').prop('hidden', true);
		$('#summary').html('&nbsp;');
		// 표가 비었으니 이전 선택도 지운다 (남겨두면 화면에 없는 편이 선택된 채로 넘어갈 수 있음)
		state.list = [];
		state.selected = null;
		$('#pick').prop('hidden', true);
	}

	// ── 서버 호출 ────────────────────────────────────────────────
	function load() {
		var seq = ++state.seq;
		if (state.xhr) state.xhr.abort();   // 앞 요청이 아직 안 끝났으면 취소
		state.lastKeyword = $.trim($('#keyword').val());
		$('#loading').prop('hidden', false);
		$('#error').prop('hidden', true);

		state.xhr = $.ajax({
			url: CTX + '/Flight',
			type: 'GET',
			dataType: 'json',
			cache: false,
			data: {
				t_gubun: 'arrivals',
				t_searchday: $('#day').val(),
				t_keyword: state.lastKeyword,
				t_status: state.status,
				t_terminal: $('#terminal').val()
			}
		}).done(function (res) {
			if (seq !== state.seq) return;   // 더 최근 요청이 있으면 이 응답은 버린다
			render(res);
		}).fail(function (xhr, textStatus) {
			if (textStatus === 'abort' || seq !== state.seq) return;
			var msg;
			try {
				msg = $.parseJSON(xhr.responseText).message;
			} catch (e) {
				if (xhr.status === 404) {
					msg = 'Flight 서블릿을 찾지 못했습니다 (HTTP 404).\n'
						+ '서버를 재시작했는지, 프로젝트가 톰캣에 올라가 있는지 확인하세요.';
				} else if (xhr.status === 0) {
					msg = '서버에 연결할 수 없습니다. 톰캣이 켜져 있는지 확인하세요.';
				} else {
					msg = '서버 오류 (HTTP ' + xhr.status + ').\n'
						+ '이클립스 Console 탭(톰캣 로그)의 빨간 에러를 확인하세요.';
				}
			}
			showError(msg || ('HTTP ' + xhr.status));
		}).always(function () {
			if (seq === state.seq) $('#loading').prop('hidden', true);
		});
	}

	// ── 화면 그리기 ──────────────────────────────────────────────
	function render(res) {
		// 날짜 목록을 서버 기준(오늘-3 ~ 오늘+6)으로 맞춘다. 처음엔 브라우저 시계로 만들어 둔 상태.
		if (state.range !== res.minDay + res.maxDay) {
			state.range = res.minDay + res.maxDay;
			buildDays(res.minDay, res.maxDay, res.today, res.searchday);
		}

		$.each(res.counts, function (k, v) {
			$('[data-count="' + k + '"]').text(v);
		});

		var codeshareCount = res.rawTotal - res.total;
		$('#summary').html(
			'<b>' + esc(dayLabel(res.searchday, res.today)) + '</b> 도착 <b>' + res.total + '</b>편'
			+ ' <span class="muted">(공동운항 편명 ' + codeshareCount + '건은 실제 운항편에 묶어서 표시)</span>'
			+ ' · 표시 <b>' + res.count + '</b>편'
			+ ' · <span class="muted">' + esc(res.fetchedAt) + ' 기준' + (res.fromCache ? ' · 캐시' : '') + '</span>'
			+ (res.stale ? ' · <span class="warn">API 응답 실패로 이전에 받은 데이터를 표시 중</span>' : '')
		);

		var html = [];
		for (var i = 0; i < res.flights.length; i++) {
			html.push(rowHtml(res.flights[i], res.searchday, i));
		}
		$('#rows').html(html.join(''));
		$('#empty').prop('hidden', res.flights.length > 0);

		state.list = res.flights;
		state.day = res.searchday;
		state.selected = null;
		$('#pick').prop('hidden', true);
	}

	function rowHtml(f, day, idx) {
		var changed = f.estimatedDateTime && f.estimatedDateTime !== f.scheduleDateTime;

		var no = '<b>' + esc(f.flightNo) + '</b>';
		if (f.codeshares.length) {
			var names = $.map(f.codeshares, function (c) {
				return c === f.matchedBy ? '<mark>' + esc(c) + '</mark>' : esc(c);
			});
			no += '<div class="cs">공동운항 ' + names.join(', ') + '</div>';
		}

		var place = [];
		if (f.exit) place.push('출구 ' + esc(f.exit));
		if (f.carousel) place.push('수취대 ' + esc(f.carousel));

		return '<tr data-idx="' + idx + '"' + (f.remark === '결항' ? ' class="row-cancel"' : '') + '>'
			+ '<td class="time">' + hm(f.scheduleDateTime, day) + '</td>'
			+ '<td class="time ' + (changed ? 'changed' : 'muted') + '">' + (changed ? hm(f.estimatedDateTime, day) : '-') + '</td>'
			+ '<td class="no">' + no + '</td>'
			+ '<td>' + esc(f.airline) + '</td>'
			+ '<td>' + esc(f.airport) + ' <span class="code">' + esc(f.airportCode) + '</span></td>'
			+ '<td class="nowrap">' + esc(f.terminal) + '</td>'
			+ '<td class="muted nowrap">' + (place.join(' · ') || '-') + '</td>'
			+ '<td><span class="badge ' + (REMARK_CLASS[f.remark] || 'plan') + '">' + esc(f.remark || '예정') + '</span></td>'
			+ '</tr>';
	}

	// ── 이벤트 ───────────────────────────────────────────────────

	// 행 선택
	$('#rows').on('click', 'tr', function () {
		var f = state.list[+$(this).attr('data-idx')];
		if (!f) return;
		$('#rows tr.sel').removeClass('sel');
		$(this).addClass('sel');

		// 공동운항 편명으로 검색했다면, 사용자 항공권에 적힌 번호는 그 편명이다
		var picked = (f.matchedBy && f.matchedBy !== f.flightNo) ? f.matchedBy : f.flightNo;
		state.selected = {
			flightNo: picked,
			masterFlightNo: f.flightNo,
			airline: f.airline,
			airport: f.airport,
			scheduleDateTime: f.scheduleDateTime,
			remark: f.remark,
			terminal: f.terminal,
			searchday: state.day
		};
		$('#pickText').html(
			'선택 : <b>' + esc(picked) + '</b>'
			+ (picked !== f.flightNo ? ' <span class="muted">(실제 운항 ' + esc(f.flightNo) + ')</span>' : '')
			+ ' · ' + esc(f.airline) + ' · ' + esc(f.airport)
			+ ' · 예정 ' + hm(f.scheduleDateTime, state.day) + ' · ' + esc(f.remark || '도착 전')
		);
		$('#pick').prop('hidden', false);
	});

	// 예약 화면에서 팝업으로 열었다면 부모창의 onFlightSelected(항공편)로 넘기고 닫는다
	$('#pickBtn').on('click', function () {
		var f = state.selected;
		if (!f) return;
		var parent = null;
		try {
			parent = (window.opener && !window.opener.closed
				&& typeof window.opener.onFlightSelected === 'function') ? window.opener : null;
		} catch (e) {
			parent = null;
		}
		if (parent) {
			parent.onFlightSelected(f);
			window.close();
		} else {
			alert('선택한 항공편 : ' + f.flightNo + ' (' + f.searchday + ')\n\n'
				+ '예약 화면에서 팝업으로 열면 이 값이 부모창의 onFlightSelected() 로 전달됩니다.');
		}
	});

	// 검색어 : 300ms 동안 입력이 멈췄을 때만 요청 (디바운스). 엔터는 바로.
	$('#keyword').on('input', function () {
		clearTimeout(state.timer);
		state.timer = setTimeout(function () {
			if ($.trim($('#keyword').val()) !== state.lastKeyword) load();
		}, 300);
	}).on('keydown', function (e) {
		if (e.keyCode === 13) {
			clearTimeout(state.timer);
			load();
		}
	});

	$('#tabs').on('click', 'button', function () {
		state.status = $(this).attr('data-status');
		$('#tabs button').removeClass('on');
		$(this).addClass('on');
		load();
	});

	$('#day, #terminal').on('change', load);

	// 첫 화면 : 서버 응답 전이라 브라우저 날짜로 목록을 만들고, 응답이 오면 서버 기준으로 다시 맞춘다
	(function () {
		var now = new Date(), from = new Date(now), to = new Date(now);
		from.setDate(from.getDate() - 3);
		to.setDate(to.getDate() + 6);
		buildDays(ymd(from), ymd(to), ymd(now), ymd(now));
	})();
	load();
})(jQuery);
</script>
</body>
</html>
