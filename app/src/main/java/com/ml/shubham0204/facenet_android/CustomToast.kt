//package com.ananta.globalwallet.ui.composables
//
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlinx.coroutines.delay
//
//@Composable
//fun CustomToast(
//    message: String,
//    isVisible: Boolean,
//    onDismiss: () -> Unit,
//    duration: Long = 2000L
//) {
//    if (isVisible) {
//        // Animation for fade-in and slide-in
//        val alpha by animateFloatAsState(
//            targetValue = if (isVisible) 1f else 0f,
//            animationSpec = tween(durationMillis = 300)
//        )
//        val offsetY by animateFloatAsState(
//            targetValue = if (isVisible) 0f else 50f,
//            animationSpec = tween(durationMillis = 300)
//        )
//
//        // Auto-dismiss after duration
//        LaunchedEffect(isVisible) {
//            if (isVisible) {
//                delay(duration)
//                onDismiss()
//            }
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .offset(y = offsetY.dp)
//                .alpha(alpha)
//                .background(
//                    color = Color(0xFFD32F2F), // Red for warning
//                    shape = RoundedCornerShape(12.dp)
//                )
//                .border(1.dp, Color(0xFFB71C1C), RoundedCornerShape(12.dp))
//                .padding(12.dp),
//            contentAlignment = Alignment.TopStart
//        ) {
//            Row(
//
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start,
//                modifier = Modifier
//                    .background(color = Color.Red, shape = RoundedCornerShape(16.dp))
//                    .padding(horizontal = 12.dp, vertical = 8.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Warning,
//                    contentDescription = "Warning Icon",
//                    tint = Color.White,
//                    modifier = Modifier
//                        .size(24.dp)
//                        .background(color = Color.Red, shape = CircleShape)
//                        .padding(4.dp)
//                )
//                Text(
//                    text = message,
//                    color = Color.White,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//    }
//}
package com.ananta.globalwallet.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CustomToast(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    duration: Long = 10000L
) {
    if (isVisible) {
        // Animation for fade-in and slide-in
        val alpha by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0f,
            animationSpec = tween(durationMillis = 300)
        )
        val offsetY by animateFloatAsState(
            targetValue = if (isVisible) 0f else 50f,
            animationSpec = tween(durationMillis = 300)
        )

        // Auto-dismiss after duration
        LaunchedEffect(isVisible) {
            if (isVisible) {
                delay(duration)
                onDismiss()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
                .offset(y = offsetY.dp)
                .alpha(alpha),
            contentAlignment = Alignment.Center // Center the toast content horizontally
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 300.dp) // Maintain maximum width
                    .background(
                        color = Color(0x6FD10202), // Solid blue for info
                        shape = RoundedCornerShape(12.dp)
                    )

            ) {
                // Side line (vertical bar on the left)
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFBD0202)) // Solid darker blue
                )
                // Main content of the toast
                Row(
                    modifier = Modifier
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(0xFFFF0202),
                                shape = CircleShape
                            ) // Match icon background to border
                            .padding(4.dp)
                    )
                    Text(
                        text = message,
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}