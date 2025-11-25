package com.example.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.ButtonColorDark
import com.example.core.R
import com.example.core.ui.theme.WarningPopUpBackground

@Composable
fun CustomPopupWarning(
    title: String = "Upozorenje",
    message: String,
    confirmText: String = "Nastavi",
    dismissText: String = "Odustani",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            color = WarningPopUpBackground,
            modifier = Modifier
                .width(264.dp)
                .height(149.dp)
                .border(
                    width = 0.5.dp,
                    color = ButtonColorDark,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_error_hint),
                        contentDescription = title,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))
                Divider(color = BackgroundNavDark)
                Spacer(modifier = Modifier.height(7.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))
                Divider(color = BackgroundNavDark)
                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallCustomButton(
                        text = dismissText,
                        onClick = onDismiss,
                        containerColor = ButtonColorDark
                    )
                    SmallCustomButton(
                        text = confirmText,
                        onClick = onConfirm,
                        containerColor = ButtonColorDark
                    )
                }
            }
        }
    }
}
