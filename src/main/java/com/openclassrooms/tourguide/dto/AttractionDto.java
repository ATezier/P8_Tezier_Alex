package com.openclassrooms.tourguide.dto;

public class AttractionDto {
    private String name;
    private double attractionLatitude;
    private double attractionLongitude;
    private double userLatitude;
    private double userLongitude;
    private double distance;
    private int rewardPoints;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getAttractionLatitude() {
        return attractionLatitude;
    }
    public void setAttractionLatitude(double attractionLatitude) {
        this.attractionLatitude = attractionLatitude;
    }
    public double getAttractionLongitude() {
        return attractionLongitude;
    }
    public void setAttractionLongitude(double attractionLongitude) {
        this.attractionLongitude = attractionLongitude;
    }
    public double getUserLatitude() {
        return userLatitude;
    }
    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }
    public double getUserLongitude() {
        return userLongitude;
    }
    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public int getRewardPoints() {
        return rewardPoints;
    }
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
