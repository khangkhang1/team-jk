// 다국어(한국어/일본어) 전환 - 화면 초안 단계 간이 구현
// 방식: data-i18n="키" 붙은 요소의 텍스트를, data-i18n-ph="키" 붙은 요소의 placeholder를 사전에서 찾아 교체.
// 실제 서버 붙는 단계에서는 message_ko.properties / message_ja.properties 로 옮길 예정 (JSTL <fmt:message>).
// 선택한 언어는 localStorage에 저장 - 페이지 이동해도 유지됨.

var I18N_DICT = {
	// 공통
	common_siteTitle:     { ko: "🅿️ 인천공항 주차관리시스템", ja: "🅿️ 仁川空港 駐車場管理システム" },
	common_login:         { ko: "로그인", ja: "ログイン" },
	common_join:          { ko: "회원가입", ja: "会員登録" },
	common_notice:        { ko: "📢 공지사항", ja: "📢 お知らせ" },
	common_reservation:   { ko: "📋 예약 내역", ja: "📋 予約履歴" },
	common_mypage:        { ko: "👤 마이페이지", ja: "👤 マイページ" },
	common_myLocation:    { ko: "🧭 내 위치", ja: "🧭 現在地" },
	common_favorite:      { ko: "⭐ 즐겨찾기", ja: "⭐ お気に入り" },
	common_back:          { ko: "← 메인으로", ja: "← メインへ" },

	// 메인 화면
	idx_searchPh:         { ko: "어디 주차장을 찾으세요? (예: 단기주차장 장애인석)", ja: "駐車場を検索（例: 短期駐車場 障がい者用）" },
	idx_search:           { ko: "검색", ja: "検索" },
	idx_timeTitle:        { ko: "🕐 이용 시간", ja: "🕐 利用時間" },
	idx_zoneTitle:        { ko: "🗺️ 구역 선택", ja: "🗺️ エリア選択" },
	idx_zoneAll:          { ko: "전체", ja: "全体" },
	idx_filterTitle:      { ko: "필터", ja: "フィルター" },
	idx_filterDisabled:   { ko: "♿ 장애인 주차", ja: "♿ 障がい者用駐車" },
	idx_filterEv:         { ko: "🔌 전기차 충전", ja: "🔌 EV充電" },
	idx_detailDistanceLabel: { ko: "터미널 도보", ja: "ターミナル徒歩" },
	idx_detailRemainLabel:   { ko: "잔여 대수", ja: "残り台数" },
	idx_detailUsersLabel:    { ko: "오늘 이용자", ja: "本日の利用者" },
	idx_confirmText:      { ko: "이 주차장을 이용하시겠습니까?", ja: "この駐車場を利用しますか？" },
	idx_selectSeat:       { ko: "확인 - 주차맵으로 이동", ja: "確認 - 駐車マップへ移動" },
	idx_flightNotice:     { ko: "✈️ 편도 항공권만 있으신가요? 이 시스템은 왕복 이용자 전용입니다 (현장 이용 안내)", ja: "✈️ 片道航空券のみですか？本システムは往復利用者専用です（現地窓口をご案内）" },

	// 주차맵 페이지 (좌석 클릭 시 뜨는 결제 모달 포함)
	res_pageTitle:        { ko: "주차맵", ja: "駐車マップ" },
	res_planFilterTitle:  { ko: "이용 방식", ja: "利用方式" },
	res_step1:            { ko: "① 이용 방식 선택", ja: "① 利用方式の選択" },
	res_plan1Name:        { ko: "예약형 (1안)", ja: "予約型（プラン1）" },
	res_plan1Desc:        { ko: "시작·종료 시각을 미리 정합니다. 왕복 항공권 정보 입력 필수. 기본 요금.", ja: "開始・終了時刻を事前に指定します。往復航空券情報の入力が必須。基本料金。" },
	res_plan2Name:        { ko: "자유출차형 (2안)", ja: "自由出庫型（プラン2）" },
	res_plan2Desc:        { ko: "시작 시각만 정하고 종료는 자유입니다. 장기주차구역, 페널티 요금(더 비쌈) 적용.", ja: "開始時刻のみ指定し、終了は自由です。長期駐車エリア、割増料金が適用されます。" },
	res_flightSectionTitle: { ko: "✈️ 항공권 정보 (필수)", ja: "✈️ 航空券情報（必須）" },
	res_flightNo:         { ko: "항공편명", ja: "便名" },
	res_flightRoundtrip:  { ko: "왕복 여부", ja: "往復区分" },
	res_flightArriveTime: { ko: "귀국 도착 예정 시각", ja: "帰国到着予定時刻" },
	res_step2:            { ko: "② 시간 선택", ja: "② 時間選択" },
	res_step3:            { ko: "③ 좌석 선택", ja: "③ 座席選択" },
	res_step4:            { ko: "④ 결제 수단", ja: "④ 決済方法" },
	res_notChosen:        { ko: "선택 안 됨", ja: "未選択" },
	res_dateLabel:        { ko: "날짜", ja: "日付" },
	res_startTimeLabel:   { ko: "시작 시각", ja: "開始時刻" },
	res_durationLabel:    { ko: "이용 시간", ja: "利用時間" },
	res_endFreeNotice:    { ko: "종료 시각은 정하지 않습니다 (자유출차, 페널티 요금 적용)", ja: "終了時刻は決めません（自由出庫、割増料金が適用されます）" },
	res_estimated:        { ko: "예상 금액", ja: "予想金額" },
	res_floorRemain:      { ko: "이 층 잔여", ja: "このフロアの空き" },
	res_random:           { ko: "🎲 랜덤 배정", ja: "🎲 ランダム割当" },
	res_legendFree:       { ko: "선택 가능", ja: "選択可能" },
	res_legendSelected:   { ko: "선택됨", ja: "選択済み" },
	res_legendTaken:      { ko: "예약됨", ja: "予約済み" },
	res_legendDisabled:   { ko: "♿ 장애인", ja: "♿ 障がい者" },
	res_legendEv:         { ko: "🔌 전기차", ja: "🔌 EV" },
	res_legendCancelled:  { ko: "✈️ 결항 재배정중", ja: "✈️ 欠航・再割当中" },
	res_payBtn:           { ko: "결제하기", ja: "決済する" },
	res_depositLabel:     { ko: "예약금", ja: "予約金" },

	// 회원정보 페이지
	mem_tabLogin:         { ko: "로그인", ja: "ログイン" },
	mem_tabJoin:          { ko: "회원가입", ja: "会員登録" },
	mem_tabMypage:        { ko: "마이페이지", ja: "マイページ" },
	mem_id:               { ko: "아이디", ja: "ID" },
	mem_pw:               { ko: "비밀번호", ja: "パスワード" },
	mem_pwCheck:          { ko: "비밀번호 확인", ja: "パスワード確認" },
	mem_name:             { ko: "이름", ja: "氏名" },
	mem_phone:            { ko: "전화번호", ja: "電話番号" },
	mem_flightSection:    { ko: "✈️ 탑승권(항공편) 정보", ja: "✈️ 搭乗券（航空便）情報" },
	mem_flightNo:         { ko: "항공편명", ja: "便名" },
	mem_roundtrip:        { ko: "왕복 여부", ja: "往復区分" },
	mem_roundtripYes:     { ko: "왕복", ja: "往復" },
	mem_roundtripNo:      { ko: "편도 (이용 불가 안내)", ja: "片道（利用不可のご案内）" },
	mem_loginBtn:         { ko: "로그인", ja: "ログイン" },
	mem_joinBtn:          { ko: "가입하기", ja: "登録する" },
	mem_myInfo:           { ko: "내 정보", ja: "会員情報" },
	mem_myReservations:   { ko: "예약 내역", ja: "予約履歴" },
	mem_statusBeforeUse:  { ko: "입차 전", ja: "入庫前" },
	mem_statusInUse:      { ko: "이용중", ja: "利用中" },
	mem_statusDone:       { ko: "이용 종료", ja: "利用終了" },
	mem_btnChange:        { ko: "예약 변경", ja: "予約変更" },
	mem_btnExtend:        { ko: "예약 연장", ja: "予約延長" },
	mem_btnDone:          { ko: "완료", ja: "完了" },

	// 공지사항/FAQ
	ntc_pageTitle:        { ko: "공지사항 · FAQ", ja: "お知らせ・FAQ" },
	ntc_tabNotice:        { ko: "공지사항", ja: "お知らせ" },
	ntc_tabFaq:           { ko: "자주 묻는 질문", ja: "よくある質問" }
};

function applyLang(lang) {
	document.querySelectorAll("[data-i18n]").forEach(function (el) {
		var key = el.getAttribute("data-i18n");
		if (I18N_DICT[key] && I18N_DICT[key][lang]) {
			el.textContent = I18N_DICT[key][lang];
		}
	});
	document.querySelectorAll("[data-i18n-ph]").forEach(function (el) {
		var key = el.getAttribute("data-i18n-ph");
		if (I18N_DICT[key] && I18N_DICT[key][lang]) {
			el.placeholder = I18N_DICT[key][lang];
		}
	});
	document.documentElement.lang = lang;
	try { localStorage.setItem("lang", lang); } catch (e) { /* 무시 */ }

	var toggleBtn = document.getElementById("langToggleBtn");
	if (toggleBtn) {
		toggleBtn.textContent = (lang === "ko") ? "🇯🇵 日本語" : "🇰🇷 한국어";
	}
}

function initLang() {
	var saved = "ko";
	try { saved = localStorage.getItem("lang") || "ko"; } catch (e) { /* 무시 */ }
	applyLang(saved);

	var toggleBtn = document.getElementById("langToggleBtn");
	if (toggleBtn) {
		toggleBtn.addEventListener("click", function () {
			var current = document.documentElement.lang === "ja" ? "ja" : "ko";
			applyLang(current === "ko" ? "ja" : "ko");
		});
	}
}

document.addEventListener("DOMContentLoaded", initLang);
