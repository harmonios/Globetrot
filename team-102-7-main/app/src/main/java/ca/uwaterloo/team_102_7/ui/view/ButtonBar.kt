package ca.uwaterloo.team_102_7.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel

@Composable
fun buttonBar(navController: NavController, signOut: Boolean = false, currPage: String, themeViewModel: SettingsViewModel) {
    var theme = themeViewModel.selectedTheme.value
    val modifier = if (signOut) {
        Modifier
            .fillMaxSize()
            .background(color = Color.White)
    } else {
        Modifier.fillMaxSize()
    }
    Box(
        modifier = modifier
    ) {
        if (signOut) {
            FloatingActionButton(
                containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(100.dp).height(60.dp)
                    .align(Alignment.Center).offset(y = (-28).dp),
                onClick = { navController.navigate("login") },
            ) {
                Text(
                    text = if (themeViewModel.selectedLanguage.value == "English") {
                        "Sign Out"
                    } else {
                        "Déconnecter"
                    }
                    ,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = Color.White,
                )
            }
        }
        Row (
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = { navController.navigate("home") },
                shape = RoundedCornerShape(10.dp),
                colors = if (currPage == "home") {
                    ButtonDefaults.buttonColors(Color(if (themeViewModel.selectedTheme.value == "Classic") {
                        0xFFDAF5FF
                    } else {
                        0xFF8FA998
                    }))
                } else {
                    ButtonDefaults.buttonColors(Color.White)
                },
                modifier = Modifier.width(100.dp).height(60.dp).border(2.dp, Color.Black, RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            Button(
                onClick = { navController.navigate("itinerary") },
                shape = RoundedCornerShape(10.dp),
                colors = if (currPage == "itinerary") {
                    ButtonDefaults.buttonColors(Color(if (themeViewModel.selectedTheme.value == "Classic") {
                        0xFFDAF5FF
                    } else {
                        0xFF8FA998
                    }))
                } else {
                    ButtonDefaults.buttonColors(Color.White)
                },
                modifier = Modifier.width(100.dp).height(60.dp).border(2.dp, Color.Black, RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Itinerary",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            Button(
                onClick = { navController.navigate("navigation") },
                shape = RoundedCornerShape(10.dp),
                colors = if (currPage == "navigation") {
                    ButtonDefaults.buttonColors(Color(if (themeViewModel.selectedTheme.value == "Classic") {
                        0xFFDAF5FF
                    } else {
                        0xFF8FA998
                    }))
                } else {
                    ButtonDefaults.buttonColors(Color.White)
                },
                modifier = Modifier.width(100.dp).height(60.dp).border(2.dp, Color.Black, RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Navigation",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            Button(
                onClick = { navController.navigate("settings") },
                shape = RoundedCornerShape(10.dp),
                colors = if (currPage == "settings") {
                    ButtonDefaults.buttonColors(Color(if (themeViewModel.selectedTheme.value == "Classic") {
                        0xFFDAF5FF
                    } else {
                        0xFF8FA998
                    }))
                } else {
                    ButtonDefaults.buttonColors(Color.White)
                },
                modifier = Modifier.width(100.dp).height(60.dp).border(2.dp, Color.Black, RoundedCornerShape(10.dp),)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black
                )
            }
        }
    }
}