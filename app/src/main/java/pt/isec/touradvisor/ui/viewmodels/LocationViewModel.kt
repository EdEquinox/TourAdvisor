package pt.isec.touradvisor.ui.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.GeoPoint
import pt.isec.touradvisor.utils.location.LocationHandler
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Suppress("UNCHECKED_CAST")
class LocationViewModelFactory(
    private val locationHandler: LocationHandler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationViewModel(locationHandler) as T
    }
}

class LocationViewModel(
    private val locationHandler: LocationHandler
) : ViewModel() {

    var coarseLocationPermission = false
    var fineLocationPermission = false


    private val _currentLocation = MutableLiveData(Location(null))
    val currentLocation : LiveData<Location>
        get() = _currentLocation

    init {
        locationHandler.onLocation = {
            _currentLocation.value = it
        }
    }

    fun startLocationUpdates() {
        if (fineLocationPermission && coarseLocationPermission) {
            locationHandler.startLocationUpdates()
        }

    }

    fun stopLocationUpdates() {
        locationHandler.stopLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }

    fun calculateDistance(point1: GeoPoint, point2: GeoPoint): Double {
        val earthRadius = 6371.0 // radius in kilometers
        val latDiff = Math.toRadians(point2.latitude - point1.latitude)
        val lonDiff = Math.toRadians(point2.longitude - point1.longitude)
        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(point1.latitude)) * cos(Math.toRadians(point2.latitude)) *
                sin(lonDiff / 2) * sin(lonDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

}
