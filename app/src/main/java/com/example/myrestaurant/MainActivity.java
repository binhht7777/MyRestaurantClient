package com.example.myrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/*
fix error Migrate an existing project using Android Studio: them enableJetifier = true
android.useAndroidX=true
android.enableJetifier=true
* */
public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp;
    TextInputEditText edtPhone, edtPassword;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        edtPassword = (TextInputEditText) findViewById(R.id.edtPassword);
        edtPhone = (TextInputEditText) findViewById(R.id.edtPhone);

        init();


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY, edtPhone.getText().toString(), edtPassword.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userModel -> {
                                    if (userModel.isSuccess()) {
                                        Common.currentUser = userModel.getResult().get(0);
                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
//                                        Intent intent = new Intent(MainActivity.this, UpdateInfoActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                        Toast.makeText(MainActivity.this, "User không tồn tại, Vui lòng đăng ký.!", Toast.LENGTH_SHORT).show();

                                    }
                                    dialog.dismiss();
                                },
                                throwable -> {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "[GET USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }
}