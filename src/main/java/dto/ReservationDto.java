package dto;

public class ReservationDto {
/*
	* reservation_id		: 예약 번호(pk)
	* reservation_status	: 예약 상태(not null / 예약중, 취소 등)
	* reservation_start_time: 예약 시 선택한 주차 시작 시각(not null)
	* reservation_end_time	: 예약 시 주차 종료 시각(단기 주차의 경우에만 등록)
	* reservation_out_time	: 실제 출차 시각
	* reservation_type		: 예약 주차 유형(단기 or 장기)
*/
	private int reservation_id;
	private String reservation_status, reservation_start_time, reservation_end_time,
					reservation_out_time, reservation_type;
	
	
}
