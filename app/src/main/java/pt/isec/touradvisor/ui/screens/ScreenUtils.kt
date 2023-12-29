package pt.isec.touradvisor.ui.screens

import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.LocationViewModel
import pt.isec.touradvisor.utils.firebase.FStorageUtil

@OptIn(ExperimentalMaterial3Api::class)
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
                            Card (onClick = {
                                onDismiss()
                                onSelect(it)
                            }) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp)
                                        .background(Color(0, 0, 0, 0))) {
                                        Text(text = it.name?:"", fontSize = 16.sp, modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(bottom = 8.dp))
                                    }
                                    Spacer(modifier = Modifier
                                        .height(8.dp)
                                        .background(Color(0, 0, 0, 0)))
                                    Row {
                                        Image(painter = it.toImage(), contentDescription = "POI Image", modifier = Modifier
                                            .width(100.dp)
                                            .height(50.dp))
                                        Text(text = it.description?:"", fontSize = 10.sp, modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp)
                                            .padding(start = 5.dp), maxLines = 3)
                                    }
                                }
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
                Text(text = "Fechar")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFilter(
    poisList: MutableState<List<POI>>,
    category: String,
    onDismiss: () -> Unit,
    onSelect: (POI) -> Unit
)
{
    poisList.value.filter{ it.category?.nome == category }
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(400.dp),
        onDismissRequest = { onDismiss() },
        title = { Text(text = category, fontSize = 20.sp) },
        text = {
            LazyVerticalGrid(columns = GridCells.Fixed(3), content = {
                poisList.value.forEach {
                    item {
                        if (it.category?.nome == category){
                            Card(onClick = {
                                onDismiss()
                                onSelect(it)
                            }, content = {
                                Column {
                                    Text(text = it.name?:"", fontSize = 14.sp)
                                    Image(painter = it.toImage(), contentDescription = "POI Image", modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp))
                                }
                            })
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
                Text(text = "Fechar")
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
    orderBy: List<String>,
    localtionList: MutableState<List<Local>>,
    sortedLocal: MutableState<List<Local>>,
    locationViewModel: LocationViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(orderBy[0]) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(2.dp)
        .padding(start = 8.dp),
        contentAlignment = Alignment.CenterStart) {
        Text(text = selected, fontSize = 12.sp, modifier = Modifier
            .clickable(onClick = { expanded = true })
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10))
            .padding(5.dp)
            .fillMaxWidth())
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            orderBy.forEach { s ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    selected = s
                    when (s) {
                        "- Distância" -> sortedLocal.value = localtionList.value.sortedByDescending { locationViewModel.calculateDistance(
                            GeoPoint(0.0,0.0), it.geoPoint?: GeoPoint(0.0,0.0)
                        ) }
                        "+ Distância" -> sortedLocal.value = localtionList.value.sortedBy { locationViewModel.calculateDistance(
                            GeoPoint(0.0,0.0), it.geoPoint?: GeoPoint(0.0,0.0)
                        ) }
                        "A-Z" -> sortedLocal.value = localtionList.value.sortedBy { it.name }
                        "Z-A" -> sortedLocal.value = localtionList.value.sortedByDescending { it.name }
                    }
                    Log.i("ola", sortedLocal.value.toString())
                },text = { Text(s) })
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
                                Toast.makeText(currentCont, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            firabaseViewModel.addLocationToFirestore(data)
                        }
                        if (tipo == "Categoria"){
                            if (data["nome"] == "" || data["descricao"] == "" || data["imagem"] == ""){
                                Toast.makeText(currentCont, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            firabaseViewModel.addCategoryToFirestore(data)
                        }
                        if (tipo == "Local de Interesse"){
                            if (data["nome"] == "" || data["descricao"] == "" || data["categoria"] == "" || data["location"] == "" || data["geoPoint"] == ""){
                                Toast.makeText(currentCont, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
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
        label = { Text("Nome") }
    )
    OutlinedTextField(
        value = descricao,
        onValueChange = { descricao = it },
        label = { Text("Descrição") },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Local atual?", fontSize = 13.sp)
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
            label = { Text("Latitude") }
        )
        OutlinedTextField(
            value = longitude.toString(),
            onValueChange = { longitude = it.toDouble() },
            label = { Text("Longitude") }
        )
    }
    Text(text = "Local:", fontSize = 13.sp)
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
    Text(text = "Categoria:", fontSize = 13.sp)
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
        label = { Text("Nome") }
    )
    OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Descrição") }
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
        label = { Text("Nome") }
    )
    OutlinedTextField(
        value = descricao,
        onValueChange = { descricao = it },
        label = { Text("Descrição") }
    )
    Text(text = "Local atual?", fontSize = 13.sp)
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
            label = { Text("Latitude") }
        )
        OutlinedTextField(
            value = longitude.toString(),
            onValueChange = { longitude = it.toDouble() },
            label = { Text("Longitude") }
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
        Text("Upload Photo")
    }

}

@Composable
fun ViewPOI(poi: POI , onDismiss: () -> Unit, own : Boolean = false, onSelect : (HashMap<String, Any>) -> Unit, firebaseViewModel: FirebaseViewModel){
    var comment by remember { mutableStateOf("") }
    var stars by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var data = hashMapOf<String,Any>()
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onDismissRequest = { onDismiss() },
        title = { Text(text = poi.name?:"", fontSize = 20.sp) },
        text = {
            LazyColumn(content = {
                item {
                    Text(text = poi.description?:"", fontSize = 14.sp, maxLines = 3)
                    Image(painter = poi.toImage(), contentDescription = "POI Image", modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comentário") },
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
                    data = hashMapOf(
                        "comment" to comment,
                        "rating" to stars,
                        "poi" to poi.name!!,
                        "user" to firebaseViewModel.userUID.value!!
                    )
                }
            })
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }
            ) {
                Text(text = "Fechar")
            }
        },
        confirmButton = {
            Button(onClick = {
                if (data["comment"] != "" && data["rating"] != 0){
                    onSelect(data)
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }
            ) {
                Text(text = "Avaliar")
            }
        },
    )
}

