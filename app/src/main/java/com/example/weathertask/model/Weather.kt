package com.example.weathertask.model

data class Weather(
    val description: String = "",
    val forecast: List<Forecast>? = null,
    val temperature: String = "",
    val wind: String = "",
    val error: Boolean
)