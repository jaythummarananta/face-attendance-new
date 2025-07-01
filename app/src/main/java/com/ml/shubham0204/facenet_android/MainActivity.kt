package com.ananta.faceapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.ananta.faceapp.ApiRepo.AuthApi
import com.ananta.faceapp.ApiRepo.UserResponse
import com.ananta.faceapp.ApiRepo.showMessage
import com.ananta.faceapp.presentation.screens.add_face.FaceDetectionPage
import com.ananta.faceapp.presentation.screens.add_user.AddUserScreen
import com.ananta.faceapp.presentation.screens.dashboard.DashboardScreen
import com.ananta.faceapp.presentation.screens.dashboard.EmployeeScreen
import com.ananta.faceapp.presentation.screens.dashboard.LeaveScreen
import com.ananta.faceapp.presentation.screens.dashboard.ReportScreen
import com.ananta.faceapp.presentation.screens.detect_screen.DetectScreen
import com.ananta.faceapp.presentation.screens.face_list.FaceListScreen
import com.ananta.faceapp.screens.EmployeePage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.yourpackage.ui.HomeScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize Firebase Realtime Database with the provided URL
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("config")

        // Add initial data to Realtime Database
        getIsEnableValue()

        setContent {
            AppNavigation()
        }
    }
    private fun getIsEnableValue() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isEnable = snapshot.child("isEnable").getValue(Boolean::class.java)
                    if (isEnable != null) {
                        Log.d("MainActivity", "isEnable value: $isEnable")
                        if (isEnable) {
                            // isEnable is true, proceed with the app
                            // UI is already set in onCreate, so no further action needed
                        } else {
                            // isEnable is false, close the app
                            Log.d("MainActivity", "isEnable is false, closing app")
                            finish() // Closes the activity and exits the app
                        }
                    } else {
                        Log.w("MainActivity", "isEnable value is null")
                        // Optional: Handle null case (e.g., close app or show error)
                        finish()
                    }
                } else {
                    Log.w("MainActivity", "No data found at the specified reference")
                    // Optional: Handle missing data (e.g., close app or show error)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to read data: ${error.message}", error.toException())
                // Optional: Handle error (e.g., close app or show error UI)
                finish()
            }
        })
    }
//private fun getIsEnableValue() {
//    myRef.addValueEventListener(object : ValueEventListener {
//        override fun onDataChange(snapshot: DataSnapshot) {
//            // Check if the snapshot exists and contains the "isEnable" key
//            if (snapshot.exists()) {
//                val isEnable = snapshot.child("isEnable").getValue(Boolean::class.java)
//                if (isEnable != null) {
//                    Log.d("MainActivity", "isEnable value: $isEnable")
//                    // Use the isEnable value (e.g., update UI or logic)
//                } else {
//                    Log.w("MainActivity", "isEnable value is null")
//                }
//            } else {
//                Log.w("MainActivity", "No data found at the specified reference")
//            }
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            Log.e("MainActivity", "Failed to read data: ${error.message}", error.toException())
//        }
//    })
//}
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel = AuthViewModel(context)

    // Determine start destination based on login status
    val startDestination = if (authViewModel.isUserLoggedIn()) "dashboard" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable("add_user") {
            AddUserScreen(navController = navController)
        }
        composable("employee_list") { EmployeePage(navController = navController) }
//        composable("face_photo/{imageIndex}") { backStackEntry ->
//            val imageIndex = backStackEntry.arguments?.getString("imageIndex")?.toIntOrNull() ?: 1
//            FaceDetectionPage(navController, imageIndex)
//        }
        composable("face_photo/{imageIndex}") { backStackEntry ->
            val imageIndex = backStackEntry.arguments?.getString("imageIndex")?.toIntOrNull() ?: 1
            FaceDetectionPage(navController = navController, imageIndex = imageIndex)
        }
//        composable("face_photo") {
//            FaceDetectionPage(
//                navController = navController,
//                imageIndex = 0
//            )
//        }
        composable("employee") { EmployeeScreen() }
        composable("report") { ReportScreen() }
        composable("attendance") { DetectScreen { navController.navigate("dashboard") } }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("dashboard") {
            DashboardScreen(
                onNavigateBack = { navController.navigateUp() },
                navController = navController
            )
        }
        composable("detect") {
            DetectScreen { navController.navigate("home") }
        }
        composable("face-list") {
            FaceListScreen(
                onNavigateBack = { navController.navigateUp() },
                onAddFaceClick = { navController.navigate("add-face") }
            )
        }
    }
}

@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRemember by remember { mutableStateOf(false) }
    val isLoading by authViewModel.isLoading
    val errorMessage by authViewModel.errorMessage
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                colors = CardColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        placeholder = { Text("abc@mail.com") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("********") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                scope.launch {
                                    authViewModel.login(
                                        email,
                                        password,
                                        isRemember,
                                        context,
                                        navController
                                    )
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),

                        )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isRemember,
                            onCheckedChange = { isRemember = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor =  Color(ContextCompat.getColor(context, R.color.primary)),
                                uncheckedColor = Color.Gray,
                                checkmarkColor = Color.White
                            )
                        )
//                        Checkbox(
//                            checked = isRemember,
//                            onCheckedChange = { isRemember = it },
//
//                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                authViewModel.login(
                                    email,
                                    password,
                                    isRemember,
                                    context,
                                    navController
                                )
                            }
                        },

                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(
                                ContextCompat.getColor(
                                    context,
                                    R.color.primary
                                )
                            ) // Primary blue
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("LOGIN")
                        }
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

class AuthViewModel(private val context: Context) : ViewModel() {
    private val authApi = AuthApi.getInstance(context)
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun login(
        email: String,
        password: String,
        isRemember: Boolean,
        context: Context,
        navController: NavHostController
    ) {
        if (email.isEmpty()) {
            _errorMessage.value = "Please enter email"
            showMessage(context, "Please enter email")
            return
        }

        if (password.isEmpty()) {
            _errorMessage.value = "Please enter password"
            showMessage(context, "Please enter password")
            return
        }

        _isLoading.value = true
        try {
            authApi.withRetry {
                authApi.bgLogin(email, password)
            }.let { bgResponse ->
                Log.d("AuthViewModel", "bgLogin response: $bgResponse")
                if (bgResponse != null) {
                    if (bgResponse.isSuccessful == true) {
                        with(sharedPreferences.edit()) {
                            Log.d(
                                "AuthViewModel",
                                "bgLogin response: ${bgResponse.body()?.data?.company_id}"
                            )
                            bgResponse.body()?.data?.company_id?.let { putString("company_id", it) }
                            apply()
                        }
                        authApi.withRetry {
                            authApi.login()
                        }.let { response ->
                            Log.d("AuthViewModel", "Login response: $response")
                            if (response != null) {
                                storeUserData(response, isRemember)
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }


                            }
                            _errorMessage.value = null

                        }
                    } else {
                        _errorMessage.value = "Background login failed"
                        showMessage(context, "Background login failed")
                    }
                } else {
                    _errorMessage.value = "Background login failed"
                    showMessage(context, "Background login failed")
                }
            }
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Error"
            showMessage(context, e.message ?: "Error")
            Log.e("AuthViewModel", "Login error: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    private fun storeUserData(response: UserResponse, isRemember: Boolean) {
        with(sharedPreferences.edit()) {
            response.data?.accessToken?.let { putString("token", it) }
            Log.d("AuthViewModel", "Token: ${response.data?.accessToken}")
            putString("user_model", gson.toJson(response))
            putBoolean("is_user_login", true)
            if (isRemember) {
                putString("email", response.data?.email)
                putBoolean("remember_me", true)
            } else {
                remove("email")
                putBoolean("remember_me", false)
            }
            apply()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        )
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_user_login", false)
    }
}