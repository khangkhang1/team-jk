package common;

/**
 * 주차 요금 규칙 (강선구). 출차 정산(command.manager.GateOut)과 관리자 화면의 정산 미리보기가 같이 쓴다.
 *
 * 값은 오윤섭 결제 화면(js/payment.js)의 HOURLY_PRICE / PLAN2_HOURLY_PRICE 와 같다.
 * 계산식도 payment.js 의 예상 금액과 같은 "이용 시간 × 시간당 요금, 원 단위 반올림" 이고,
 * 최소 1시간은 받는다 (예약 화면이 출차 시각을 시작 + 1시간 이상으로만 잡는 것과 맞춤).
 *
 * ※ 요금 규칙이 확정되면(6차 회의 미결 : 예약금 시간 비례, 장기 할인 등) 여기 상수와 totalFee 만 고치면 된다.
 *    같은 값이 payment.js 에도 있으니 그쪽도 같이 바꿀 것. (나중에는 서버 값 하나만 남기는 게 맞다)
 */
public class FeeRule {

	public static final int HOURLY_RESERVED = 3000;   // 1안 예약형    (reservation_type = '1')
	public static final int HOURLY_FREE     = 4500;   // 2안 자유출차형 (reservation_type = '2', 페널티 요금)
	public static final double MIN_HOURS    = 1.0;    // 최소 과금 시간

	public static int hourlyRate(String reservationType) {
		return "2".equals(reservationType) ? HOURLY_FREE : HOURLY_RESERVED;
	}

	// 과금 대상 시간 : 1시간 미만이면 1시간
	public static double billableHours(double hours) {
		return hours < MIN_HOURS ? MIN_HOURS : hours;
	}

	// 총 요금 = 과금 시간 × 시간당 요금 (원 단위 반올림)
	public static int totalFee(String reservationType, double hours) {
		return (int) Math.round(billableHours(hours) * hourlyRate(reservationType));
	}
}
