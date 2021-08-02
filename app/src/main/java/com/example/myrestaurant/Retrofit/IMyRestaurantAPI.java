package com.example.myrestaurant.Retrofit;

import com.example.myrestaurant.Model.AddonModel;
import com.example.myrestaurant.Model.FoodModel;
import com.example.myrestaurant.Model.MenuModel;
import com.example.myrestaurant.Model.RestaurantModel;
import com.example.myrestaurant.Model.SizeModel;
import com.example.myrestaurant.Model.UpdateUserModel;
import com.example.myrestaurant.Model.UserModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {
    @GET("user")
    Observable<UserModel> getUser(@Query("key") String apiKey,
                                  @Query("userPhone") String userPhone,
                                  @Query("password") String password);

    // GET nha hang
    @GET("restaurant")
    Observable<RestaurantModel> getRestaurant(@Query("key") String apiKey);

    @GET("menu")
    Observable<MenuModel> getCategory(@Query("key") String apiKey, @Query("restaurantId") int restaurantId);


    @GET("food")
    Observable<FoodModel> getFoodOfMenu(@Query("key") String apiKey,
                                        @Query("menuId") int menuId);

    @GET("size")
    Observable<SizeModel> getSizeOfFood(@Query("key") String apiKey,
                                        @Query("foodId") int foodId);

    @GET("addon")
    Observable<AddonModel> getAddonOfFood(@Query("key") String apiKey,
                                          @Query("foodId") int foodId);

    // ***** METHOD POST
    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("userPhone") String userPhone,
                                               @Field("name") String userName,
                                               @Field("address") String userAddress,
                                               @Field("fbid") String fbid,
                                               @Field("password") String password);
}
