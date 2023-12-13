package pt.isec.touradvisor.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.touradviser.R

@Composable
fun ProfileScreen(
    navController: NavController
) {
    Column {
        NavigationBar (content = {
            IconButton(onClick = { navController.navigateUp() }){
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White)
            }
            IconButton(onClick = { navController.navigate(Screens.SETTINGS.route)}){
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Back",
                    tint = Color.White)
            }
        })
        Box(modifier = Modifier
            .background(color = colorResource(id = R.color.white))
        ){
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            ){
                Image(painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = "PFP",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(color = colorResource(id = R.color.white)))
                Image(painter = painterResource(id = R.drawable.tour_advisor_logo), contentDescription = "PFP",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .border(2.dp, Color.White, CircleShape)
                        .align(alignment = Alignment.BottomCenter))
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.white))
        ){
            val items = (0..100).toList()
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
                items(items = items) { item ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Text(
                            text = "Item $item",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
