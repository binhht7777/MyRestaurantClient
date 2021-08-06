package com.example.myrestaurant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.EventBus.FoodDetailEvent;
import com.example.myrestaurant.FoodDetailActivity;
import com.example.myrestaurant.Interface.IOnRecyclerViewClickListener;
import com.example.myrestaurant.Model.Favorite;
import com.example.myrestaurant.Model.Restaurant;
import com.example.myrestaurant.R;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.MyViewHolder> {
    Context context;
    List<Favorite> favoriteList;
    CompositeDisposable compositeDisposable;
    IMyRestaurantAPI myRestaurantAPI;

    public MyFavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
        compositeDisposable = new CompositeDisposable();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    public void onDestroy() {
        compositeDisposable.clear();

    }


    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_favorite_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Picasso.get().load(favoriteList.get(position).getFoodImage()).into(holder.img_food);
        holder.txt_food_name.setText(favoriteList.get(position).getFoodName());
        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(favoriteList.get(position).getPrice()));
        holder.txt_restaurant_name.setText(favoriteList.get(position).getRestaurantName());

        holder.setListener((view, position1) -> {
            compositeDisposable.add(myRestaurantAPI.getFoodById(Common.API_KEY,
                    favoriteList.get(position).getFoodId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {
                        if (foodModel.isSuccess()) {
                            context.startActivity(new Intent(context, FoodDetailActivity.class));
                            if (Common.currentRestaurant == null)
                                Common.currentRestaurant = new Restaurant();
                            Common.currentRestaurant.setId(favoriteList.get(position).getRestaurantId());
                            Common.currentRestaurant.setName(favoriteList.get(position).getRestaurantName());
                            EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodModel.getResult().get(0)));

                        } else {
                            Toast.makeText(context, "[GET FOOD BY RESULT]" + foodModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        Toast.makeText(context, "[GET FOOD BY ID]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));

        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food)
        ImageView img_food;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.txt_restaurant_name)
        TextView txt_restaurant_name;

        Unbinder unbinder;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onCLick(v, getAdapterPosition());
        }
    }
}
