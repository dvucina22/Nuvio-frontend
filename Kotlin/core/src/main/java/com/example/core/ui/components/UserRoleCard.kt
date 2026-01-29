package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.auth.dto.Role
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White

@Composable
fun UserRoleCard(
    name: String,
    email: String,
    role: Role?,
    allRoles: List<Role>,
    onRoleSelected: (Role) -> Unit,
    onDeactivate: () -> Unit,
    isActive: Boolean,
    profilePictureUrl: String?,
    modifier: Modifier = Modifier
) {
    val avatarSize = 54.dp
    val avatarSpacer = 12.dp
    val dropdownStartPadding = avatarSize + avatarSpacer

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isActive) 1f else 0.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceDim,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!profilePictureUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = profilePictureUrl,
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(avatarSize)
                                .clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "U",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.size(avatarSpacer))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = email,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }

                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(1.dp, AccentColor, shape = CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceDim)
                            .clickable { onDeactivate() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = Error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            CustomDropdown(
                value = role,
                items = allRoles,
                itemLabel = { r -> roleDisplayName(r.name) },
                placeholder = stringResource(R.string.role_not_set),
                onItemSelected = onRoleSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dropdownStartPadding),
                textStyle = MaterialTheme.typography.labelSmall,
                getItemColor = { r -> roleColor(r.name) },
                enabled = isActive
            )
        }
    }
}

@Composable
private fun roleDisplayName(raw: String?): String {
    return when (raw) {
        "admin" -> stringResource(R.string.role_admin)
        "seller" -> stringResource(R.string.role_seller)
        "buyer" -> stringResource(R.string.role_buyer)
        else -> stringResource(R.string.role_not_set)
    }
}

private fun roleColor(raw: String?): Color {
    return when (raw) {
        "admin" -> Color(0xFFB3261E)
        "seller" -> Color(0xFF2E7D32)
        "buyer" -> Color(0xFF1565C0)
        else -> Color(0xFF5A5A5A)
    }
}
