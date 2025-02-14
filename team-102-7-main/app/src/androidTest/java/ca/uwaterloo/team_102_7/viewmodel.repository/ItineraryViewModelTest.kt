package ca.uwaterloo.team_102_7.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uwaterloo.team_102_7.data.domain.Itinerary
import ca.uwaterloo.team_102_7.data.domain.TravelEvent
import ca.uwaterloo.team_102_7.data.domain.TravelType
import ca.uwaterloo.team_102_7.data.repository.ItineraryRepository
import ca.uwaterloo.team_102_7.ui.viewmodel.ItineraryViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ItineraryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var itineraryRepository: ItineraryRepository

    private lateinit var itineraryViewModel: ItineraryViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        itineraryViewModel = ItineraryViewModel(context, itineraryRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getCurrentItineraries_updatesCurrentItinerariesList() = runTest {
        // Arrange
        val userId = 1
        val currentItineraries = listOf(
            Itinerary(
                id = 1,
                creator = userId,
                friends = listOf(),
                title = "Current Trip 1",
                start = LocalDate.now().toString(),
                end = LocalDate.now().plusDays(5).toString(),
                location = "Test Location 1",
                lat = 43.4723,
                long = -80.5449,
                travelEvents = listOf()
            ),
            Itinerary(
                id = 2,
                creator = userId,
                friends = listOf(),
                title = "Current Trip 2",
                start = LocalDate.now().toString(),
                end = LocalDate.now().plusDays(3).toString(),
                location = "Test Location 2",
                lat = 43.4723,
                long = -80.5449,
                travelEvents = listOf()
            )
        )

        coEvery { itineraryRepository.getCurrentItineraries(userId) } returns currentItineraries

        // Act
        itineraryViewModel.getCurrentItineraries(userId)

        // Assert
        val result = itineraryViewModel.currentItineraries.value
        assertEquals(currentItineraries, result)
    }

    @Test
    fun getPastItineraries_updatesPastItinerariesList() = runTest {
        // Arrange
        val userId = 1
        val pastItineraries = listOf(
            Itinerary(
                id = 3,
                creator = userId,
                friends = listOf(),
                title = "Past Trip 1",
                start = LocalDate.now().minusDays(10).toString(),
                end = LocalDate.now().minusDays(5).toString(),
                location = "Test Location 3",
                lat = 43.4723,
                long = -80.5449,
                travelEvents = listOf()
            )
        )

        coEvery { itineraryRepository.getPastItineraries(userId) } returns pastItineraries

        // Act
        itineraryViewModel.getPastItineraries(userId)

        // Assert
        val result = itineraryViewModel.pastItineraries.value
        assertEquals(pastItineraries, result)
    }


    @Test
    fun getItinerary_updatesItineraryAndPrices() = runTest {
        // Arrange
        val itineraryId = 1
        val itinerary = Itinerary(
            id = itineraryId,
            creator = 1,
            friends = listOf(),
            title = "Test Trip",
            start = "2024-01-01",
            end = "2024-01-05",
            location = "Test Location",
            lat = 43.4723,
            long = -80.5449,
            travelEvents = listOf()
        )

        val travelEvents = listOf(
            TravelEvent(
                id = 1,
                nameText = "Park Event",
                travelType = TravelType.PARK,
                date = "2024-01-01",
                startTime = "10:00:00+00",
                endTime = "12:00:00+00",
                price = 10.0,
                itineraryId = itineraryId,
                createdByUserId = 1,
                latitude = 43.4723,
                longitude = -80.5449,
                travelTime = "10.0"
            ),
            TravelEvent(
                id = 2,
                nameText = "Restaurant Event",
                travelType = TravelType.RESTAURANT,
                date = "2024-01-01",
                startTime = "13:00:00+00",
                endTime = "14:00:00+00",
                price = 20.0,
                itineraryId = itineraryId,
                createdByUserId = 1,
                latitude = 43.4723,
                longitude = -80.5449,
                travelTime = "10.0"
            )
        )

        coEvery { itineraryRepository.getItinerary(itineraryId) } returns itinerary
        coEvery { itineraryRepository.getDetailedTravelEvents(itineraryId) } returns travelEvents

        // Act
        itineraryViewModel.getItinerary(itineraryId)

        // Assert
        val resultItinerary = itineraryViewModel.itinerary.value
        val parkTotalPrice = itineraryViewModel.parkTotalPrice.value
        val restaurantTotalPrice = itineraryViewModel.restaurantTotalPrice.value
        val sportsTotalPrice = itineraryViewModel.sportsTotalPrice.value

        assertEquals(itinerary, resultItinerary)
        assertEquals(10.0, parkTotalPrice)
        assertEquals(20.0, restaurantTotalPrice)
        assertEquals(0.0, sportsTotalPrice)
    }

    @Test
    fun addCustomTravelEvent_savesEventToRepository() = runTest {
        // Arrange
        val name = "Custom Event"
        val type = TravelType.PARK
        val date = "2024-01-01"
        val startTime = "10:00:00+00"
        val endTime = "12:00:00+00"
        val travelTime = "PT30M"
        val price = "10.0"
        val lat = 43.4723
        val long = -80.5449
        val description = "Test Description"
        val creator = 1
        val itineraryId = 1

        // Act
        itineraryViewModel.addCustomTravelEvent(
            name, type, date, startTime, endTime, travelTime,
            price, lat, long, description, creator, itineraryId
        )

        // Assert
        coVerify {
            itineraryRepository.saveTravelEventAndAddToItinerary(match {
                it.nameText == name &&
                        it.travelType == type &&
                        it.date == date &&
                        it.startTime == startTime &&
                        it.endTime == endTime &&
                        it.travelTime == travelTime &&
                        it.price == price.toDouble() &&
                        it.latitude == lat &&
                        it.longitude == long &&
                        it.descriptionText == description &&
                        it.createdByUserId == creator &&
                        it.itineraryId == itineraryId
            })
        }
    }

    @Test
    fun updateTravelEventDate_updatesDateInRepository() = runTest {
        // Arrange
        val eventId = 1
        val newDate = "2024-02-01"

        // Act
        itineraryViewModel.updateTravelEventDate(eventId, newDate)

        // Assert
        coVerify { itineraryRepository.updateTravelEventDate(eventId, newDate) }
    }

    @Test
    fun updateTravelEventPrice_updatesPriceInRepository() = runTest {
        // Arrange
        val eventId = 1
        val newPrice = 25.0

        // Act
        itineraryViewModel.updateTravelEventPrice(eventId, newPrice)

        // Assert
        coVerify { itineraryRepository.updateTravelEventPrice(eventId, newPrice) }
    }

    @Test
    fun updateTravelEventDescription_updatesDescriptionInRepository() = runTest {
        // Arrange
        val eventId = 1
        val newDescription = "Updated description"

        // Act
        itineraryViewModel.updateTravelEventDescription(eventId, newDescription)

        // Assert
        coVerify { itineraryRepository.updateTravelEventDescription(eventId, newDescription) }
    }
}
