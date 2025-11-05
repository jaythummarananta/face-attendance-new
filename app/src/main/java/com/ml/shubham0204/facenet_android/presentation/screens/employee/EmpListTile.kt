//package com.ananta.faceapp.presentation.screens.employee
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import com.ananta.faceapp.ApiRepo.RetrofitClient
//import com.ananta.faceapp.data.employeeModel.Item
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import coil.compose.AsyncImagePainter
//import coil.compose.SubcomposeAsyncImage
//import coil.compose.SubcomposeAsyncImageContent
//import com.ananta.faceapp.R
//
//@Composable
//fun EmpListTile(item: Item) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp, vertical = 5.dp)
//            .shadow(
//                elevation = 10.dp,
//                shape = RoundedCornerShape(8.dp),
//
//            ),
//        shape = RoundedCornerShape(8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(
//            modifier = Modifier.padding(8.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Start
//            ) {
//                item.faces.forEach { face ->
//                    AsyncImage(
//                        model = "${RetrofitClient.BASE_URL1}${face.faceImage}",
//                        contentDescription = "Face Image",
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .size(60.dp)
//                            .clip(CircleShape)
//                            .border(1.dp, Color.Black, CircleShape),
//                        contentScale = ContentScale.Crop,
//                        placeholder = painterResource(id = R.drawable.placeholder), // 📌 placeholder from res
//                        error = painterResource(id = R.drawable.placeholder)       // 📌 fallback on error
//                    )
//                }
//            }
//            ReportTileWidget(
//                title = "Name",
//                value = "${item.firstName} ${item.lastName}"
//            )
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Email", value = item.email)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Phone", value = item.mobile)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "BirthDate", value = item.dob)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Blood group", value = item.bloodGroup)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Department", value = item.department)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Role", value = item.designation)
//            Spacer(modifier = Modifier.height(15.dp))
//        }
//    }
//}
//
//@Composable
//fun ReportTileWidget(title: String, value: String) {
//    Row(
//        modifier = Modifier.padding(vertical = 2.5.dp)
//    ) {
//        Text(
//            text = "$title: ",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black,
//            modifier = Modifier.weight(1f)
//        )
//        Text(
//            text = value,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Medium,
//            color = Color.Black
//        )
//    }
//}
package com.ananta.faceapp.presentation.screens.employee

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ananta.faceapp.ApiRepo.RetrofitClient
import com.ananta.faceapp.R
import com.ananta.faceapp.data.employeeModel.Item
import com.ananta.faceapp.viewModel.AuthViewModel

@Composable
fun EmpListTile(item: Item, authViewModel: AuthViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Log.d("","empoyee size--${item}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(8.dp),
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start
                ) {
                    Log.d("","empoyee size--${RetrofitClient.BASE_URL1}${item.faces[0].faceImage}")
                    AsyncImage(
                        model = "${RetrofitClient.BASE_URL1}${item.faces[0].faceImage}",
                        contentDescription = "Face Image",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Black, CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder),
                        error = painterResource(id = R.drawable.placeholder)
                    )

//                    item.faces.forEach { face ->
//                        AsyncImage(
//                            model = "${RetrofitClient.BASE_URL1}${face.faceImage}",
//                            contentDescription = "Face Image",
//                            modifier = Modifier
//                                .padding(8.dp)
//                                .size(60.dp)
//                                .clip(CircleShape)
//                                .border(1.dp, Color.Black, CircleShape),
//                            contentScale = ContentScale.Crop,
//                            placeholder = painterResource(id = R.drawable.placeholder),
//                            error = painterResource(id = R.drawable.placeholder)
//                        )
//                    }
                }
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Employee",
                        tint = Color.Red
                    )
                }
            }
            ReportTileWidget(
                title = "Name",
                value = "${item.firstName} ${item.lastName}"
            )
            ReportTileWidget(
                title = "Bio ID",
                value = item.bioId?:""
            )
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Email", value = item.email)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Phone", value = item.mobile)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "BirthDate", value = item.dob)
//            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Blood group", value = item.bloodGroup)
            ReportTileWidget(title = "Department", value = item.department)
            Spacer(modifier = Modifier.height(5.dp))
//            ReportTileWidget(title = "Role", value = item.designation)
//            Spacer(modifier = Modifier.height(15.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Employee") },
            text = { Text("Are you sure you want to delete ${item.firstName} ${item.lastName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.deleteUser(item.publicId) // Assuming item.id exists
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White // Match FaceNetAndroidTheme
        )
    }
}

@Composable
fun ReportTileWidget(title: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.5.dp)
    ) {
        Text(
            text = "$title: ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}