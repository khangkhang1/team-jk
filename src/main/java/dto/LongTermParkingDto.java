package dto;

// 장기주차장 P1~P5의 혼잡도 계산 결과를 담는 DTO
public class LongTermParkingDto {

    private String parkLotNo;

    private int totalCount;

    private int occupiedCount;

    private int availableCount;

    private double occupancyRate;

    private String congestion;

    public LongTermParkingDto() {

    }

    public LongTermParkingDto(String parkLotNo, int totalCount, int occupiedCount) {

        this.parkLotNo = parkLotNo;

        this.totalCount = totalCount;

        this.occupiedCount = occupiedCount;

        this.availableCount = Math.max(0,totalCount - occupiedCount);

        if (totalCount > 0) {
            this.occupancyRate =
                    (double) occupiedCount / totalCount * 100;
        } else {
            this.occupancyRate = 0;
        }

        if (occupancyRate >= 95) {
            this.congestion = "매우 혼잡";

        } else if (occupancyRate >= 80) {
            this.congestion = "혼잡";

        } else if (occupancyRate >= 50) {
            this.congestion = "보통";

        } else {
            this.congestion = "여유";
        }
    }

    public String getParkLotNo() {
        return parkLotNo;
    }

    public void setParkLotNo(String parkLotNo) {
        this.parkLotNo = parkLotNo;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getOccupiedCount() {
        return occupiedCount;
    }

    public void setOccupiedCount(int occupiedCount) {
        this.occupiedCount = occupiedCount;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public String getCongestion() {
        return congestion;
    }

    public void setCongestion(String congestion) {
        this.congestion = congestion;
    }

    // ------------------------------------------------------------
    // 2026-09-11 추가 : 화면 "실시간 배지"에 쓰는 두 값.
    //   /Parking(강선구) 쪽으로 나뉘어 있던 구현을 여기로 통일하면서,
    //   그쪽 응답에만 있던 항목을 옮겨온 것이다.
    //   floor  : 공공데이터가 주는 실제 구역명 (예: "T1 장기 P1 주차장")
    //            우리 화면 라벨(P1~P9)은 팀이 임의로 붙인 것이라, 발표 때
    //            "실제로 어느 주차장 데이터인지"를 보여주려면 이 값이 필요하다.
    //   dateTm : 집계 시각. 화면에 "OO시 기준"으로 표시한다.
    // ------------------------------------------------------------
    private String floor;
    private String dateTm;

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDateTm() {
        return dateTm;
    }

    public void setDateTm(String dateTm) {
        this.dateTm = dateTm;
    }
}
