package com.sourcepointmeta.app.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.sourcepointmeta.app.StaticTestData;
import com.sourcepointmeta.app.database.AppDataBase;
import com.sourcepointmeta.app.database.dao.PropertyListDao;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.repository.PropertyListRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsentViewViewModelTest {

    private final PropertyListDao propertyListDao = mock(PropertyListDao.class);
    private final TargetingParamDao targetingParamDao = mock(TargetingParamDao.class);
    private PropertyListRepository propertyListRepository;
    private ConsentViewViewModel viewModel ;

    @Before
    public void getViewModel(){

        AppDataBase appDataBase = mock(AppDataBase.class);
        when(appDataBase.propertyListDao()).thenReturn(propertyListDao);
        when(appDataBase.targetingParamDao()).thenReturn(targetingParamDao);
        propertyListRepository = mock(PropertyListRepository.class);

        viewModel =  new ConsentViewViewModel(propertyListRepository);

    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void addProperty() {
        MutableLiveData<Long> propertyID = new MutableLiveData<>();

        Property property = StaticTestData.PROPERTIES.get(0);
        property.setId(1);
        long id = 1;

        when(propertyListDao.insert(property)).thenReturn(id);
        propertyID.postValue(propertyListDao.insert(property));
        doReturn(propertyID).when(propertyListRepository).addProperty(property);

        Observer<Long> observer = mock(Observer.class);
        viewModel.addProperty(property).observeForever(observer);

        verify(observer).onChanged(id);
    }

    @Test
    public void updateProperty() {

        MutableLiveData<Integer> propertyID = new MutableLiveData<>();
        Property property = StaticTestData.PROPERTIES.get(0);
        property.setId(1);

        when(propertyListDao.update(property.getAccountID(), property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId(), property.getId())).thenReturn(1);
        propertyID.postValue(propertyListDao.update(property.getAccountID() , property.getPropertyID(), property.getProperty(), property.getPmID(), property.isStaging(), property.isShowPM(), property.getAuthId(), property.getId()));
        doReturn(propertyID).when(propertyListRepository).updateProperty(property);


        Observer<Integer> observer = mock(Observer.class);
        viewModel.updateProperty(property).observeForever(observer);

        verify(observer).onChanged(1);
    }
}