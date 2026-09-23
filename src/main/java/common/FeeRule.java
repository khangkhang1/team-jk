package common;

import java.util.Map;

public class FeeRule {

	public static final int LONG_TERM_HOURLY  = 2000;
	public static final int SHORT_TERM_HOURLY = 3000;
	public static final int DEPOSIT           = 5000;
	public static final int UNIT_MINUTES      = 30;
	public static final int UNIT_PRICE        = 4500;

	public static boolean isLongTerm(String lotId) {
		return "P1".equals(lotId) || "P2".equals(lotId);
	}

	public static int hourlyPrice(String lotId) {
		return isLongTerm(lotId) ? LONG_TERM_HOURLY : SHORT_TERM_HOURLY;
	}

	public static int units(long minutes) {
		return minutes <= 0 ? 0 : (int) Math.ceil(minutes / (double) UNIT_MINUTES);
	}

	public static Settlement settle(String reservationType, long parkedMinutes, long overMinutes, int estimate, int prepaid) {
		Settlement s = new Settlement();
		s.planFree = "2".equals(reservationType);
		s.minutes  = s.planFree ? parkedMinutes : overMinutes;
		s.units    = s.planFree ? Math.max(1, units(parkedMinutes)) : units(overMinutes);
		s.base     = s.planFree ? 0 : estimate;
		s.extra    = s.units * UNIT_PRICE;
		s.total    = s.base + s.extra;
		s.prepaid  = prepaid;
		s.due      = Math.max(0, s.total - prepaid);
		return s;
	}

	public static Settlement settle(Map<String, Object> view) {
		return settle(String.valueOf(view.get("reservation_type")),
				num(view.get("parked_minutes")), num(view.get("over_minutes")),
				(int) num(view.get("estimate")), (int) num(view.get("paid_prepay")));
	}

	private static long num(Object o) {
		return o instanceof Number ? ((Number) o).longValue() : 0;
	}

	public static class Settlement {
		private boolean planFree;
		private long minutes;
		private int units, base, extra, total, prepaid, due;

		public boolean isPlanFree() { return planFree; }
		public long getMinutes()    { return minutes; }
		public int getUnits()       { return units; }
		public int getBase()        { return base; }
		public int getExtra()       { return extra; }
		public int getTotal()       { return total; }
		public int getPrepaid()     { return prepaid; }
		public int getDue()         { return due; }
	}

}
