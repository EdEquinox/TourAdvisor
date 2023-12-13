package pt.isec.touradvisor.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SettingScreen(
    navController: NavController
) {

    var switchState by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Setting 1")
        Switch(
            checked = switchState,
            onCheckedChange = { switchState = it }
        )
        Divider()
        Text(text = "Setting 2")
        Switch(
            checked = switchState,
            onCheckedChange = { switchState = it }
        )
        Divider()
        Text(text = "Setting 3")
        Switch(
            checked = switchState,
            onCheckedChange = { switchState = it }
        )
        Divider()

        // Add more settings here
    }

}