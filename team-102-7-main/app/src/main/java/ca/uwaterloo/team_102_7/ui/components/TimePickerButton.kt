import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel
import java.util.*

@Composable
fun timePickerApp(
    context: Context,
    placeholderText: String,
    themeViewModel: SettingsViewModel,
    onTimeSelected: (String) -> Unit,
) {
    var timePickerDialogState by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(placeholderText) }

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    Button(
        onClick = { timePickerDialogState = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
            0xFF00487C
        } else {
            0xFF4F345A
        }))
    ) {
        Text(text = selectedTime)
    }

    if (timePickerDialogState) {
        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formattedTime = if (selectedHour > 12) {
                    String.format("%02d:%02d PM", selectedHour - 12, selectedMinute)
                } else {
                    String.format("%02d:%02d AM", selectedHour, selectedMinute)
                }
                selectedTime = formattedTime
                onTimeSelected(selectedTime)
                timePickerDialogState = false
            },
            hour,
            minute,
            false
        ).show()
    }
}