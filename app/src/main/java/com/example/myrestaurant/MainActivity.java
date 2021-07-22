package com.example.myrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.Retrofit.IMyRestaurantAPI;
import com.example.myrestaurant.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp;
    TextInputEditText edtPhone, edtPassword;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

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
                compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY, edtPhone.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .subscribe(userModel -> {
                            if(userModel.isSuccess()){
                                Common.currentUser = userModel.getResult().get(0);

                            }else{

                            }
                                },
                                throwable -> Toast.makeText(MainActivity.this, "[GET USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show()));
            }
        });
    }

    private void init() {
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }
}