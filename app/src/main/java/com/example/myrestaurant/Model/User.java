package com.example.myrestaurant.Model;

public class User {
    private String Fbid, UserPhone, Name, Address, Password;

    public User(String fbid, String userPhone, String name, String address, String password) {
        Fbid = fbid;
        UserPhone = userPhone;
        Name = name;
        Address = address;
        Password = password;
    }

    public String getFbid() {
        return Fbid;
    }

    public void setFbid(String fbid) {
        Fbid = fbid;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
