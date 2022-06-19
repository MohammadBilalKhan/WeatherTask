package com.example.weathertask

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathertask.adapters.FavListAdapter
import com.example.weathertask.databinding.ActivityMainBinding
import com.example.weathertask.utils.Constants
import com.example.weathertask.utils.SharedPrefHelper
import com.example.weathertask.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LocationListener {


    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private var city: String? = null
    private var listOFavCities: ArrayList<String> = ArrayList<String>()
    private var adapter: FavListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.weatherResp.observe(this) { Weather ->

            if (Weather.error.not()) {
                binding.apply {
                    tvCityName.text = city
                    tvTemp.text = Weather.temperature
                    weatherCondition.text = Weather.description
                    tvWindSpeed.text = Weather.wind
                    val weather1 = Weather.forecast!![0]
                    val weather2 = Weather.forecast[0]
                    val weather3 = Weather.forecast[0]


                    tvDay1Value.text = "${weather1.temperature} / ${weather1.wind}"
                    tvDay2Value.text = "${weather2.temperature} / ${weather2.wind}"
                    tvDay3Value.text = "${weather3.temperature} / ${weather3.wind}"
                    binding.progressBar.visibility = View.GONE
                }
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Data Not Found", Toast.LENGTH_SHORT).show()
            }
        }

        handleClicks()
        setuprecyclerview()
        getCurrentLocationAndWeatherUpdates()

    }

    private fun setuprecyclerview() {

        binding.rvFavCities.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                true
            )
        )
        val prefs: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        if (prefs.getString(Constants.FAV_LIST, null) != null) {
            listOFavCities = SharedPrefHelper.getArrayList(Constants.FAV_LIST, this@MainActivity)
        }
        adapter = FavListAdapter(listOFavCities)
        adapter!!.onItemClick = {
            city = it
            viewModel.getWeather(it)
        }
        binding.rvFavCities.adapter = adapter

    }


    private fun getCurrentLocationAndWeatherUpdates() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleClicks() {
        binding.tvGo.setOnClickListener {
            if (binding.inputFindCityWeather.text.toString().isNullOrEmpty().not()) {
                binding.progressBar.visibility = View.VISIBLE
                city = binding.inputFindCityWeather.text.toString()
                viewModel.getWeather(binding.inputFindCityWeather.text.toString())
                binding.inputFindCityWeather.setText("")
            }
        }

        binding.tvFav.setOnClickListener {
            var doExist = false
            if (listOFavCities.size > 0) {
                listOFavCities.forEachIndexed { index, s ->
                    if (city == s) {
                        doExist = true
                        Toast.makeText(
                            this@MainActivity,
                            "City is already marked favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (doExist.not()) {
                    listOFavCities.add(city!!)
                }
            } else {
                listOFavCities.add(city!!)
            }
            SharedPrefHelper.saveArrayList(listOFavCities, Constants.FAV_LIST, this@MainActivity)
            adapter?.setItems(listOFavCities)
        }
    }

    private fun getCurrentLocation() {
        binding.progressBar.visibility = View.VISIBLE

        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        LocationServices.getFusedLocationProviderClient(this@MainActivity)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(applicationContext)
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0) {
                        val latestlocIndex = locationResult.locations.size - 1
                        val lati = locationResult.locations[latestlocIndex].latitude
                        val longi = locationResult.locations[latestlocIndex].longitude
                        val location = Location("providerNA")
                        location.setLongitude(longi)
                        location.setLatitude(lati)
                        onLocationChanged(location)
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }, Looper.getMainLooper())
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onLocationChanged(location: Location) {
        //You had this as int. It is advised to have Lat/Loing as double.
        //You had this as int. It is advised to have Lat/Loing as double.
        val lat: Double = location.getLatitude()
        val lng: Double = location.getLongitude()

        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            val address: List<Address> = geoCoder.getFromLocation(lat, lng, 1)
            if (address != null && address.size > 0) {
                city = address.get(0).subAdminArea
                binding.tvCityName.text = city
                viewModel.getWeather(city!!)
            }
        } catch (e: IOException) {
            // Handle IOException
        } catch (e: NullPointerException) {
            // Handle NullPointerException
        }
    }


}