package ca.uwaterloo.team_102_7.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel

@Composable
fun plusButton(onClick: () -> Unit, themeViewModel: SettingsViewModel) {
    var theme = themeViewModel.selectedTheme.value
    Button(
        modifier = Modifier
            .size(50.dp)
            .background(Color(
                if (theme == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                }
            ), shape = CircleShape)
            .clip(CircleShape)
            .wrapContentSize(Alignment.Center),
        onClick = onClick,  // Use the provided onClick function
        shape = CircleShape,
        enabled = true,
        colors = ButtonDefaults.buttonColors(containerColor = Color(
            if (theme == "Classic") {
                0xFF00487C
            } else {
                0xFF4F345A
            }
        )),
        border = null,
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = "+",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 25.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}