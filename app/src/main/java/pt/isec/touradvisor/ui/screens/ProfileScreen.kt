package pt.isec.touradvisor.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import pt.isec.touradviser.R
import pt.isec.touradvisor.data.POI
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    firebaseViewModel: FirebaseViewModel
) {
    val pfp = firebaseViewModel.myPfp
    var openPoiCard by remember { mutableStateOf(false) }
    var selectedPOI: POI? by remember { mutableStateOf(null) }
    val nick = firebaseViewModel.getNickname()
    val pois by remember {
        mutableStateOf(firebaseViewModel.myPOIs.value)
    }
    val ratings by remember {
        mutableStateOf(firebaseViewModel.myRatings.value)
    }
    val configuration = LocalConfiguration.current
    val portrait = remember { mutableIntStateOf(configuration.orientation) }
    if (portrait.intValue == Configuration.ORIENTATION_PORTRAIT){
        Column(
            modifier = Modifier
                .background(color = colorResource(id = R.color.light_sky_blue))
        ) {
            Box(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.light_sky_blue))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tour_advisor_banner),
                        contentDescription = "PFP",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(color = colorResource(id = R.color.light_sky_blue)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .align(alignment = Alignment.BottomCenter)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        if (pfp.value != "") {
                            Image(
                                painter = rememberImagePainter(data = pfp.value),
                                contentDescription = "PFP",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape), contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = rememberImagePainter(data = R.drawable.profile_pic),
                                contentDescription = "PFP",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape), contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.light_sky_blue))
            ) {

                Column {
                    if (nick != null) {
                        Text(
                            text = nick,
                            fontSize = 30.sp,
                            modifier = Modifier
                                .padding(10.dp)
                                .align(alignment = Alignment.CenterHorizontally)
                                .fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.user),
                            fontSize = 30.sp,
                            modifier = Modifier
                                .padding(10.dp)
                                .align(alignment = Alignment.CenterHorizontally)
                                .fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.my_pois),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .align(alignment = Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    )
                    LazyRow {
                        items(pois.size) { index ->
                            val poi = pois[index]
                            Card(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .width(150.dp)
                                    .height(150.dp),
                                onClick = {
                                    openPoiCard = true
                                    selectedPOI = poi
                                }
                            ) {
                                Column {
                                    poi.name?.let { Text(text = it, fontSize = 20.sp) }
                                    poi.description?.let { Text(text = it, fontSize = 15.sp) }
                                    poi.category?.let {
                                        it.nome?.let { it1 ->
                                            Text(
                                                text = it1,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                    poi.location?.let {
                                        it.name?.let { it1 ->
                                            Text(
                                                text = it1,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                    poi.image?.let {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_launcher_background),
                                            contentDescription = "PFP",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .background(color = colorResource(id = R.color.white))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.my_ratings),
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.light_sky_blue))
            ) {
                LazyRow {
                    items(ratings.size) { index ->
                        val rating = ratings[index]
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .width(150.dp)
                                .height(150.dp),
                        ) {
                            Column {
                                Text(text = rating.poi, fontSize = 20.sp)
                                Text(text = rating.comment, fontSize = 15.sp, maxLines = 2)
                                Text(text = rating.rating.toString(), fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }else{
        Spacer(modifier = Modifier.height(80.dp))
        Row(
            modifier = Modifier
                .background(color = colorResource(id = R.color.light_sky_blue))
                .fillMaxHeight()
        ) {

            Column(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.light_sky_blue))
                    .width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Text(
                    text = stringResource(R.string.my_pois),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                LazyRow {
                    items(pois.size) { index ->
                        val poi = pois[index]
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .width(150.dp)
                                .height(150.dp),
                            onClick = {
                                openPoiCard = true
                                selectedPOI = poi
                            }
                        ) {
                            Column {
                                poi.name?.let { Text(text = it, fontSize = 20.sp) }
                                poi.description?.let { Text(text = it, fontSize = 15.sp) }
                                poi.category?.let {
                                    it.nome?.let { it1 ->
                                        Text(
                                            text = it1,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                                poi.location?.let {
                                    it.name?.let { it1 ->
                                        Text(
                                            text = it1,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                                poi.image?.let {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_launcher_background),
                                        contentDescription = "PFP",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .background(color = colorResource(id = R.color.white))
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.light_sky_blue))
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Text(
                    text = stringResource(R.string.my_ratings),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                LazyRow {
                    items(ratings.size) { index ->
                        val rating = ratings[index]
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .width(150.dp)
                                .height(150.dp),
                        ) {
                            Column {
                                Text(text = rating.poi, fontSize = 20.sp)
                                Text(text = rating.comment, fontSize = 15.sp, maxLines = 2)
                                Text(text = rating.rating.toString(), fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (openPoiCard) {
        ViewPOI(
            poi = selectedPOI,
            onDismiss =
            {
                openPoiCard = false
            },
            onSelect = {
                openPoiCard = false
            },
            firebaseViewModel = firebaseViewModel
        )
    }
}
