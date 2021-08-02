package com.example.myrestaurant.Model;

public class Addon {
    private int Id;
    private String Name, Description;
    private float ExtraPrice;

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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public float getExtraPrice() {
        return ExtraPrice;
    }

    public void setExtraPrice(float extraPrice) {
        ExtraPrice = extraPrice;
    }
}
