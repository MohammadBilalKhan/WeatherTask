package com.example.weathertask.utils

import android.app.Activity
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object Constants {

    const val BASE_URL = "https://goweather.herokuapp.com"

    const val PACKAGE_NAME = "com.example.currentaddress"
    const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
    const val RECEVIER = "$PACKAGE_NAME.RECEVIER"
    const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"

    const val ADDRESS = "$PACKAGE_NAME.ADDRESS"
    const val LOCAITY = "$PACKAGE_NAME.LOCAITY"
    const val COUNTRY = "$PACKAGE_NAME.COUNTRY"
    const val DISTRICT = "$PACKAGE_NAME.DISTRICT"
    const val POST_CODE = "$PACKAGE_NAME.POST_CODE"
    const val STATE = "$PACKAGE_NAME.STATE"

    const val SUCCESS_RESULT = 1
    const val FAILURE_RESULT = 0

    const val FAV_LIST = "fav list"

   }