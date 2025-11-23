package com.example.nuviofrontend.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.R
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.White
import com.example.nuviofrontend.core.ui.components.CustomButton

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    firstName: String?,
    lastName: String? = null,
    email: String? = null,
    onSignOut: () -> Unit = {},
    onEdit: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onChangePassword: () -> Unit = {}
) {
    val displayName = if (isLoggedIn && !firstName.isNullOrBlank()) {
        if (!lastName.isNullOrBlank()) "$firstName $lastName" else firstName
    } else {
        stringResource(R.string.guest)
    }


    val displayEmail = if (isLoggedIn && !email.isNullOrBlank()) {
        email
    } else {
        stringResource(R.string.not_logged_in)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, end = 0.dp, start = 0.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_title),
                        color = White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.logo_dark_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(43.dp)
                            .padding(6.dp)
                            .align(Alignment.TopEnd),
                        contentScale = ContentScale.Fit
                    )
                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo_light_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = displayName,
                    color = White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = displayEmail,
                    color = Color(0xFF9AA4A6),
                    style = MaterialTheme.typography.labelSmall,
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoggedIn) {
                    CustomButton(
                        text = stringResource(R.string.edit_button),
                        onClick = onEdit
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Divider(color = BackgroundNavDark)

            if (isLoggedIn) {
                ProfileMenuItem(Icons.Default.Settings, stringResource(R.string.settings))
                ProfileMenuItem(Icons.Default.CreditCard, stringResource(R.string.saved_cards))
                ProfileMenuItem(Icons.Default.List, stringResource(R.string.order_history))
                ProfileMenuItem(Icons.Default.Lock, stringResource(R.string.change_password)){
                    onChangePassword()
                }
                Divider(color = BackgroundNavDark)
            }

            ProfileMenuItem(Icons.Default.Help, stringResource(R.string.help))

            if (isLoggedIn) {
                ProfileMenuItem(Icons.Default.ExitToApp, stringResource(R.string.sign_out)) {
                    onSignOut()
                }
            } else {
                ProfileMenuItem(Icons.Default.ExitToApp, stringResource(R.string.login_button)) {
                    onNavigateToLogin()
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF818E96),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = White,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF818E96),
            modifier = Modifier.size(22.dp)
        )
    }
}
