package ca.uwaterloo.team_102_7.ui.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.team_102_7.data.domain.Itinerary
import ca.uwaterloo.team_102_7.data.domain.TravelEvent
import ca.uwaterloo.team_102_7.data.domain.TravelType
import ca.uwaterloo.team_102_7.data.repository.ItineraryRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*
import android.util.Log
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import android.Manifest


class ItineraryViewModel(
    private val context: Context,
    private val itineraryRepository: ItineraryRepository,
): ViewModel() {
    private val _currentItineraries = MutableLiveData<List<Itinerary>>()
    val currentItineraries: LiveData<List<Itinerary>> get() = _currentItineraries

    private val _pastItineraries = MutableLiveData<List<Itinerary>>()
    val pastItineraries: LiveData<List<Itinerary>> get() = _pastItineraries

    private val _itineraryId = MutableLiveData<Int>()
    val itineraryId: LiveData<Int> get() = _itineraryId
    private val _itinerary = MutableLiveData<Itinerary>()
    val itinerary: LiveData<Itinerary> get() = _itinerary

    private val _numDays = MutableLiveData<Int>()
    val numDays: LiveData<Int> get() = _numDays

    private val _travelEvents = MutableLiveData<List<TravelEvent>>()
    val travelEvents: LiveData<List<TravelEvent>> get() = _travelEvents

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate
    private val _selectedTravelEvents = MutableLiveData<List<TravelEvent>>()
    val selectedTravelEvents: LiveData<List<TravelEvent>> get() = _selectedTravelEvents

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> get() = _userId

    private val _parkTotalPrice = MutableLiveData<Double>()
    val parkTotalPrice: LiveData<Double> get() = _parkTotalPrice

    private val _restaurantTotalPrice = MutableLiveData<Double>()
    val restaurantTotalPrice: LiveData<Double> get() = _restaurantTotalPrice

    private val _sportsTotalPrice = MutableLiveData<Double>()
    val sportsTotalPrice: LiveData<Double> get() = _sportsTotalPrice

    fun setUserId(id: Int?) {
        viewModelScope.launch {
            if (id != null) {
                _userId.value = id
            }
        }
    }

    fun setItineraryId(id: Int?) {
        viewModelScope.launch {
            if (id != null) {
                _itineraryId.value = id
            }
        }
    }

    fun setSelectedDate(date: String) {
        viewModelScope.launch {
            _selectedDate.value = date
        }
    }

    fun setSelectedTravelEvents(travelEvents: List<TravelEvent>) {
        viewModelScope.launch {
            val sortedTravelEvents = travelEvents.sortedBy {
                val fixedStartTime = if (it.startTime.endsWith("+00")) {
                    it.startTime.replace("+00", "+00:00") // Fix the timezone offset
                } else {
                    it.startTime
                }
                LocalTime.parse(fixedStartTime, DateTimeFormatter.ISO_OFFSET_TIME)
            }
            _selectedTravelEvents.value = sortedTravelEvents
        }
    }

    fun getItinerary(id: Int?) {
        viewModelScope.launch {
            if (id != null) {
                val res = itineraryRepository.getItinerary(id)
                if (res != null) {
                    _itinerary.value = res

                    val detailedEvents = itineraryRepository.getDetailedTravelEvents(id)

                    val priceSumsByType = detailedEvents.groupBy { it.travelType }
                        .mapValues { (_, events) -> events.sumOf { it.price ?: 0.0 } }

                    _parkTotalPrice.value = priceSumsByType[TravelType.PARK] ?: 0.0
                    _restaurantTotalPrice.value = priceSumsByType[TravelType.RESTAURANT] ?: 0.0
                    _sportsTotalPrice.value = priceSumsByType[TravelType.SPORTS] ?: 0.0
                }
            }
        }
    }

    fun createNewItinerary(userId: Int, itineraryName: String, startDate: String, endDate: String, location: String, lat: Double, long: Double) {
        viewModelScope.launch {
            val startLocalDate = LocalDate.parse(startDate)
            val endLocalDate = LocalDate.parse(endDate)
            val res = itineraryRepository.createItinerary(userId, itineraryName, startLocalDate, endLocalDate, location, lat, long)
            if (res?.id != null) {
                _itineraryId.value = res.id
            }
        }
    }

    fun itineraryDuration(startDate: String?, endDate: String?) {
        viewModelScope.launch {
            if (startDate != null && endDate != null) {
                val start = LocalDate.parse(startDate)
                val end = LocalDate.parse(endDate)
                _numDays.value = ChronoUnit.DAYS.between(start, end).toInt()
            }
        }
    }

    fun getTravelEvents(itinerary: Itinerary?) {
        viewModelScope.launch {
            if (itinerary != null) {
                val res = itineraryRepository.getTravelEvents(itinerary.id!!)
                _travelEvents.value = res
            }
        }
    }

    fun getTravelEventsByDate(travelEvents: List<TravelEvent>?, date: String): List<TravelEvent> {
        if (travelEvents == null) return emptyList()
        return travelEvents.filter { it.date == date }
    }

    fun getCurrentItineraries(userId: Int?) {
        viewModelScope.launch {
            if (userId != null) {
                val res = itineraryRepository.getCurrentItineraries(userId)
                _currentItineraries.value = res
            }
        }
    }

    fun getPastItineraries(userId: Int?) {
        viewModelScope.launch {
            if (userId != null) {
                val res = itineraryRepository.getPastItineraries(userId)
                _pastItineraries.value = res
            }
        }
    }

    fun fetchNearbyRestaurants(
        itinerary: Itinerary?,
        onSuccess: (List<TravelEvent>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (itinerary != null) {
                    val placesClient: PlacesClient = Places.createClient(context)
                    val combinedResults = mutableListOf<TravelEvent>()
                    val uniqueNames = mutableSetOf<String>()

                    // Change if other keywords are needed
                    val queries = listOf("restaurant", "cafe", "diner", "bistro", "food court", "coffee shop")

                    for (query in queries) {

                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(query)
                            .setLocationBias(
                                com.google.android.libraries.places.api.model.RectangularBounds.newInstance(
                                    LatLng(itinerary.lat - 0.0563, itinerary.long - 0.0563), // southwest corner
                                    LatLng(itinerary.lat + 0.0563, itinerary.long + 0.0563)  // northeast corner
                                )
                            )
                            .build()


                        val response = placesClient.findAutocompletePredictions(request).await()

                        // Fetch detailed place info including lat/long
                        for (prediction in response.autocompletePredictions) {
                            val placeName = prediction.getPrimaryText(null).toString()

                            // preprocess so we don't get multiples of the same event
                            val normalizedName = placeName.lowercase().removeSuffix("s").trim()
                            if (!uniqueNames.contains(normalizedName)) {
                                uniqueNames.add(normalizedName) // Track unique name

                                // Fetch detailed place information to get lat/long
                                val placeRequest = FetchPlaceRequest.builder(
                                    prediction.placeId,
                                    listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
                                ).build()
                                val placeResponse = placesClient.fetchPlace(placeRequest).await()
                                val place = placeResponse.place

                                // Create TravelEvent
                                val travelEvent = TravelEvent(
                                    nameText = place.name ?: placeName,
                                    travelType = TravelType.RESTAURANT,
                                    date = _selectedDate.value ?: itinerary.start,
                                    startTime = LocalTime.now().toString(),
                                    endTime = LocalTime.now().plusHours(2).toString(),
                                    travelTime = Duration.ofMinutes(15).toString(),
                                    latitude = place.latLng?.latitude ?: itinerary.lat, // Use actual latitude if available
                                    longitude = place.latLng?.longitude ?: itinerary.long, // Use actual longitude if available
                                    createdByUserId = itinerary.creator,
                                    price = 0.0,
                                    descriptionText = place.address ?: "",
                                    itineraryId = itinerary.id!!
                                )

                                combinedResults.add(travelEvent)
                                // Log the fetched item details with lat/long
                                Log.d("FetchNearbyRestaurants", "Fetched place: ${travelEvent.nameText}, Latitude: ${travelEvent.latitude}, Longitude: ${travelEvent.longitude}")
                            }
                        }
                    }
                    onSuccess(combinedResults)
                }
            } catch (e: Exception) {

                Log.e("FetchNearbyRestaurants", "Error fetching restaurants and cafes: ${e.localizedMessage}")

                onError(e.localizedMessage ?: "Error fetching restaurants and cafes")
            }
        }
    }


    fun fetchNearbyParks(
        itinerary: Itinerary?,
        onSuccess: (List<TravelEvent>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (itinerary != null) {
                    val placesClient: PlacesClient = Places.createClient(context)
                    val combinedResults = mutableListOf<TravelEvent>()
                    val uniqueNames = mutableSetOf<String>()

                    // Change if other keywords are needed
                    val queries = listOf("nature reserve", "hiking trail", "waterfall", "scenic view", "recreation area")

                    for (query in queries) {

                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(query)
                            .setLocationBias(
                                com.google.android.libraries.places.api.model.RectangularBounds.newInstance(
                                    LatLng(itinerary.lat - 0.0563, itinerary.long - 0.0563), // southwest corner
                                    LatLng(itinerary.lat + 0.0563, itinerary.long + 0.0563)  // northeast corner
                                )
                            )
                            .build()

                        // Fetch predictions for each query type
                        val response = placesClient.findAutocompletePredictions(request).await()

                        // Fetch detailed place info including lat/long
                        for (prediction in response.autocompletePredictions) {
                            val placeName = prediction.getPrimaryText(null).toString()


                            val normalizedName = placeName.lowercase().removeSuffix("s").trim()
                            if (!uniqueNames.contains(normalizedName)) {
                                uniqueNames.add(normalizedName) // Track unique name

                                // Fetch detailed place information to get lat/long
                                val placeRequest = FetchPlaceRequest.builder(prediction.placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)).build()
                                val placeResponse = placesClient.fetchPlace(placeRequest).await()
                                val place = placeResponse.place

                                // Create TravelEvent with actual latitude and longitude
                                val travelEvent = TravelEvent(
                                    nameText = place.name ?: placeName,
                                    travelType = TravelType.PARK,
                                    date = _selectedDate.value ?: itinerary.start,
                                    startTime = LocalTime.now().toString(),
                                    endTime = LocalTime.now().plusHours(2).toString(),
                                    travelTime = Duration.ofMinutes(15).toString(),
                                    latitude = place.latLng?.latitude ?: itinerary.lat, // Use actual latitude if available
                                    longitude = place.latLng?.longitude ?: itinerary.long, // Use actual longitude if available
                                    createdByUserId = itinerary.creator,
                                    price = 0.0,
                                    descriptionText = place.address ?: "",
                                    itineraryId = itinerary.id!!
                                )

                                combinedResults.add(travelEvent)
                                Log.d("FetchNearbyNatureSpots", "Fetched place: ${travelEvent.nameText}, Latitude: ${travelEvent.latitude}, Longitude: ${travelEvent.longitude}")
                            }
                        }
                    }

                    onSuccess(combinedResults)
                }
            } catch (e: Exception) {
                Log.e("FetchNearbyNatureSpots", "Error fetching nature spots: ${e.localizedMessage}")

                onError(e.localizedMessage ?: "Error fetching nature spots")
            }
        }
    }

    fun fetchNearbySportsVenues(
        itinerary: Itinerary?,
        onSuccess: (List<TravelEvent>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (itinerary != null) {
                    val placesClient: PlacesClient = Places.createClient(context)
                    val combinedResults = mutableListOf<TravelEvent>()
                    val uniqueNames = mutableSetOf<String>()

                    // Change if other keywords are needed
                    val queries = listOf("stadium", "arena", "sports complex", "gym", "fitness center", "basketball court", "tennis court", "soccer field")

                    for (query in queries) {

                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(query)
                            .setLocationBias(
                                com.google.android.libraries.places.api.model.RectangularBounds.newInstance(
                                    LatLng(itinerary.lat - 0.0563, itinerary.long - 0.0563), // southwest corner
                                    LatLng(itinerary.lat + 0.0563, itinerary.long + 0.0563)  // northeast corner
                                )
                            )
                            .build()


                        val response = placesClient.findAutocompletePredictions(request).await()

                        // Fetch detailed place info including lat/long
                        for (prediction in response.autocompletePredictions) {
                            val placeName = prediction.getPrimaryText(null).toString()


                            val normalizedName = placeName.lowercase().removeSuffix("s").trim()
                            if (!uniqueNames.contains(normalizedName)) {
                                uniqueNames.add(normalizedName) // Track unique name

                                // Fetch detailed place information to get lat/long
                                val placeRequest = FetchPlaceRequest.builder(
                                    prediction.placeId,
                                    listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
                                ).build()
                                val placeResponse = placesClient.fetchPlace(placeRequest).await()
                                val place = placeResponse.place

                                // Create TravelEvent
                                val travelEvent = TravelEvent(
                                    nameText = place.name ?: placeName,
                                    travelType = TravelType.SPORTS,
                                    date = _selectedDate.value ?: itinerary.start,
                                    startTime = LocalTime.now().toString(),
                                    endTime = LocalTime.now().plusHours(2).toString(),
                                    travelTime = Duration.ofMinutes(15).toString(),
                                    latitude = place.latLng?.latitude ?: itinerary.lat, // Use actual latitude if available
                                    longitude = place.latLng?.longitude ?: itinerary.long, // Use actual longitude if available
                                    createdByUserId = itinerary.creator,
                                    price = 0.0,
                                    descriptionText = place.address ?: "",
                                    itineraryId = itinerary.id!!
                                )

                                combinedResults.add(travelEvent)
                                Log.d("FetchNearbySportsVenues", "Fetched place: ${travelEvent.nameText}, Latitude: ${travelEvent.latitude}, Longitude: ${travelEvent.longitude}")
                            }
                        }
                    }
                    onSuccess(combinedResults)
                }
            } catch (e: Exception) {
                Log.e("FetchNearbySportsVenues", "Error fetching sports venues: ${e.localizedMessage}")
                onError(e.localizedMessage ?: "Error fetching sports venues")
            }
        }
    }



    fun addSuggestedTravelEvent(travelEvent: TravelEvent, description: String, language: String) {
        viewModelScope.launch {
            travelEvent.descriptionText = description
            itineraryRepository.saveTravelEventAndAddToItinerary(travelEvent)

            val currDate = LocalDate.parse(travelEvent.date)
            val formatter = DateTimeFormatter.ofPattern(
                "EEE, MMM d",
                Locale.getDefault()
            )
            val formattedDate = currDate.format(formatter)

            val successToast = if (language == "English") {
                "Success! Added event to $formattedDate"
            } else {
                "Succès! Événement ajouté au $formattedDate"
            }

            Toast.makeText(context, successToast, Toast.LENGTH_LONG).show()
        }
    }

    fun addCustomTravelEvent(
        name: String,
        travelType: TravelType,
        date: String,
        startTime: String,
        endTime: String,
        travelTime: String,
        price: String,
        latitude: Double,
        longitude: Double,
        description: String,
        creator: Int?,
        itineraryId: Int?,
    ) {
        viewModelScope.launch {
            if (creator != null && itineraryId != null) {
                val travelEvent = TravelEvent(
                    nameText = name,
                    travelType = travelType,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    travelTime = travelTime,
                    price = price.toDouble(), // 0.0 shows up as NULL in Supabase
                    latitude = latitude,
                    longitude = longitude,
                    descriptionText = description,
                    createdByUserId = creator,
                    itineraryId = itineraryId,
                )
                itineraryRepository.saveTravelEventAndAddToItinerary(travelEvent)
            }
        }
    }

    fun updateTravelEventDate(id: Int?, date: String?) {
        viewModelScope.launch {
            if (date != null && id != null) {
                itineraryRepository.updateTravelEventDate(id, date)
            }
        }
    }

    fun updateTravelEventPrice(id: Int?, price: Double?) {
        viewModelScope.launch {
            if (price != null && id != null) {
                itineraryRepository.updateTravelEventPrice(id, price)
            }
        }
    }

    fun addFriend(context: Context, itineraryId: Int?, friendToAdd: String, language: String) {
        viewModelScope.launch {
            if (itineraryId != null) {
                try {
                    val friendId = friendToAdd.toInt()
                    itineraryRepository.addFriend(itineraryId, friendId)
                    val successToast = if (language == "English") {
                        "Success! Your friend has been added"
                    } else {
                        "Succès! Votre ami a été ajouté"
                    }

                    Toast.makeText(context, successToast, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun updateTravelEventDescription(id: Int?, description: String) {
        viewModelScope.launch {
            if (id != null) {
                itineraryRepository.updateTravelEventDescription(id, description)
            }
        }
    }
}