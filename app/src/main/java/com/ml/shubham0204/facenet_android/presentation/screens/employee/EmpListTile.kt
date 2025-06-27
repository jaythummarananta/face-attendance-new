package com.ml.shubham0204.facenet_android.presentation.screens.employee

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ml.shubham0204.facenet_android.ApiRepo.RetrofitClient
import com.ml.shubham0204.facenet_android.data.employeeModel.Item

@Composable
fun EmpListTile(item: Item) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(8.dp),
//                ambientColor = ColorConstants.Love.copy(alpha = 0.2f),
//                spotColor = ColorConstants.Love.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                item.faces.forEach { face ->
                    AsyncImage(
                        model = "${RetrofitClient.BASE_URL1}${face.faceImage}",
                        contentDescription = "Face Image",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Black, CircleShape),
                        contentScale = ContentScale.Crop,
//                        placeholder = painterResource(),
//                        error = painterResource(R.drawable.placeholder)
                    )
                }
            }
            ReportTileWidget(
                title = "Name",
                value = "${item.firstName} ${item.lastName}"
            )
            Spacer(modifier = Modifier.height(5.dp))
            ReportTileWidget(title = "Email", value = item.email)
            Spacer(modifier = Modifier.height(5.dp))
            ReportTileWidget(title = "Phone", value = item.mobile)
            Spacer(modifier = Modifier.height(5.dp))
            ReportTileWidget(title = "BirthDate", value = item.dob)
            Spacer(modifier = Modifier.height(5.dp))
            ReportTileWidget(title = "Blood group", value = item.bloodGroup)
            Spacer(modifier = Modifier.height(5.dp))
            ReportTileWidget(title = "Department", value = item.department)
            Spacer(modifier = Modifier.height(5.dp))
            ReportTileWidget(title = "Role", value = item.designation)
            Spacer(modifier = Modifier.height(15.dp))
        }
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