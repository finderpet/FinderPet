package com.finder.pet.ui.donate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DonateViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DonateViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragment Donate");
    }

    public LiveData<String> getText() {
        return mText;
    }
}