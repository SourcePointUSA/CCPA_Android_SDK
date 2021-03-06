package com.sourcepointccpa.app.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sourcepointccpa.app.database.entity.Property;
import com.sourcepointccpa.app.repository.PropertyListRepository;

public class NewPropertyViewModel extends ViewModel {

    private final PropertyListRepository mPropertyListRepository;
    private final String TAG = "NewPropertyViewModel";

    public NewPropertyViewModel(PropertyListRepository repository) {
        mPropertyListRepository = repository;
    }

    public LiveData<Integer> getPropertyWithDetails(Property property){
       return mPropertyListRepository.getPropertyWithDetails(property);
    }

    public MutableLiveData<Long> addProperty(Property property) {
        return mPropertyListRepository.addProperty(property);

    }

    public MutableLiveData<Integer> updateProperty(Property property){
        return mPropertyListRepository.updateProperty(property);
    }

}
