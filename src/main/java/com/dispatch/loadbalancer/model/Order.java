package com.dispatch.loadbalancer.model;

public class Order {
    private String orderId;
    private double latitude;
    private double longitude;
    private String address;
    private double packageWeight;
    private String priority; // "HIGH", "MEDIUM", "LOW"

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getPackageWeight() { return packageWeight; }
    public void setPackageWeight(double packageWeight) { this.packageWeight = packageWeight; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}