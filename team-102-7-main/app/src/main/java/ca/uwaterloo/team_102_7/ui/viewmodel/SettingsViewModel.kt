package ca.uwaterloo.team_102_7.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.team_102_7.data.domain.User
import ca.uwaterloo.team_102_7.data.repository.LocalStorageRepository
import ca.uwaterloo.team_102_7.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Currency
import javax.inject.Inject

class SettingsViewModel (private val userRepository: UserRepository,
                                            private val localStorageRepository: LocalStorageRepository,
) : ViewModel() {

    private val _savedUserId = MutableStateFlow<Int?>(null)
    val savedUserId: StateFlow<Int?> get() = _savedUserId

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    fun getUser() {
        viewModelScope.launch {
            try {
                localStorageRepository.getUserIDFlow().collect { userID ->
                    if (userID != null) {
                        _savedUserId.value = userID
                        val fetchedUser = userRepository.getUser(userID)
                        _user.value = fetchedUser

                        fetchedUser?.let {
                            _selectedfName.value = it.first
                            _selectedlName.value = it.last
                            _selectedemail.value = it.email
                            _selectedLanguage.value = it.language ?: "English"
                            _selectedTheme.value = it.theme ?: "Classic"
                            _selectedCurrency.value = it.currency ?: "CAD"
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private val _selectedLanguage = mutableStateOf("English")
    val selectedLanguage: State<String> = _selectedLanguage

    private val _selectedCurrency = mutableStateOf("CAD")
    val selectedCurrency: State<String> = _selectedCurrency

    private val _selectedfName = mutableStateOf("FNAME")
    val selectedFName: State<String> = _selectedfName

    private val _selectedlName = mutableStateOf("LNAME")
    val selectedlName: State<String> = _selectedlName

    private val _selectedemail = mutableStateOf("email.com")
    val selectedEmail: State<String> = _selectedemail

    private val _selectedSharing = mutableStateOf(true)
    val selectedSharing: State<Boolean> = _selectedSharing

    private val _selectedTheme = mutableStateOf("Classic")
    val selectedTheme: State<String> = _selectedTheme

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    fun setfName(fName: String) {
        _selectedfName.value = fName
    }

    fun setlName(lName: String) {
        _selectedlName.value = lName
    }

    fun setEmail(email: String) {
        _selectedemail.value = email
    }

    fun setSharing(shared: Boolean) {
        _selectedSharing.value = shared
    }

    fun setTheme(theme: String) {
        _selectedTheme.value = theme
    }

    fun updateFName(newFName: String) {
        val userId = _savedUserId.value
        if (userId != null) {
            viewModelScope.launch {
                try {
                    userRepository.updateFName(userId, newFName)
                    _selectedfName.value = newFName
                } catch (e: Exception) {
                }
            }
        }
    }

    fun updateLName(newLName: String) {
        val userId = _savedUserId.value
        if (userId != null) {
            viewModelScope.launch {
                try {
                    userRepository.updateLName(userId, newLName)
                    _selectedlName.value = newLName
                } catch (e: Exception) {
                }
            }
        }
    }

    fun updateTheme(newTheme: String) {
        val userId = _savedUserId.value
        if (userId != null) {
            viewModelScope.launch {
                try {
                    userRepository.updateTheme(userId, newTheme)
                    _selectedTheme.value = newTheme
                } catch (e: Exception) {
                }
            }
        }
    }

    fun updateLanguage(newLanguage: String) {
        val userId = _savedUserId.value
        if (userId != null) {
            viewModelScope.launch {
                try {
                    userRepository.updateLanguage(userId, newLanguage)
                    _selectedLanguage.value = newLanguage
                } catch (e: Exception) {
                }
            }
        }
    }

    fun updateCurrency(newCurrency: String) {
        val userId = _savedUserId.value
        if (userId != null) {
            viewModelScope.launch {
                try {
                    userRepository.updateCurrency(userId, newCurrency)
                    _selectedCurrency.value = newCurrency
                } catch (e: Exception) {
                }
            }
        }
    }

    fun convertCurrency(preCurrency: String, toCurrency: String, amount: Double): Double {
        if (preCurrency == "CAD" && toCurrency == "USD") {
            return amount * 0.72
        } else if (preCurrency == "USD" && toCurrency == "CAD") {
            return amount * 1.39
        } else {
            return amount
        }
    }
}