package ca.uwaterloo.team_102_7.ui.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import ca.uwaterloo.team_102_7.ui.components.eventBox
import ca.uwaterloo.team_102_7.ui.viewmodel.ItineraryViewModel
import android.location.Location
import android.util.Log
import com.google.maps.android.compose.*
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import ca.uwaterloo.team_102_7.ui.viewmodel.PersonalChangesViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import ca.uwaterloo.team_102_7.data.domain.Itinerary
import ca.uwaterloo.team_102_7.ui.components.BackButton
import ca.uwaterloo.team_102_7.ui.components.HE2ESButton
import ca.uwaterloo.team_102_7.ui.components.plusButton
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import kotlinx.coroutines.tasks.await
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.core.content.res.ResourcesCompat
import ca.uwaterloo.team_102_7.R
import ca.uwaterloo.team_102_7.data.domain.TravelEvent
import ca.uwaterloo.team_102_7.data.domain.TravelType
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.PopupProperties


@Composable
fun itineraryScreen(
    context: Context,
    itineraryViewModel: ItineraryViewModel,
    navController: NavController,
    themeViewModel: SettingsViewModel
) {
    val itinerary by itineraryViewModel.itinerary.observeAsState()
    val itineraryId by itineraryViewModel.itineraryId.observeAsState()
    var showDialog by remember { mutableStateOf(false) }  // Add dialog state

    LaunchedEffect(itineraryId) {
        itineraryId?.let {
            itineraryViewModel.getItinerary(itineraryId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                mapScreen(itinerary, itineraryViewModel, themeViewModel)
            }

            HorizontalDivider(
                thickness = 4.dp,
                color = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                    0xFF00487C
                } else {
                    0xFF4F345A
                })
            )

            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
            ) {
                itineraryBox(context, itineraryViewModel, itinerary, themeViewModel, navController)
            }
        }

        // Navigation buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("home") },
                containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") 0xFF00487C else 0xFF4F345A),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            }

            FloatingActionButton(
                onClick = { navController.navigate("settings") },
                containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") 0xFF00487C else 0xFF4F345A),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }

            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") 0xFF00487C else 0xFF4F345A),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.invite),
                    contentDescription = "Add Friend",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Add dialog
        if (showDialog) {
            friendFormDialog(
                context = context,
                onDismiss = { showDialog = false },
                itinerary = itinerary,
                itineraryViewModel = itineraryViewModel,
                themeViewModel = themeViewModel,
                navController = navController
            )
        }
    }
}

@Composable
fun mapScreen(
    itinerary: Itinerary?,
    itineraryViewModel: ItineraryViewModel,
    themeViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        MapsInitializer.initialize(context)
    }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val travelEvents by itineraryViewModel.travelEvents.observeAsState(initial = emptyList())

    var locationPermissionGranted by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }


    // Create custom marker icons for different travel types
    val markerIcons = remember {
        mapOf(
            TravelType.RESTAURANT to BitmapDescriptorFactory.fromBitmap(
                toBitmap(context, R.drawable.restaurant, 100, 100) // Width and height in pixels
            ),
            TravelType.PARK to BitmapDescriptorFactory.fromBitmap(
                toBitmap(context, R.drawable.park, 100, 100)
            ),
            TravelType.SPORTS to BitmapDescriptorFactory.fromBitmap(
                toBitmap(context, R.drawable.sports, 100, 100)
            ),
            TravelType.HOTEL to BitmapDescriptorFactory.fromBitmap(
                toBitmap(context, R.drawable.hotel, 100, 100)
            ),
            TravelType.FLIGHT to BitmapDescriptorFactory.fromBitmap(
                toBitmap(context, R.drawable.flight, 100, 100)
            )
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            locationPermissionGranted = isGranted
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let { loc ->
                        userLocation = LatLng(itinerary?.lat ?: loc.latitude, itinerary?.long ?: loc.longitude)
                    }
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context.findActivity(themeViewModel),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    showRationale = true
                } else {
                    Toast.makeText(
                        context,
                        if (themeViewModel.selectedLanguage.value == "English") {
                            "Please enable location permission in settings"
                        } else {
                            "Veuillez activer la permission de localisation dans les paramètres"
                        },
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { loc ->
                    userLocation = LatLng(itinerary?.lat ?: loc.latitude, itinerary?.long ?: loc.longitude)
                }
            }
        }
    }

    // Fetch travel events when itinerary changes
    LaunchedEffect(itinerary) {
        itineraryViewModel.getTravelEvents(itinerary)
    }

    if (showRationale) {
        PermissionRationaleDialog(
            onDismiss = { showRationale = false },
            onConfirm = {
                showRationale = false
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            themeViewModel
        )
    }

    val defaultLat = 43.466667
    val defaultLong = -80.516670
    val initialPosition = LatLng(defaultLat, defaultLong)
    userLocation = LatLng(itinerary?.lat ?: defaultLat, itinerary?.long ?: defaultLong)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: initialPosition,
            15f
        )
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            coroutineScope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(location, 15f),
                    1000
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false
            )
        ) {
            // Use travelEvents from ViewModel
            travelEvents.forEach { event ->
                val eventLocation = LatLng(
                    event.latitude,
                    event.longitude
                )
                // Get the appropriate marker icon based on travel type
                val markerIcon = markerIcons[event.travelType] ?: BitmapDescriptorFactory.defaultMarker()

                Marker(
                    state = MarkerState(position = eventLocation),
                    title = event.nameText,
                    snippet = event.descriptionText ?: "",
                    // Set the marker icon based on travel type
                    icon = markerIcon
                )
            }
        }

        IconButton(
            onClick = {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let { loc ->
                        userLocation = LatLng(loc.latitude, loc.longitude)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f),
                                1000
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .background(Color.White, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = if (themeViewModel.selectedLanguage.value == "English") {
                    "Center to my location"
                } else {
                    "Centrer sur ma position"
                }

            )
        }
    }
}





// Extension function to get Activity from Context
fun Context.findActivity(themeViewModel: SettingsViewModel): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = (context as ContextWrapper).baseContext
    }
    throw IllegalStateException(
        if (themeViewModel.selectedLanguage.value == "English") {
        "Permissions should be called in the context of an Activity"
    } else {
        "Les permissions doivent être demandées dans le contexte d'une Activité"
    }
    )
}

@Composable
fun PermissionRationaleDialog(onDismiss: () -> Unit, onConfirm: () -> Unit, themeViewModel: SettingsViewModel) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text =
                    if (themeViewModel.selectedLanguage.value == "English") {
                        "Location Permission Needed"
                    } else {
                        "Permission de Localisation Nécessaire"
                    })
                },
        text = { Text(if (themeViewModel.selectedLanguage.value == "English") {
            "This app requires access to your location to show your current position on the map."
        } else {
            "Cette application nécessite l'accès à votre localisation pour afficher votre position actuelle sur la carte."
        }
        ) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(if (themeViewModel.selectedLanguage.value == "English") {
                    "Grant Permission"
                } else {
                    "Accorder la Permission"
                }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (themeViewModel.selectedLanguage.value == "English") {
                    "Cancel"
                } else {
                    "Annuler"
                }
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesAutoCompleteSearchBar(
    themeViewModel: SettingsViewModel,
    onPlaceSelected: (Place) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val predictions = remember { mutableStateListOf<AutocompletePrediction>() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Create the Places client after initialization
    val placesClient = remember {
        Places.createClient(context)
    }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query

                if (query.length >= 3) {
                    // Fetch autocomplete predictions
                    coroutineScope.launch {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(query)
                            .build()

                        try {
                            val response = placesClient.findAutocompletePredictions(request).await()
                            predictions.clear()
                            predictions.addAll(response.autocompletePredictions)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    predictions.clear()
                }
            },
            placeholder = { Text(if (themeViewModel.selectedLanguage.value == "English") {
                "Search for a place"
            } else {
                "Rechercher un lieu"
            }) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(40.dp)),
            trailingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            shape = RoundedCornerShape(40.dp)
        )

        DropdownMenu(
            expanded = predictions.isNotEmpty(),
            onDismissRequest = { predictions.clear() },
            properties = PopupProperties(
                focusable = false // Prevent the DropdownMenu from stealing focus
            )
        ) {
            predictions.forEach { prediction ->
                DropdownMenuItem(
                    text = { Text(prediction.getFullText(null).toString()) },
                    onClick = {
                        // Get the place details
                        val placeId = prediction.placeId
                        val placeFields = listOf(
                            Place.Field.ID,
                            Place.Field.NAME,
                            Place.Field.LAT_LNG,
                            Place.Field.ADDRESS
                        )
                        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

                        coroutineScope.launch {
                            try {
                                val response = placesClient.fetchPlace(request).await()
                                val place = response.place
                                onPlaceSelected(place)
                                searchQuery = place.name ?: ""
                                predictions.clear()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )
            }
        }
    }
}



@Composable
fun PriceSection(
    parkPrice: Double,
    restaurantPrice: Double,
    sportsPrice: Double,
    settingsViewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = if (settingsViewModel.selectedLanguage.value == "English") {
                "Total Prices"
            } else {
                "Prix Totaux"
            }
            ,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val selectedCurrency = settingsViewModel.selectedCurrency.value

        val parksL = if (settingsViewModel.selectedLanguage.value == "English") {
            "Parks"
        } else {
            "Parcs"
        }

        Text(
            text = "$parksL: $selectedCurrency " +
                    settingsViewModel.convertCurrency("CAD", selectedCurrency, parkPrice)
                        .format(2),
            fontSize = 16.sp
        )

        Text(
            text = "Restaurants: $selectedCurrency $" +
                    settingsViewModel.convertCurrency("CAD", selectedCurrency, restaurantPrice)
                        .format(2),
            fontSize = 16.sp
        )
        Text(
            text = "Sports: $selectedCurrency $" +
                    settingsViewModel.convertCurrency("CAD", selectedCurrency, sportsPrice)
                        .format(2),
            fontSize = 16.sp
        )
    }
}

// Extension function to format doubles
fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun itineraryBox(
    context: Context,
    itineraryViewModel: ItineraryViewModel,
    itinerary: Itinerary?,
    themeViewModel: SettingsViewModel,
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val numDays by itineraryViewModel.numDays.observeAsState()
    val travelEvents by itineraryViewModel.travelEvents.observeAsState()
    val parkTotalPrice by itineraryViewModel.parkTotalPrice.observeAsState(0.0)
    val restaurantTotalPrice by itineraryViewModel.restaurantTotalPrice.observeAsState(0.0)
    val sportsTotalPrice by itineraryViewModel.sportsTotalPrice.observeAsState(0.0)

    // Fetch once at the start for all travel events by Itinerary
    // TODO: some reason this calls n times where n=numDays
    itineraryViewModel.getTravelEvents(itinerary)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add the PriceSection here
        PriceSection(
            parkPrice = parkTotalPrice ?: 0.0,
            restaurantPrice = restaurantTotalPrice ?: 0.0,
            sportsPrice = sportsTotalPrice ?: 0.0,
            settingsViewModel = themeViewModel
        )
        //Total Prices section
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//                .background(Color.White)
//                .padding(16.dp)
//        ) {
//            Text(
//                text = "Total Prices",
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//            Text(text = "Parks: $${parkTotalPrice}", fontSize = 16.sp)
//            Text(text = "Restaurants: $${restaurantTotalPrice}", fontSize = 16.sp)
//            Text(text = "Sports: $${sportsTotalPrice}", fontSize = 16.sp)
//                BackButton(themeViewModel, navController)
//        }

        // Lazy Load Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight - (screenHeight * 0.55f)) // This allows for space at the bottom
        ) {
            // Each Column
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f)
            ) {
                // Form section for Location and Dates
                item {
                    formSection(itinerary, themeViewModel)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                itineraryViewModel.itineraryDuration(itinerary?.start, itinerary?.end)
                if (numDays != null) {
                    // This would be called multiple times (i.e., call for Aug 14, Aug 15, etc.)
                    for (i in 0 .. numDays!!) {
                        val currDate = LocalDate.parse(itinerary?.start).plusDays(i.toLong()).toString()

                        val displayDate = LocalDate.parse(itinerary?.start)
                            .plusDays(i.toLong())
                            .format(DateTimeFormatter.ofPattern("EEE MMM d"))

                        item {
                            val travelEventsByDate = itineraryViewModel.getTravelEventsByDate(travelEvents, currDate)
                            eventBox(
                                displayDate,
                                if (themeViewModel.selectedLanguage.value == "English") {
                                    "There are ${travelEventsByDate.size} things planned"
                                } else {
                                    "Il y a ${travelEventsByDate.size} choses prévues"
                                },
                                null,
                                themeViewModel,
                                onClick = {
                                    // travelEvents already set in LiveData
                                    itineraryViewModel.setSelectedDate(currDate)
                                    itineraryViewModel.setSelectedTravelEvents(travelEventsByDate)
                                    navController.navigate("navigation")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun toBitmap(context: Context, resourceId: Int, width: Int, height: Int): Bitmap {
    val drawable = ResourcesCompat.getDrawable(context.resources, resourceId, null)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    drawable?.setBounds(0, 0, width, height)
    drawable?.draw(canvas)
    return bitmap
}

@Composable
fun formSection(itinerary: Itinerary?, themeViewModel: SettingsViewModel) {
    Column {
        // Location Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start // Ensures space between Text and TextField
        ) {
            Text(
                text = if (themeViewModel.selectedLanguage.value == "English") {
                    "Location: "
                } else {
                    "Emplacement: "
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold, // Make the label bold
                modifier = Modifier.align(Alignment.CenterVertically) // Left-align the label
            )

            Text(
                text = itinerary?.location ?: "WATERLOO", // Use a default value in case it's null
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // TODO: make the location changeable and that will update the DB as well
        }

        // Dates Row with label, two text fields, and "To" in between
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dates: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, // Make the label bold
                    modifier = Modifier.align(Alignment.CenterVertically) // Left-align the label
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between label and first field

                // TODO: Display calendar UI

                Text(
                    text = itinerary?.start ?: "",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically) // Left-align the label
                )

                Text(
                    text = if (themeViewModel.selectedLanguage.value == "English") {
                        "to"
                    } else {
                        "à"
                    },
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically) // Align "To" in the middle
                )

                Text(
                    text = itinerary?.end ?: "",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically) // Left-align the label
                )
            }

            // TODO: Display calendar UI
        }
    }
}

@Composable
fun friendFormDialog(
    context: Context,
    onDismiss: () -> Unit,
    itinerary: Itinerary?,
    itineraryViewModel: ItineraryViewModel,
    themeViewModel: SettingsViewModel,
    navController: NavController,
) {
    var friendId by remember { mutableStateOf("") }
    val userId by itineraryViewModel.userId.observeAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (themeViewModel.selectedLanguage.value == "English") {
            "Invite a Friend to Your Itinerary"
        } else {
            "Invitez Un Ami à Votre Itinéraire"
        }) },
        text = {
            Column {
                Text(
                    text = if (themeViewModel.selectedLanguage.value == "English") {
                        "Your ID to share with your friend: ${userId ?: "NO_ID"}"
                    } else {
                        "Votre identifiant à partager avec votre ami : ${userId ?: "NO_ID"}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                BasicTextField(
                    value = friendId,
                    onValueChange = { friendId = it },
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
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    onClick = onDismiss
                ) {
                    Text(if (themeViewModel.selectedLanguage.value == "English") {
                        "Close"
                    } else {
                        "Fermez"
                    })
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(if (themeViewModel.selectedTheme.value == "Classic") {
                        0xFF00487C
                    } else {
                        0xFF4F345A
                    })),
                    onClick = {
                        if (friendId.isNotEmpty()) {
                            itineraryViewModel.addFriend(
                                context,
                                itinerary?.id,
                                friendId,
                                themeViewModel.selectedLanguage.value
                            )
                            navController.navigate("itinerary")
                        } else {
                            Toast.makeText(context, "Please add a friend!", Toast.LENGTH_LONG).show()
                        }
                    }) {
                    Text(if (themeViewModel.selectedLanguage.value == "English") {
                        "Share"
                    } else {
                        "Partagez"
                    })
                }
            }
        }
    )
}