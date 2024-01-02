package pt.isec.touradvisor.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    val context = LocalContext.current
    val email = remember { mutableStateOf("")}
    val password = remember { mutableStateOf("")}
    val error = viewModel.error.value
    val user by remember { viewModel.user }
    val configuration = LocalConfiguration.current
    val portrait = remember{ mutableIntStateOf(configuration.orientation) }
    val fields = stringResource(R.string.please_fill_all_fields)


    LaunchedEffect(key1 = user) {
        if (user != null && error == null) {
            onLogin()
        }
    }

    if (portrait.value == Configuration.ORIENTATION_LANDSCAPE){
        Row {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(16.dp)) {
                Text(text = stringResource(id = R.string.welcome_to_tour_advisor), fontSize = 30.sp, modifier = Modifier.align(alignment = CenterHorizontally))
                Spacer(modifier = Modifier.height(80.dp))
                Image(
                    painter = painterResource(id = R.drawable.tour_advisor_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .align(alignment = CenterHorizontally)
                        .size(250.dp)
                )
            }
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text(text = stringResource(R.string.email)) },
                    modifier = Modifier
                        .width(300.dp)
                        .align(alignment = CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text(text = stringResource(R.string.password)) },
                    modifier = Modifier
                        .width(300.dp)
                        .align(alignment = CenterHorizontally),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(50.dp))
                Row(modifier = Modifier.align(alignment = CenterHorizontally)) {
                    Button(
                        onClick = {
                            if (email.value.isBlank() || password.value.isBlank()){
                                Toast.makeText(context, fields, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.signInWithEmail(email.value, password.value)},
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text(text = stringResource(id = R.string.login))
                    }
                    Spacer(modifier = Modifier.width(60.dp))
                    Button(
                        onClick = {
                            if (email.value.isBlank() || password.value.isBlank()){
                                Toast.makeText(context, fields, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.createUserWithEmail(email.value, password.value)
                        },
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text(text = stringResource(id = R.string.register))
                    }
                }
            }
        }
    } else{
        Column(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)) {

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = stringResource(R.string.welcome_to_tour_advisor),
                fontSize = 30.sp,
                modifier = Modifier
                    .align(alignment = CenterHorizontally))
            Spacer(modifier = Modifier.height(80.dp))
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(text = stringResource(id = R.string.email)) },
                modifier = Modifier
                    .width(300.dp)
                    .align(alignment = CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = stringResource(id = R.string.password)) },
                modifier = Modifier
                    .width(300.dp)
                    .align(alignment = CenterHorizontally),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(50.dp))
            Row(modifier = Modifier.align(alignment = CenterHorizontally)) {
                Button(
                    onClick = {
                        if (email.value.isBlank() || password.value.isBlank()){
                            Toast.makeText(context, fields, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.signInWithEmail(email.value, password.value)},
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(text = stringResource(R.string.login))
                }
                Spacer(modifier = Modifier.width(60.dp))
                Button(
                    onClick = {
                        if (email.value.isBlank() || password.value.isBlank()){
                            Toast.makeText(context, fields, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.createUserWithEmail(email.value, password.value)
                    },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(text = stringResource(R.string.register))
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
        }
    }
    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
}