package pt.isec.touradvisor

import android.app.Application
import android.location.LocationManager
import pt.isec.touradvisor.utils.location.LocationHandler
import pt.isec.touradvisor.utils.location.LocationManagerHandler

class TourAdviserApp : Application() {

    val locationHandler: LocationHandler by lazy {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        LocationManagerHandler(locationManager)
    }

    override fun onCreate() {
        super.onCreate()
    }
}