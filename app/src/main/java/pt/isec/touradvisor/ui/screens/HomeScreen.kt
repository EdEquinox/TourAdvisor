package pt.isec.touradvisor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.touradviser.R
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    locationViewModel: LocationViewModel,
    firebaseViewModel: FirebaseViewModel,
    navController: NavController?,
    onLogout: () -> Unit
) {

    var autoEnabled by remember { mutableStateOf(false) }
    val location by locationViewModel.currentLocation.observeAsState()
    var geoPoint by remember {
        mutableStateOf(location?.let { GeoPoint(it.latitude, it.longitude) })
    }
    val user by remember { firebaseViewModel.user }
    var mapCenter by remember { mutableStateOf(geoPoint) }
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var searchHistory = remember{
        mutableStateListOf("ola", "ole", "oli")
    }
    var searchTrue: Boolean by remember { mutableStateOf(false) }

    if (autoEnabled) {
        geoPoint = location?.let { GeoPoint(it.latitude, it.longitude) }
    }

    LaunchedEffect(key1 = user){
        if (user == null){
            onLogout()
        }
    }

    Column(modifier = modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*Row(modifier = Modifier
            .height(100.dp)
            .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween)
        {
            Text(text = "Latitude: ${location?.latitude?:"--"}")
            Switch(checked = autoEnabled, onCheckedChange = {
                autoEnabled = it
            })
            Text(text = "Longitude: ${location?.longitude ?:"--"}")
        }*/

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clipToBounds()
        ) {
            SearchBar( query = query,
                onQueryChange = { query = it },
                onSearch = {
                    active = false },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text(text = "Procurar") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon") },
                trailingIcon = { if (active) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        modifier = Modifier.clickable {
                            query = ""
                            active = false })
                    }
                    else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle, //TODO: Change to profile pic
                            contentDescription = "Profile Icon",
                            modifier = Modifier.clickable {
                                navController?.navigate(Screens.PROFILE.route)
                            }
                                .size(40.dp))
                    }
                },
                content = {
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        items(searchHistory) {
                            Row(modifier = Modifier
                                .padding(all = 10.dp)
                                .clickable {
                                    query = it
                                    active = false
                                    searchTrue = true
                                }
                                .fillMaxWidth()
                                .height(30.dp)) {
                                Icon(imageVector = Icons.Default.History, contentDescription = "History Icon",
                                    modifier = Modifier.padding(end = 8.dp))
                                Text(text = it, fontSize = 20.sp, modifier = Modifier.
                                clickable {
                                    query = it
                                    active = false
                                }
                                    .align(Alignment.CenterVertically)
                                    .fillMaxWidth())
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    )
            AndroidView(factory = { context->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(false)
                    controller.setZoom(18.0)
                    mapCenter?.let {
                        controller.setCenter(it)
                    }
                    for (poi in locationViewModel.POIs) {
                        overlays.add(
                            Marker(this).apply {
                                position = GeoPoint(poi.latitude, poi.longitude)
                                title = poi.team
                                setAnchor(
                                    Marker.ANCHOR_CENTER,
                                    Marker.ANCHOR_BOTTOM
                                )
                            }
                        )
                    }
                }
            })
        }
        TabRow(selectedTabIndex = 0, modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .align(Alignment.CenterHorizontally)) {
            Tab(selected = true, onClick = {
                navController?.navigate(Screens.PROFILE.route)
            }) {
                Text(text = "POIs")
            }
            Tab(selected = false, onClick = { firebaseViewModel.signOut() }) {
                Text(text = "Restaurants")
            }
            Tab(selected = false, onClick = { /*TODO*/ }) {
                Text(text = "Hotels")
            }
        }
        LazyColumn(modifier = Modifier
            .fillMaxSize()
        ) {
            items(locationViewModel.POIs) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(128,224,255),
                        contentColor = Color(0,0,128)
                    ),
                    onClick = {
                        geoPoint = GeoPoint(it.latitude, it.longitude)
                        mapCenter = geoPoint
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = it.team, fontSize = 20.sp)
                        Text(text = "${it.latitude} ${it.longitude}", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
