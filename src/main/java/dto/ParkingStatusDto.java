package dto;

// 인천공항공사 StatusOfParking API의 구역별 실시간 주차 현황 DTO
public class ParkingStatusDto {

    private String floor;
    private int parking;
    private int parkingArea;
    private String dateTm;

    public ParkingStatusDto() {

    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public int getParking() {
        return parking;
    }

    public void setParking(int parking) {
        this.parking = parking;
    }

    public int getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(int parkingArea) {
        this.parkingArea = parkingArea;
    }

    public String getDateTm() {
        return dateTm;
    }

    public void setDateTm(String dateTm) {
        this.dateTm = dateTm;
    }
}