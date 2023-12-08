package pt.isec.touradvisor.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: FirebaseViewModel,
    modifier: Modifier = Modifier,
    onLogin: () -> Unit = {}
    ) {

    val email = remember { mutableStateOf("")}
    val password = remember { mutableStateOf("")}
    val error = viewModel.error.value
    val user by remember { viewModel.user }

    LaunchedEffect(key1 = user) {
        if (user != null && error == null) {
            onLogin()
        }
    }

    Column {
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.signInWithEmail(email.value, password.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.createUserWithEmail(email.value, password.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }
        if (error != null) {
            Text(text = error)
        }
    }
}