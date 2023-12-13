package pt.isec.touradvisor.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isec.touradviser.R
import pt.isec.touradvisor.ui.viewmodels.FirebaseViewModel

@Composable
fun LoginScreen(
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

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Welcome to Tour Advisor!", fontSize = 30.sp, modifier = Modifier.align(alignment = CenterHorizontally))
        Spacer(modifier = Modifier.height(80.dp))
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") },
            modifier = Modifier
                .width(300.dp)
                .align(alignment = CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = "Password") },
            modifier = Modifier
                .width(300.dp)
                .align(alignment = CenterHorizontally),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(50.dp))
        Row(modifier = Modifier.align(alignment = CenterHorizontally)) {
            Button(
                onClick = { viewModel.signInWithEmail(email.value, password.value) },
                modifier = Modifier.width(120.dp)
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.width(60.dp))
            Button(
                onClick = { viewModel.createUserWithEmail(email.value, password.value) },
                modifier = Modifier.width(120.dp)
            ) {
                Text(text = "Register")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.tour_advisor_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(alignment = CenterHorizontally)
                .size(250.dp)
        )

        if (error != null) {
            Toast.makeText(null, error, Toast.LENGTH_SHORT).show()
        }
    }

}