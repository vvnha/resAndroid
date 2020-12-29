package com.example.dacn.ui.ordered;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderedViewModel  extends ViewModel {
    private MutableLiveData<String> mText;

    public OrderedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ordered fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
