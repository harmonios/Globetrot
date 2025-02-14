package ca.uwaterloo.team_102_7.data.repository

import ca.uwaterloo.team_102_7.data.domain.Itinerary
import ca.uwaterloo.team_102_7.data.domain.TravelEvent
import java.time.LocalDate


class ItineraryRepository {
    suspend fun getItinerary(itineraryId: Int): Itinerary? {
        return Itinerary.getItinerary(itineraryId)
    }

    suspend fun getPastItineraries(userId: Int): List<Itinerary> {
        return Itinerary.getItineraries(userId, true)
    }

    suspend fun getCurrentItineraries(userId: Int): List<Itinerary> {
        return Itinerary.getItineraries(userId, false)
    }

    suspend fun createItinerary(
        creator: Int,
        title: String,
        start: LocalDate,
        end: LocalDate,
        location: String,
        lat: Double,
        long: Double,
    ): Itinerary? {
        return Itinerary.appendToDB(creator, title, start, end, location, lat, long)
    }

    suspend fun getTravelEvents(itineraryId: Int): List<TravelEvent> {
        return TravelEvent.getTravelEvents(itineraryId)
    }

    suspend fun saveTravelEventAndAddToItinerary(event: TravelEvent) {
        // add event first
        val travelEvent = TravelEvent.appendToDB(event)
        if (travelEvent != null) {
            // then add the event to the Itinerary.travelEvents[]
            Itinerary.addTravelEvent(travelEvent.itineraryId, travelEvent.id!!)
        }
    }

    suspend fun updateTravelEventDate(id: Int, date: String) {
        TravelEvent.updateTravelEventDate(id, date)
    }

    suspend fun updateTravelEventPrice(id: Int, price: Double) {
        TravelEvent.updateTravelEventPrice(id, price)
    }

    suspend fun addFriend(id: Int, friendId: Int) {
        Itinerary.addFriend(id, friendId)
    }

    suspend fun updateTravelEventDescription(id: Int, description: String) {
        TravelEvent.updateTravelEventDescription(id, description)
    }

    suspend fun getTravelEventDetails(eventId: Int): TravelEvent? {
        return TravelEvent.getTravelEventById(eventId)
    }


    suspend fun getDetailedTravelEvents(itineraryId: Int): List<TravelEvent> {
        val travelEventIds = getTravelEvents(itineraryId).mapNotNull { it.id } // Only IDs
        return travelEventIds.mapNotNull { getTravelEventDetails(it) }
    }

}
