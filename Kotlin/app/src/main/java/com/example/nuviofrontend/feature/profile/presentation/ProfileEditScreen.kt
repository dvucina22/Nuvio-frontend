package com.example.nuviofrontend.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.R
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.White
import com.example.nuviofrontend.core.ui.components.CustomButton
import com.example.nuviofrontend.core.ui.components.CustomTextField

@Composable
fun ProfileEditScreen(
    firstName: String = "",
    lastName: String = "",
    email: String = "",
    phoneNumber: String = "",
    hasProfilePicture: Boolean = false,
    isLoading: Boolean = false,
    firstNameError: String? = null,
    lastNameError: String? = null,
    emailError: String? = null,
    phoneNumberError: String? = null,
    onBack: () -> Unit = {},
    onSave: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onProfilePictureClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var firstNameState by remember { mutableStateOf(firstName) }
    var lastNameState by remember { mutableStateOf(lastName) }
    var emailState by remember { mutableStateOf(email) }
    var phoneNumberState by remember { mutableStateOf(phoneNumber) }

    LaunchedEffect(firstName, lastName, email, phoneNumber) {
        firstNameState = firstName
        lastNameState = lastName
        emailState = email
        phoneNumberState = phoneNumber
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            CustomTopBar(
                title = stringResource(R.string.edit_profile_title),
                showBack = true,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_light_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (hasProfilePicture) Color.Transparent else Color(0xFF5A676A),
                                shape = CircleShape
                            )
                            .clickable { onProfilePictureClick() },
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF2D3E4A))
                            .border(2.dp, Color(0xFF1A2634), CircleShape)
                            .clickable { onProfilePictureClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hasProfilePicture) Icons.Default.Edit else Icons.Default.Add,
                            contentDescription = if (hasProfilePicture) "Edit profile picture" else "Add profile picture",
                            tint = White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Spacer(modifier = Modifier.height(24.dp)) // dodatni spacing prije polja

                CustomTextField(
                    value = firstNameState,
                    onValueChange = { firstNameState = it },
                    label = stringResource(R.string.label_first_name),
                    placeholder = stringResource(R.string.placeholder_first_name),
                    textStyle = MaterialTheme.typography.labelSmall,
                    isError = firstNameError != null,
                    errorMessage = firstNameError
                )

                CustomTextField(
                    value = lastNameState,
                    onValueChange = { lastNameState = it },
                    label = stringResource(R.string.label_last_name),
                    placeholder = stringResource(R.string.placeholder_last_name),
                    textStyle = MaterialTheme.typography.labelSmall,
                    isError = lastNameError != null,
                    errorMessage = lastNameError
                )

                CustomTextField(
                    value = emailState,
                    onValueChange = { emailState = it },
                    label = stringResource(R.string.label_email),
                    placeholder = stringResource(R.string.placeholder_email),
                    textStyle = MaterialTheme.typography.labelSmall,
                    isError = emailError != null,
                    errorMessage = emailError
                )

                CustomTextField(
                    value = phoneNumberState,
                    onValueChange = { phoneNumberState = it },
                    label = stringResource(R.string.label_phone),
                    placeholder = stringResource(R.string.placeholder_phone),
                    textStyle = MaterialTheme.typography.labelSmall,
                    isError = phoneNumberError != null,
                    errorMessage = phoneNumberError
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = White
                    )
                }

                CustomButton(
                    text = stringResource(R.string.save_button),
                    onClick = {
                        onSave(
                            firstNameState,
                            lastNameState,
                            emailState,
                            phoneNumberState
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
