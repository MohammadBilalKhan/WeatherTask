package com.example.weathertask.repository

import com.example.weathertask.api.ApiService
import javax.inject.Inject

class WeatherRepository
@Inject
constructor(private  val apiService: ApiService)
{
    suspend fun getWeather(city:String) = apiService.getWeather(city)

}