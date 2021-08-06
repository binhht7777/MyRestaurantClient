package com.example.myrestaurant.Common;

import com.example.myrestaurant.Model.Addon;
import com.example.myrestaurant.Model.Favorite;
import com.example.myrestaurant.Model.FavoriteOnlyId;
import com.example.myrestaurant.Model.Restaurant;
import com.example.myrestaurant.Model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Common {
    public static String API_RESTAURANT_ENDPOINT = "http://192.168.1.4:3000/";
    public static final String API_KEY = "1234";
    public static User currentUser;
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static Set<Addon> addonList = new HashSet<>();
    public static Restaurant currentRestaurant;
    public static List<FavoriteOnlyId> currentFavoriteRestaurant;

    public static boolean checkFavorite(int id) {
        boolean result = false;
        for (FavoriteOnlyId item : currentFavoriteRestaurant) {
            if (item.getFoodId() == id) {
                result = true;
            }
        }
        return result;
    }

    public static void removeFavorite(int id) {
        for (FavoriteOnlyId item : currentFavoriteRestaurant) {
            if (item.getFoodId() == id) {
                currentFavoriteRestaurant.remove(item);
            }
        }
    }
}
