package com.example.nuviofrontend.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.components.SearchField
import com.example.core.ui.components.UserRoleCard
import com.example.core.ui.theme.White

@Composable
fun UsersScreen(
    onBack: () -> Unit,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var confirmDeactivateId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        CustomTopBar(
            title = stringResource(R.string.users_title),
            showBack = true,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchField(
            modifier = Modifier.fillMaxWidth(),
            value = state.query,
            onValueChange = { viewModel.onQueryChange(it) },
            placeholder = stringResource(R.string.users_search_placeholder),
            label = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading && state.visibleUsers.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.visibleUsers.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.users_empty),
                        color = White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(state.visibleUsers, key = { _, user -> user.id }) { index, user ->
                        if (index == state.visibleUsers.lastIndex) {
                            LaunchedEffect(key1 = index) {
                                viewModel.loadMore()
                            }
                        }

                        UserRoleCard(
                            name = listOfNotNull(user.firstName, user.lastName)
                                .joinToString(" ")
                                .ifBlank { user.email },
                            email = user.email,
                            role = user.roles?.firstOrNull(),
                            allRoles = state.roles,
                            onRoleSelected = { role ->
                                viewModel.onRoleSelected(user.id, role)
                            },
                            onDeactivate = {
                                confirmDeactivateId = user.id
                            },
                            isActive = user.isActive,
                            profilePictureUrl = user.profilePictureUrl
                        )
                    }

                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }

        if (state.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text(text = stringResource(R.string.users_snackbar_dismiss))
                    }
                }
            ) {
                Text(text = state.error ?: "")
            }
        }

        if (confirmDeactivateId != null) {
            AlertDialog(
                onDismissRequest = { confirmDeactivateId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmDeactivateId?.let { viewModel.deactivateUser(it) }
                            confirmDeactivateId = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB3261E),
                            contentColor = White
                        )
                    ) {
                        Text(text = stringResource(R.string.users_deactivate_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDeactivateId = null }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                title = { Text(text = stringResource(R.string.users_deactivate_title)) },
                text = { Text(text = stringResource(R.string.users_deactivate_message)) }
            )
        }
    }
}
