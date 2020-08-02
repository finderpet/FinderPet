package com.finder.pet.ui.maps;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MapsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Este es el fragment para mostrar el mapa");
    }

    public LiveData<String> getText() {
        return mText;
    }
}