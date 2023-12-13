package pt.isec.touradvisor.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    locationViewModel: LocationViewModel,
    firebaseViewModel: FirebaseViewModel
) {

    var showAddAction by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    navController.addOnDestinationChangedListener() { controller, destination, arguments ->
        showAddAction = (destination.route in
                arrayOf(Screens.HOME.route, Screens.SETTINGS.route))
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
                HomeScreen(navController = navController ,locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel) {
                    navController.navigate(
                        Screens.LOGIN.route
                    )
                }
            }
            composable(Screens.PROFILE.route) {
                ProfileScreen( navController = navController)
            }
            composable(Screens.LOGIN.route) {
                LoginScreen(viewModel = firebaseViewModel, onLogin = { navController.navigate(Screens.HOME.route) })
            }
            composable(Screens.SETTINGS.route) {
                SettingScreen(navController = navController)
            }
        }

    }
}