package pt.isec.touradvisor.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isec.touradviser.R
import pt.isec.touradvisor.data.Avaliacao
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel
import pt.isec.touradvisor.ui.viewmodels.SearchHistoryViewModel

@Composable
fun SearchScreen(
    firebaseViewModel: FirebaseViewModel,
    searchHistoryViewModel: SearchHistoryViewModel
) {

    val searchedPOIs = searchHistoryViewModel.searchedPOIs
    val searchedLocals = searchHistoryViewModel.searchedLocals
    val searchedCategories = searchHistoryViewModel.searchedCategories

    var openPoiCard by remember { mutableStateOf(false) }
    var selectedPOI: POI? by remember { mutableStateOf(null) }
    var openLocalCard by remember { mutableStateOf(false) }
    var selectedLocal: Local? by remember { mutableStateOf(null) }
    var openCategoryCard by remember { mutableStateOf(false) }
    var selectedCategory: Category? by remember { mutableStateOf(null) }
    val categoriesList by remember { mutableStateOf(firebaseViewModel.categories) }
    val poisList by remember { mutableStateOf(firebaseViewModel.pois) }
    var showPois by remember { mutableStateOf(true) }
    var showLocals by remember { mutableStateOf(false) }
    var showCategories by remember { mutableStateOf(false) }

    var avaliacao: Avaliacao? by remember { mutableStateOf(null) }


    Column {
        Spacer(modifier = Modifier.height(70.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = androidx.compose.ui.Alignment.CenterHorizontally)
        ) {
            Button(onClick = {
                showPois = true
                showCategories = false
                showLocals = false
            }) {
                Text(text = stringResource(R.string.pois))
            }
            Button(onClick = {
                showLocals = true
                showCategories = false
                showPois = false
            }) {
                Text(text = stringResource(R.string.locals))
            }
            Button(onClick = {
                showCategories = true
                showLocals = false
                showPois = false
            }) {
                Text(text = stringResource(R.string.categories))
            }
        }
        if (searchedPOIs.value.isNotEmpty() && showPois) {
            Text(
                text = stringResource(id = R.string.pois), fontSize = 30.sp, modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
            LazyColumn(content = {
                items(searchedPOIs.value.size, itemContent = {
                    Card {
                        POICard(
                            poi = searchedPOIs.value[it], onClick = {
                                selectedPOI = searchedPOIs.value[it]
                                openPoiCard = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                })
            })
        }

        if (searchedLocals.value.isNotEmpty() && showLocals) {
            Text(text = stringResource(id = R.string.locals))
            LazyColumn(content = {
                items(searchedLocals.value.size, itemContent = {
                    Card {
                        LocalCard(local = searchedLocals.value[it], onClick = {
                            selectedLocal = searchedLocals.value[it]
                            openLocalCard = true
                        })
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                })
            })
        }
        if (searchedCategories.value.isNotEmpty() && showCategories) {
            Text(text = stringResource(id = R.string.categories))
            LazyColumn(content = {
                items(searchedCategories.value.size, itemContent = {
                    Card {
                        CategoryCard(category = searchedCategories.value[it]) {
                            selectedCategory = searchedCategories.value[it]
                            openCategoryCard = true
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                })
            })
        }
    }
    if (openPoiCard) {
        selectedPOI?.let { it ->
            ViewPOI(
                poi = it,
                onDismiss = { openPoiCard = false },
                firebaseViewModel = firebaseViewModel,
                onSelect = {
                    avaliacao = Avaliacao(
                        it["comment"].toString(),
                        it["rating"] as Int,
                        it["user"].toString(),
                        it["poi"].toString()
                    )
                    firebaseViewModel.addAvaliacao(
                        avaliacao ?: Avaliacao(
                            "Error",
                            0,
                            "Error",
                            "Error"
                        )
                    )
                    openPoiCard = false
                })
        }
    }
    if (openLocalCard) {
        selectedLocal?.let { it ->
            ViewLocation(
                location = it,
                onDismiss = { openLocalCard = false },
                poisList = poisList.value,
                onSelect = {
                    openLocalCard = false
                    selectedPOI = it
                })
        }
    }
    if (openCategoryCard) {
        selectedCategory?.let {
            ViewFilter(
                poisList = poisList,
                category = selectedCategory?.nome ?: "",
                onDismiss = { openCategoryCard = false },
                onSelect = {
                    openCategoryCard = false
                    selectedPOI = it
                },
                categorias = categoriesList
            )
        }
    }
}

