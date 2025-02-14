import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePickerButton(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    themeViewModel: SettingsViewModel,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Button(
        onClick = { showDatePicker = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
            0xFF00487C
        } else {
            0xFF4F345A
        }))
    ) {
        Text(text = if (selectedDate.isEmpty()) {
            if (themeViewModel.selectedLanguage.value == "English") {
                "Select $label"
            } else {
                "Sélectionnez $label"
            }
        }
        else {
            "$label: $selectedDate"
        }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateInMillis = datePickerState.selectedDateMillis
                        if (selectedDateInMillis != null) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            val date = Date(selectedDateInMillis)
                            val formattedDate = sdf.format(date)
                            onDateSelected(formattedDate)
                            showDatePicker = false
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text(if (themeViewModel.selectedLanguage.value == "English") {
                        "Cancel"
                    } else {
                        "Annuler"
                    }
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}