package pt.isec.touradvisor.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.GeoPoint
import pt.isec.touradvisor.data.Avaliacao
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel
import pt.isec.touradvisor.ui.viewmodels.SearchHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    locationViewModel: LocationViewModel,
    firebaseViewModel: FirebaseViewModel,
    searchHistoryViewModel: SearchHistoryViewModel,
    navController: NavController?,
    onLogout: () -> Unit
) {


    val location by locationViewModel.currentLocation.observeAsState()
    var geoPoint by remember {
        mutableStateOf(location?.let { GeoPoint(it.latitude, it.longitude) })
    }
    val user by remember { firebaseViewModel.user }
    var mapCenter by remember { mutableStateOf(geoPoint) }
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var searchHistory by remember{ mutableStateOf(searchHistoryViewModel.searchHistory)}
    var searchTrue: Boolean by remember { mutableStateOf(false) }
    var openCardDialog by remember { mutableStateOf(false) }
    var poisList by remember { mutableStateOf(firebaseViewModel.POIs) }
    var selectedLocal: Local? by remember { mutableStateOf(null) }
    val categoriesList by remember { mutableStateOf(firebaseViewModel.categories) }
    val locationsList by remember { mutableStateOf(firebaseViewModel.locations) }
    var selectedCategory: Category? by remember { mutableStateOf(null) }
    val orderBy = remember { mutableListOf(
        "A-Z",
        "Z-A",
        "+ Distância",
        "- Distância"
    ) }
    var searchList by remember { mutableStateOf(firebaseViewModel.POIs) }
    var sortedLocal by remember { mutableStateOf(firebaseViewModel.sortedLocal) }
    var categoriaDialog by remember { mutableStateOf(false) }
    var openPOIdialog by remember { mutableStateOf(false) }
    var selectedPOI: POI? by remember { mutableStateOf(null) }
    var avaliacao:Avaliacao? by remember { mutableStateOf(null) }
    var pfp by remember { mutableStateOf(firebaseViewModel.myPfp) }
    var searchedPOIs : List<POI>?
    var searchedLocations : List<Local>?
    var searchedCategories : List<Category>?

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
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(470.dp)
            .clipToBounds()
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    searchedCategories = categoriesList.value
                    searchedLocations = locationsList.value
                    searchedPOIs = poisList.value
                    active = false
                    searchTrue = true
                    searchHistoryViewModel.addSearch(query)
                    searchHistory = searchHistoryViewModel.searchHistory
                    searchedCategories = searchedCategories?.filter { it.nome?.contains(query, ignoreCase = true) == true }
                    searchedLocations = searchedLocations?.filter { it.name?.contains(query, ignoreCase = true) == true }
                    searchedPOIs = searchedPOIs?.filter { it.name?.contains(query, ignoreCase = true) == true }
                    searchHistoryViewModel.searchedCategories.value = searchedCategories!!
                    searchHistoryViewModel.searchedLocals.value = searchedLocations!!
                    searchHistoryViewModel.searchedPOIs.value = searchedPOIs!!
                    navController?.navigate(Screens.SEARCH.route)
                },
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
                        if (firebaseViewModel.myPfp.value != ""){
                            Box(modifier = Modifier.border(2.dp, Color.White, CircleShape)
                                .size(40.dp)){
                                Image(
                                    painter = rememberImagePainter(data = pfp.value),
                                    contentDescription = "pfp",
                                    modifier = Modifier
                                        .clickable {
                                            firebaseViewModel.stopObserver()
                                            navController?.navigate(Screens.PROFILE.route)
                                        }.clip(CircleShape).fillMaxSize(),
                                    contentScale = ContentScale.Crop)
                            }
                        }
                    else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account Icon",
                            modifier = Modifier.clickable {
                                firebaseViewModel.stopObserver()
                                navController?.navigate(Screens.PROFILE.route)
                            })
                        }
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
                                Text(text = it, fontSize = 20.sp, modifier = Modifier
                                    .clickable {
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
            selectedLocal?.let { MapViewComposable(mapCenter = mapCenter, poisList = poisList.value, selecedLocal = it) }
            Row(
                modifier
                    .zIndex(1f)
                    .align(Alignment.BottomCenter),
            ){
                AddButton(
                    firabaseViewModel = firebaseViewModel,
                    categorias = categoriesList,
                    locations = locationsList,
                    locationViewModel = locationViewModel
                )

        }
        MyLocButton(
            locationViewModel = locationViewModel
        ) {
            mapCenter = it
            Log.i("Map", locationViewModel.currentLocation.value.toString())
        }
        }
        TabFilter(categoriesList) {
            selectedCategory = it
            categoriaDialog = true
        }
        if (categoriaDialog) {
            ViewFilter(poisList = poisList, category = selectedCategory?.nome?:"", onDismiss = { categoriaDialog = false }, onSelect = {
                geoPoint = it.geoPoint?: GeoPoint(0.0, 0.0)
                mapCenter = geoPoint
                openPOIdialog = true
                selectedPOI = it
            }, categorias = categoriesList)
        }
        Ordenacao(
            orderBy = orderBy,
            localtionList = locationsList,
            sortedLocal = sortedLocal,
            locationViewModel = locationViewModel
        )
        LazyRow(modifier = Modifier
            .fillMaxSize()
        ) {
            items(sortedLocal.value) {
                if (sortedLocal.value.isEmpty()){
                    sortedLocal.value = locationsList.value
                }
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
                        geoPoint = GeoPoint(it.geoPoint?.latitude?:0.0, it.geoPoint?.longitude?:0.0)
                        mapCenter = geoPoint
                        openCardDialog = true
                        selectedLocal = it
                        Log.i("Msadsaap", "Clicked on ${it.name} ${it.image.toString()}")
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(8.dp)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        content = {
                            item {
                                Row (
                                    modifier = Modifier.fillMaxWidth()
                                ){
                                    Text(text = it.name?:"", fontSize = 20.sp)
                                    Icon(imageVector = Icons.Default.LocationCity, contentDescription = "Location Icon")
                                }
                                Spacer(modifier = Modifier
                                    .height(8.dp)
                                    .background(Color(0, 0, 0, 0)))
                                Text(text = "${it.description}", fontSize = 14.sp, modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp), maxLines = 3)
                                Image(painter = it.toImage(), contentDescription = "Filter Image")                            }
                        }
                    )
                }

            }
        }
    }
    if (openCardDialog) {
        selectedLocal?.let {
            ViewLocation(location = it, onDismiss = { openCardDialog = false }, poisList = poisList.value, onSelect = {
                geoPoint = it.geoPoint?: GeoPoint(0.0, 0.0)
                mapCenter = geoPoint
                openPOIdialog = true
                selectedPOI = it
            })
        }
    }
    if (openPOIdialog) {
        selectedPOI?.let {
            ViewPOI(poi = it, onDismiss = { openPOIdialog = false }, firebaseViewModel = firebaseViewModel, onSelect = {
                avaliacao = Avaliacao(it["comment"].toString(), it["rating"] as Int, it["user"].toString(), it["poi"].toString())
                firebaseViewModel.addAvaliacao(avaliacao?:Avaliacao("Error", 0, "Error", "Error"))
                openPOIdialog = false
            })
        }
    }
}