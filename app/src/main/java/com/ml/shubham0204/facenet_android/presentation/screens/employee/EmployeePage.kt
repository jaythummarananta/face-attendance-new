package com.ananta.faceapp.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ananta.faceapp.R
import com.ananta.faceapp.presentation.screens.employee.EmpListTile
import com.ananta.faceapp.viewModel.AuthViewModel
import com.ananta.faceapp.viewModel.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeePage(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val appBgColor = Color(ContextCompat.getColor(context, R.color.white))
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // State variables
    var password by remember { mutableStateOf("") }
//    var isAuthenticated by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    // Fetch data only after authentication
//    LaunchedEffect(isAuthenticated) {
//        if (isAuthenticated) {
            authViewModel.fetchAllUserData()
//        }
//    }

    val users by authViewModel.users.observeAsState(emptyList())
    val isLoading by authViewModel.isLoading.observeAsState(false)

    Scaffold(
        containerColor = appBgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Employee",
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
//            if (!isAuthenticated) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = 20.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.Start
//                ) {
//                    Text(
//                        "Enter Password",
//                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
//                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//                    OutlinedTextField(
//                        value = password,
//                        onValueChange = { password = it },
//                        placeholder = { Text("Enter password") },
//                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(),
//                        trailingIcon = {
//                            IconButton(onClick = { showPassword = !showPassword }) {
//                                Icon(
//                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
//                                    contentDescription = if (showPassword) "Hide password" else "Show password"
//                                )
//                            }
//                        }
//                    )
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Button(
//                        onClick = {
//                            // Verify password
//                            val storedPassword = sharedPreferences.getString("company_password", null)
//                            if (password == storedPassword) {
//                                isAuthenticated = true
//                                Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
//                            } else {
//                                Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
//                            }
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(ContextCompat.getColor(context, R.color.primary))
//                        )
//                    ) {
//                        Text("VERIFY")
//                    }
//                }
//            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        if (users.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Text(
                                    text = "No employees found",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                items(users) { user ->
                                    EmpListTile(item = user, authViewModel = authViewModel)
                                }
                            }
                        }
                    }
                }
            }
//        }
    }
}