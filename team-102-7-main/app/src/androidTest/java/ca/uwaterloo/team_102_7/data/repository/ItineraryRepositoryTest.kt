package ca.uwaterloo.team_102_7.data.repository

import ca.uwaterloo.team_102_7.data.domain.Itinerary
import ca.uwaterloo.team_102_7.data.domain.TravelEvent
import ca.uwaterloo.team_102_7.data.domain.TravelType
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ItineraryRepositoryTest {

    @Test
    fun getItinerary() = runBlocking {
        val expected = Itinerary(
            id = 33,
            creator = 19,
            friends = emptyList(),
            title = "try",
            start = "2024-11-06",
            end = "2024-11-12",
            location = "Trysil Municipality",
            lat = 61.2373874,
            long = 12.2892361,
            travelEvents = emptyList(),
        )
        val actual = ItineraryRepository().getItinerary(33)
        assertEquals(expected, actual)
    }

    @Test
    fun createItinerary() {
        assertDoesNotThrow {
            runBlocking {
                ItineraryRepository().createItinerary(
                    creator = 19,
                    title = "itinerary test",
                    start = LocalDate.parse("2024-11-06", DateTimeFormatter.ISO_LOCAL_DATE),
                    end = LocalDate.parse("2024-11-12", DateTimeFormatter.ISO_LOCAL_DATE),
                    location = "Trysil Municipality",
                    lat = 61.2373874,
                    long = 12.2892361,
                )
            }
        }
    }

    @Test
    fun getTravelEvents() {
        val expected = emptyList<TravelEvent>()
        assertEquals(expected, runBlocking { ItineraryRepository().getTravelEvents(2) })
    }

    @Test
    fun saveTravelEventAndAddToItinerary() {
        val travelEvent = TravelEvent(
            nameText = "itinerary test",
            travelType = TravelType.PARK,
            date = "2024-11-25",
            startTime = "17:57:10.294+00",
            endTime = "19:57:10.294+00",
            travelTime = "15min",
            price = 10.0,
            latitude = 1.0,
            longitude = 1.0,
            descriptionText = "Unit test",
            createdByUserId = 19,
            itineraryId = 0, // Add to dummy, imaginary itinerary
        )
        assertDoesNotThrow {
            runBlocking {
                ItineraryRepository().saveTravelEventAndAddToItinerary(travelEvent)
            }
        }
    }

    @Test
    fun updateTravelEventDate() {
        assertDoesNotThrow {
            runBlocking {
                ItineraryRepository().updateTravelEventDate(89, LocalDate.now().toString())
            }
        }
    }

    @Test
    fun updateTravelEventPrice() {
        assertDoesNotThrow {
            runBlocking {
                ItineraryRepository().updateTravelEventPrice(89, 2.0)
            }
        }
    }

    @Test
    fun updateTravelEventDescription() {
        assertDoesNotThrow {
            runBlocking {
                ItineraryRepository().updateTravelEventDescription(89, "itinerary test")
            }
        }
    }
}