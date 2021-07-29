package com.example.myrestaurant.Model;

public class Food {
    private int Id, Discount;
    private String Name, Description, Image;
    private Double Price;
    private boolean IsSize, IsAddon;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getDiscount() {
        return Discount;
    }

    public void setDiscount(int discount) {
        Discount = discount;
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

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public boolean isSize() {
        return IsSize;
    }

    public void setSize(boolean size) {
        IsSize = size;
    }

    public boolean isAddon() {
        return IsAddon;
    }

    public void setAddon(boolean addon) {
        IsAddon = addon;
    }
}
