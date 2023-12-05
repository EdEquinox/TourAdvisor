package pt.isec.touradvisor.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isec.touradviser.R
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController = rememberNavController(), viewModel: LocationViewModel) {

    var showAddAction by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    navController.addOnDestinationChangedListener() { controller, destination, arguments ->
        showAddAction = (destination.route in
                arrayOf(Screens.HOME.route, Screens.POI.route, Screens.FAVORITES.route, Screens.VISITED.route, Screens.SETTINGS.route))
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = Modifier.fillMaxSize()
    ){
        NavHost(
            navController = navController,
            startDestination = Screens.LANDING.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screens.LANDING.route) {
                LandingScreen(navController = navController)
            }
            composable(Screens.HOME.route) {
                HomeScreen(navController = navController ,viewModel = viewModel)
            }
            composable(Screens.PROFILE.route) {
                ProfileScreen( navController = navController)
            }
        }

    }
}