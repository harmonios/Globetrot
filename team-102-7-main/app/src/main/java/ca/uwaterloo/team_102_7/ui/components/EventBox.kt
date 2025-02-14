package ca.uwaterloo.team_102_7.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel

@Composable
fun eventBox(
    nameText: String,
    descriptionText: String?,
    imageResId: Int?,
    themeViewModel: SettingsViewModel,
    onClick: () -> Unit = {},
) {
    // Coloured block with name of Event, desc, image, length of travel
    var theme = themeViewModel.selectedTheme.value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(
                if (theme == "Classic") {
                    0xFFA6E1FA
                } else {
                    0xFF9CBFA7
                }
            )) // Background color of block
            .padding(8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            // Name of Event
            Text(
                text = nameText,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 4.dp),
                fontWeight = FontWeight.Bold
            )

            if (descriptionText != null) {
                // Description Text
                Text(
                    text = descriptionText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp) // Space between bold and description
                )
            }
        }

        if (imageResId != null) {
            // Image of Event
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Image",
                modifier = Modifier
                    .size(80.dp) // Adjust size of image
                    .clip(RoundedCornerShape(10.dp))
                    .padding(),
                contentScale = ContentScale.Crop
            )
        }
    }
}