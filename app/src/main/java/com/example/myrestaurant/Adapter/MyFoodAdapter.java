package com.example.myrestaurant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.example.myrestaurant.FavoriteActivity;
import com.example.myrestaurant.FoodDetailActivity;
import com.example.myrestaurant.Interface.IFoodDetailOrCartClickListener;
import com.example.myrestaurant.Model.FavoriteOnlyId;
import com.example.myrestaurant.Model.Food;
import com.example.myrestaurant.R;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder> implements Filterable {
    Context context;
    List<Food> foodList;
    List<Food> foodListOld;
    private MyFoodAdapter.SelectedFood selectedFood;

    CompositeDisposable completDisposable;
    CartDataSource cartDataSource;
    IMyRestaurantAPI myRestaurantAPI;


    public void onStop() {
        completDisposable.clear();

    }

    public MyFoodAdapter(Context context, List<Food> foodList, SelectedFood selectedFood) {
        this.context = context;
        this.foodList = foodList;
        this.foodListOld = foodList;
        this.selectedFood = selectedFood;
        completDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
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

        //check favorite
        if (Common.currentFavoriteRestaurant != null && Common.currentFavoriteRestaurant.size() > 0) {
            if (Common.checkFavorite(foodList.get(position).getId())) {
                holder.img_fav.setImageResource(R.drawable.ic_baseline_favorite_24);
                holder.img_fav.setTag(true);
            } else {
                holder.img_fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                holder.img_fav.setTag(false);
            }
        } else {
            holder.img_fav.setTag(false);
        }
        holder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView fav = (ImageView) v;
                if ((Boolean) fav.getTag()) {
                    completDisposable.add(myRestaurantAPI.removeFavorite(Common.API_KEY,
                            Common.currentUser.getFbid(),
                            foodList.get(position).getId(),
                            Common.currentRestaurant.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(favoriteModel -> {
                                        if (favoriteModel.isSuccess() && favoriteModel.getMessage().contains("Success")) {
                                            fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                            fav.setTag(false);
                                            if (Common.currentFavoriteRestaurant != null) {
                                                Common.removeFavorite(foodList.get(position).getId());
                                            }
                                        }
                                    },
                                    throwable -> {
//                                        Toast.makeText(context, "[REMOVE FAV]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                } else {
                    completDisposable.add(myRestaurantAPI.insertFavorite(Common.API_KEY,
                            Common.currentUser.getFbid(),
                            foodList.get(position).getId(),
                            Common.currentRestaurant.getId(),
                            Common.currentRestaurant.getName(),
                            foodList.get(position).getName(),
                            foodList.get(position).getImage(),
                            foodList.get(position).getPrice())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(favoriteModel -> {
                                        if (favoriteModel.isSuccess() && favoriteModel.getMessage().contains("Success")) {
                                            fav.setImageResource(R.drawable.ic_baseline_favorite_24);
                                            fav.setTag(true);
                                            if (Common.currentFavoriteRestaurant != null) {
                                                Common.currentFavoriteRestaurant.add(new FavoriteOnlyId(foodList.get(position).getId()));
                                            }
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(context, "[ADD FAV]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                }
            }
        });
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    foodList = foodListOld;
                } else {
                    List<Food> list = new ArrayList<>();
                    for (Food food : foodListOld) {
                        if (food.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(food);
                        }
                    }
                    foodList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = foodList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                foodList = (List<Food>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface SelectedFood {
        void selectedFood(Food foodModel);

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

        @BindView(R.id.img_fav)
        ImageView img_fav;

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
            selectedFood.selectedFood(foodList.get(getAdapterPosition()));

        }

    }
}
