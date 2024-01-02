package pt.isec.touradvisor.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import pt.isec.touradvisor.TourAdviserApp
import pt.isec.touradvisor.ui.screens.MainScreen
import pt.isec.touradvisor.ui.theme.TourAdvisorTheme
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModelFactory
import pt.isec.touradvisor.ui.viewmodels.SearchHistoryViewModel

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TourAdviserApp }
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory(app.locationHandler)
    }
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private val searchHistoryViewModel: SearchHistoryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        org.osmdroid.config.Configuration.getInstance().load(this, getSharedPreferences("OSM", MODE_PRIVATE))
        setContent {
            TourAdvisorTheme {
                MainScreen(locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel, searchHistoryViewModel = searchHistoryViewModel )
            }
        }
        verifyPermissions()
    }

    override fun onResume() {
        super.onResume()
        locationViewModel.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        locationViewModel.stopLocationUpdates()
        firebaseViewModel.stopObserver()
    }

    private fun verifyPermissions() : Boolean{
        locationViewModel.coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        locationViewModel.fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (!locationViewModel.coarseLocationPermission && !locationViewModel.fineLocationPermission) {
            basicPermissionsAuthorization.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            false
        } else
            true
    }

    private val basicPermissionsAuthorization = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        locationViewModel.coarseLocationPermission = results[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        locationViewModel.fineLocationPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        locationViewModel.startLocationUpdates()
    }

}

