package com.example.myrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myrestaurant.Adapter.MyRestaurantAdapter;
import com.example.myrestaurant.Adapter.RestaurantSliderAdapter;
import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.EventBus.RestaurantLoadEvent;
import com.example.myrestaurant.Model.Restaurant;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;

import com.example.myrestaurant.Services.PicassoImageLoadingService;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrestaurant.databinding.ActivityHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;


public class HomeActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    TextView tvUserName, tvUserPhone;

//    @BindView(R.id.banner_slider)
//    Slider banner_slider;

    @BindView(R.id.recycler_restaurant)
    RecyclerView recycler_restaurant;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);
//        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_signout) {
                    SignOut();
                } else if (id == R.id.nav_nearby) {
                    Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                } else if (id == (R.id.nav_order_history)) {

                } else if (id == (R.id.nav_update_info)) {

                }
                return true;
            }
        });

        View headerView = navigationView.getHeaderView(0);
        tvUserName = (TextView) headerView.findViewById(R.id.tvUserName);
        tvUserPhone = (TextView) headerView.findViewById(R.id.tvUserPhone);

        tvUserName.setText(Common.currentUser.getName());
        tvUserPhone.setText(Common.currentUser.getUserPhone());
        init();
        initView();
        LoadRestaurant();

    }


    private void LoadRestaurant() {
        dialog.show();
        compositeDisposable.add(myRestaurantAPI.getRestaurant(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantModel -> {
                            EventBus.getDefault().post(new RestaurantLoadEvent(true, restaurantModel.getResult()));

                        },
                        throwable -> {
                            EventBus.getDefault().post(new RestaurantLoadEvent(false, throwable.getMessage()));
                        }));
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_restaurant.setLayoutManager(layoutManager);
        recycler_restaurant.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }

    private void SignOut() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn muốn đăng xuất.?")
                .setNegativeButton("Bỏ qua", ((dialog, i) -> dialog.dismiss()))
                .setPositiveButton("Đồng ý", ((dialog, i) -> {
                    Common.currentUser = null;
                    Common.currentRestaurant = null;
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 210, 230));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 210, 230));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString spannableString = new SpannableString(
                    menu.getItem(i).getTitle().toString()
            );
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    spannableString.length(), 0);
            menuItem.setTitle(spannableString);
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        Slider.init(new PicassoImageLoadingService());
    }

    // Register event BUS

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processRestaurantLoadEvent(RestaurantLoadEvent event) {
        if (event.isSuccess()) {
            displayBanner(event.getRestaurantList());
            displayRestaurant(event.getRestaurantList());
        } else {
            Toast.makeText(this, "[RESTAURANT LOAD]" + event.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    private void displayRestaurant(List<Restaurant> restaurantList) {
        MyRestaurantAdapter adapter = new MyRestaurantAdapter(this, restaurantList);
        recycler_restaurant.setAdapter(adapter);
    }

    private void displayBanner(List<Restaurant> restaurantList) {
//        banner_slider.setAdapter(new RestaurantSliderAdapter(restaurantList));
    }
}