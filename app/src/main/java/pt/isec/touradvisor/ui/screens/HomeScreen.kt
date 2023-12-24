package pt.isec.touradvisor.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.POI
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel
import pt.isec.touradvisor.utils.firebase.FStorageUtil.Companion.uploadFile


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    locationViewModel: LocationViewModel,
    firebaseViewModel: FirebaseViewModel,
    navController: NavController?,
    onLogout: () -> Unit
) {
    firebaseViewModel.startObserver();
    firebaseViewModel.getUserPOIs()

//    @Composable
//    fun YourScreen() {
//        val data = viewModel.data.collectAsState(initial = Loading)
//
//        when (val result = data.value) {
//            is Loading -> CircularProgressIndicator() // Display a loading indicator
//            is Success -> DisplayData(result.data) // Display the data
//            is Error -> Text("Error: ${result.exception.message}") // Display an error message
//        }
//    }

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
    var userCode by remember { mutableStateOf("") }
    var searchTrue: Boolean by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    val poisList by remember { mutableStateOf(firebaseViewModel.POIs) }
    val categoriesList by remember { mutableStateOf(firebaseViewModel.categories) }
    var selectedCategory: Category? by remember { mutableStateOf(null) }

    if (autoEnabled) {
        geoPoint = location?.let { GeoPoint(it.latitude, it.longitude) }
        locationViewModel.stopLocationUpdates()
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(470.dp)
            .clipToBounds()
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { active = false },
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
                            modifier = Modifier
                                .clickable {
                                    firebaseViewModel.stopObserver()
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
            MapViewComposable(mapCenter = mapCenter, poisList = poisList.value)
            Row(
                modifier
                    .zIndex(1f)
                    .align(Alignment.BottomCenter),
            ){
                AddButton( modifier = Modifier
                    .align(Alignment.CenterVertically), firabaseViewModel = firebaseViewModel, categorias = categoriesList, locationViewModel = locationViewModel)
        }

        }
        TabFilter(categoriesList) {
            selectedCategory = it
        }
        LazyRow(modifier = Modifier
            .fillMaxSize()
        ) {
            items(poisList.value) {
                if (selectedCategory == null || it.category == selectedCategory){
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
                            Log.i("Map", "Clicked on ${it.geoPoint?.latitude} ${it.geoPoint?.longitude}")
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .width(150.dp)
                                .padding(8.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = it.name?:"", fontSize = 20.sp)
                            Text(text = "${it.category?.nome}", fontSize = 14.sp)
                            Image(painter = it.toImage(), contentDescription = "POI Image")
                            Text(text = it.geoPoint.toString(), fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapViewComposable(mapCenter: GeoPoint?, poisList: List<POI>) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(18.0)
                for (poi in poisList) {
                    overlays.add(
                        Marker(this).apply {
                            position =
                                poi.geoPoint?.let {
                                    org.osmdroid.util.GeoPoint(it.latitude, poi.geoPoint.longitude)
                                }
                            title = poi.name
                            setAnchor(
                                Marker.ANCHOR_CENTER,
                                Marker.ANCHOR_BOTTOM
                            )
                        }
                    )
                }
            }
        }, update = { mapView ->
            mapView.overlays.clear()
            for (poi in poisList) {
                mapView.overlays.add(
                    Marker(mapView).apply {
                        position =
                            poi.geoPoint?.let {
                                org.osmdroid.util.GeoPoint(it.latitude, poi.geoPoint.longitude)
                            }
                        title = poi.name
                        setAnchor(
                            Marker.ANCHOR_CENTER,
                            Marker.ANCHOR_BOTTOM
                        )
                    }
                )
            }
            mapCenter?.let {
                mapView.controller.animateTo(
                    org.osmdroid.util.GeoPoint(
                        it.latitude,
                        it.longitude
                    )
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0f)
    )
}

@Composable
fun TabFilter(
    categorias:MutableState<List<Category>>,
    modifier: Modifier = Modifier,
    onFilter: (Category) -> Unit
) {
    LazyRow(modifier = modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        items(categorias.value) {
            IconButton(onClick = {
                onFilter(it)
            }){
                Image(painter = it.toImage(), contentDescription = "Filter Image")
            }
        }

    }
}

@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    firabaseViewModel: FirebaseViewModel,
    categorias:MutableState<List<Category>>,
    locationViewModel: LocationViewModel
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var tipo by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .height(150.dp)
        .width(180.dp)
        .offset(y = (40).dp)) {
        IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color(68, 138, 255, 255), CircleShape)) {
            Icon(Icons.Default.Add, contentDescription = "Main button")
        }

        if (isExpanded) {

            IconButton(onClick = {
                                    isExpanded = !isExpanded
                                    showDialog = true
                                    tipo = "Localização"
                                 },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .clip(shape = CircleShape)
                    .background(Color(60, 59, 59, 100), CircleShape)
                    ) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = "Location button")
            }

            IconButton(onClick = { isExpanded = !isExpanded
                showDialog = true
                tipo = "Categoria" },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(shape = CircleShape)
                    .background(Color(60, 59, 59, 100), CircleShape)
                    ) {
                Icon(Icons.Default.Category, contentDescription = "Category button")
            }

            IconButton(onClick = { isExpanded = !isExpanded
                showDialog = true
                tipo = "Local de Interesse" },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(shape = CircleShape)
                    .background(Color(60, 59, 59, 100), CircleShape)
                    ) {
                Icon(Icons.Default.Stadium, contentDescription = "POI button")
            }
        }

        if (showDialog) {
            var data = hashMapOf<String,Any>()
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Adicionar $tipo", fontSize = 20.sp) },
                text = {
                    Column {      //substituir por um switch case

                        if (tipo == "Localização") {
                            data = dialogLocalização()
                        }
                        if (tipo == "Categoria"){
                            data = dialogCategoria()
                        }
                        if (tipo == "Local de Interesse"){
                            data = dialogLocalInteresse(categorias, firabaseViewModel, locationViewModel)
                        }


                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (tipo == "Localização") {
                            firabaseViewModel.addPOIToFirestore(data)
                        }
                        if (tipo == "Categoria"){
                            firabaseViewModel.addCategoryToFirestore(data)
                        }
                        if (tipo == "Local de Interesse"){
                            firabaseViewModel.addPOIToFirestore(data)
                        }
                        showDialog = false
                    }
                    ) {
                        Text(text = "Adicionar")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text(text = "Cancelar")
                    }
                },
            )
        }
    }
}

@Composable
fun dialogLocalInteresse(
    categorias: MutableState<List<Category>>,
    firebaseViewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel
): HashMap<String, Any> {

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var selectedCategory by remember { mutableStateOf(categorias.value[0]) }
    var geoPoint by remember { mutableStateOf(GeoPoint(0.0,0.0)) }
    var checkedLoc by remember { mutableStateOf(true) }
    var expandedCat by remember { mutableStateOf(false) }
    var imagem by remember { mutableStateOf("") }

    OutlinedTextField(
        value = nome,
        onValueChange = { nome = it },
        label = { Text("Nome") }
    )
    OutlinedTextField(
        value = descricao,
        onValueChange = { descricao = it },
        label = { Text("Descrição")},
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Local atual?", fontSize = 13.sp)
    Switch(checked = checkedLoc, onCheckedChange = {
        checkedLoc = it
    })
    if (checkedLoc){
        geoPoint = locationViewModel.currentLocation.value?.let { GeoPoint(it.latitude, it.longitude) }!!
    } else {
        OutlinedTextField(
            value = latitude.toString(),
            onValueChange = { latitude = it.toDouble() },
            label = { Text("Latitude") }
        )
        OutlinedTextField(
            value = longitude.toString(),
            onValueChange = { longitude = it.toDouble() },
            label = { Text("Longitude") }
        )
    }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Text(text = selectedCategory.nome.toString(), fontSize = 16.sp, modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { expandedCat = true })
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10))
            .padding(18.dp))
        DropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
            categorias.value.forEach { s ->
                DropdownMenuItem(onClick = {
                    expandedCat = false
                    selectedCategory = s
                }, text = { s.nome?.let { Text(it) } })
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    UploadPhotoButton(onUriReady ={ imagem = it}, type = "poi", picName = nome)
    val db = Firebase.firestore
    val docRef = db.collection("Categorias").document(selectedCategory.nome.toString())

    val data = hashMapOf<String,Any>(
        "nome" to nome,
        "descricao" to descricao,
        "categoria" to docRef,
        "geoPoint" to geoPoint,
        "imagem" to imagem,
        "user" to firebaseViewModel.userUID.value!!
    )
    return data
}

@Composable
fun dialogCategoria(): HashMap<String, Any> {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Nome") }
    )
    OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Descrição") }
    )
    UploadPhotoButton(onUriReady ={ image = it}, type = "category", picName = name)
    val data = hashMapOf<String,Any>(
        "nome" to name,
        "descricao" to description,
        "imagem" to image
    )
    return data

}

@Composable
fun dialogLocalização(): HashMap<String, Any> {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var imagem by remember { mutableStateOf("") }

    OutlinedTextField(
        value = nome,
        onValueChange = { nome = it },
        label = { Text("Nome") }
    )
    OutlinedTextField(
        value = descricao,
        onValueChange = { descricao = it },
        label = { Text("Descrição") }
    )
    OutlinedTextField(
        value = localizacao,
        onValueChange = { localizacao = it },
        label = { Text("Coordenadas") }
    )
    OutlinedTextField(
        value = imagem,
        onValueChange = { imagem = it },
        label = { Text("Imagem") }
    )
    UploadPhotoButton(onUriReady ={ imagem = it}, type = "poi", picName = nome)
    val data = hashMapOf<String,Any>(
        "nome" to nome,
        "descricao" to descricao,
        "latitude" to 0,
        "longitude" to 0,
        "imagem" to "imagem"
    )
    return data
}

@Composable
fun UploadPhotoButton(onUriReady: (String) -> Unit, type: String, picName: String) {
    val context = LocalContext.current
    val uri = remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        selectedUri: Uri? ->
        selectedUri?.let {
            context.contentResolver.openInputStream(it)?.let { inputStream ->
                uploadFile(inputStream, type,picName){downloadUrl ->
                    uri.value = downloadUrl
                    onUriReady(downloadUrl)
                }
            }
        }
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Upload Photo")
    }

}

