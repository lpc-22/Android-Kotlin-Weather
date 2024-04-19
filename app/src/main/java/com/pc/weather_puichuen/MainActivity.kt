package com.pc.weather_puichuen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.pc.weather_puichuen.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.pc.weather_puichuen.api.MyInterface
import com.pc.weather_puichuen.api.RetrofitInstance
import com.pc.weather_puichuen.data.History
import com.pc.weather_puichuen.data.HistoryRepository
import com.pc.weather_puichuen.models.Weather
import kotlinx.coroutines.launch
import java.util.Locale
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var geocoder:Geocoder
    private var api: MyInterface = RetrofitInstance.retrofitService

    private val TAG = "MainActivity"
    private val API_KEY = "J6XML7D5ZQFTQ64XA7YCQG7PY"

    private lateinit var foundLocation: Address
    private lateinit var currWeather: Weather

    private lateinit var historyRepository: HistoryRepository

    // ==================== Permission ====================
    // Permissions Array
    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    // Show permission dialog
    private val multiplePermissionResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){ resultsList ->
        Log.d(TAG, resultsList.toString())

        var anyPermissionsGrantedTracker = false
        for(item in resultsList.entries){
            if(item.key in APP_PERMISSIONS_LIST && item.value == true){
                anyPermissionsGrantedTracker = true
            }
        }

        if (anyPermissionsGrantedTracker == true){
            // At least one permission granted
            binding.tvWeatherReport.text = "Please restart the application"
        }else{
            // No Permissions is Granted
            binding.tvWeatherReport.text = "Sorry, you need to give us permissions before we can get your location and weather information. Check your settings menu and update your location permission for this app."
        }
    }
    // Check permission
    private fun checkPermission(): Boolean{
        // Check Permission
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // Permission Not Granted
            // Ask for permission
            multiplePermissionResultLauncher.launch(APP_PERMISSIONS_LIST)
            return false
        }else{
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        geocoder = Geocoder(this, Locale.getDefault())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Update location for every 10 seconds
        locationRequest = LocationRequest.create().setInterval(1000)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations){
                    Log.d(TAG, "locationResult: ${location}")
                }
            }
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        this.historyRepository = HistoryRepository(application)

        // Get Current location
        getDeviceLocationToUI()

        binding.btnGetReport.setOnClickListener {
            binding.tvWeatherReport.text = ""
            val cityFromUI: String = binding.etCity.text.toString()
            if (cityFromUI == null){
                 binding.etCity.setError("Please enter a city")
                return@setOnClickListener
            }
            // Turn city name into Latitude and Longitude
            try {
                val searchResults = geocoder.getFromLocationName(cityFromUI, 1)
                // No searchResults
                if (searchResults == null || searchResults.size == 0){
                    Toast.makeText(this, "Location Not Found", Toast.LENGTH_SHORT).show()
                    binding.llResult.visibility = View.GONE
                    return@setOnClickListener
                }
                // Have at least one result
                if (searchResults.size > 0){
                    foundLocation = searchResults.get(0)
                    getWeatherToUI()
                }
            }catch (ex: Exception){
                Log.e(TAG, "btnGetReport: Couldn't get the LatLng for the given address "+ex)
            }

        }

        binding.btnGetCurrentLocation.setOnClickListener {
            getDeviceLocationToUI()
        }

        binding.btnSaveToHistory.setOnClickListener {
            if(foundLocation != null && currWeather != null){
                // Save foundLocation & currWeather in Room db
                val newHistory = History(foundLocation.getAddressLine(0), currWeather.currentConditions.datetime, currWeather.currentConditions.temp, currWeather.currentConditions.humidity, currWeather.currentConditions.conditions)
                Log.d(TAG, "Saving: ${newHistory}")
                lifecycleScope.launch {
                    historyRepository.insertHistory(newHistory)
                    Toast.makeText(this@MainActivity, "Saved", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Unable to save. Please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDeviceLocationToUI(){
        if(checkPermission()){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Got last known location
                if(location === null){
                    Log.d(TAG, "onResume: location: ${location}")
                    binding.tvWeatherReport.text = "Unable to access your current location. Please check your permission."
                    binding.btnSaveToHistory.visibility = View.GONE
                    return@addOnSuccessListener
                }
                binding.btnSaveToHistory.visibility = View.VISIBLE
                // Get location name by latitude and longitude
                val tmp = getLocationByLatLon(location.latitude, location.longitude)
                if(tmp != null){
                    foundLocation = tmp
                }
                // Get Weather
                getWeatherToUI()

            }.addOnFailureListener {e ->
                Log.e(TAG, "Unable to get device location${e}")
            }
        }else{
            binding.tvWeatherReport.text = "No Permission"
            binding.btnSaveToHistory.visibility = View.GONE
        }
    }

    private fun getWeatherToUI(){
        lifecycleScope.launch {
            currWeather = api.getWeatherByLatLong(foundLocation.latitude, foundLocation.longitude)
            Log.d(TAG, "currWeather"+currWeather.toString())
            if(currWeather != null){
                binding.tvWeatherReport.text = ""
                binding.tvWeatherReport.text = "Location: ${foundLocation.getAddressLine(0)}\nTime: ${currWeather.currentConditions.datetime}\nTemperature: ${currWeather.currentConditions.temp}℃\nHumidity: ${currWeather.currentConditions.humidity}%\nCondition: ${currWeather.currentConditions.conditions}"
                binding.llResult.visibility = View.VISIBLE
                binding.btnSaveToHistory.visibility = View.VISIBLE
            }else{
                binding.llResult.visibility = View.GONE
            }
        }
    }

    private fun getLocationByLatLon(latitude: Double, longitude: Double): Address?{
        try {
            val searchResults:MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (searchResults == null){
                Log.d(TAG, "Location not found")
                return null
            }
            if (searchResults.size > 0){
                val addressObject = searchResults.get(0)
                return addressObject
            }
        }catch (exception: java.lang.Exception){
            Log.e(TAG, "Error: ${exception}")
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if(checkPermission()){
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }else{
            Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // ==================== Menu ====================
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.default_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_history -> {
                // TODO: Start new activity
                val historyIntent = Intent(this@MainActivity, HistoryActivity::class.java)
                startActivity(historyIntent)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
}