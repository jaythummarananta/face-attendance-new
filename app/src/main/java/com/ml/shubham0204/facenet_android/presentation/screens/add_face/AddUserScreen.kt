package com.ml.shubham0204.facenet_android.presentation.screens.add_user

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ml.shubham0204.facenet_android.ApiRepo.AuthApi
import com.ml.shubham0204.facenet_android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authApi = AuthApi.getInstance(context)
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var imagePath1 by remember { mutableStateOf("") }
    var imagePath2 by remember { mutableStateOf("") }
    var imagePath3 by remember { mutableStateOf("") }
    var imagePath4 by remember { mutableStateOf("") }
    var isImage1Set by remember { mutableStateOf(false) }
    var isImage2Set by remember { mutableStateOf(false) }
    var isImage3Set by remember { mutableStateOf(false) }
    var isImage4Set by remember { mutableStateOf(false) }
    val imagePaths = remember { mutableStateListOf<String?>("", "", "", "") }

    var showProgress by remember { mutableStateOf(false) }
    var isPermissionRequested by remember { mutableStateOf(false) }


    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf("Item1", "Item2", "Item3")
    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown


    // Permission launcher
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        isPermissionRequested = false
//        if (isGranted) {
//            navController.navigate("face_photo")
//        } else {
//            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }

    // Permission launcher (move this outside the LazyColumn item for better scope)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isPermissionRequested = false
        if (isGranted) {
            // Navigate based on the current enabled ImageWidget
            when {
                !isImage1Set -> navController.navigate("face_photo/imagePath1")
                isImage1Set && !isImage2Set -> navController.navigate("face_photo/imagePath2")
                isImage2Set && !isImage3Set -> navController.navigate("face_photo/imagePath3")
                isImage3Set && !isImage4Set -> navController.navigate("face_photo/imagePath4")
            }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    for (i in 1..4) {
        val imagePath = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<String?>("image_path_$i", null)
            ?.collectAsState()?.value

        imagePath?.let { path ->
            imagePaths[i - 1] = path // ✅ Replace only specific image
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "REGISTER USER",
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (showProgress) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(15.dp)
                ) {
                    item {
                        Text(
                            "First Name",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            TextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                placeholder = { Text("Enter your first name", color = Color.Gray) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Last Name",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            TextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                placeholder = { Text("Enter your last name", color = Color.Gray) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Email Address",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = { Text("Enter Email", color = Color.Gray) },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Email
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Phone",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            TextField(
                                value = phone,
                                onValueChange = { phone = it },
                                placeholder = { Text("Enter phone number", color = Color.Gray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Select Birthdate",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .clickable {
                                    showDatePicker(context) { date ->
                                        birthDate = date
                                    }
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = birthDate.ifEmpty { "Select Birthdate" },
                                color = if (birthDate.isEmpty()) Color.Gray else Color.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Select Blood Group",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownMenu(
                            items = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
                            selectedItem = bloodGroup,
                            onItemSelected = { bloodGroup = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "User Role",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownMenu(
                            items = listOf(
                                "Web Developer",
                                "Frontend Developer",
                                "Backend Developer",
                                "Full Stack Developer",
                                "Mobile App Developer",
                                "Software Developer",
                                "Game Developer",
                                "DevOps Engineer",
                                "Data Engineer",
                                "Cloud Developer",
                                "Embedded Systems Developer",
                                "AI/ML Developer",
                                "Blockchain Developer",
                                "Database Developer",
                                "UI/UX Developer"
                            ),
                            selectedItem = userRole,
                            onItemSelected = { userRole = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Department",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownMenu(
                            items = listOf(
                                "Software Development",
                                "Project Management",
                                "IT Administration",
                                "Quality Assurance",
                                "Business Analysis",
                                "DevOps",
                                "UI/UX Design",
                                "Database Management",
                                "Networking",
                                "Technical Support",
                                "Cybersecurity",
                                "Cloud Computing",
                                "Data Science",
                                "Artificial Intelligence",
                                "Machine Learning",
                                "Web Development",
                                "Mobile Application Development",
                                "Game Development",
                                "IT Infrastructure",
                                "ERP Systems Management",
                                "IT Consulting",
                                "IT Training and Education",
                                "Research and Development",
                                "Help Desk",
                                "Big Data Analytics",
                                "System Architecture",
                                "Virtualization",
                                "IT Procurement",
                                "IT Compliance and Auditing",
                                "IoT (Internet of Things)"
                            ),
                            selectedItem = department,
                            onItemSelected = { department = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Choose 4 Type Face Image",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ImageGridSection(
                            imagePaths = imagePaths,
                            navController = navController,
                            permissionLauncher = permissionLauncher,
                            isPermissionRequested = isPermissionRequested,
                            onPermissionRequested = { isPermissionRequested = true }
                        )
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            ImageWidget(
//                                imagePath = imagePath1,
//                                number = "1",
//                                onClick = {
//                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                                        isImage1Set = true
//
//                                        navController.navigate("face_photo/1")
//                                    } else if (!isPermissionRequested) {
//                                        isPermissionRequested = true
//                                        permissionLauncher.launch(Manifest.permission.CAMERA)
//                                    }
//                                }
//                            )
//                            ImageWidget(
//                                imagePath = imagePath2,
//                                number = "2",
//                                onClick = {
//                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                                        isImage2Set = true
//
//                                        navController.navigate("face_photo/2")
//
//                                    } else if (!isPermissionRequested) {
//                                        isPermissionRequested = true
//                                        permissionLauncher.launch(Manifest.permission.CAMERA)
//                                    }
//                                }
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(10.dp))
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            ImageWidget(
//                                imagePath = imagePath3,
//                                number = "3",
//                                onClick = {
//                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//
//                                        isImage3Set = true
//
//                                        navController.navigate("face_photo/3")
//
//                                    } else if (!isPermissionRequested) {
//                                        isPermissionRequested = true
//                                        permissionLauncher.launch(Manifest.permission.CAMERA)
//                                    }
//                                }
//                            )
//                            ImageWidget(
//                                imagePath = imagePath4,
//                                number = "4",
//                                onClick = {
//                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//
//                                        isImage4Set = true
//                                        navController.navigate("face_photo/4")
//
//                                    } else if (!isPermissionRequested) {
//                                        isPermissionRequested = true
//                                        permissionLauncher.launch(Manifest.permission.CAMERA)
//                                    }
//                                }
//                            )
//                        }
                    }
                }
                Button(
                    onClick = {
                        doRegister(
                            context = context,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phone = phone,
                            birthDate = birthDate,
                            bloodGroup = bloodGroup,
                            userRole = userRole,
                            department = department,
                            imagePaths = listOf(
                                imagePaths[0],
                                imagePaths[1],
                                imagePaths[2],
                                imagePaths[3]
                            ),
                            onStart = { showProgress = true },
                            onEnd = { showProgress = false },
                            onSuccess = { navController.popBackStack() },
                            authApi = authApi,
                            coroutineScope = coroutineScope
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text("REGISTER")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGridSection(
    imagePaths: SnapshotStateList<String?>,
    navController: NavController,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    isPermissionRequested: Boolean,
    onPermissionRequested: () -> Unit
) {
    val context = LocalContext.current

    LazyVerticalGrid(

        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
    ) {
        items(4) { index ->
            ImageWidget(
                imagePath = imagePaths[index].toString(),
                number = "${index + 1}",
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        navController.navigate("face_photo/${index + 1}")
                    } else if (!isPermissionRequested) {
                        onPermissionRequested()
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentDropdown(
    department: String,
    onDepartmentSelected: (String) -> Unit
) {
    val departments = listOf(
        "Software Development", "Project Management", "IT Administration",
        "Quality Assurance", "Business Analysis", "DevOps", "UI/UX Design",
        "Database Management", "Networking", "Technical Support",
        "Cybersecurity", "Cloud Computing", "Data Science",
        "Artificial Intelligence", "Machine Learning", "Web Development",
        "Mobile Application Development", "Game Development",
        "IT Infrastructure", "ERP Systems Management", "IT Consulting",
        "IT Training and Education", "Research and Development", "Help Desk",
        "Big Data Analytics", "System Architecture", "Virtualization",
        "IT Procurement", "IT Compliance and Auditing", "IoT (Internet of Things)"
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = department,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Department") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { expanded = true }, // 👈 makes the whole field clickable
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            departments.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onDepartmentSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ImageWidget(imagePath: String, number: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(180.dp, 175.dp)
            .padding(top = 8.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imagePath.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Camera",
                    modifier = Modifier.size(35.dp)
                )
                Text(text = number, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        } else {
            AsyncImage(
                model = imagePath,
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun DropdownMenu(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val date = Calendar.getInstance().apply {
                set(year, month, day)
            }.time
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            onDateSelected(formatter.format(date))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun doRegister(
    context: Context,
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    birthDate: String,
    bloodGroup: String,
    userRole: String,
    department: String,
    imagePaths: List<String?>,
    onStart: () -> Unit,
    onEnd: () -> Unit,
    onSuccess: () -> Unit,
    authApi: AuthApi,
    coroutineScope: CoroutineScope
) {
    if (firstName.isEmpty()) {
        Toast.makeText(context, "Please enter first name", Toast.LENGTH_SHORT).show()
        return
    }
    if (lastName.isEmpty()) {
        Toast.makeText(context, "Please enter last name", Toast.LENGTH_SHORT).show()
        return
    }
    if (email.isEmpty()) {
        Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
        return
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        Toast.makeText(context, "Please enter valid email address", Toast.LENGTH_SHORT).show()
        return
    }
    if (phone.isEmpty()) {
        Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show()
        return
    }
    if (birthDate.isEmpty()) {
        Toast.makeText(context, "Please select birthdate", Toast.LENGTH_SHORT).show()
        return
    }
    if (bloodGroup.isEmpty()) {
        Toast.makeText(context, "Please select blood group", Toast.LENGTH_SHORT).show()
        return
    }
    if (userRole.isEmpty()) {
        Toast.makeText(context, "Please select user role", Toast.LENGTH_SHORT).show()
        return
    }
//    if (imagePaths[0]?.isEmpty() == true) {
//        Toast.makeText(context, "Please select first user image", Toast.LENGTH_SHORT).show()
//        return
//    }
//    if (imagePaths[1]?.isEmpty() == true) {
//        Toast.makeText(context, "Please select second user image", Toast.LENGTH_SHORT).show()
//        return
//    }

    onStart()
    coroutineScope.launch {
        val success = authApi.addUser(
            firstName = firstName,
            lastName = lastName,
            email = email,
            mobile = phone,
            dob = birthDate,
            bloodGroup = bloodGroup,
            designation = userRole,
            department = department,
            imagePaths = imagePaths
        )
        onEnd()
        if (success) {
            Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }
}