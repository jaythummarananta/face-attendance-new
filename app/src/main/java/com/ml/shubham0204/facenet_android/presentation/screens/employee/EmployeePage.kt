package com.ml.shubham0204.facenet_android.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ml.shubham0204.facenet_android.presentation.screens.employee.EmpListTile
import com.ml.shubham0204.facenet_android.viewModel.AuthViewModel
import com.ml.shubham0204.facenet_android.viewModel.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeePage(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
) {
    // Fetch data on screen load
    LaunchedEffect(Unit) {
        authViewModel.fetchAllUserData()
    }

    val users by authViewModel.users.observeAsState(emptyList())
    val isLoading = authViewModel.isLoading.observeAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EMPLOYEE",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Row
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 10.dp, vertical = 10.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                ReportTile(
//                    icon = Icons.Default.Group,
//                    value = "12", // TODO: Fetch from API
//                    text = "Total Employee"
//                )
//                ReportTile(
//                    icon = Icons.Default.Person,
//                    value = "10", // TODO: Fetch from API
//                    text = "Total Developer"
//                )
//            }
            // Centered Trainee Tile
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = (0.3f * 360.dp).coerceAtMost(360.dp * 0.3f)),
//                contentAlignment = Alignment.Center
//            ) {
//                ReportTile(
//                    icon = Icons.Default.Group,
//                    value = "02", // TODO: Fetch from API
//                    text = "Total Trainee"
//                )
//            }
            // Employee List
            if (isLoading == true) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    items(users) { user ->
                        EmpListTile(item = user)
                    }
                }
            }
        }
    }
}

