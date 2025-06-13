package com.yourpackage.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.ml.shubham0204.facenet_android.ApiRepo.AuthApi
import com.ml.shubham0204.facenet_android.R
import kotlinx.coroutines.delay

//
//@Composable
//fun HomeScreen(navController: NavHostController) {
//    Box(
//        modifier = Modifier.fillMaxSize().background(Color.White),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(24.dp)
//        ) {
//
//
//            Image(
//                painter = painterResource(id = R.drawable.check), // Replace with your image resource
//                contentDescription = "Logo",
//                modifier = Modifier
//                    .size(150.dp)
//                    .clickable {
//                        navController.navigate("detect") // Navigate on image click
//                    }
//            )
//
////            Button(
////                onClick = { navController.navigate("detect") }
////            ) {
////                Text("Go to Detect Screen")
////            }
//        }
//    }
//}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    isFakeUser: Boolean = false,
    context: android.content.Context = LocalContext.current
) {
    var showProgressDialog by remember { mutableStateOf(false) }



    // Show progress dialog after 3 spoof attempts
    if (showProgressDialog) {
        LaunchedEffect(Unit) {
            delay(3000L) // Delay for 3 seconds
            showProgressDialog = false // Dismiss dialog
        }
        Dialog(
            onDismissRequest = { /* Non-dismissable */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    if (AuthApi.spoofAttempts >= 3) {
                        IconButton(
                            onClick = {
                                AuthApi.spoofAttempts = 0 // Reset attempts on refresh
                                showProgressDialog = true // Hide progress dialog
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,           // AppBar background
                        titleContentColor = Color.Black,        // Title text color
                        navigationIconContentColor = Color.Black // Back icon color
                    ),
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.check), // Replace with your image resource
//                        contentDescription = "Logo",
//                        modifier = Modifier
//                            .size(150.dp)
//                            .clickable {
//                                navController.navigate("detect")
//                            }
//                    )
                    Image(
                        painter = painterResource(id = R.drawable.check), // Replace with your image resource
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .then(
                                if (AuthApi.spoofAttempts < 3) {
                                    Modifier.clickable {
                                        navController.navigate("detect")
                                    }
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
        }
    )
}