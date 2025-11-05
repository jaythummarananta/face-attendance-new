package com.ananta.faceapp.presentation.screens.dashboard

import ShowCustomAlertDialog
import ShowCustomNotFoundAlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.ananta.faceapp.ApiRepo.AuthApi
import com.ananta.faceapp.ApiRepo.AuthApi.Companion.spoofAttempts
import com.ananta.faceapp.R
import com.ananta.faceapp.presentation.theme.FaceNetAndroidTheme
import com.google.gson.Gson
import com.ml.shubham0204.facenet_android.domain.model.attendance.AttendanceModel
import kotlinx.coroutines.delay

// Asset constants (equivalent to AssetConstant.dart)
object AssetsConstant {
    val check = R.drawable.check
    val correct = R.drawable.correct
    val customer = R.drawable.customer
    val report = R.drawable.report
    val calender = R.drawable.ic_attendance
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigateBack: (() -> Unit), navController: NavController) {
    var showProgressDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showAttendanceDialog by remember { mutableStateOf(false) }
    var showNotAttendanceDialog by remember { mutableStateOf(false) }
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val appBgColor = remember {
        Color(ContextCompat.getColor(context, R.color.white))
    }
    var attendanceResponse by remember { mutableStateOf<AttendanceModel?>(null) }
    // Collect attendanceResponse from backstack
    val gson = Gson()

    // Collect attendanceResponse from backstack as JSON string
    val attendanceJson by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("attendanceResponse", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    // Handle attendanceResponse updates
    LaunchedEffect(attendanceJson) {
        attendanceJson?.let { json ->
            Log.d("DashboardScreen", "Received attendanceResponse JSON: $json")
            try {
                val response = gson.fromJson(json, AttendanceModel::class.java)
                attendanceResponse = response
                Log.d("DashboardScreen", "Parsed attendanceResponse: $response")
                if (response != null) {

                    if(response.error!=null){
                        showAttendanceDialog = true
                    }
                    else {
                        val userName = response.data!!.userDetails.firstName
                        toastMessage = "Attendance recorded for $userName"
                        showToast = true
                        showAttendanceDialog = true
                    }
                } else {
                    showNotAttendanceDialog = true
                    toastMessage = "Attendance recording failed:"
                    showToast = true
                }
                // Clear the backstack data to prevent re-processing
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "attendanceResponse",
                    null
                )
            } catch (e: Exception) {
                Log.e("DashboardScreen", "Failed to parse attendanceResponse: ${e.message}")
                toastMessage = "Error processing attendance data"
                showToast = true
            }
        }
    }

//    attendanceResponse?.let { response ->
//        ShowCustomAlertDialog(
//            response = response,
//        )
//
//        // Auto-dismiss after 2 seconds
//        LaunchedEffect(Unit) {
//            delay(1500)
//        }
//    }
    // Show attendance details dialog
    if (showAttendanceDialog) {
        ShowCustomAlertDialog(
            response = attendanceResponse!!
        )
        // Auto-dismiss after 2 seconds
        LaunchedEffect(Unit) {
            delay(5000L)
            showAttendanceDialog = false
        }
    }


    if (showNotAttendanceDialog) {
        ShowCustomNotFoundAlertDialog(
        )
        // Auto-dismiss after 2 seconds
        LaunchedEffect(Unit) {
            delay(2000L)
            showNotAttendanceDialog = false
        }
    }
    // Trigger toast based on spoofAttempts
    LaunchedEffect(spoofAttempts) {
        if (spoofAttempts > 0) {
            AuthApi.spoofAttempts = spoofAttempts // Sync with AuthApi
            toastMessage = if (spoofAttempts > 3) {
                showProgressDialog = true
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
            delay(5000L) // Delay for 3 seconds
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



    FaceNetAndroidTheme {
        Scaffold(
            containerColor = appBgColor,
            topBar = {
                TopAppBar(
                    title = {
                        sharedPreferences.getString("company_name", "Dashboard")?.let { Text(it) }
                    },
                    navigationIcon = {
                        if (spoofAttempts >= 3) {
                            IconButton(
                                onClick = {
                                    spoofAttempts = 0 // Reset attempts on refresh
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
                    actions = {
                        IconButton(onClick = {
                            navController.navigate("settings")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }

                        IconButton(onClick = {
                            showLogoutDialog = true // Show logout dialog
                            // TODO: Handle logout logic here
//                            AuthApi.logout() // Example
//                            navController.navigate("login") {
//                                with(sharedPreferences.edit()) {
//                                    clear() // Clear all SharedPreferences data
//                                    apply()
//                                }
//                                popUpTo("dashboard") { inclusive = true }
//                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout"
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
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    DashboardWidget(
//                        title = "Add User",
//                        imageRes = AssetsConstant.check,
//                        onClick = {
//                            navController.navigate("add_user")
//                        },
//                        modifier = Modifier.weight(1f)
//                    )
//                    Spacer(modifier = Modifier.width(10.dp))
//                    DashboardWidget(
//                        title = "Employee",
//                        imageRes = AssetsConstant.customer,
//                        onClick = {
//                            navController.navigate("employee_list")
//                        },
//                        modifier = Modifier.weight(1f)
//                    )
//
//                }
//                Spacer(modifier = Modifier.height(10.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    DashboardWidget(
//                        title = "Employee",
//                        imageRes = AssetsConstant.customer,
//                        onClick = {
//                            navController.navigate("employee_list")
//                        },
//                        modifier = Modifier.weight(1f)
//                    )
//                    Spacer(modifier = Modifier.width(10.dp))
//                    DashboardWidget(
//                        title = "Report",
//                        imageRes = AssetsConstant.report,
//                        onClick = {
//                            navController.navigate("report")
//                        },
//                        modifier = Modifier.weight(1f)
//                    )
//                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DashboardWidget(
                        title = "Attendance",
                        imageRes = AssetsConstant.calender,
                        onClick = {
                            if (spoofAttempts >= 3) {
                                spoofAttempts = 0 // Reset attempts on refresh
                                showProgressDialog = true
                            } else {
                                if (spoofAttempts < 3) {
                                    navController.navigate("attendance")
                                } else {
                                    toastMessage = "Access blocked due to multiple spoof attempts"
                                    showToast = true
                                }
                            }

                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Logout") },
                    text = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Handle logout logic
                                AuthApi.spoofAttempts = 0 // Reset spoof attempts
                                with(sharedPreferences.edit()) {
                                    clear() // Clear all SharedPreferences data
                                    apply()
                                }
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                                showLogoutDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showLogoutDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Cancel")
                        }
                    },
                    containerColor = appBgColor
                )
            }
        }
    }
}

@Composable
fun DashboardWidget(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(175.dp)
            .clip(
                RoundedCornerShape(
                    topEnd = 15.dp,
                    bottomStart = 15.dp
                )
            )
            .background(colorResource(id = R.color.secondary))
            .border(
                1.dp,
                colorResource(id = R.color.white),
                RoundedCornerShape(topEnd = 15.dp, bottomStart = 15.dp)
            )
            .clickable { onClick() }
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(90.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(0.85f)
            )
        }
    }
}

// Placeholder composables for navigation destinations


@Composable
fun LeaveScreen() {
    // Implement Leave screen
    Text("Leave Screen")
}

@Composable
fun EmployeeScreen() {
    // Implement Employee screen
    Text("Employee Screen")
}

@Composable
fun ReportScreen() {
    // Implement Report screen
    Text("Report Screen")
}

@Composable
fun AttendanceScreen() {
    // Implement Attendance screen
    Text("Attendance Screen")
}