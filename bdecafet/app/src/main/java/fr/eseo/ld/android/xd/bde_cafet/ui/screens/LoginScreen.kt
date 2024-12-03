package fr.eseo.ld.android.xd.bde_cafet.ui.screens

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.AuthenticationViewModel


@Composable
fun LoginScreen(
    authenticationViewModel: AuthenticationViewModel,
    googleSignInLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val errorState by authenticationViewModel.errorMessage.observeAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    val emptyFieldsError = stringResource(id = R.string.error_empty_fields)
    val invalidEmailError = stringResource(id = R.string.error_invalid_email)
    val shortPasswordError = stringResource(id = R.string.error_short_password)

    errorState?.let { error ->
        errorMessage = error
        isLoading = false
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.eseo_logo_couleur_positif),
            contentDescription = null,
            modifier = Modifier
                .size(256.dp)
                .align(Alignment.CenterHorizontally)
        )
        // Application name
        Text(
            text = stringResource(id = R.string.app_name),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
                authenticationViewModel.clearErrorMessage()
            },
            label = { Text(text = stringResource(id = R.string.username)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
                authenticationViewModel.clearErrorMessage()
            },
            label = { Text(text = stringResource(id = R.string.password)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        // Login button
        Button(
            onClick = {
                errorMessage = null // Reset error message
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = emptyFieldsError
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = invalidEmailError
                } else if (password.length < 6) {
                    errorMessage = shortPasswordError
                } else {
                    isLoading = true
                    authenticationViewModel.login(email, password)
                }
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp)
        ) {
            Text(text = stringResource(id = R.string.connection), fontSize = 16.sp)
        }

        // Sign up button
        Button(
            onClick = {
                errorMessage = null // Reset error message
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = emptyFieldsError
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = invalidEmailError
                } else if (password.length < 6) {
                    errorMessage = shortPasswordError
                } else {
                    isLoading = true
                    authenticationViewModel.signUp(email, password)
                }
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp) // Height similar to the design
        ) {
            Text(text = stringResource(id = R.string.sign_up), fontSize = 16.sp)
        }

        Button(
            onClick = {
                val googleSignInClient = authenticationViewModel.getGoogleSignInClient()
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp)
                .border(
                    width = 1.dp, // Border width
                    color = Color.Gray,
                    shape = RoundedCornerShape(50)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.sign_in_with_google),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text(text = stringResource(id = R.string.success)) },
                text = { Text(text = stringResource(id = R.string.account_created)) },
                confirmButton = {
                    Button(onClick = { showSuccessDialog = false }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            )
        }
    }
}
