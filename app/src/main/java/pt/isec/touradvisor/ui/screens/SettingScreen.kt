package pt.isec.touradvisor.ui.screens

import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel

@Composable
fun SettingScreen(
    firebaseViewModel: FirebaseViewModel
) {
    val showDialogPassword = remember { mutableStateOf(false) }
    val showDialogEmail = remember { mutableStateOf(false) }
    val showDialogNick = remember { mutableStateOf(false) }
    val showDialogDelete = remember { mutableStateOf(false) }
    val showDialogInfo = remember { mutableStateOf(false) }
    val showDialogPFP = remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(100.dp))
    Column(modifier = Modifier.fillMaxWidth()) {

        Setting(text = "Change Profile Picture",
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Pic",
            onClick = {
                showDialogPFP.value = true
                Log.i("TAG", "SettingScreen: " + showDialogPFP.value)
            }
        )
        Setting(text = "Change Profile Picture",
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Pic",
            onClick = {
                showDialogPFP.value = true
                Log.i("TAG", "SettingScreen: " + showDialogPFP.value)
            }
        )
        Setting(text = "Change Password", imageVector = Icons.Default.Key, contentDescription = "Change Password" , onClick = {
            showDialogPassword.value = true
            Log.i("TAG", "SettingScreen: " + showDialogPassword.value)
        })
        Setting(text = "Change Email", imageVector = Icons.Default.Mail, contentDescription = "Change Email" ) {
            showDialogEmail.value = true
        }
        Setting(text = "Change Nickname", imageVector = Icons.Default.DriveFileRenameOutline, contentDescription = "Change Nick" ) {
            showDialogNick.value = true
        }
        Setting(text = "Delete Account", imageVector = Icons.Default.Delete, contentDescription = "Delete Account" ) {
            showDialogDelete.value = true
        }

        Setting(text = "Info", imageVector = Icons.Default.Info, contentDescription = "Info" ) {
            showDialogInfo.value = true
        }
    }

    if (showDialogPassword.value){
        ChangePassword(firebaseViewModel = firebaseViewModel) { showDialogPassword.value = false }
    }
    if (showDialogEmail.value){
        ChangeEmail(firebaseViewModel = firebaseViewModel) { showDialogEmail.value = false }
    }
    if (showDialogNick.value){
        ChangeNick(firebaseViewModel = firebaseViewModel) { showDialogNick.value = false }
    }
    if (showDialogDelete.value){
        DeleteAccount(firebaseViewModel = firebaseViewModel) { showDialogDelete.value = false }
    }
    if (showDialogInfo.value){
        InfoApp { showDialogInfo.value = false }
    }
    if (showDialogPFP.value){
        ChangePFP(firebaseViewModel = firebaseViewModel) { showDialogPFP.value = false }
    }

}
@Composable
fun Setting(
    text: String,
    imageVector: ImageVector,
    contentDescription : String,
    onClick: () -> Unit = { },
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(8.dp)
            .clickable { onClick() }){
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



@Composable
fun ChangePassword(
    firebaseViewModel: FirebaseViewModel,
    onDismissRequest: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newPassword2 by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Change Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Password Antiga") }
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Password Nova") }
                )
                OutlinedTextField(
                    value = newPassword2,
                    onValueChange = { newPassword2 = it },
                    label = { Text("Password Nova") }
                )
            }

               },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.changePassword(oldPassword, newPassword, newPassword2)
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        }
    )

    }

@Composable
fun ChangeEmail(
    firebaseViewModel: FirebaseViewModel,
    onDismissRequest: () -> Unit
) {
    var newEmail by remember { mutableStateOf("") }


    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Change Email") },
        text = {
            Column {

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Email Novo") }
                )
            }

        },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.changeEmail(newEmail)
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        }
    )

}

@Composable
fun ChangeNick(
    firebaseViewModel: FirebaseViewModel,
    onDismissRequest: () -> Unit
) {
    var newNick by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Change Nickname") },
        text = {
            Column {

                OutlinedTextField(
                    value = newNick,
                    onValueChange = { newNick = it },
                    label = { Text("Nickname Novo") }
                )
            }

        },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.changeNick(newNick)
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        }
    )

}

@Composable
fun DeleteAccount(
    firebaseViewModel: FirebaseViewModel,
    onDismissRequest: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Delete Account") },
        text = {
            Column {

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") }
                )
            }

        },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.deleteAccount(password)
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        }
    )

}

@Composable
fun InfoApp(
    onDismissRequest: () -> Unit
) {

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Info") },
        text = {
            Column {
                Row {
                    Text(text = "Aplicação desenvolvida por: \n\n" +
                            "José Marques - 2018019295\n" +
                            "Ana Ferreira - 2017011822\n" +
                            "Carolina Rosa - 2017012933\n"
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismissRequest()
            }) {
                Text(text = "Ok")
            }
        }
    )
}

@Composable
fun ChangePFP(
    firebaseViewModel: FirebaseViewModel,
    onDismissRequest: () -> Unit
) {
    var newPFP by remember { mutableStateOf("") }
    var user = firebaseViewModel.userUID.value

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Change Profile Picture") },
        text = {
            Column {

                UploadPhotoButton(onUriReady = {
                    newPFP = it
                    Log.i("TAG", "ChangePFP: " + newPFP)
                    user?.let { firebaseViewModel.addPFPToFirestore(user, newPFP) }
                }, type = "Profile" , picName = user.toString() )
            }

        },
        confirmButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        }
    )
}

