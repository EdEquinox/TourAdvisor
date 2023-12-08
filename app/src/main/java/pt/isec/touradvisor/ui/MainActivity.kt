package pt.isec.touradvisor.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import pt.isec.touradvisor.TourAdviserApp
import pt.isec.touradvisor.ui.screens.MainScreen
import pt.isec.touradvisor.ui.theme.TourAdvisorTheme
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModelFactory

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TourAdviserApp }
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory(app.locationHandler)
    }
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("OSM", MODE_PRIVATE))
        setContent {
            TourAdvisorTheme {
                MainScreen(locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            locationViewModel.backgroundLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else
            locationViewModel.backgroundLocationPermission = locationViewModel.coarseLocationPermission || locationViewModel.fineLocationPermission

        if (!locationViewModel.coarseLocationPermission && !locationViewModel.fineLocationPermission) {
            basicPermissionsAuthorization.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return false
        } else
            verifyBackgroundPermission()
        return true
    }

    private val basicPermissionsAuthorization = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        locationViewModel.coarseLocationPermission = results[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        locationViewModel.fineLocationPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        locationViewModel.startLocationUpdates()
        verifyBackgroundPermission()
    }

    private fun verifyBackgroundPermission() {
        if (!(locationViewModel.coarseLocationPermission || locationViewModel.fineLocationPermission))
            return

        if (!locationViewModel.backgroundLocationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                val dlg = AlertDialog.Builder(this)
                    .setTitle("Background Location")
                    .setMessage(
                        "This application needs your permission to use location while in the background.\n" +
                                "Please choose the correct option in the following screen" +
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                                    " (\"${packageManager.backgroundPermissionOptionLabel}\")."
                                else
                                    "."
                    )
                    .setPositiveButton("Ok") { _, _ ->
                        backgroundPermissionAuthorization.launch(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                    }
                    .create()
                dlg.show()
            }
        }
    }

    private val backgroundPermissionAuthorization = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        locationViewModel.backgroundLocationPermission = result
        Toast.makeText(this,"Background location enabled: $result", Toast.LENGTH_LONG).show()
    }
}

