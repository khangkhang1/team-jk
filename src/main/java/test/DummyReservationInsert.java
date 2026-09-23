package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import common.DBConnection;
import common.FeeRule;
import dao.PaymentDao;
import dto.PaymentDto;
import dto.ReservationInfoDto;

// 예약·결제 더미 데이터 생성기. 저장은 오윤섭 PaymentDao.saveReservation / savePayment 을 그대로 쓴다.
// 예약번호·결제번호도 같은 형식(R26-09-0001)이라 실제 예약과 섞여도 규칙이 깨지지 않는다.
public class DummyReservationInsert {

	private static final int DAYS_BACK   = 90;
	private static final int DAYS_AHEAD  = 7;
	private static final String[] METHODS = { "kakaoPay", "creditCard", "naverPay", "bankTransfer" };
	private static final String[] AIRLINES = { "KE", "OZ", "7C", "LJ", "TW", "BX", "NH", "JL" };

	private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat MIN  = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static void main(String[] args) throws Exception {
		if (args.length > 0 && args[0].equals("delete")) {
			delete();
			return;
		}

		Random r = new Random(20260923);
		PaymentDao dao = PaymentDao.getDao();

		List<String> members = members();
		Map<String, List<String>> lotSeats = seats();
		if (members.isEmpty()) {
			System.out.println("더미 회원이 없습니다. DummyMemberInsert 를 먼저 실행하세요.");
			return;
		}

		String rPrefix = prefix(dao.getReservationId());
		int rSeq = seq(dao.getReservationId());
		String pPrefix = prefix(dao.getPaymentId());
		int pSeq = seq(dao.getPaymentId());
		DecimalFormat df = new DecimalFormat("0000");

		Set<String> taken = new HashSet<>();
		long now = System.currentTimeMillis();
		int resvCnt = 0, payCnt = 0;

		for (int day = -DAYS_BACK; day <= DAYS_AHEAD; day++) {
			Calendar base = Calendar.getInstance();
			base.add(Calendar.DATE, day);
			int week = base.get(Calendar.DAY_OF_WEEK);
			int count = (week == Calendar.FRIDAY || week == Calendar.SATURDAY || week == Calendar.SUNDAY)
					? 7 + r.nextInt(6) : 3 + r.nextInt(5);

			for (int i = 0; i < count; i++) {
				String lot  = lot(r);
				List<String> seatList = lotSeats.get(lot);
				if (seatList == null || seatList.isEmpty()) continue;
				String seat = seatList.get(r.nextInt(seatList.size()));
				if (!taken.add(day + "/" + seat)) continue;

				String type = r.nextInt(10) < 7 ? "1" : "2";
				int hourly  = FeeRule.hourlyPrice(lot);

				Calendar start = (Calendar) base.clone();
				start.set(Calendar.HOUR_OF_DAY, 5 + r.nextInt(17));
				start.set(Calendar.MINUTE, r.nextInt(2) * 30);
				start.set(Calendar.SECOND, 0);

				int stayMinutes = FeeRule.isLongTerm(lot)
						? (12 + r.nextInt(60)) * 60           // 장기 : 12 ~ 72시간
						: (2 + r.nextInt(9)) * 60;            // 단기 : 2 ~ 10시간
				Calendar end = (Calendar) start.clone();
				end.add(Calendar.MINUTE, stayMinutes);

				int estimate = type.equals("1") ? (int) Math.round(stayMinutes / 60.0 * hourly) : 0;
				int prepaid  = type.equals("1") ? estimate : FeeRule.DEPOSIT;

				Calendar regDate = (Calendar) start.clone();
				regDate.add(Calendar.DATE, -(1 + r.nextInt(10)));

				String status;
				if (end.getTimeInMillis() < now - 3600000L) status = r.nextInt(100) < 8 ? "4" : "3";
				else if (start.getTimeInMillis() < now)     status = "2";
				else                                        status = "1";

				String rid = rPrefix + df.format(rSeq++);
				String member = members.get(r.nextInt(members.size()));
				ReservationInfoDto dto;
				if (type.equals("1")) {
					String flight = AIRLINES[r.nextInt(AIRLINES.length)] + (100 + r.nextInt(900));
					Calendar arrive = (Calendar) end.clone();
					arrive.add(Calendar.MINUTE, -60);
					dto = new ReservationInfoDto(rid, flight, MIN.format(arrive.getTime()), status,
							DATE.format(start.getTime()), TIME.format(start.getTime()),
							DATE.format(end.getTime()), TIME.format(end.getTime()),
							type, member, seat, estimate, 0, FULL.format(regDate.getTime()));
				} else {
					dto = new ReservationInfoDto(rid, status,
							DATE.format(start.getTime()), TIME.format(start.getTime()),
							type, member, seat, FeeRule.DEPOSIT, FULL.format(regDate.getTime()));
				}
				if (dao.saveReservation(dto) != 1) continue;
				resvCnt++;

				String payId = pPrefix + df.format(pSeq++);
				if (dao.savePayment(new PaymentDto(payId, rid, prepaid, METHODS[r.nextInt(METHODS.length)],
						"1", FULL.format(regDate.getTime()))) == 1) payCnt++;

				if (status.equals("3")) {
					Calendar parkingStart = (Calendar) start.clone();
					parkingStart.add(Calendar.MINUTE, r.nextInt(25));
					Calendar out = (Calendar) end.clone();
					if (type.equals("1")) {
						out.add(Calendar.MINUTE, r.nextInt(100) < 20 ? 20 + r.nextInt(120) : -r.nextInt(40));
					} else {
						out = (Calendar) parkingStart.clone();
						out.add(Calendar.MINUTE, stayMinutes + r.nextInt(120) - 60);
					}
					long parked = (out.getTimeInMillis() - parkingStart.getTimeInMillis()) / 60000L;
					long over   = type.equals("1") ? Math.max(0, (out.getTimeInMillis() - end.getTimeInMillis()) / 60000L) : 0;
					FeeRule.Settlement fee = FeeRule.settle(type, parked, over, estimate, prepaid);

					finishReservation(rid, FULL.format(parkingStart.getTime()), FULL.format(out.getTime()), fee.getTotal());
					if (fee.getDue() > 0) {
						payId = pPrefix + df.format(pSeq++);
						if (dao.savePayment(new PaymentDto(payId, rid, fee.getDue(), METHODS[r.nextInt(METHODS.length)],
								"2", FULL.format(out.getTime()))) == 1) payCnt++;
					}
				} else if (status.equals("4")) {
					Calendar cancel = (Calendar) regDate.clone();
					cancel.add(Calendar.HOUR_OF_DAY, 2 + r.nextInt(40));
					payId = pPrefix + df.format(pSeq++);
					if (dao.savePayment(new PaymentDto(payId, rid, prepaid, METHODS[r.nextInt(METHODS.length)],
							"3", FULL.format(cancel.getTime()))) == 1) payCnt++;
				}
			}
		}
		System.out.println("예약 " + resvCnt + "건 / 결제 " + payCnt + "건 등록");
	}

	private static String lot(Random r) {
		int n = r.nextInt(100);
		if (n < 15) return "P1";
		if (n < 30) return "P2";
		return "P" + (3 + r.nextInt(7));
	}

	private static String prefix(String id) {
		return id.substring(0, id.lastIndexOf('-') + 1);
	}

	private static int seq(String id) {
		return Integer.parseInt(id.substring(id.lastIndexOf('-') + 1));
	}

	private static List<String> members() throws Exception {
		List<String> list = new ArrayList<>();
		try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT member_id FROM icn_member WHERE email LIKE '%@example.com'")) {
			while (rs.next()) list.add(rs.getString(1));
		}
		return list;
	}

	private static Map<String, List<String>> seats() throws Exception {
		Map<String, List<String>> map = new HashMap<>();
		try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT lot_id, seat_no FROM icn_seat ORDER BY lot_id, seat_no")) {
			while (rs.next()) {
				map.computeIfAbsent(rs.getString(1), k -> new ArrayList<>()).add(rs.getString(2));
			}
		}
		return map;
	}

	// 실제 입차·출차 시각과 최종 요금은 오윤섭 insert 에 없는 컬럼이라 여기서 채운다
	private static void finishReservation(String rid, String parkingStart, String outTime, int finalAmount) throws Exception {
		String sql = "UPDATE icn_reservation SET reservation_parking_start_time = TO_DATE(?,'yyyy-MM-dd hh24:mi:ss'),"
				+ " reservation_out_time = TO_DATE(?,'yyyy-MM-dd hh24:mi:ss'), reservation_final_amount = ?"
				+ " WHERE reservation_id = ?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, parkingStart);
			ps.setString(2, outTime);
			ps.setInt(3, finalAmount);
			ps.setString(4, rid);
			ps.executeUpdate();
		}
	}

	private static void delete() throws Exception {
		String where = " WHERE reservation_id IN (SELECT r.reservation_id FROM icn_reservation r"
				+ " JOIN icn_member m ON m.member_id = r.member_id WHERE m.email LIKE '%@example.com')";
		try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement()) {
			int pay = st.executeUpdate("DELETE FROM icn_payment" + where);
			int resv = st.executeUpdate("DELETE FROM icn_reservation r WHERE r.member_id IN"
					+ " (SELECT member_id FROM icn_member WHERE email LIKE '%@example.com')");
			System.out.println("결제 " + pay + "건 / 예약 " + resv + "건 삭제");
		}
	}

}
