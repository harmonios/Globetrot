package ca.uwaterloo.team_102_7.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.team_102_7.data.domain.*
import ca.uwaterloo.team_102_7.data.repository.ItineraryRepository
import ca.uwaterloo.team_102_7.data.repository.LocalStorageRepository
import ca.uwaterloo.team_102_7.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val itineraryRepository: ItineraryRepository,
    private val localStorageRepository: LocalStorageRepository
): ViewModel() {
    private val _savedUserId = MutableStateFlow<Int?>(null)
    val savedUserId: StateFlow<Int?> get() = _savedUserId

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _checkBoxItems = MutableStateFlow<List<String>>(emptyList())
    val checkBoxItems: StateFlow<List<String>> get() = _checkBoxItems

    fun getUser() {
        viewModelScope.launch {
            localStorageRepository.getUserIDFlow().collect { userID ->
                _savedUserId.value = userID
                _user.value = userRepository.getUser(userID)
            }
        }
    }

    // Retrieve the checkbox items from the repository and update the state flow
    fun retrieveCheckboxItems(userId: Int?) {
        viewModelScope.launch {
            localStorageRepository.getCheckboxItemsFlow(userId).collect { items ->
                _checkBoxItems.value = items
            }
        }
    }

    // Save checkbox items to DataStore via the repository
    fun saveCheckboxItems(userId: Int?, items: List<String>) {
        viewModelScope.launch {
            localStorageRepository.saveCheckboxItems(userId, items)
        }
    }
}