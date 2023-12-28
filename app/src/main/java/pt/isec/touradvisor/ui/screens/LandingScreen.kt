package pt.isec.touradvisor.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import pt.isec.touradviser.R
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel

@Composable
fun LandingScreen(
    navController: NavHostController,
    firebaseViewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel
) {


    val infiniteTransition = rememberInfiniteTransition(label = "animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF97CCEB)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tour_advisor_logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale)
        )
    }

    LaunchedEffect(key1 = true) {
        locationViewModel.startLocationUpdates()
        firebaseViewModel.startObserver()
        firebaseViewModel.getUserPOIs()
        navController.navigate(Screens.HOME.route) // navigate to HomeScreen after delay
    }

}