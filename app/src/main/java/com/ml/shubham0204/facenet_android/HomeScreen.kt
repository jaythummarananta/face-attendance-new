package com.yourpackage.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.ananta.globalwallet.ui.composables.CustomToast
import com.ml.shubham0204.facenet_android.ApiRepo.AuthApi
import com.ml.shubham0204.facenet_android.ApiRepo.AuthApi.Companion.spoofAttempts
import com.ml.shubham0204.facenet_android.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    isFakeUser: Boolean = false,
    context: android.content.Context = LocalContext.current
) {

    var showProgressDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Trigger toast based on spoofAttempts
    LaunchedEffect(spoofAttempts) {
        if (spoofAttempts > 0) {
            AuthApi.spoofAttempts = spoofAttempts // Sync with AuthApi
            toastMessage = if (spoofAttempts > 3) {
                "Please Call Developer You are Blocked"
            } else {
                "Spoof detected $spoofAttempts time"
            }
            showToast = true
        }
    }

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
                    Image(
                        painter = painterResource(id = R.drawable.attendance), // Replace with your drawable
                        contentDescription = "Attendance",
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Button(
                        onClick = {
                            if (AuthApi.spoofAttempts < 3) {
                                navController.navigate("detect")
                            }
                        },
                        enabled = AuthApi.spoofAttempts < 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 10.dp,
                            disabledElevation = 0.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (AuthApi.spoofAttempts < 3) MaterialTheme.colorScheme.primary else Color.Gray,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Take Attendance",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Submit Button
//                    Button(
//                        onClick = {
//                            if (AuthApi.spoofAttempts < 3) {
//                                Modifier.clickable {
//                                    navController.navigate("detect")
//                                }
//                            } else {
//                                Modifier
//                            }
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.primary
//                        )
//                    ) {
//                        Text("Take Attendance", color = Color.White, fontSize = 16.sp)
//                    }
//                    Image(
//                        painter = painterResource(id = R.drawable.check), // Replace with your image resource
//                        contentDescription = "Logo",
//                        modifier = Modifier
//                            .size(150.dp)
//                            .then(
//                                if (AuthApi.spoofAttempts < 3) {
//                                    Modifier.clickable {
//                                        navController.navigate("detect")
//                                    }
//                                } else {
//                                    Modifier
//                                }
//                            )
//                    )
                }

//                // Custom Toast
//                Box(
//                    modifier = Modifier
//                        .wrapContentSize(Alignment.BottomCenter)
//                ) {
//                    CustomToast(
//                        message = toastMessage,
//                        isVisible = showToast,
//                        onDismiss = { showToast = false },
//                        duration = 2000L
//                    )
//                }
            }
        }
    )
}