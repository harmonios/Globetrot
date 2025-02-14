package ca.uwaterloo.team_102_7.ui.view

import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.items
import ca.uwaterloo.team_102_7.ui.components.BackButton
import ca.uwaterloo.team_102_7.ui.components.HE2ESButton
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel


@Composable
fun settingsScreen(settingsViewModel: SettingsViewModel,
                   navController: NavController) {
    LaunchedEffect(Unit) {
        settingsViewModel.getUser()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        newSettings(navController, settingsViewModel)
    }
}

@Composable
fun newSettings(navController: NavController, settingsViewModel: SettingsViewModel) {
    var theme = settingsViewModel.selectedTheme.value
    Column(
        modifier = Modifier.background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-150).dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Color(
                        if (theme == "Classic") {
                            0xFFA6E1FA
                        } else {
                            0xFF9CBFA7
                        }
                    )
                )
        ) {
            Text(
                text = if (settingsViewModel.selectedLanguage.value == "English") {
                    "Settings"
                } else {
                    "Paramètres"
                }
                ,
                modifier = Modifier.fillMaxWidth().padding(top = 90.dp),
                textAlign = TextAlign.Center,
                fontSize = 48.sp,
                color = Color.Black
            )
        }
        Box(
            modifier = Modifier
                .height(500.dp)
                .width(300.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    subtitle(if (settingsViewModel.selectedLanguage.value == "English") {
                        "Security"
                    } else {
                        "Sécurité"
                    })
                    profileOpt(settingsViewModel)
                }
                item {
                    subtitle(if (settingsViewModel.selectedLanguage.value == "English") {
                        "Data and Permissions"
                    } else {
                        "Données et Permissions"
                    })
                    locationOpt(settingsViewModel)
                    wifiOpt(settingsViewModel)
                }
                item {
                    subtitle(if (settingsViewModel.selectedLanguage.value == "English") {
                        "Personal"
                    } else {
                        "Personnel"
                    }
                    )
                    languageOpt(settingsViewModel)
                    currencyOpt(settingsViewModel)
                    themeOpt(settingsViewModel)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        FloatingActionButton(
            containerColor = Color(if (settingsViewModel.selectedTheme.value == "Classic") {
                0xFF00487C
            } else {
                0xFF4F345A
            }),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(100.dp).height(60.dp)
                .align(Alignment.Center).offset(y = (-28).dp),
            onClick = {
                navController.navigate("login") {
                    // reset stack on log out
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
        ) {
            Text(
                text = if (settingsViewModel.selectedLanguage.value == "English") {
                    "Sign Out"
                } else {
                    "Déconnectez"
                }
                ,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.White,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
            ) {
                BackButton(settingsViewModel, navController)
            }
            Box(
            ) {
                HE2ESButton("home", settingsViewModel, navController)
            }
        }
    }
}

@Composable
fun profileOpt(settingsViewModel: SettingsViewModel) {
    var showProfileDialog by remember { mutableStateOf(false) }
    var submission by remember { mutableStateOf(false) }

    val firstName by settingsViewModel.selectedFName
    val lastName by settingsViewModel.selectedlName
    val email by settingsViewModel.selectedEmail

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showProfileDialog = true }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Face,
            contentDescription = "Profile Icon"
        )
        settingOption(if (settingsViewModel.selectedLanguage.value == "English") {
            "Profile"
        } else {
            "Profil"
        }, if (settingsViewModel.selectedLanguage.value == "English") {
            90.dp
        } else {
            95.dp
        })
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Arrow Icon",
            modifier = Modifier.size(25.dp)
        )
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = {
                showProfileDialog = false
            },
            title = {
                Text(text = if (settingsViewModel.selectedLanguage.value == "English") {
                    "Profile Options"
                } else {
                    "Options de Profil"
                }
                )
            },
            text = {
                Column {
                    if (submission) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { settingsViewModel.setfName(it) },
                            label = { Text(if (settingsViewModel.selectedLanguage.value == "English") {
                                "First Name"
                            } else {
                                "Prénom"
                            }
                            ) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { settingsViewModel.setlName(it) },
                            label = { Text(if (settingsViewModel.selectedLanguage.value == "English") {
                                "Last Name"
                            } else {
                                "Nom de Famille"
                            }
                            ) }
                        )
                    } else {
                        Text(text = if (settingsViewModel.selectedLanguage.value == "English") {
                            "First Name: $firstName"
                        } else {
                            "Prénom : $firstName"
                        })
                        Text(text = if (settingsViewModel.selectedLanguage.value == "English") {
                            "Last Name: $lastName"
                        } else {
                            "Nom de Famille : $lastName"
                        }
                        )
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showProfileDialog = false }) {
                        Text(if (settingsViewModel.selectedLanguage.value == "English") {
                            "Dismiss"
                        } else {
                            "Ignorez"
                        }
                        )
                    }
                    if (submission) {
                        Button(onClick = {
                            settingsViewModel.updateFName(firstName)
                            settingsViewModel.updateLName(lastName)
                            submission = false
                        }) {
                            Text(if (settingsViewModel.selectedLanguage.value == "English") {
                                "Submit"
                            } else {
                                "Soumettez"
                            })
                        }
                    } else {
                        Button(onClick = { submission = true }) {
                            Text(if (settingsViewModel.selectedLanguage.value == "English") {
                                "Edit"
                            } else {
                                "Modifiez"
                            }
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
fun acctOpt() {
    Row (
        modifier = Modifier.fillMaxWidth().clickable{}.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Image"

        )
        settingOption("Google Account", 51.dp)
        Icon (
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Image",
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun locationOpt(settingsViewModel: SettingsViewModel) {
    var showLocationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var checkPermLocation by remember { mutableStateOf(false) }
    val LocationModal = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        checkPermLocation = isGranted
    }
    val contextFindLoc = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (contextFindLoc == true) { checkPermLocation = true }
    Row (
        modifier = Modifier.fillMaxWidth().clickable{ showLocationDialog = true }.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Image"

        )
        settingOption(if (settingsViewModel.selectedLanguage.value == "English") {
            "Location"
        } else {
            "Emplacement"
        }
            , if (settingsViewModel.selectedLanguage.value == "English") {
                80.dp
            } else {
                60.dp
            })
        Icon (
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Image",
            modifier = Modifier.size(30.dp)
        )
    }
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = {
                showLocationDialog = false
            },
            title = {
                Text(text = if (settingsViewModel.selectedLanguage.value == "English") {
                    "Location Options"
                } else {
                    "Options de Localisation"
                }
                )
            },
            text = {
                Text(if (settingsViewModel.selectedLanguage.value == "English") {
                    "Do you wish to edit Location Permissions?"
                } else {
                    "Souhaitez-vous modifier les permissions de localisation ?"
                }
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                            showLocationDialog = false
                        }
                    ) {
                        Text(if (settingsViewModel.selectedLanguage.value == "English") {
                            "Dismiss"
                        } else {
                            "Ignorez"
                        }
                        )
                    }
                    Button(onClick = {
                        showLocationDialog = false
                        if (!checkPermLocation) {
                            LocationModal.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        } else {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    }
                    ) {
                        if (!checkPermLocation) {
                            Text(if (settingsViewModel.selectedLanguage.value == "English") {
                                "Edit"
                            } else {
                                "Modifiez"
                            }
                            )
                        } else {
                            Text(if (settingsViewModel.selectedLanguage.value == "English") {
                                "Go To Settings"
                            } else {
                                "Paramètres"
                            }
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
fun wifiOpt(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    Row (
        modifier = Modifier.fillMaxWidth().clickable{
            val intent = Intent(Settings.ACTION_DATA_USAGE_SETTINGS)
            context.startActivity(intent)
        }.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Image"

        )
        settingOption(if (settingsViewModel.selectedLanguage.value == "English") {
            "Data Usage"
        } else {
            "Données"
        }, if (settingsViewModel.selectedLanguage.value == "English") {
            68.dp
        } else {
            80.dp
        })
        Icon (
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Image",
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun languageOpt(settingsViewModel: SettingsViewModel) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    val selectedLanguage by settingsViewModel.selectedLanguage
    Row (
        modifier = Modifier.fillMaxWidth().clickable{ showLanguageDialog = true }.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MailOutline,
            contentDescription = "Image"

        )
        settingOption(if (settingsViewModel.selectedLanguage.value == "English") {
            "Language"
        } else {
            "Langue"
        }
            , if (settingsViewModel.selectedLanguage.value == "English") {
                75.dp
            } else {
                85.dp
            })
        Icon (
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Image",
            modifier = Modifier.size(30.dp)
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = {
                showLanguageDialog = false
            },
            title = {
                Text(text = if (settingsViewModel.selectedLanguage.value == "English") {
                    "Choose Language"
                } else {
                    "Choisir la Langue"
                }
                )
            },
            text = {
                LazyColumn {
                    items(listOf("English", "French")) { chosen ->
                        optionSelect(chosen, settingsViewModel.selectedLanguage.value) {
                            settingsViewModel.updateLanguage(chosen)
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { showLanguageDialog = false }) {
                        Text(if (settingsViewModel.selectedLanguage.value == "English") {
                            "Dismiss"
                        } else {
                            "Ignorer"
                        }
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun optionSelect(text: String, currLang: String, onClickAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClickAction)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = text == currLang,
            onClick = onClickAction
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text)
    }
}

@Composable
fun currencyOpt(settingsViewModel: SettingsViewModel) {
    var showCurrencyDialog by remember { mutableStateOf(false) }
    val selectedCurrency by settingsViewModel.selectedCurrency
    Row (
        modifier = Modifier.fillMaxWidth().clickable{ showCurrencyDialog = true}.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Image"

        )
        settingOption(if (settingsViewModel.selectedLanguage.value == "English") {
            "Currency"
        } else {
            "Devise"
        }
            , if (settingsViewModel.selectedLanguage.value == "English") {
                78.dp
            } else {
                88.dp
            })
        Icon (
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Image",
            modifier = Modifier.size(30.dp)
        )
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = {
                showCurrencyDialog = false
            },
            title = {
                Text(text = if (settingsViewModel.selectedLanguage.value == "English") {
                    "Choose Currency"
                } else {
                    "Choisir la Devise"
                }
                )
            },
            text = {
                LazyColumn {
                    items(listOf("CAD", "USD")) { chosen ->
                        optionSelect(chosen, settingsViewModel.selectedCurrency.value) {
                            settingsViewModel.updateCurrency(chosen)
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { showCurrencyDialog = false }) {
                        Text(if (settingsViewModel.selectedLanguage.value == "English") {
                            "Dismiss"
                        } else {
                            "Ignorez"
                        }
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun themeOpt(settingsViewModel: SettingsViewModel) {
    var showThemeDialog by remember { mutableStateOf(false) }
    Row (
        modifier = Modifier.fillMaxWidth().clickable{ showThemeDialog = true}.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Addchart,
            contentDescription = "Image"

        )
        settingOption(if (settingsViewModel.selectedLanguage.value == "English") {
            "Theme"
        } else {
            "Thème"
        }
            , 86.dp)
        Icon (
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Image",
            modifier = Modifier.size(30.dp)
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = {
                showThemeDialog = false
            },
            title = {
                Text(text = if (settingsViewModel.selectedLanguage.value == "English") {
                    "Choose Theme"
                } else {
                    "Choisir le Thème"
                }
                )
            },
            text = {
                LazyColumn {
                    val EngClassc = "Classic"
                    val BG = "Bruised Green"
                    items(listOf(EngClassc, BG)) { chosen ->
                        optionSelect(chosen, settingsViewModel.selectedTheme.value) {
                            settingsViewModel.setTheme(chosen)
                            settingsViewModel.updateTheme(chosen)
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { showThemeDialog = false }) {
                        Text(if (settingsViewModel.selectedLanguage.value == "English") {
                            "Dismiss"
                        } else {
                            "Ignorez"
                        }
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun subtitle(name: String) {
    Text(
        text = name,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black,
        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
    )
}

@Composable
fun settingOption(name: String, width: Dp) {
    Spacer(modifier = Modifier.width(width))
    Text (
        text = name,
        fontSize = 18.sp,
        color = Color.Black,
    )
    Spacer(modifier = Modifier.width(width))
}