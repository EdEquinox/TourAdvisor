package pt.isec.touradvisor.ui.screens

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.touradviser.R
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel
import pt.isec.touradvisor.utils.firebase.FStorageUtil

@Composable
fun ViewLocation(location: Local, onDismiss: () -> Unit, poisList: List<POI>, onSelect : (POI) -> Unit) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onDismissRequest = { onDismiss() },
        title = { Text(text = location.name?:"", fontSize = 20.sp) },
        text = {
            LazyColumn(content = {
                item {
                    Text(text = location.description?:"", fontSize = 14.sp)
                    Image(painter = location.toImage(), contentDescription = "POI Image", modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp))
                }
                poisList.forEach {
                    item {
                        if (it.location?.name == location.name){
                            POICard(poi = it){
                                onSelect(it)
                            }
                            Spacer(modifier = Modifier
                                .height(8.dp)
                                .background(Color(0, 0, 0, 0)))
                        }
                    }
                }
            })
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }
            ) {
                Text(text = stringResource(R.string.fechar))
            }
        },
    )
}

@Composable
fun ViewFilter(
    poisList: MutableState<List<POI>>,
    categorias: MutableState<List<Category>>,
    category: String,
    onDismiss: () -> Unit,
    onSelect: (POI) -> Unit
)
{
    var categoria by remember { mutableStateOf(Category()) }
    for (cat in categorias.value){
        if (cat.nome == category){
            categoria = cat
        }
    }
    poisList.value.filter{ it.category?.nome == category }
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onDismissRequest = { onDismiss() },
        title = { Text(text = category, fontSize = 20.sp) },
        text = {
            Column {
                Image(painter = rememberImagePainter(data = categoria.imagem ), contentDescription = "Filter Image", modifier = Modifier
                    .size(40.dp))
                LazyColumn(content = {
                    poisList.value.forEach {
                        item {
                            if (it.category?.nome == category){
                                POICard(poi = it){
                                    onSelect(it)
                                }
                            }
                        }
                    }
                })
            }

        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }
            ) {
                Text(text = stringResource(R.string.fechar))
            }
        },
    )
}


@Composable
fun MapViewComposable(mapCenter: GeoPoint?, poisList: List<POI>, selecedLocal: Local) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                setBuiltInZoomControls(false)
                controller.setZoom(15.0)
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
                if (poi.location?.name == selecedLocal.name){
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
fun Ordenacao(
    orderBy: HashMap<Int, String>,
    localtionList: MutableState<List<Local>>,
    sortedLocal: MutableState<List<Local>>,
    locationViewModel: LocationViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf(orderBy[selected]) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(2.dp)
        .padding(start = 8.dp),
        contentAlignment = Alignment.CenterStart) {
        Text(text = text.toString(), fontSize = 12.sp, modifier = Modifier
            .clickable(onClick = { expanded = true })
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10))
            .padding(5.dp)
            .fillMaxWidth())
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            orderBy.forEach { index ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    selected = index.key
                    when (index.key) {
                        0-> sortedLocal.value = localtionList.value.sortedByDescending { locationViewModel.calculateDistance(
                            GeoPoint(0.0,0.0), it.geoPoint?: GeoPoint(0.0,0.0)
                        ) }
                        1 -> sortedLocal.value = localtionList.value.sortedBy { locationViewModel.calculateDistance(
                            GeoPoint(0.0,0.0), it.geoPoint?: GeoPoint(0.0,0.0)
                        ) }
                        2 -> sortedLocal.value = localtionList.value.sortedBy { it.name }
                        3 -> sortedLocal.value = localtionList.value.sortedByDescending { it.name }
                    }
                },text = { Text(index.value) })
            }
        }
    }
}

@Composable
fun TabFilter(
    categorias: MutableState<List<Category>>,
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
    firabaseViewModel: FirebaseViewModel,
    categorias: MutableState<List<Category>>,
    locations: MutableState<List<Local>>,
    locationViewModel: LocationViewModel
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var tipo by remember { mutableStateOf("") }
    val currentCont = LocalContext.current
    val fields = stringResource(id = R.string.please_fill_all_fields)

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
                title = { Text(text = stringResource(R.string.adicionar, tipo), fontSize = 20.sp) },
                text = {
                    Column {      //substituir por um switch case

                        if (tipo == "Localização") {
                            data = dialogLocalizacao(locationViewModel)
                        }
                        if (tipo == "Categoria"){
                            data = dialogCategoria()
                        }
                        if (tipo == "Local de Interesse"){
                            data = dialogLocalInteresse(categorias, locations, firabaseViewModel, locationViewModel)
                        }


                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (tipo == "Localização") {
                            if (data["nome"] == "" || data["descricao"] == "" || data["geopoint"] == ""){
                                Toast.makeText(currentCont, fields, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            firabaseViewModel.addLocationToFirestore(data)
                        }
                        if (tipo == "Categoria"){
                            if (data["nome"] == "" || data["descricao"] == "" || data["imagem"] == ""){
                                Toast.makeText(currentCont, fields, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            firabaseViewModel.addCategoryToFirestore(data)
                        }
                        if (tipo == "Local de Interesse"){
                            if (data["nome"] == "" || data["descricao"] == "" || data["categoria"] == "" || data["location"] == "" || data["geoPoint"] == ""){
                                Toast.makeText(currentCont, fields, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            firabaseViewModel.addPOIToFirestore(data)
                        }
                        showDialog = false
                    }
                    ) {
                        Text(text = stringResource(id = R.string.adicionar))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text(text = stringResource(R.string.cancelar))
                    }
                },
            )
        }
    }
}

@Composable
fun MyLocButton(
    locationViewModel: LocationViewModel,
    onLoc: (GeoPoint) -> Unit,
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(450.dp)
        .padding(end = 10.dp),
    ) {
        IconButton(
            onClick = {
                locationViewModel.currentLocation.value?.let {
                    val geoPoint = GeoPoint(it.latitude, it.longitude)
                    onLoc(geoPoint)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color(118, 156, 219, 149), CircleShape)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "My location button")
        }
    }
}

@Composable
fun dialogLocalInteresse(
    categorias: MutableState<List<Category>>,
    locations: MutableState<List<Local>>,
    firebaseViewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel
): HashMap<String, Any> {

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var selectedCategory by remember { mutableStateOf(categorias.value[0]) }
    var selectedLocation by remember { mutableStateOf(locations.value[0]) }
    var geoPoint by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }
    var checkedLoc by remember { mutableStateOf(true) }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedLoc by remember { mutableStateOf(false) }
    var imagem by remember { mutableStateOf("") }

    OutlinedTextField(
        value = nome,
        onValueChange = { nome = it },
        label = { Text(stringResource(R.string.nome)) }
    )
    OutlinedTextField(
        value = descricao,
        onValueChange = { descricao = it },
        label = { Text(stringResource(R.string.descri_o)) },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = stringResource(R.string.local_atual), fontSize = 13.sp)
    Switch(checked = checkedLoc, onCheckedChange = {
        checkedLoc = it
    })
    if (checkedLoc) {
        geoPoint =
            locationViewModel.currentLocation.value?.let { GeoPoint(it.latitude, it.longitude) }!!
    } else {
        OutlinedTextField(
            value = latitude.toString(),
            onValueChange = { latitude = it.toDouble() },
            label = { Text(stringResource(R.string.latitude)) }
        )
        OutlinedTextField(
            value = longitude.toString(),
            onValueChange = { longitude = it.toDouble() },
            label = { Text(stringResource(R.string.longitude)) }
        )
    }
    Text(text = stringResource(R.string.local), fontSize = 13.sp)
    Spacer(modifier = Modifier.height(3.dp))
    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {

        Text(
            text = selectedLocation.name.toString(), fontSize = 16.sp, modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expandedLoc = true })
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(10))
                .padding(18.dp)
        )
        DropdownMenu(expanded = expandedLoc, onDismissRequest = { expandedLoc = false }) {
            locations.value.forEach { s ->
                DropdownMenuItem(onClick = {
                    expandedLoc = false
                    selectedLocation = s
                }, text = { s.name?.let { Text(it) } })
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = stringResource(R.string.categoria), fontSize = 13.sp)
    Spacer(modifier = Modifier.height(3.dp))
    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Text(
            text = selectedCategory.nome.toString(), fontSize = 16.sp, modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expandedCat = true })
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(10))
                .padding(18.dp)
        )
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
    UploadPhotoButton(onUriReady = { imagem = it }, type = "poi", picName = nome)
    val db = Firebase.firestore
    val docRefCat = db.collection("Categorias").document(selectedCategory.nome.toString())
    val docRefLoc = db.collection("Localizacao").document(selectedLocation.name.toString())

    return hashMapOf(
        "nome" to nome,
        "descricao" to descricao,
        "categoria" to docRefCat,
        "location" to docRefLoc,
        "geoPoint" to geoPoint,
        "imagem" to imagem,
        "user" to firebaseViewModel.userUID.value!!
    )
}

@Composable
fun dialogCategoria(): HashMap<String, Any> {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text(stringResource(id = R.string.nome )) }
    )
    OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text(stringResource(id = R.string.descri_o )) }
    )

    UploadPhotoButton(onUriReady = { image = it }, type = "category", picName = name)
    return hashMapOf(
        "nome" to name,
        "descricao" to description,
        "imagem" to image
    )

}

@Composable
fun dialogLocalizacao(
    locationViewModel: LocationViewModel
): HashMap<String, Any> {
    var name by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var checkedLoc by remember { mutableStateOf(true) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var geoPoint by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text(stringResource(id = R.string.nome )) }
    )
    OutlinedTextField(
        value = descricao,
        onValueChange = { descricao = it },
        label = { Text(stringResource(id = R.string.descri_o )) }
    )
    Text(text = stringResource(id = R.string.local_atual ), fontSize = 13.sp)
    Switch(checked = checkedLoc, onCheckedChange = {
        checkedLoc = it
    })
    if (checkedLoc) {
        geoPoint =
            locationViewModel.currentLocation.value?.let { GeoPoint(it.latitude, it.longitude) }!!
    } else {
        OutlinedTextField(
            value = latitude.toString(),
            onValueChange = { latitude = it.toDouble() },
            label = { Text(stringResource(id = R.string.latitude)) }
        )
        OutlinedTextField(
            value = longitude.toString(),
            onValueChange = { longitude = it.toDouble() },
            label = { Text(stringResource(id = R.string.longitude)) }
        )
    }
    UploadPhotoButton(onUriReady = { image = it }, type = "location", picName = name)
    return hashMapOf(
        "nome" to name,
        "descricao" to descricao,
        "geopoint" to geoPoint,
        "imagem" to image
    )
}

@Composable
fun UploadPhotoButton(onUriReady: (String) -> Unit, type: String, picName: String) {
    val context = LocalContext.current
    val uri = remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            selectedUri: Uri? ->
        selectedUri?.let {
            context.contentResolver.openInputStream(it)?.let { inputStream ->
                FStorageUtil.uploadFile(inputStream, type,picName){ downloadUrl ->
                    uri.value = downloadUrl
                    onUriReady(downloadUrl)
                }
            }
        }
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text(stringResource(R.string.upload_photo))
    }

}

@Composable
fun ViewPOI(poi: POI? , onDismiss: () -> Unit, onSelect : (HashMap<String, Any>) -> Unit, firebaseViewModel: FirebaseViewModel){
    var comment by remember { mutableStateOf("") }
    var stars by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var data = hashMapOf<String,Any>()
    val own by remember { mutableStateOf(poi?.user == firebaseViewModel.userUID.value) }
    val fields = stringResource(id = R.string.please_fill_all_fields)
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onDismissRequest = { onDismiss() },
        title = {
            if (poi != null) {
                Text(text = poi.name?:"", fontSize = 20.sp)
            }
        },
        text = {
            LazyColumn(content = {
                item {
                    if (poi != null) {
                        Text(text = poi.description?:"", fontSize = 14.sp, maxLines = 3)
                    }
                    if (poi != null) {
                        Image(painter = poi.toImage(), contentDescription = "POI Image", modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp))
                    }
                    if (!own){
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text(stringResource(R.string.coment_rio)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                        Row {
                            for (i in 1..3) {
                                IconButton(onClick = {
                                    stars = i
                                }) {
                                    if (i <= stars) {
                                        Icon(Icons.Default.Star, contentDescription = "Star")
                                    } else {
                                        Icon(Icons.Default.StarOutline, contentDescription = "Star")
                                    }
                                }
                            }
                        }
                    }
                    if (poi != null) {
                        data = hashMapOf(
                            "comment" to comment,
                            "rating" to stars,
                            "poi" to poi.name!!,
                            "user" to firebaseViewModel.userUID.value!!
                        )
                    }
                }
            })
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }
            ) {
                Text(text = stringResource(id = R.string.cancelar ))
            }
        },
        confirmButton = {
            if (own){
                Button(onClick = {
                    if (poi != null) {
                        firebaseViewModel.removePoiFromFirestore(poi.name!!)
                    }
                    onDismiss()
                }) {
                    Text(text = stringResource(R.string.remover))
                }
            } else{
                Button(onClick = {
                    if (data["comment"] != "" && data["rating"] != 0){
                        onSelect(data)
                    } else {
                        Toast.makeText(context, fields, Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = stringResource(R.string.avaliar))
                }
            }

        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(category: Category,onClick: () -> Unit = {}) {

    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(150.dp),
        onClick = {
            onClick()
        }
    ) {
        Column {
            category.nome?.let { Text(text = it, fontSize = 20.sp) }
            category.descricao?.let { Text(text = it, fontSize = 15.sp) }
            category.descricao?.let {
                Text(
                    text = it,
                    fontSize = 15.sp
                )
            }
            category.imagem?.let {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "PFP",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RectangleShape)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalCard(local: Local, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(150.dp),
        onClick = {
            onClick()
        }
    ) {
        Column {
            local.name?.let { Text(text = it, fontSize = 20.sp) }
            local.description?.let { Text(text = it, fontSize = 15.sp) }
            local.image?.let {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "PFP",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RectangleShape)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POICard(poi: POI, onClick: () -> Unit = {}) {

    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(150.dp),
        onClick = {
            onClick()
        }
    ) {
        Column {
            poi.name?.let { Text(text = it, fontSize = 20.sp) }
            poi.description?.let { Text(text = it, fontSize = 15.sp, maxLines = 1) }
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
                    painter = poi.toImage(),
                    contentDescription = "PFP",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RectangleShape)
                )
            }
        }
    }

}


