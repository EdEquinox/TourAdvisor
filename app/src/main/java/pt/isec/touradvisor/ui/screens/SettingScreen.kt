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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.touradviser.R
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
    LazyColumn(content = {
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
        item {
            Setting(text = stringResource(R.string.change_profile_picture),
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Pic",
                onClick = {
                    showDialogPFP.value = true
                }
            )
        }
        item {
            Setting(text = stringResource(R.string.change_password),
                imageVector = Icons.Default.Key,
                contentDescription = "Change Password",
                onClick = {
                    showDialogPassword.value = true
                })
        }
        item {
            Setting(
                text = stringResource(R.string.change_email),
                imageVector = Icons.Default.Mail,
                contentDescription = "Change Email"
            ) {
                showDialogEmail.value = true
            }
        }
        item {
            Setting(
                text = stringResource(R.string.change_nickname),
                imageVector = Icons.Default.DriveFileRenameOutline,
                contentDescription = "Change Nick"
            ) {
                showDialogNick.value = true
            }
        }
        item {
            Setting(
                text = stringResource(R.string.delete_account),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Account"
            ) {
                showDialogDelete.value = true
            }
        }
        item {
            Setting(
                text = stringResource(R.string.info),
                imageVector = Icons.Default.Info,
                contentDescription = "Info"
            ) {
                showDialogInfo.value = true
            }
        }
    })

    if (showDialogPassword.value) {
        ChangePassword(firebaseViewModel = firebaseViewModel) { showDialogPassword.value = false }
    }
    if (showDialogEmail.value) {
        ChangeEmail(firebaseViewModel = firebaseViewModel) { showDialogEmail.value = false }
    }
    if (showDialogNick.value) {
        ChangeNick(firebaseViewModel = firebaseViewModel) { showDialogNick.value = false }
    }
    if (showDialogDelete.value) {
        DeleteAccount(firebaseViewModel = firebaseViewModel) { showDialogDelete.value = false }
    }
    if (showDialogInfo.value) {
        InfoApp { showDialogInfo.value = false }
    }
    if (showDialogPFP.value) {
        ChangePFP(firebaseViewModel = firebaseViewModel) { showDialogPFP.value = false }
    }

}

@Composable
fun Setting(
    text: String,
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit = { },
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(8.dp)
            .clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
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
        title = { Text(text = stringResource(R.string.change_password)) },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text(stringResource(R.string.password_antiga)) }
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.password_nova)) }
                )
                OutlinedTextField(
                    value = newPassword2,
                    onValueChange = { newPassword2 = it },
                    label = { Text(stringResource(R.string.password_nova)) }
                )
            }

        },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.changePassword(oldPassword, newPassword, newPassword2)
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.cancelar))
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
        title = { Text(text = stringResource(R.string.change_email)) },
        text = {
            Column {

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text(stringResource(R.string.email_novo)) }
                )
            }

        },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.changeEmail(newEmail)
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.cancelar))
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
        title = { Text(text = stringResource(R.string.change_nickname)) },
        text = {
            Column {

                OutlinedTextField(
                    value = newNick,
                    onValueChange = { newNick = it },
                    label = { Text(stringResource(R.string.nickname_novo)) }
                )
            }

        },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.changeNick(newNick)
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.cancelar))
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
        title = { Text(text = stringResource(R.string.delete_account)) },
        text = {
            Column {

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) }
                )
            }

        },
        confirmButton = {
            Button(onClick = {
                firebaseViewModel.deleteAccount(password)
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.cancelar))
            }
        }
    )

}

@Composable
fun InfoApp(
    onDismissRequest: () -> Unit
) {

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = stringResource(R.string.info)) },
        text = {
            Column {
                Row {
                    Text(text = stringResource(R.string.creditos))
                }
                Row {
                    Text(text = stringResource(R.string.versao))
                }
                Row {
                    Text(text = stringResource(R.string.direitos))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.ok))
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
    val user = firebaseViewModel.userUID.value

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(text = stringResource(R.string.change_profile_picture)) },
        text = {
            Column {
                UploadPhotoButton(onUriReady = {
                    newPFP = it
                    user?.let { firebaseViewModel.addPFPToFirestore(user, newPFP) }
                }, type = "Profile", picName = user.toString())
            }

        },
        confirmButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.cancelar))
            }
        }
    )
}

