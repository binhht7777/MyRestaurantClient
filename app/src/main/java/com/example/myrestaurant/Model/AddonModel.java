package com.example.myrestaurant.Model;

import java.util.List;

public class AddonModel {
    private boolean success;
    private List<Addon> result;
    private  String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Addon> getResult() {
        return result;
    }

    public void setResult(List<Addon> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
