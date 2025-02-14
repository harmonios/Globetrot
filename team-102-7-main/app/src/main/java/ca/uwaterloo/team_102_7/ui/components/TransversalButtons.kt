package ca.uwaterloo.team_102_7.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel

@Composable
fun HE2ESButton(endDestination: String, themeViewModel: SettingsViewModel, navController: NavController) {
    var theme = themeViewModel.selectedTheme.value
    Button(
        modifier = Modifier
            .size(50.dp)
            .background(
                Color(
                if (theme == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                }
            ), shape = CircleShape
            )
            .clip(CircleShape)
            .wrapContentSize(Alignment.Center),
        onClick = {
            if (endDestination == "home") {
                navController.navigate("home")
            } else {
                navController.navigate("settings")
            }
        },
        shape = CircleShape,
        enabled = true,
        colors = ButtonDefaults.buttonColors(containerColor = Color(
            if (theme == "Classic") {
                0xFF00487C
            } else {
                0xFF4F345A
            }
        )
        ),
        border = null,
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = if (endDestination == "settings") {
                Icons.Default.Settings
            } else {
                Icons.Default.Home
            },
            contentDescription = null
        )
    }
}

@Composable
fun BackButton(themeViewModel: SettingsViewModel, navController: NavController) {
    var theme = themeViewModel.selectedTheme.value
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Button(
        modifier = Modifier
            .size(50.dp)
            .background(
                Color(
                    if (theme == "Classic") {
                        0xFF00487C
                    } else {
                        0xFF4F345A
                    }
                ), shape = CircleShape
            )
            .clip(CircleShape)
            .wrapContentSize(Alignment.Center),
        onClick = {
            if (currentRoute == "itinerary") {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            } else if (currentRoute == "navigation") {
                navController.navigate("itinerary") {
                    popUpTo("itinerary")
                }
            } else {
                navController.popBackStack()
            }
        },
        shape = CircleShape,
        enabled = true,
        colors = ButtonDefaults.buttonColors(containerColor = Color(
            if (theme == "Classic") {
                0xFF00487C
            } else {
                0xFF4F345A
            }
        )
        ),
        border = null,
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null
        )
    }
}