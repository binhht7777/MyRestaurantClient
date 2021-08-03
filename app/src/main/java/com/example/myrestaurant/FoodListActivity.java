package com.example.myrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myrestaurant.Adapter.MyFoodAdapter;
import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.EventBus.FoodListEvent;
import com.example.myrestaurant.Model.Food;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;
import com.example.myrestaurant.Utils.SpacesItemDecoration;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodListActivity extends AppCompatActivity {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;

    @BindView(R.id.img_category)
    ImageView img_category;

    @BindView(R.id.recycler_food_list)
    RecyclerView recycler_food_list;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MyFoodAdapter adapter;

    // Khai bao 1 recyclerview moi
    RecyclerView rcvFoodList;
    MyFoodAdapter foodAdapter;
    SearchView searchView;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        if (adapter != null) {
            adapter.onStop();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_food_list.setLayoutManager(layoutManager);
        recycler_food_list.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        // Khai bao 1 recyclerview moi
        rcvFoodList = findViewById(R.id.recycler_food_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvFoodList.setLayoutManager(linearLayoutManager);


    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadFoodListByCategpory(FoodListEvent event) {
        if (event.isSuccess()) {
            Picasso.get().load(event.getCategory().getImage()).into(img_category);
            toolbar.setTitle(event.getCategory().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            dialog.show();
            compositeDisposable.add(myRestaurantAPI.getFoodOfMenu(Common.API_KEY,
                    event.getCategory().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {
                                if (foodModel.isSuccess()) {
                                    adapter = new MyFoodAdapter(this, foodModel.getResult(), this::selectedFood);
                                    recycler_food_list.setAdapter(adapter);

                                    // set adapter search food
                                    foodAdapter = new MyFoodAdapter(this, foodModel.getResult(), this::selectedFood);
                                    rcvFoodList.setAdapter(foodAdapter);
                                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                                    rcvFoodList.addItemDecoration(itemDecoration);

                                } else {
                                    Toast.makeText(this, "[GET FOOD RESULT]" + foodModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            },
                            throwable -> {
                                dialog.dismiss();
                                Toast.makeText(this, "[GET FOOD]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.nav_food_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                foodAdapter.getFilter().filter(query);
                recycler_food_list.setAdapter(foodAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                foodAdapter.getFilter().filter(newText);
                recycler_food_list.setAdapter(foodAdapter);
                return true;
            }
        });

        return true;
    }

    public void selectedFood(Food foodModel) {
        // BinhPT06 - Get  categoryId and send to new activity
        Intent foodDetail = new Intent(FoodListActivity.this, FoodDetailActivity.class);
        foodDetail.putExtra("FoodId", foodModel.getId());
        startActivity(foodDetail);
    }
}