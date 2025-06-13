package com.ml.shubham0204.facenet_android
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.ml.shubham0204.facenet_android.presentation.screens.add_face.AddFaceScreen
//import com.ml.shubham0204.facenet_android.presentation.screens.detect_screen.DetectScreen
//import com.ml.shubham0204.facenet_android.presentation.screens.face_list.FaceListScreen
//
//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            val navHostController = rememberNavController()
//            NavHost(
//                navController = navHostController,
//                startDestination = "detect",
//                enterTransition = { fadeIn() },
//                exitTransition = { fadeOut() }
//            ) {
//                composable("add-face") { AddFaceScreen { navHostController.navigateUp() } }
//                composable("detect") { DetectScreen { navHostController.navigate("face-list") } }
//                composable("face-list") {
//                    FaceListScreen(
//                        onNavigateBack = { navHostController.navigateUp() },
//                        onAddFaceClick = { navHostController.navigate("add-face") }
//                    )
//                }
//            }
//        }
//    }
//}


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.ml.shubham0204.facenet_android.presentation.screens.add_face.AddFaceScreen
import com.ml.shubham0204.facenet_android.presentation.screens.detect_screen.DetectScreen
import com.ml.shubham0204.facenet_android.presentation.screens.face_list.FaceListScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ml.shubham0204.facenet_android.ApiRepo.AuthApi
import com.ml.shubham0204.facenet_android.ApiRepo.UserResponse
import com.ml.shubham0204.facenet_android.ApiRepo.showMessage
import com.yourpackage.ui.HomeScreen
import kotlinx.serialization.encodeToString

//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            val navHostController = rememberNavController()
//            val authViewModel = AuthViewModel(LocalContext.current)
//            NavHost(
//                navController = navHostController,
//                startDestination = "detect",
//                enterTransition = { fadeIn() },
//                exitTransition = { fadeOut() }
//            ) {
//                composable("login") {
//                    LoginScreen(
//                        authViewModel = authViewModel,
//                        navController = navHostController
//                    )
//                }
//                composable("add-face") { AddFaceScreen { navHostController.navigateUp() } }
//                composable("detect") { DetectScreen { navHostController.navigate("face-list") } }
//                composable("face-list") {
//                    FaceListScreen(
//                        onNavigateBack = { navHostController.navigateUp() },
//                        onAddFaceClick = { navHostController.navigate("add-face") }
//                    )
//                }
//            }
//        }
//    }
//}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel = AuthViewModel(context)

    // Determine start destination based on login status
    val startDestination = if (authViewModel.isUserLoggedIn()) "home" else "login"

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
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("add-face") {
            AddFaceScreen { navController.navigateUp() }
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
                                    authViewModel.login(email, password, isRemember, context, navController)
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
                            onCheckedChange = { isRemember = it }
                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                authViewModel.login(email, password, isRemember, context, navController)
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
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

//class AuthViewModel(private val context: Context) {
//    private val authApi = AuthApi.getInstance(context)
//    private val _isLoading = mutableStateOf(false)
//    val isLoading: State<Boolean> = _isLoading
//    private val _errorMessage = mutableStateOf<String?>(null)
//    val errorMessage: State<String?> = _errorMessage
//    private val sharedPreferences: SharedPreferences =
//        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
//
//    suspend fun login(
//        email: String,
//        password: String,
//        isRemember: Boolean,
//        context: Context,
//        navController: NavHostController
//    ) {
//        if (email.isEmpty()) {
//            _errorMessage.value = "Please enter email"
//            showMessage(context, "Please enter email")
//            return
//        }
//        if (!isValidEmail(email)) {
//            _errorMessage.value = "Please enter valid email address"
//            showMessage(context, "Please enter valid email address")
//            return
//        }
//        if (password.isEmpty()) {
//            _errorMessage.value = "Please enter password"
//            showMessage(context, "Please enter password")
//            return
//        }
//
//        _isLoading.value = true
//        try {
//            authApi.withRetry {
//                authApi.bgLogin()
//            }.let { bgResponse ->
//                Log.d("AuthViewModel", "bgLogin response: $bgResponse")
//                if (bgResponse.data?.success == false) {
//                    authApi.withRetry {
//                        authApi.login(email, password)
//                    }.let { response ->
//                        Log.d("AuthViewModel", "Login response: $response")
//                        storeUserData(response, isRemember)
//                        _errorMessage.value = null
//                        navController.navigate("detect") {
//                            popUpTo("login") { inclusive = true }
//                        }
//                    }
//                } else {
//                    _errorMessage.value = "Background login failed"
//                    showMessage(context, "Background login failed")
//                }
//            }
//        } catch (e: Exception) {
////            _errorMessage.value = e.message ?: "Error"
//            showMessage(context, e.message ?: "Error")
//            Log.e("AuthViewModel", "Login error: ${e.message}")
//        } finally {
//            _isLoading.value = false
//        }
//    }
//
//    private fun storeUserData(response: UserResponse, isRemember: Boolean) {
//        with(sharedPreferences.edit()) {
//            response.data?.accessToken?.let { putString("token", it) }
//            putString("user_model", Json.encodeToString(response))
//            putBoolean("is_user_login", true)
//            if (isRemember) {
//                putString("email", response.data?.email)
//                putBoolean("remember_me", true)
//            } else {
//                remove("email")
//                putBoolean("remember_me", false)
//            }
//            apply()
//        }
//    }
//
//    private fun isValidEmail(email: String): Boolean {
//        return email.matches(
//            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
//        )
//    }
//}
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
        if (!isValidEmail(email)) {
            _errorMessage.value = "Please enter valid email address"
            showMessage(context, "Please enter valid email address")
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
                authApi.bgLogin()
            }.let { bgResponse ->
                Log.d("AuthViewModel", "bgLogin response: $bgResponse")
                if (bgResponse != null) {
                    if (bgResponse.data?.success == false) {
                        authApi.withRetry {
                            authApi.login(email, password)
                        }.let { response ->
                            Log.d("AuthViewModel", "Login response: $response")
                            if (response != null) {
                                storeUserData(response, isRemember)
                            }
                            _errorMessage.value = null
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    } else {
                        _errorMessage.value = "Background login failed"
                        showMessage(context, "Background login failed")
                    }
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