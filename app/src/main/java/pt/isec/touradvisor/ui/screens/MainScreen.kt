package pt.isec.touradvisor.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel
import pt.isec.touradvisor.ui.viewmodels.SearchHistoryViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    locationViewModel: LocationViewModel,
    firebaseViewModel: FirebaseViewModel,
    searchHistoryViewModel: SearchHistoryViewModel
) {

    var showSettingsAction by remember { mutableStateOf(false) }
    var showLogoutAction by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen by navController.currentBackStackEntryAsState()

    navController.addOnDestinationChangedListener() { controller, destination, arguments ->
        showSettingsAction = (destination.route == Screens.PROFILE.route)
        showLogoutAction = (destination.route == Screens.PROFILE.route)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != null && Screens.valueOf(currentScreen!!.destination.route!!).showAppBar)
                TopAppBar(
                    title = {
                        currentScreen.toString()
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (showSettingsAction)
                            IconButton(onClick = {
                                navController.navigate(Screens.SETTINGS.route)
                            }) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        if (showLogoutAction)
                            IconButton(onClick = {
                                firebaseViewModel.signOut()
                                Log.d("MainScreen", "Logout")
                                navController.navigate(Screens.LOGIN.route)
                            }) {
                                Icon(
                                    Icons.Filled.Logout,
                                    contentDescription = "Logout"
                                )
                            }
                    }
                )
        }
    ){
        NavHost(
            navController = navController,
            startDestination = Screens.LANDING.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screens.LANDING.route) {
                LandingScreen(navController = navController, firebaseViewModel = firebaseViewModel, locationViewModel = locationViewModel)
            }
            composable(Screens.HOME.route) {
                HomeScreen(navController = navController ,locationViewModel = locationViewModel, firebaseViewModel = firebaseViewModel, searchHistoryViewModel = searchHistoryViewModel  ) {
                    navController.navigate(
                        Screens.LOGIN.route
                    )
                }
            }
            composable(Screens.PROFILE.route) {
                ProfileScreen( navController = navController, firebaseViewModel = firebaseViewModel)
            }
            composable(Screens.LOGIN.route) {
                LoginScreen(viewModel = firebaseViewModel, onLogin = { navController.navigate(Screens.LANDING.route) })
            }
            composable(Screens.SETTINGS.route) {
                SettingScreen(firebaseViewModel = firebaseViewModel)
            }
            composable(Screens.SEARCH.route) {
                SearchScreen(
                    firebaseViewModel = firebaseViewModel,
                    searchHistoryViewModel = searchHistoryViewModel
                )
            }
        }

    }
}