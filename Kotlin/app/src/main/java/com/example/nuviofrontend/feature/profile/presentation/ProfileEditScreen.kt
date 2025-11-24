package com.example.nuviofrontend.feature.profile.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
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
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.ui.components.CustomDropdown
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.White
import com.example.nuviofrontend.core.ui.components.CustomButton
import com.example.nuviofrontend.core.ui.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    firstName: String = "",
    lastName: String = "",
    phoneNumber: String = "",
    gender: String = "",
    profilePictureUrl: String = "",
    hasProfilePicture: Boolean = false,
    isLoading: Boolean = false,
    isUploadingImage: Boolean = false,
    firstNameError: String? = null,
    lastNameError: String? = null,
    phoneNumberError: String? = null,
    genderError: String? = null,
    onBack: () -> Unit = {},
    onSave: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onProfilePictureSelected: (Uri) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var firstNameState by remember { mutableStateOf(firstName) }
    var lastNameState by remember { mutableStateOf(lastName) }
    var phoneNumberState by remember { mutableStateOf(phoneNumber) }
    var genderState by remember { mutableStateOf(gender) }
    var expandedGender by remember { mutableStateOf(false) }

    LaunchedEffect(firstName, lastName, phoneNumber, gender) {
        firstNameState = firstName
        lastNameState = lastName
        phoneNumberState = phoneNumber
        genderState = gender
    }

    val genderOptions = listOf(
        "male" to stringResource(R.string.gender_male),
        "female" to stringResource(R.string.gender_female),
        "other" to stringResource(R.string.gender_other)
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { onProfilePictureSelected(it) }
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
                    AsyncImage(
                        model = profilePictureUrl.ifEmpty { R.drawable.logo_light_icon },
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (hasProfilePicture) Color.Transparent else Color(
                                    0xFF5A676A
                                ),
                                shape = CircleShape
                            )
                            .clickable {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.logo_light_icon),
                        error = painterResource(id = R.drawable.logo_light_icon)
                    )

                    if (isUploadingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(90.dp),
                            color = White,
                            strokeWidth = 3.dp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF2D3E4A))
                            .border(2.dp, Color(0xFF1A2634), CircleShape)
                            .clickable {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hasProfilePicture) Icons.Default.Edit else Icons.Default.Add,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Divider(color = BackgroundNavDark, modifier = Modifier.padding(vertical = 16.dp))

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
                    value = phoneNumberState,
                    onValueChange = { phoneNumberState = it },
                    label = stringResource(R.string.label_phone),
                    placeholder = stringResource(R.string.placeholder_phone),
                    textStyle = MaterialTheme.typography.labelSmall,
                    isError = phoneNumberError != null,
                    errorMessage = phoneNumberError
                )

                CustomDropdown(
                    label = stringResource(R.string.label_gender),
                    value = genderState,
                    items = listOf("male", "female", "other"),
                    itemLabel = { code ->
                        when (code) {
                            "male" -> stringResource(R.string.gender_male)
                            "female" -> stringResource(R.string.gender_female)
                            else -> stringResource(R.string.gender_other)
                        }
                    },
                    placeholder = stringResource(R.string.placeholder_gender),
                    onItemSelected = { genderState = it },
                    isError = genderError != null,
                    errorMessage = genderError
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = White
                    )
                }

                Divider(color = BackgroundNavDark, modifier = Modifier.padding(vertical = 16.dp))

                CustomButton(
                    text = stringResource(R.string.save_button),
                    onClick = {
                        onSave(
                            firstNameState,
                            lastNameState,
                            phoneNumberState,
                            genderState
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}