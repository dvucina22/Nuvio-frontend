package com.example.nuviofrontend.feature.profile.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.R
import com.example.core.auth.dto.Role
import com.example.core.user.dto.UserListItemDto
import com.example.nuviofrontend.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UsersState(
    val query: String = "",
    val users: List<UserListItemDto> = emptyList(),
    val visibleUsers: List<UserListItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val roles: List<Role> = emptyList()
)

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val app: Application
) : ViewModel() {

    private val _state = MutableStateFlow(UsersState())
    val state: StateFlow<UsersState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val pageSize = 10
    private var currentOffset = 0

    init {
        viewModelScope.launch {
            loadRoles()
            searchUsers("")
        }

        viewModelScope.launch {
            queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .collectLatest { query ->
                    currentOffset = 0
                    searchUsers(query)
                }
        }
    }

    private suspend fun loadRoles() {
        try {
            val roles = userRepository.getAllRoles()
            _state.update { it.copy(roles = roles) }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    error = app.getString(R.string.users_error_roles)
                )
            }
        }
    }

    private suspend fun searchUsers(query: String) {
        _state.update { it.copy(isLoading = true, error = null, query = query) }
        try {
            val all = userRepository.filterUsersByName(query.trim())
            currentOffset = pageSize.coerceAtMost(all.size)
            val firstPage = all.take(pageSize)
            _state.update {
                it.copy(
                    users = all,
                    visibleUsers = firstPage,
                    isLoading = false,
                    endReached = all.size <= pageSize
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = app.getString(R.string.users_error_fetch),
                    users = emptyList(),
                    visibleUsers = emptyList(),
                    endReached = true
                )
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || current.endReached) return

        val all = current.users
        if (currentOffset >= all.size) {
            _state.update { it.copy(endReached = true, isLoadingMore = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }

            val nextOffset = (currentOffset + pageSize).coerceAtMost(all.size)
            val nextChunk = all.subList(currentOffset, nextOffset)
            currentOffset = nextOffset

            _state.update {
                it.copy(
                    visibleUsers = it.visibleUsers + nextChunk,
                    isLoadingMore = false,
                    endReached = currentOffset >= all.size
                )
            }
        }
    }

    fun onRoleSelected(userId: String, role: Role) {
        viewModelScope.launch {
            val previousState = _state.value
            try {
                userRepository.updateUserRole(userId, role)
                val updatedUsers = previousState.users.map { user ->
                    if (user.id == userId) user.copy(roles = listOf(role)) else user
                }
                val updatedVisible = previousState.visibleUsers.map { user ->
                    if (user.id == userId) user.copy(roles = listOf(role)) else user
                }
                _state.update {
                    it.copy(
                        users = updatedUsers,
                        visibleUsers = updatedVisible
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = app.getString(R.string.users_error_change_role)
                    )
                }
            }
        }
    }

    fun deactivateUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.deactivateUser(userId)
                _state.update { state ->
                    state.copy(
                        users = state.users.filterNot { it.id == userId },
                        visibleUsers = state.visibleUsers.filterNot { it.id == userId }
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = app.getString(R.string.users_error_deactivate)
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
