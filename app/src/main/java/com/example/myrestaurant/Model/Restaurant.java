package com.example.myrestaurant.Model;

public class Restaurant {
    private int Id, UserOwner;
    private String Name, Address, Phone, Image, PaymentUrl;
    private float Lat, Lng;

    public Restaurant(int id, String name, String address, String phone, int userOwner, String image, String paymentUrl, float lat, float lng) {
        Id = id;
        Name = name;
        Address = address;
        Phone = phone;
        UserOwner = userOwner;
        Image = image;
        PaymentUrl = paymentUrl;
        Lat = lat;
        Lng = lng;
    }

    public Restaurant() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getUserOwner() {
        return UserOwner;
    }

    public void setUserOwner(int userOwner) {
        UserOwner = userOwner;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPaymentUrl() {
        return PaymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        PaymentUrl = paymentUrl;
    }

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        Lat = lat;
    }

    public float getLng() {
        return Lng;
    }

    public void setLng(float lng) {
        Lng = lng;
    }
}
