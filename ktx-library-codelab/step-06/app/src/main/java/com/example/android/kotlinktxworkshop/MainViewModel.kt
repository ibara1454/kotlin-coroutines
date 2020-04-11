package com.example.android.kotlinktxworkshop

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.android.myktxlibrary.asString
import com.example.android.myktxlibrary.awaitLastLocation
import com.example.android.myktxlibrary.locationFlow
import com.example.android.myktxlibrary.wrapByResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.*


class MainViewModel(context: Application) : AndroidViewModel(context) {
    // The fused location client
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(getApplication<Application>())

    // The flow of locations.
    // Which is started by the latest received location.
    private val locationFlow
        get(): Flow<Result<Location>> =
            fusedLocationClient.locationFlow()
                // Emit the latest location if it is not null
                .onStart { fusedLocationClient.awaitLastLocation()?.also { emit(it) } }
                .conflate()
                .wrapByResult()

    val locationLiveData: LiveData<String> =
        locationFlow
            .map { result ->
                result.fold(
                    onSuccess = { it.asString(Location.FORMAT_MINUTES) },
                    onFailure = {
                        Log.d(TAG, "Unable to get location")
                        "Unable to get location. $it"
                    }
                )
            }
            .asLiveData()
}