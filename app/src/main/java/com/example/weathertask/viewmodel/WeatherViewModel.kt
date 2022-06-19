package com.example.weathertask.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertask.model.Weather
import com.example.weathertask.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel
@Inject
constructor(private val repository: WeatherRepository) : ViewModel() {

    private val resp = MutableLiveData<Weather>()
    val weatherResp: LiveData<Weather>
        get() = resp


//    init {
//        getWeather("sheikhupura")
//    }

    fun getWeather(city: String) = viewModelScope.launch {
        repository.getWeather(city.toLowerCase()).let { response ->
            if (response.isSuccessful) {
                resp.postValue(response.body())
            } else {
                resp.postValue(Weather(error = true))
                Log.d("tag", "Error: ${response.message()}")
            }
        }

    }

}