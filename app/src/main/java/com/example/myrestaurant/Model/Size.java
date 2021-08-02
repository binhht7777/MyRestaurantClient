package com.example.myrestaurant.Model;

public class Size {
    private int Id;
    private String Description;
    private Float ExtraPrice;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public float getExtraPrice() {
        return ExtraPrice;
    }

    public void setExtraPrice(Float extraPrice) {
        ExtraPrice = extraPrice;
    }
}
