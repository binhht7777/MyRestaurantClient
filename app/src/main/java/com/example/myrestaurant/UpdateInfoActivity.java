package com.example.myrestaurant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateInfoActivity extends AppCompatActivity {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @BindView(R.id.edtUserName)
    EditText edtUserName;

    @BindView(R.id.edtUserAddress)
    EditText edtUserAddress;

    @BindView(R.id.btnUpdate)
    Button btnUpdate;

    @BindView(R.id.edtPhoneNumber)
    EditText edtPhoneNumber;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    String fbid = "";

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        ButterKnife.bind(this);
        init();
        initView();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
//        toolbar.setTitle(getString(R.string.update_information));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fbid = UUID.randomUUID().toString();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                compositeDisposable.add(myRestaurantAPI.updateUserInfo(Common.API_KEY,
                        edtPhoneNumber.getText().toString(),
                        edtUserName.getText().toString(),
                        edtUserAddress.getText().toString(),
                        fbid.toLowerCase(),
                        edtPassword.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updateUserModel -> {
                                    if (updateUserModel.isSuccess()) {
                                        compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY, edtPhoneNumber.getText().toString(), edtPassword.getText().toString())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(userModel -> {
                                                            if (userModel.isSuccess()) {
                                                                Common.currentUser = userModel.getResult().get(0);
                                                                startActivity(new Intent(UpdateInfoActivity.this, HomeActivity.class));
                                                                finish();
                                                            } else {
                                                                Toast.makeText(UpdateInfoActivity.this, "GET USER RETURN" + userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }

                                                            dialog.dismiss();
                                                        },
                                                        throwable -> {
                                                            dialog.dismiss();
                                                            Toast.makeText(UpdateInfoActivity.this, "[GET USER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        })
                                        );
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(UpdateInfoActivity.this, "[UPDATE USER API RETURN]" + updateUserModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> {
                                    dialog.dismiss();
                                    Toast.makeText(UpdateInfoActivity.this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
            }
        });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }
}