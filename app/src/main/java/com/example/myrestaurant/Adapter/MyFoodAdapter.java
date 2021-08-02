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
import com.example.myrestaurant.Database.CartDataSource;
import com.example.myrestaurant.Database.CartDatabase;
import com.example.myrestaurant.Database.CartItem;
import com.example.myrestaurant.Database.LocalCartDataSource;
import com.example.myrestaurant.EventBus.FoodDetailEvent;
import com.example.myrestaurant.FoodDetailActivity;
import com.example.myrestaurant.Interface.IFoodDetailOrCartClickListener;
import com.example.myrestaurant.Model.Food;
import com.example.myrestaurant.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder> {
    Context context;
    List<Food> foodList;

    CompositeDisposable completDisposable;
    CartDataSource cartDataSource;


    public void onStop() {
        completDisposable.clear();

    }

    public MyFoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
        completDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Picasso.get().load(foodList.get(position).getImage())
                .placeholder(null)
                .into(holder.img_food);
        holder.txt_food_name.setText(foodList.get(position).getName());
        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(foodList.get(position).getPrice()));
        holder.txt_food_name.setText(foodList.get(position).getName());
        holder.setListener((view, position1, isDetail) -> {
            if (isDetail) {
                context.startActivity(new Intent(context, FoodDetailActivity.class));
                EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodList.get(position)));
            } else {
                // cart create
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(foodList.get(position).getId());
                cartItem.setFoodName(foodList.get(position).getName());
                cartItem.setFoodPrice(foodList.get(position).getPrice());
                cartItem.setFoodNIamge(foodList.get(position).getImage());
                cartItem.setFoodQuantity(1);
                cartItem.setUserPhone(Common.currentUser.getUserPhone());
                cartItem.setRestaurantId(Common.currentRestaurant.getId());
                cartItem.setFoodAddon("Normal");
                cartItem.setFoodSize("Normal");
                cartItem.setFoodExtraPrice(0.0);
                completDisposable.add(
                        cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
                                        },
                                        throwable -> {
                                            Toast.makeText(context, "[ADD CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food)
        ImageView img_food;

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;

        @BindView(R.id.txt_food_price)
        TextView txt_food_price;

        @BindView(R.id.img_detail)
        ImageView img_detail;

        @BindView(R.id.img_card)
        ImageView img_card;

        IFoodDetailOrCartClickListener listener;

        public void setListener(IFoodDetailOrCartClickListener listener) {
            this.listener = listener;
        }

        Unbinder unbinder;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

            img_detail.setOnClickListener(this);
            img_card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_detail) {
                listener.onFoodItemClickListener(v, getAdapterPosition(), true);
            } else if (v.getId() == R.id.img_card) {
                listener.onFoodItemClickListener(v, getAdapterPosition(), false);
            }

        }

    }
}
