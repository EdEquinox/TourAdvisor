package pt.isec.touradvisor.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingScreen(
    navController: NavController
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Setting(text = "Change Profile Picture", imageVector = Icons.Default.Person, contentDescription = "Profile Pic" ) {
            // TODO
        }
        Setting(text = "Change Password", imageVector = Icons.Default.Key, contentDescription = "Change Password" ) {
            // TODO
        }
        Setting(text = "Change Email", imageVector = Icons.Default.Mail, contentDescription = "Change Email" ) {
            // TODO
        }
        Setting(text = "Change Nickname", imageVector = Icons.Default.DriveFileRenameOutline, contentDescription = "Change Nick" ) {
            // TODO
        }
        Setting(text = "Delete Account", imageVector = Icons.Default.Delete, contentDescription = "Delete Account" ) {

        }
        Setting(text = "Info", imageVector = Icons.Default.Info, contentDescription = "Info" ) {
            // TODO
        }
    }

}
@Composable
fun Setting(
    text: String,
    imageVector: ImageVector,
    contentDescription : String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(8.dp)
            .clickable { onClick() }) {
        Row(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.Center)
        ) {
            Icon(imageVector = imageVector , contentDescription = contentDescription, modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }

}