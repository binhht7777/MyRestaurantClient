package com.example.myrestaurant.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {
    @Query("Select * From Cart Where userPhone=:userPhone And restaurantId=:restaurantId")
    Flowable<List<CartItem>> getAllCart(String userPhone, int restaurantId);

    @Query("Select Count(*) From Cart Where userPhone=:userPhone And restaurantId=:restaurantId")
    Single<Integer> countCart(String userPhone, int restaurantId);

    @Query("Select SUM(foodPrice*foodQuantity)+(foodExtraPrice*foodQuantity) From Cart Where userPhone=:userPhone And restaurantId=:restaurantId")
    Single<Long> sumPrice(String userPhone, int restaurantId);

    @Query("Select * From Cart Where foodId=:foodId And userPhone=:userPhone And restaurantId=:restaurantId")
    Single<CartItem> getItemInCart(String foodId, String userPhone, int restaurantId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCart(CartItem cart);

    @Delete
    Single<Integer> deleteCart(CartItem cart);

    @Query("Delete From Cart Where userPhone=:userPhone And restaurantId=:restaurantId")
    Single<Integer> cleanCart(String userPhone, int restaurantId);
}
