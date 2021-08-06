package com.example.myrestaurant.Model;

public class FavoriteOnlyId {
    private int FoodId;

    public FavoriteOnlyId(int foodId) {
        this.FoodId = foodId;
    }

    public int getFoodId() {
        return FoodId;
    }

    public void setFoodId(int foodId) {
        this.FoodId = foodId;
    }
}
