package ca.uwaterloo.team_102_7.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class PersonalChangesViewModel : ViewModel() {

    private val _selectedTheme = mutableStateOf("Classic")
    val selectedTheme: State<String> = _selectedTheme

    fun setTheme(theme: String) {
        _selectedTheme.value = theme
    }

    val ClassicTheme = arrayOf(
        0xFF00072D,
        0xFF001C55,
        0xFF0A2472,
        0xFF0E6BA8,
        0xFFA6E1FA
    )

    val colorCodes = arrayOf(
        0xFF4F345A,
        0xFF5D4E6D,
        0xFF8FA998,
        0xFF9CBFA7
    )

}