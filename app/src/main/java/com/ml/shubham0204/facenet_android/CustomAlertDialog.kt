import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ml.shubham0204.facenet_android.ApiRepo.UserFaceAuthModel
import com.ml.shubham0204.facenet_android.LoginScreen
import com.ml.shubham0204.facenet_android.R


@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    userDetails: String? = null,
    positiveButtonText: String,
    imageResId: Int,
    onPositiveButtonClick: () -> Unit,
    onDismissRequest: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                userDetails?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onPositiveButtonClick()
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(positiveButtonText)
                }
            }
        }
    }
}

@Composable
fun ShowCustomAlertDialog(
    response: UserFaceAuthModel,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit
) {
    val showDialog = remember { mutableStateOf(true) }

    Log.d("TAG", "ShowCustomAlertDialog: ${response}")
    if (showDialog.value) {
        val userDetails = if (response.data?.userDetails != null) {
            val details = response.data.userDetails
            buildString {
                append(details.firstName ?: "Unknown")
                append(" ")
                append(details.lastName ?: "")
            }.trim()
        } else {
            null
        }

        val isSuccess = response.success == true
        val checkInOut = if (response.data?.isCheckIn == true) "checked in" else "checked out"

        val title = when {
            isSuccess && userDetails != null -> "Attendance Recorded"
            isSuccess && userDetails == null -> "Face Not Detected"
            else -> "Attendance Failed"
        }

        val message = when {
            isSuccess && userDetails != null -> "Attendance $checkInOut successfully."
            isSuccess && userDetails == null -> "Face was not properly detected. Please try again."
            else -> "Attendance not taken. Please try again."
        }

        CustomAlertDialog(
            title = title,
            message = message,
            userDetails = userDetails,
            positiveButtonText = "OK",
            imageResId = if (isSuccess && userDetails != null) R.drawable.check else R.drawable.correct,
            onPositiveButtonClick = {
                showDialog.value = false
                onConfirm()
            },
            onDismissRequest = {
                showDialog.value = false
                onDismissRequest()
            }
        )
    }
}

//@Composable
//fun ShowCustomAlertDialog(
//    response: UserFaceAuthModel,
//    onDismissRequest: () -> Unit = {},
//    onConfirm: () -> Unit
//) {
//    val showDialog = remember { mutableStateOf(true) }
//
//    if (showDialog.value) {
//        val userDetails = if (response.success == true && response.data?.userDetails != null) {
//            val details = response.data.userDetails
//            buildString {
//                append(details.firstName ?: "Unknown")
//                append(" ")
//                append(details.lastName ?: "")
//
//            }
//        } else {
//            null
//        }
//
//        CustomAlertDialog(
//            title = if (response.data?.userDetails != null) "Attendance Recorded" else "Attendance Failed",
//            message = if (response.success == true) {
//                val checkInOut =
//                    response.data?.userDetails != null? if (response.data?.isCheckIn == true) "checked in" else "checked out"
//                "Attendance $checkInOut successfully."
//            } else {
//                "Attendance not Taken."
//            },
//            userDetails = userDetails,
//            positiveButtonText = "OK",
//            imageResId = if (response.success == true) R.drawable.check else R.drawable.correct,
//            onPositiveButtonClick = {
//                showDialog.value = false
//                onConfirm()
//            },
//            onDismissRequest = {
//                showDialog.value = false
//                onDismissRequest()
//            }
//        )
//    }
//}
