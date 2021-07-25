package com.example.myrestaurant.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myrestaurant.Common.Common;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> mText1;

//    String userName = Common.currentUser.getName();
//    String userPhone= Common.currentUser.getUserPhone();
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText1 = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
//        mText.setValue(userName);
//        mText1.setValue(userPhone);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getText1() {
        return mText1;
    }
}