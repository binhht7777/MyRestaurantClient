package com.example.myrestaurant.Model;

public class Category {
    private int Id;
    private String Name, Description, Image;

    public Category(int id, String name, String description, String image) {
        Id = id;
        Name = name;
        Description = description;
        Image = image;
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
}
