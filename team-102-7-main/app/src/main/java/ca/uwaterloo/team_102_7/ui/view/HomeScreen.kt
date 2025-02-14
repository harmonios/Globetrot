package ca.uwaterloo.team_102_7.ui.view

import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import ca.uwaterloo.team_102_7.data.domain.Itinerary
import ca.uwaterloo.team_102_7.data.domain.User
import ca.uwaterloo.team_102_7.ui.components.BackButton
import ca.uwaterloo.team_102_7.ui.components.HE2ESButton
import ca.uwaterloo.team_102_7.ui.components.plusButton
import ca.uwaterloo.team_102_7.ui.viewmodel.HomeViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.ItineraryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel
import datePickerButton
import java.text.SimpleDateFormat

@Composable
fun homeScreen(
    context: Context,
    homeViewModel: HomeViewModel,
    itineraryViewModel: ItineraryViewModel,
    navController: NavController,
    themeViewModel: SettingsViewModel
) {
    val userId by homeViewModel.savedUserId.collectAsState()
    val checkBoxItems by homeViewModel.checkBoxItems.collectAsState()
    val user by homeViewModel.user.collectAsState()
    val currentItineraries by itineraryViewModel.currentItineraries.observeAsState()
    val pastItineraries by itineraryViewModel.pastItineraries.observeAsState()

    var isDialogOpen by remember { mutableStateOf(false) }
    var newItemText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // Load items from DataStore when the screen is first opened
    LaunchedEffect(Unit) {
        homeViewModel.getUser() // from local storage
    }

    LaunchedEffect(Unit) {
        themeViewModel.getUser()
    }

    LaunchedEffect(userId) {
        userId?.let {
            homeViewModel.retrieveCheckboxItems(it)
            itineraryViewModel.getCurrentItineraries(userId)
            itineraryViewModel.getPastItineraries(userId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                welcomeText(user, themeViewModel)
            }
            Spacer(modifier = Modifier.height(30.dp))

            itineraries(userId, currentItineraries, true, navController, itineraryViewModel, themeViewModel)
            itineraries(userId, pastItineraries, false, navController, itineraryViewModel, themeViewModel)

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (themeViewModel.selectedLanguage.value == "English") {
                        "Your Checklist"
                    } else {
                        "Liste de Contrôle"
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Add checkboxes here
            Spacer(modifier = Modifier.height(16.dp))

            // Add button to add new checkbox item
            Button(
                onClick = { isDialogOpen = true },
                modifier = Modifier.padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                }))
            ) {
                Text(text = if (themeViewModel.selectedLanguage.value == "English") {
                    "Add New Item"
                } else {
                    "Ajouter un Nouvel Élément"
                }
                )
            }

            // Add checkboxes dynamically from the list
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(top = 1.dp)) {
                checkBoxItems.forEach { item ->
                    // Remove checkbox item and update DataStore
                    checkbox(item, themeViewModel) {
                        val updatedItems = checkBoxItems.filter { it != item }
                        homeViewModel.saveCheckboxItems(userId, updatedItems)
                    }
                }
            }

            // Show modal dialog when `isDialogOpen` is true
            if (isDialogOpen) {
                AlertDialog(
                    onDismissRequest = { isDialogOpen = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newItemText.isNotEmpty()) {
                                    val updatedItems = checkBoxItems + newItemText
                                    homeViewModel.saveCheckboxItems(userId, updatedItems)
                                    newItemText = ""
                                    isDialogOpen = false
                                } else {
                                    Toast.makeText(context, "Missing fields!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                                0xFF00487C
                            } else {
                                0xFF4F345A
                            }))
                        ) {
                            Text(if (themeViewModel.selectedLanguage.value == "English") {
                                "Add"
                            } else {
                                "Ajouter"
                            }
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { isDialogOpen = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                                0xFF00487C
                            } else {
                                0xFF4F345A
                            }))
                        ) {
                            Text(if (themeViewModel.selectedLanguage.value == "English") {
                                "Cancel"
                            } else {
                                "Annuler"
                            }
                            )
                        }
                    },
                    title = { Text(if (themeViewModel.selectedLanguage.value == "English") {
                        "Add New Item"
                    } else {
                        "Ajouter un Nouvel Élément"
                    }
                    ) },
                    text = {
                        Column {
                            Text(text = if (themeViewModel.selectedLanguage.value == "English") {
                                "Enter the name of the new item:"
                            } else {
                                "Entrez le nom du nouvel élément:"
                            }
                            )
                            TextField(
                                value = newItemText,
                                onValueChange = { newItemText = it },
                                placeholder = { Text(if (themeViewModel.selectedLanguage.value == "English") {
                                    "New Item"
                                } else {
                                    "Nouvel Élément"
                                }
                                ) }
                            )
                        }
                    }
                )
            }
            Box(
            ) {
                if (showDialog) {
                    itineraryFormDialog(
                        context,
                        onDismiss = { showDialog = false },
                        onSubmit = { tripName, startDate, endDate, location, lat, long ->
                            // Handle the new trip creation with location
                            createNewItinerary(itineraryViewModel, userId, tripName, startDate, endDate, location, lat, long)
                            // Navigate to the itinerary screen
                            navController.navigate("itinerary")
                        },
                        themeViewModel
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 85.dp)
        ) {
            HE2ESButton("settings", themeViewModel, navController)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 25.dp)
        ) {
            plusButton(onClick = { showDialog = true }, themeViewModel)
        }
    }
}

@Composable
fun welcomeText(user: User?, themeViewModel: SettingsViewModel) {
    Text(
        text = if (themeViewModel.selectedLanguage.value == "English") {
            "Welcome ${user?.first}"
        } else {
            "Bienvenue ${user?.first}"
        }
        ,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun itineraries(
    userId: Int?,
    itineraries: List<Itinerary>?,
    isCurrent: Boolean,
    navController: NavController,
    itineraryViewModel: ItineraryViewModel,
    themeViewModel: SettingsViewModel,
) {
    val header = if (themeViewModel.selectedLanguage.value == "English") {
        if (isCurrent) "Current Trips" else "Past Trips"
    } else {
        if (isCurrent) "Voyages Actuels" else "Voyages Passés"
    }


    if (itineraries != null) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = header,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        // Lazy Load row
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            //Lazy Load Box
            Box(
                modifier = Modifier.weight(1f)
                    .padding(end = 10.dp)
            ) {
                lazyLoadingTripBoxes(userId, itineraries, navController, itineraryViewModel, themeViewModel)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun lazyLoadingTripBoxes(
    userId: Int?,
    itineraries: List<Itinerary>,
    navController: NavController,
    itineraryViewModel: ItineraryViewModel,
    themeViewModel: SettingsViewModel
) {
    LazyRow(
        modifier = Modifier
            .padding(start = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Adjust spacing between boxes here
    ) {
        items(itineraries) { item -> // Use items instead of item
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color(if (themeViewModel.selectedTheme.value == "Classic") {
                            0xFFA6E1FA
                        } else {
                            0xFF4F345A
                        }), RoundedCornerShape(8.dp))
                    .clickable {
                        itineraryViewModel.setItineraryId(item.id)
                        itineraryViewModel.setUserId(userId)
                        navController.navigate(route = "itinerary")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                            0xFF000000
                        } else {
                            0xFFFFFFFF
                        })
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val displayStart = LocalDate.parse(item.start)
                    val displayEnd = LocalDate.parse(item.end)
                    Text(
                        text = "${displayStart.format(DateTimeFormatter.ofPattern("MMM d"))} - ${
                            displayEnd.format(DateTimeFormatter.ofPattern("MMM d"))
                        }",
                        fontSize = 16.sp,
                        color = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                            0xFF000000
                        } else {
                            0xFFFFFFFF
                        })
                    )
                }
            }
        }
    }
}

@Composable
fun checkbox(msg: String, themeViewModel: SettingsViewModel, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { checked ->
                // TODO: show the checkmark animation
                if (checked) {
                    onDelete()
                }
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                }), // Custom color for checked state
                uncheckedColor = Color.Gray, // Custom color for unchecked state
                checkmarkColor = Color.White // Color of the checkmark itself
            )
        )
        Text(text = msg)
    }
}

@Composable
fun itineraryFormDialog(
    context: Context,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Double, Double) -> Unit,
    themeViewModel: SettingsViewModel
) {
    var tripName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf(0.0) }
    var long by remember { mutableStateOf(0.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (themeViewModel.selectedLanguage.value == "English") {
            "New Trip"
        } else {
            "Nouveau Voyage"
        }
        ) },
        text = {
            Column {
                OutlinedTextField(
                    value = tripName,
                    onValueChange = { tripName = it },
                    label = { Text(if (themeViewModel.selectedLanguage.value == "English") {
                        "Trip Name"
                    } else {
                        "Nom du Voyage"
                    }
                    ) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Add the location search bar
                Text(
                    text = if (themeViewModel.selectedLanguage.value == "English") {
                        "Location"
                    } else {
                        "Emplacement"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                PlacesAutoCompleteSearchBar(themeViewModel) { place ->
                    location = place.name ?: ""
                    getLatLong(
                        context = context,
                        location = location,
                        onLat = { cord -> lat = cord },
                        onLong = { cord -> long = cord },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Start Date Picker
                datePickerButton(
                    label = if (themeViewModel.selectedLanguage.value == "English") {
                        "Start Date"
                    } else {
                        "Date de Début"
                    }
                    ,
                    selectedDate = startDate,
                    onDateSelected = { date -> startDate = date },
                    themeViewModel
                )

                Spacer(modifier = Modifier.height(8.dp))



                // End Date Picker
                datePickerButton(
                    label = if (themeViewModel.selectedLanguage.value == "English") {
                        "End Date"
                    } else {
                        "Date de Fin"
                    }
                    ,
                    selectedDate = endDate,
                    onDateSelected = { date -> endDate = date },
                    themeViewModel,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tripName.isNotEmpty() &&
                        startDate.isNotEmpty() &&
                        endDate.isNotEmpty() &&
                        location.isNotEmpty()) {

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        if (dateFormat.parse(startDate)!! <= dateFormat.parse(endDate)) {
                            onSubmit(tripName, startDate, endDate, location, lat, long)
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Start date can't be after end!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "There are missing fields!", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                }))
            ) {
                Text(if (themeViewModel.selectedLanguage.value == "English") {
                    "Create"
                } else {
                    "Créer"
                }
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                }))
            ) {
                Text(if (themeViewModel.selectedLanguage.value == "English") {
                    "Cancel"
                } else {
                    "Annuler"
                })
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false),
    )
}

// Updated helper function to handle trip creation with location
private fun createNewItinerary(
    itineraryViewModel: ItineraryViewModel,
    userId: Int?,
    itineraryName: String,
    startDate: String,
    endDate: String,
    location: String,
    lat: Double,
    long: Double,
) {
    if (userId != null) {
        itineraryViewModel.createNewItinerary(userId, itineraryName, startDate, endDate, location, lat, long)
    }
}

fun getLatLong(
    context: Context,
    location: String,
    onLat: (Double) -> Unit,
    onLong: (Double) -> Unit,
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addressList = geocoder.getFromLocationName(location, 1)

    if (!addressList.isNullOrEmpty()) {
        val locationLatLng = addressList[0].let {
            it.latitude to it.longitude
        }

        onLat(locationLatLng.first)
        onLong(locationLatLng.second)
    }
}

