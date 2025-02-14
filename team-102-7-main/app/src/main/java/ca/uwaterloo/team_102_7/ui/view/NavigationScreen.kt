package ca.uwaterloo.team_102_7.ui.view

import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.ui.res.painterResource
import ca.uwaterloo.team_102_7.R
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uwaterloo.team_102_7.data.domain.Itinerary
import ca.uwaterloo.team_102_7.data.domain.TravelEvent
import ca.uwaterloo.team_102_7.data.domain.TravelType
import ca.uwaterloo.team_102_7.ui.components.BackButton
import ca.uwaterloo.team_102_7.ui.components.HE2ESButton
import ca.uwaterloo.team_102_7.ui.components.eventBox
import ca.uwaterloo.team_102_7.ui.components.plusButton
import ca.uwaterloo.team_102_7.ui.viewmodel.ItineraryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import ca.uwaterloo.team_102_7.ui.viewmodel.NavigationViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel
import timePickerApp
import java.time.OffsetTime

@Composable
fun navigationScreen(context: Context, navigationViewModel: NavigationViewModel, itineraryViewModel: ItineraryViewModel, navController: NavController, themeViewModel: SettingsViewModel) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val itinerary by itineraryViewModel.itinerary.observeAsState()
    val selectedDate by itineraryViewModel.selectedDate.observeAsState()
    val selectedTravelEvents by itineraryViewModel.selectedTravelEvents.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(screenHeight * 0.02f))

        Row(
           // horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 75.dp)
                .offset(y = (-10).dp)
        ) {
            dateText(selectedDate, themeViewModel)
        }

        Spacer(modifier = Modifier.height(screenHeight * 0.04f))

        //Lazy Load Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight - (screenHeight * 0.25f)) // This allows for space at the bottom
        ) {
            //Each Column
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (selectedTravelEvents != null) {
                    items(selectedTravelEvents!!) { item ->
                        travelEventItem(
                            context = context,
                            timeText = "${timeFormatter(item.startTime)} - ${timeFormatter(item.endTime)}",
                            nameText = item.nameText,
                            descriptionText = item.descriptionText,
                            imageResId = item.imageResId,
                            themeViewModel = themeViewModel,
                            itinerary = itinerary,
                            travelEvent = item,
                            itineraryViewModel = itineraryViewModel,
                            navController = navController,
                        )
                    }
                }
            }
        }

        var showCreateOwn by remember { mutableStateOf(false) }
        var showModal by remember { mutableStateOf(false) }
        var typeOfEventToggle by remember { mutableStateOf<TravelType?>(null) } // Variable to store the type of event was clicked

        var fetchedRestaurants by remember { mutableStateOf<List<TravelEvent>>(emptyList()) }
        var fetchedParks by remember { mutableStateOf<List<TravelEvent>>(emptyList()) }
        var fetchedSportsVenues by remember { mutableStateOf<List<TravelEvent>>(emptyList()) }
        var isFetching by remember { mutableStateOf(false) } // Indicator to show if fetching is happening
        var selectedRestaurant by remember { mutableStateOf<TravelEvent?>(null) } // To keep track of the selected restaurant

        var descriptionText by remember { mutableStateOf("") }

        var name by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf<TravelType?>(null) }
        var selectedStartTime by remember { mutableStateOf("") }
        var selectedEndTime by remember { mutableStateOf("") }
        var lat by remember { mutableStateOf(0.0) }
        var long by remember { mutableStateOf(0.0) }
        var price by remember { mutableStateOf("0") }
        var travelTime by remember { mutableStateOf("") }

        val options = listOf(
            TravelType.PARK,
            TravelType.RESTAURANT,
            TravelType.SPORTS,
        )

        if (showModal) {
            AlertDialog(
                onDismissRequest = { showModal = false },
                title = { Text(if (themeViewModel.selectedLanguage.value == "English") {
                    "Choose your Events"
                } else {
                    "Choisissez vos Événements"
                }) },
                text = {
                    if (!showCreateOwn) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.9f) // Adjust height for more vertical space
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            val boxSize = 55.dp // size of the boxes

                            // List of images for boxes
                            val imageList = listOf(
                                Pair(R.drawable.park, TravelType.PARK),
                                Pair(R.drawable.restaurant, TravelType.RESTAURANT),
                                Pair(R.drawable.sports, TravelType.SPORTS)
                            )

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between boxes
                            ) {
                                items(imageList) { (imageResId, travelType) ->
                                    Box(
                                        modifier = Modifier
                                            .size(boxSize)
                                            .background(Color.Gray)
                                            .clickable {
                                                typeOfEventToggle = travelType // Update the typeOfEventToggle based on the box clicked
                                                fetchedRestaurants = emptyList() // Clear restaurants when switching
                                                fetchedParks = emptyList() // Clear parks when switching
                                                fetchedSportsVenues = emptyList() // Clear sports venues when switching

                                                if (typeOfEventToggle == TravelType.RESTAURANT && !isFetching) {
                                                    isFetching = true
                                                    itineraryViewModel.fetchNearbyRestaurants(
                                                        itinerary = itinerary,
                                                        onSuccess = { travelEvents ->
                                                            fetchedRestaurants = travelEvents
                                                            isFetching = false
                                                        },
                                                        onError = { error ->
                                                            println("Error fetching restaurants: $error")
                                                            isFetching = false
                                                        }
                                                    )
                                                } else if (typeOfEventToggle == TravelType.PARK && !isFetching) {
                                                    isFetching = true
                                                    itineraryViewModel.fetchNearbyParks(
                                                        itinerary = itinerary,
                                                        onSuccess = { travelEvents ->
                                                            fetchedParks = travelEvents
                                                            isFetching = false
                                                        },
                                                        onError = { error ->
                                                            println("Error fetching parks: $error")
                                                            isFetching = false
                                                        }
                                                    )
                                                } else if (typeOfEventToggle == TravelType.SPORTS && !isFetching) {
                                                    isFetching = true
                                                    itineraryViewModel.fetchNearbySportsVenues(
                                                        itinerary = itinerary,
                                                        onSuccess = { travelEvents ->
                                                            fetchedSportsVenues = travelEvents
                                                            isFetching = false
                                                        },
                                                        onError = { error ->
                                                            println("Error fetching sports venues: $error")
                                                            isFetching = false
                                                        }
                                                    )
                                                }
                                            }



                                            .border(
                                                width = if (typeOfEventToggle == travelType) 3.dp else 1.dp,
                                                color = if (typeOfEventToggle == travelType) Color.Blue else Color.Transparent,
                                            )
                                            .shadow(
                                                elevation = if (typeOfEventToggle == travelType) 10.dp else 0.dp,
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = imageResId),
                                            contentDescription = "Event Image",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop // Make image cover the box
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            if (isFetching) {
                                CircularProgressIndicator() // Show a loading indicator while fetching
                            } else if (typeOfEventToggle == TravelType.RESTAURANT && fetchedRestaurants.isNotEmpty()) {
                                // Display restaurants if they are fetched
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.8f)
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(fetchedRestaurants) { restaurant ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (selectedRestaurant == restaurant) Color.Gray else Color(0xFFF0F0F0),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    selectedRestaurant = restaurant // Only allow one restaurant to be selected at a time
                                                }
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = restaurant.nameText,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (selectedRestaurant == restaurant) Color.White else Color.Black
                                            )
                                        }
                                    }
                                }
                            } else if (typeOfEventToggle == TravelType.PARK && fetchedParks.isNotEmpty()) {
                                // Display parks if they are fetched
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.8f)
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(fetchedParks) { park ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (selectedRestaurant == park) Color.Gray else Color(0xFFF0F0F0),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    selectedRestaurant = park // Only allow one park to be selected at a time
                                                }
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = park.nameText,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (selectedRestaurant == park) Color.White else Color.Black
                                            )
                                        }
                                    }
                                }
                            } else if (typeOfEventToggle == TravelType.SPORTS && fetchedSportsVenues.isNotEmpty()) {
                                // Display sports venues if they are fetched
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.8f)
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(fetchedSportsVenues) { sportsVenue ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (selectedRestaurant == sportsVenue) Color.Gray else Color(0xFFF0F0F0),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    selectedRestaurant = sportsVenue // Only allow one sports venue to be selected at a time
                                                }
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = sportsVenue.nameText,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (selectedRestaurant == sportsVenue) Color.White else Color.Black
                                            )
                                        }
                                    }
                                }
                            } else if (typeOfEventToggle == TravelType.RESTAURANT) {
                                Text(if (themeViewModel.selectedLanguage.value == "English") {
                                    "No restaurants found"
                                } else {
                                    "Aucun restaurant trouvé"
                                }, modifier = Modifier.padding(8.dp))
                            } else if (typeOfEventToggle == TravelType.PARK) {
                                Text(if (themeViewModel.selectedLanguage.value == "English") {
                                    "No parks or tourist spots found"
                                } else {
                                    "Aucun parc ou site touristique trouvé"
                                }
                                    , modifier = Modifier.padding(8.dp))
                            } else if (typeOfEventToggle == TravelType.SPORTS) {
                                Text(if (themeViewModel.selectedLanguage.value == "English") {
                                    "No sports venues found"
                                } else {
                                    "Aucun lieu sportif trouvé"
                                }
                                    , modifier = Modifier.padding(8.dp))
                            } else {
                                Text(if (themeViewModel.selectedLanguage.value == "English") {
                                    "Please choose an event category"
                                } else {
                                    "Veuillez choisir une catégorie d'événement"
                                }
                                )
                            }


                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            item {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                                        "Name of the event:"
                                    } else {
                                        "Nom de l'événement :"
                                    }
                                    )
                                    PlacesAutoCompleteSearchBar(themeViewModel) { place ->
                                        name = place.name ?: ""
                                        getLatLong(
                                            context = context,
                                            location = name,
                                            onLat = { cord -> lat = cord },
                                            onLong = { cord -> long = cord },
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                                        "Type of event:"
                                    } else {
                                        "Type d'événement :"
                                    }
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            // Trigger button
                                            Button(
                                                onClick = { expanded = !expanded },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(
                                                        if (themeViewModel.selectedTheme.value == "Classic") {
                                                            0xFF00487C
                                                        } else {
                                                            0xFF4F345A
                                                        }
                                                    )
                                                )
                                            ) {
                                                Text(text = selectedOption?.label ?: if (themeViewModel.selectedLanguage.value == "English") {
                                                    "Select an option"
                                                } else {
                                                    "Sélectionnez une option"
                                                }
                                                )
                                            }

                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                options.forEach { value ->
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            selectedOption = value
                                                            expanded = false
                                                        }
                                                    ) {
                                                        Text(text = value.label)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                                        "Select a time:"
                                    } else {
                                        "Sélectionnez une heure:"
                                    }
                                    )
                                    timePickerApp(
                                        context = context,
                                        placeholderText = if (themeViewModel.selectedLanguage.value == "English") {
                                            "Select start time"
                                        } else {
                                            "Sélectionnez l'heure de début"
                                        }
                                        ,
                                        themeViewModel = themeViewModel,
                                        onTimeSelected = { time -> selectedStartTime = time }
                                    )
                                    timePickerApp(
                                        context = context,
                                        placeholderText = if (themeViewModel.selectedLanguage.value == "English") {
                                            "Select end time"
                                        } else {
                                            "Sélectionnez l'heure de fin"
                                        }
                                        ,
                                        themeViewModel = themeViewModel,
                                        onTimeSelected = { time -> selectedEndTime = time }
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                                        "Enter price:"
                                    } else {
                                        "Entrez le prix :"
                                    })
                                    inputField(onInputChanged = { input -> price = input })
                                    Spacer(modifier = Modifier.height(12.dp))

                                    fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                                        "Enter the commute time:"
                                    } else {
                                        "Entrez le temps de trajet :"
                                    }
                                    )
                                    inputField(onInputChanged = { input -> travelTime = input })
                                    Spacer(modifier = Modifier.height(12.dp))

                                    descriptionField(
                                        onDescriptionSelected = { text -> descriptionText = text },
                                        themeViewModel
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val createOwn = if (themeViewModel.selectedLanguage.value == "English") {
                            "Create your own instead"
                        } else {
                            "Créez le vôtre à la place"
                        }

                        val suggested = if (themeViewModel.selectedLanguage.value == "English") {
                            "Get a suggestion"
                        } else {
                            "Obtenez une suggestion"
                        }

                        Text(
                            text = if (!showCreateOwn) createOwn else suggested,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable {
                                    showCreateOwn = !showCreateOwn
                                }
                                .align(Alignment.End)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                                    0xFF00487C
                                } else {
                                    0xFF4F345A
                                })),
                                onClick = {
                                    typeOfEventToggle = null
                                    showModal = false
                                }
                            ) {
                                Text(if (themeViewModel.selectedLanguage.value == "English") {
                                    "Cancel"
                                } else {
                                    "Annuler"
                                }
                                )
                            }
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                                    0xFF00487C
                                } else {
                                    0xFF4F345A
                                })),
                                onClick = {
                                    try {
                                        val currDate = LocalDate.parse(selectedDate!!)
                                        val formatter = DateTimeFormatter.ofPattern(
                                            "EEE, MMM d",
                                            Locale.getDefault()
                                        )
                                        val formattedDate = currDate.format(formatter)
                                        var noFormErrors = true

                                        if (selectedRestaurant != null && !showCreateOwn) {
                                            itineraryViewModel.addSuggestedTravelEvent(
                                                selectedRestaurant!!,
                                                descriptionText,
                                                themeViewModel.selectedLanguage.value
                                            )
                                            showModal = false
                                        } else if (selectedRestaurant == null && !showCreateOwn) {
                                            Toast.makeText(context, "Please select an option", Toast.LENGTH_LONG).show()
                                            noFormErrors = false
                                        } else if (showCreateOwn) {
                                            if (
                                                name.isEmpty() ||
                                                selectedStartTime.isEmpty() ||
                                                selectedEndTime.isEmpty() ||
                                                travelTime.isEmpty() ||
                                                lat == 0.0 ||
                                                long == 0.0
                                            ) {
                                                Toast.makeText(context, "There are missing fields!", Toast.LENGTH_LONG).show()
                                                noFormErrors = false
                                            } else {
                                                itineraryViewModel.addCustomTravelEvent(
                                                    name = name,
                                                    travelType = selectedOption ?: TravelType.UNASSIGNED,
                                                    date = selectedDate!!,
                                                    startTime = selectedStartTime,
                                                    endTime = selectedEndTime,
                                                    travelTime = travelTime,
                                                    price = price,
                                                    latitude = lat,
                                                    longitude = long,
                                                    description = descriptionText,
                                                    creator = itinerary?.creator,
                                                    itineraryId = itinerary?.id,
                                                )
                                            }
                                        }

                                        if (noFormErrors) {
                                            Toast.makeText(
                                                context,
                                                if (themeViewModel.selectedLanguage.value == "English") {
                                                    "Success! Added event to $formattedDate"
                                                } else {
                                                    "Succès! Événement ajouté au $formattedDate"
                                                },
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.navigate("itinerary")
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            ) {
                                Text(if (themeViewModel.selectedLanguage.value == "English") {
                                    "Confirm"
                                } else {
                                    "Confirmez"
                                }
                                )
                            }
                        }
                    }
                }

            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-2).dp, y = (-64).dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .offset(y = (-65).dp, x = 2.dp)
            ) {
                HE2ESButton("home", themeViewModel, navController)
            }
            Box(
                modifier = Modifier
                    .offset(y = (-5).dp, x = 2.dp)
            ) {
                HE2ESButton("settings", themeViewModel, navController)
            }
            Box(
                modifier = Modifier
                    .offset(y = (55).dp, x = 2.dp)
            ) {
                plusButton(onClick = { showModal = true }, themeViewModel)
            }
            Box(
                modifier = Modifier
                    .offset(y = (-670).dp, x = (-335).dp)
            ) {
                BackButton(themeViewModel, navController)
            }
        }
    }
}

//Gets the current date and displays it
@Composable
fun dateText(date: String?, themeViewModel: SettingsViewModel) {
    if (date != null) {
        val currDate = LocalDate.parse(date)
        val formatter = DateTimeFormatter.ofPattern(
            "EEE, MMM d",
            Locale.getDefault()
        )
        val formattedDate = currDate.format(formatter)

        Text(
            text = if (themeViewModel.selectedLanguage.value == "English") {
                "$formattedDate at a glance"
            } else {
                "Jour de $formattedDate"
            }
            ,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun travelEventItem(
    context: Context,
    timeText: String,
    nameText: String,
    descriptionText: String?,
    imageResId: Int?,
    themeViewModel: SettingsViewModel,
    itinerary: Itinerary?,
    travelEvent: TravelEvent,
    itineraryViewModel: ItineraryViewModel,
    navController: NavController,
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedNewDate by remember { mutableStateOf("") }
    var newDescriptionText by remember { mutableStateOf("") }
    var newPriceText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Time-Time text
        Text(
            text = timeText,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        eventBox(nameText, descriptionText, imageResId, themeViewModel, onClick = { showDialog = true })
        if (showDialog) {
            itineraryViewModel.itineraryDuration(itinerary?.start, itinerary?.end)

            AlertDialog(
                modifier = Modifier.fillMaxSize(),
                onDismissRequest = { showDialog = false },
                title = { Text(text = if (themeViewModel.selectedLanguage.value == "English") {
                    "Edit your travel event"
                } else {
                    "Modifiez votre événement de voyage"
                }, fontSize = 24.sp
                ) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                            "Edit your event description:"
                        } else {
                            "Modifiez la description de votre événement :"
                        }
                        )

                        BasicTextField(
                            value = newDescriptionText,
                            onValueChange = { newDescriptionText = it },
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                                .height(60.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) { innerTextField ->
                            if (newDescriptionText.isEmpty()) {
                                Text(
                                    text = travelEvent.descriptionText ?: "Enter description",
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                            "Move event to"
                        } else {
                            "Déplacer l'événement vers"
                        })
                        dateSelectorFromItineraryDates(
                            itinerary = itinerary,
                            itineraryViewModel = itineraryViewModel,
                            themeViewModel = themeViewModel,
                            onDateSelected = { date -> selectedNewDate = date },
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
                            "Add a price to the event (CAD)"
                        } else {
                            "Ajouter un prix à l'événement (CAD)"
                        })

                        BasicTextField(
                            value = newPriceText,
                            onValueChange = { newPriceText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                                .height(60.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) { innerTextField ->
                            if (newPriceText.isEmpty()) {
                                Text(
                                    text = if (themeViewModel.selectedLanguage.value == "English") {
                                        "Enter price"
                                    } else {
                                        "Entrez le prix"
                                    },
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    if (themeViewModel.selectedTheme.value == "Classic") {
                                        0xFF00487C
                                    } else {
                                        0xFF4F345A
                                    }
                                )
                            ),
                            onClick = { showDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (themeViewModel.selectedLanguage.value == "English") {
                                "Close"
                            } else {
                                "Fermez"
                            })
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    if (themeViewModel.selectedTheme.value == "Classic") {
                                        0xFF00487C
                                    } else {
                                        0xFF4F345A
                                    }
                                )
                            ),
                            onClick = {
                                try {
                                    if (
                                        newPriceText == "" &&
                                        newDescriptionText == "" &&
                                        selectedNewDate == ""
                                    ) {
                                        // They didn't edit anything
                                        Toast.makeText(context, "You didn't change anything!", Toast.LENGTH_LONG).show()
                                    } else {
                                        val price = newPriceText.toDoubleOrNull() ?: 0.0 // get price input
                                        itineraryViewModel.updateTravelEventDate(travelEvent.id, selectedNewDate)
                                        if (newDescriptionText != travelEvent.descriptionText) {
                                            itineraryViewModel.updateTravelEventDescription(
                                                travelEvent.id,
                                                newDescriptionText
                                            )
                                        }
                                        itineraryViewModel.updateTravelEventPrice(
                                            travelEvent.id,
                                            price
                                        ) // Save price to event
                                        navController.navigate("itinerary")
                                        Toast.makeText(
                                            context,
                                            if (themeViewModel.selectedLanguage.value == "English") {
                                                "Success! Your event has been updated"
                                            } else {
                                                "Succès! Votre événement a été mis à jour"
                                            },
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (themeViewModel.selectedLanguage.value == "English") {
                                "Confirm"
                            } else {
                                "Confirmer"
                            })
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun dateSelectorFromItineraryDates(
    itinerary: Itinerary?,
    itineraryViewModel: ItineraryViewModel,
    themeViewModel: SettingsViewModel,
    onDateSelected: (String) -> Unit,
) {
    val numDays by itineraryViewModel.numDays.observeAsState()
    var selectedNewDate by remember { mutableStateOf<String?>(null) }

    if (numDays != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (day in 0 .. numDays!!+1) {
                val currDate = LocalDate.parse(itinerary?.start)
                    .plusDays(day.toLong())
                val displayDate = currDate
                    .format(DateTimeFormatter.ofPattern("EEE MMM d"))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (selectedNewDate == currDate.toString()) {
                                if (themeViewModel.selectedTheme.value == "Classic") {
                                    Color(0xFF00487c)
                                } else {
                                    Color(0xFF4F345A)
                                }
                            } else {
                                Color(0xFFF0F0F0)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            selectedNewDate = currDate.toString()
                            onDateSelected(selectedNewDate!!)
                        }
                        .padding(8.dp)
                ) {
                    Text(
                        text = displayDate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedNewDate == currDate.toString()) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun descriptionField(
    onDescriptionSelected: (String) -> Unit, themeViewModel: SettingsViewModel
) {
    fieldTitle(if (themeViewModel.selectedLanguage.value == "English") {
        "Enter a description for your event:"
    } else {
        "Entrez une description pour votre événement :"
    }
    )
    inputField(onInputChanged = onDescriptionSelected)
}

@Composable
fun fieldTitle(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun inputField(
    onInputChanged: (String) -> Unit,
) {
    var valueInput by remember { mutableStateOf("") }
    BasicTextField(
        value = valueInput,
        onValueChange = {
            valueInput = it
            onInputChanged(valueInput)
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            textAlign = TextAlign.Start
        ),
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(4.dp))
            .height(40.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp)
    )
}

fun timeFormatter(timeString: String): String {
    // Add the colon in the timezone offset if missing
    val fixedTimeString = if (timeString.endsWith("+00")) {
        timeString.replace("+00", "+00:00") // Add the required colon
    } else {
        timeString
    }

    val parsedTime = OffsetTime.parse(fixedTimeString, DateTimeFormatter.ISO_OFFSET_TIME)
    return parsedTime.format(DateTimeFormatter.ofPattern("h:mm a"))
}