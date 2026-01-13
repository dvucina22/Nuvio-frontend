package com.example.nuviofrontend.feature.profile.presentation

import androidx.compose.material.icons.filled.People
import com.example.auth.presentation.AuthViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import androidx.compose.ui.res.stringResource
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.components.ProfileHeader
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.IconDark
import com.example.core.ui.theme.White

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSignOut: () -> Unit = {},
    onEdit: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onNavigateToSavedCards: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val profileState by viewModel.profileState.collectAsState()
    val isLoggedIn = profileState.isLoaded && profileState.email.isNotBlank()

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val isAdmin = authState.isAdmin

    val displayName = if (isLoggedIn) {
        listOfNotNull(profileState.firstName, profileState.lastName).joinToString(" ")
    } else stringResource(R.string.guest)

    val displayEmail = if (isLoggedIn) profileState.email else stringResource(R.string.not_logged_in)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 26.dp,
                        bottom = 13.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.profile_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                ProfileHeader(
                    displayName = displayName,
                    displayEmail = displayEmail,
                    profilePictureUrl = profileState.profilePictureUrl ?: ""
                )

                if (isLoggedIn) {
                    CustomButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.edit_button),
                        onClick = onEdit
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = BackgroundNavDark)

                if (isLoggedIn) {
                    ProfileMenuItem(Icons.Default.Settings, stringResource(R.string.settings)){
                        onNavigateToSettings()
                    }
                    ProfileMenuItem(
                        Icons.Default.CreditCard,
                        stringResource(R.string.saved_cards)
                    ) {
                        onNavigateToSavedCards()
                    }
                    ProfileMenuItem(Icons.Default.List, stringResource(R.string.order_history))
                    ProfileMenuItem(Icons.Default.Lock, stringResource(R.string.change_password)) {
                        onChangePassword()
                    }

                    if (isAdmin) {
                        ProfileMenuItem(Icons.Default.People, stringResource(R.string.users)) {
                            onNavigateToUsers()
                        }
                    }

                    Divider(color = BackgroundNavDark)
                }

                ProfileMenuItem(Icons.Default.Help, stringResource(R.string.help)){
                    onNavigateToSupport()
                }

                if (isLoggedIn) {
                    ProfileMenuItem(Icons.Default.ExitToApp, stringResource(R.string.sign_out)) {
                        onSignOut()
                    }
                } else {
                    ProfileMenuItem(
                        Icons.Default.ExitToApp,
                        stringResource(R.string.login_button)
                    ) {
                        onNavigateToLogin()
                    }
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
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(22.dp)
        )
    }
}