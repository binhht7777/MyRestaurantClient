package com.example.myrestaurant.Common;

import com.example.myrestaurant.Model.Restaurant;
import com.example.myrestaurant.Model.User;

public class Common {
    public static String API_RESTAURANT_ENDPOINT = "http://192.168.1.4:3000/";
    public static final String API_KEY = "1234";
    public static User currentUser;
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static Restaurant currentRestaurant;
}
