package com.ml.shubham0204.facenet_android.presentation.setting

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.benchmark.perfetto.PerfettoConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ananta.faceapp.ApiRepo.AuthApi.Companion.spoofAttempts
import com.ananta.faceapp.R
import com.ananta.faceapp.presentation.screens.dashboard.AssetsConstant
import com.ananta.faceapp.presentation.screens.dashboard.DashboardWidget
import com.ananta.faceapp.presentation.theme.FaceNetAndroidTheme
import com.ananta.faceapp.viewModel.AuthViewModel
import com.ananta.faceapp.viewModel.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController,
                   authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
                   ) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val appBgColor = remember {
        Color(ContextCompat.getColor(context, R.color.white))
    }
//    var isAuthenticated by remember { mutableStateOf(false) }
    val isAuthenticated by authViewModel.isAuthenticated.observeAsState(false)
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    // Show toast if triggered
    LaunchedEffect(showToast) {
        if (showToast) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            showToast = false
        }
    }
    // Handle system back press
//    BackHandler(enabled = true) {
//
////        isAuthenticated = false // Reset authentication state
//        navController.popBackStack() // Navigate back to previous screen
//    }
    FaceNetAndroidTheme {
        Scaffold(
            containerColor = appBgColor,
//            topBar = {
//                TopAppBar(
//                    title = { Text("Settings") },
//                    navigationIcon = {
//                        IconButton(onClick = { navController.popBackStack() }) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "Back"
//                            )
//                        }
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = appBgColor,
//                        titleContentColor = Color.Black,
//                        navigationIconContentColor = Color.Black
//                    )
//                )
//            }
            topBar = {
                TopAppBar(
                    title = {
                            androidx.compose.material3.Text(
                                "Settings"
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
                        containerColor = appBgColor,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    )
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor =  Color(ContextCompat.getColor(context, R.color.checkbox_color)),           // AppBar background
//                        titleContentColor = Color.Black,        // Title text color
//                        navigationIconContentColor = Color.Black // Back icon color
//                    ),
                )
            },

        ) { padding ->
            if (isAuthenticated) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DashboardWidget(
                            title = "Add User",
                            imageRes = AssetsConstant.check,
                            onClick = { navController.navigate("add_user") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        DashboardWidget(
                            title = "Employee",
                            imageRes = AssetsConstant.customer,
                            onClick = { navController.navigate("employee_list") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    androidx.compose.material3.Text(
                        "Enter Password",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { androidx.compose.material3.Text("Enter password") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),

                        modifier = Modifier.fillMaxWidth(),

                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            // Verify password (replace "admin123" with your actual password check)
                            val storedPassword =
                                sharedPreferences.getString("company_password", null)
                            if (password == storedPassword) { // Fallback default password
                                authViewModel.isAuthenticated(true)
//                                authViewModel.isAuthenticated = true
//                                isAuthenticated = true
                                Toast.makeText(
                                    context,
                                    "Authentication successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(
                                ContextCompat.getColor(
                                    context,
                                    R.color.primary
                                )
                            )
                        )
                    ) {
                        androidx.compose.material3.Text("VERIFY")
                    }
                }
            }
        }
    }
}