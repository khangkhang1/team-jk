package dto;
//asdf
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
}