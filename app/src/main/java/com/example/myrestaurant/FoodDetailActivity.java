package com.example.myrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myrestaurant.Adapter.MyAddonAdapter;
import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.Database.CartDataSource;
import com.example.myrestaurant.Database.CartDatabase;
import com.example.myrestaurant.Database.CartItem;
import com.example.myrestaurant.Database.LocalCartDataSource;
import com.example.myrestaurant.EventBus.AddonEventChange;
import com.example.myrestaurant.EventBus.AddonLoadEvent;
import com.example.myrestaurant.EventBus.FoodDetailEvent;
import com.example.myrestaurant.EventBus.SizeLoadEvent;
import com.example.myrestaurant.Model.Addon;
import com.example.myrestaurant.Model.Food;
import com.example.myrestaurant.Model.Size;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
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

public class FoodDetailActivity extends AppCompatActivity {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;
    CartDataSource cartDataSource;

    @BindView(R.id.fad_add_to_cart)
    FloatingActionButton fad_add_to_cart;

    @BindView(R.id.btn_view_cart)
    Button btn_view_cart;

    @BindView(R.id.txt_money)
    TextView txt_money;

    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;

    @BindView(R.id.recycler_addon)
    RecyclerView recycler_addon;

    @BindView(R.id.txt_description)
    TextView txt_description;

    @BindView(R.id.img_food_detail)
    ImageView img_food_detail;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Food selectedFood;
    Double oriPrice;
    private double sizePrice = 0.0;
    private String sizeSelected;
    private Double addonPrice = 0.0;
    private Double extraPrice = 0.0;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        fad_add_to_cart.setOnClickListener(v -> {
            CartItem cartItem = new CartItem();
            cartItem.setFoodId(selectedFood.getId());
            cartItem.setFoodName(selectedFood.getName());
            cartItem.setFoodPrice(selectedFood.getPrice());
            cartItem.setFoodNIamge(selectedFood.getImage());
            cartItem.setFoodQuantity(1);
            cartItem.setUserPhone(Common.currentUser.getUserPhone());
            cartItem.setRestaurantId(Common.currentRestaurant.getId());
            cartItem.setFoodAddon(new Gson().toJson(Common.addonList));
            cartItem.setFoodSize(sizeSelected);
            cartItem.setFoodExtraPrice(extraPrice);

            compositeDisposable.add(
                    cartDataSource.insertOrReplaceAll(cartItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                        Toast.makeText(FoodDetailActivity.this, "Add to cart", Toast.LENGTH_SHORT).show();
                                    },
                                    throwable -> {
                                        Toast.makeText(FoodDetailActivity.this, "[ADD CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
            );
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
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
    public void displayFoodDetail(FoodDetailEvent event) {
        if (event.isSuccess()) {
            toolbar.setTitle(event.getFood().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            selectedFood = event.getFood();
            oriPrice = event.getFood().getPrice();
            txt_money.setText(String.valueOf(oriPrice));
            txt_description.setText(event.getFood().getDescription());
            Picasso.get().load(event.getFood().getImage()).into(img_food_detail);
            if (event.getFood().isSize() && event.getFood().isAddon()) {
                // load sise and addon from server
                dialog.show();
                compositeDisposable.add(myRestaurantAPI.getSizeOfFood(Common.API_KEY, event.getFood().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sizeModel -> {
                                    EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));

                                    // load addon after liad size
                                    dialog.show();
                                    compositeDisposable.add(myRestaurantAPI.getAddonOfFood(Common.API_KEY, event.getFood().getId())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(addonModel -> {
                                                        dialog.dismiss();
                                                        EventBus.getDefault().post(new AddonLoadEvent(true, addonModel.getResult()));
                                                    },
                                                    throwable -> {
                                                        dialog.dismiss();
                                                        Toast.makeText(this, "[LOAD ADDON]", Toast.LENGTH_SHORT).show();
                                                    }
                                            ));
                                },
                                throwable -> {
                                    dialog.dismiss();
                                    Toast.makeText(this, "[LOAD SIE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
            } else {
                if (event.getFood().isSize()) {
                    dialog.show();
                    compositeDisposable.add(myRestaurantAPI.getSizeOfFood(Common.API_KEY, event.getFood().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(sizeModel -> {
                                        EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));
                                    },
                                    throwable -> {
                                        dialog.dismiss();
                                        Toast.makeText(this, "[LOAD SIE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
                }
                if (event.getFood().isAddon()) {
                    dialog.show();
                    compositeDisposable.add(myRestaurantAPI.getAddonOfFood(Common.API_KEY, event.getFood().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(addonModel -> {
                                        dialog.dismiss();
                                        EventBus.getDefault().post(new AddonLoadEvent(true, addonModel.getResult()));
                                    },
                                    throwable -> {
                                        dialog.dismiss();
                                        Toast.makeText(this, "[LOAD ADDON]", Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displaySize(SizeLoadEvent event) {
        if (event.isSuccess()) {
            // create radio button base on size lenght
            for (Size size : event.getSizeList()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            sizePrice = size.getExtraPrice();
                        } else {
                            sizePrice = -size.getExtraPrice();
                        }
                        calculatorPrice();
                        sizeSelected = size.getDescription();
                    }
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                radioButton.setLayoutParams(params);
                radioButton.setText(size.getDescription());
                radioButton.setTag(size.getExtraPrice());
                radioButton.setTextColor(Color.rgb(0, 153, 188));
                radioButton.setPadding(0, 0, 20, 0);
                if (size.getDescription() == "Large") {
                    radioButton.setChecked(true);
                }
                rdi_group_size.addView(radioButton, params);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayAddon(AddonLoadEvent event) {
        if (event.isSuccess()) {
            recycler_addon.setHasFixedSize(true);
            recycler_addon.setLayoutManager(new LinearLayoutManager(this));
            recycler_addon.setAdapter(new MyAddonAdapter(FoodDetailActivity.this, event.getAddonList()));
        }
    }

    private void calculatorPrice() {
        extraPrice = 0.0;
        double newPrice;

        extraPrice += sizePrice;
        extraPrice += addonPrice;
        newPrice = oriPrice + extraPrice;
        txt_money.setText(String.valueOf(newPrice));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void priceChange(AddonEventChange eventChange) {
        if (eventChange.isAdd()) {
            addonPrice += eventChange.getAddon().getExtraPrice();
        } else {
            addonPrice -= eventChange.getAddon().getExtraPrice();
        }
        calculatorPrice();
    }

}