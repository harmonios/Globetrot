package ca.uwaterloo.team_102_7.data.domain

import android.util.Log
import ca.uwaterloo.team_102_7.data.DbClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Itinerary(
    val id: Int? = 0, // Use internally to represent what Supabase assigns the ID to

    // Metadata
    val creator: Int, // [User.user_id]
    val friends: List<Int>, // other [User.user_id]'s

    // Trip Info
    val title: String,
    val start: String, // LocalDate YYYY-MM-DD
    val end: String, // LocalDate YYYY-MM-DD
    val location: String,
    val lat: Double,
    val long: Double,

    // Events
    val travelEvents: List<Int> // List<TravelEvents.id>,
) {
    companion object {
        suspend fun appendToDB(
            creator: Int,
            title: String,
            start: LocalDate,
            end: LocalDate,
            location: String,
            lat: Double,
            long: Double,
        ): Itinerary? {
            try {
                val itineraryData = Itinerary(
                    creator = creator,
                    friends = emptyList(),
                    title = title,
                    start = start.toString(),
                    end = end.toString(),
                    location = location,
                    lat = lat,
                    long = long,
                    travelEvents = emptyList(),
                )
                val res = DbClient.getInstance().client.from("itineraries").insert(itineraryData) {
                    select()
                }.decodeSingle<Itinerary>()
                Log.d("ITINERARY", "Itinerary has been added to the database successfully")
                return res
            } catch (e: Exception) {
                Log.e("ITINERARY", e.toString())
                return null
            }
        }

        suspend fun getItineraries(userId: Int, isPast: Boolean): List<Itinerary> {
            try {
                val userCreatedItineraries = DbClient.getInstance().client.from("itineraries")
                    .select {
                        filter {
                            eq("creator", userId)
                            // cant filter by [end] here because the string comparison
                            // on date types may lead to inaccurate info (potentially)
                        }
                    }.decodeList<Itinerary>()

                val sharedItineraries = DbClient.getInstance().client.from("itineraries")
                    .select {
                        filter {
                            contains("friends", listOf(userId))
                        }
                    }.decodeList<Itinerary>()

                // Instead, just get all itineraries by userId, then we'll compare ourselves
                val result: MutableList<Itinerary> = emptyList<Itinerary>().toMutableList()
                for (itinerary in (userCreatedItineraries + sharedItineraries)) {
                    val end = LocalDate.parse(itinerary.end)
                    if (!isPast && !end.isBefore(LocalDate.now())) {
                        // Get current itineraries
                        // If it hasn't ended, it's still considered a "current trip"
                        result.add(itinerary)
                    } else if (isPast && end.isBefore(LocalDate.now())) {
                        // Get past itineraries
                        // If the end date is in the past then it's considered a "past trip"
                        result.add(itinerary)
                    }
                }


                Log.d("ITINERARY", "getItineraries success!")
                return result.reversed()
            } catch (e: Exception) {
                Log.e("ITINERARY", e.toString())
                return emptyList()
            }
        }

        suspend fun getItinerary(itineraryId: Int): Itinerary? {
            try {
                val res = DbClient.getInstance().client.from("itineraries")
                    .select {
                        filter {
                            eq("id", itineraryId)
                        }
                    }.decodeSingleOrNull<Itinerary>()

                Log.d("ITINERARY", "getItinerary success!")
                return res
            } catch (e: Exception) {
                Log.e("ITINERARY", e.toString())
                return null
            }
        }

        suspend fun addTravelEvent(itineraryId: Int, travelEventId: Int) {
            try {
                // Step 1: Fetch the current array
                val result = DbClient.getInstance().client
                    .from("itineraries")
                    .select {
                        filter {
                            eq("id", itineraryId)
                        }
                    }.decodeSingle<Itinerary>()
                val currTravelEvents = result.travelEvents

                // Step 2: Append the new integer to the array
                val updatedTravelEvents = currTravelEvents + travelEventId

                // Step 3: Update the array in the database
                DbClient.getInstance().client
                    .from("itineraries")
                    .update(
                        mapOf("travelEvents" to updatedTravelEvents)
                    ) {
                        filter {
                            eq("id", itineraryId)
                        }
                    }

                Log.d("ITINERARY", "addTravelEvent success!")
            } catch (e: Exception) {
                Log.e("ITINERARY", e.toString())
            }
        }

        suspend fun addFriend(itineraryId: Int, friendId: Int) {
            try {
                val result = DbClient.getInstance().client
                    .from("itineraries")
                    .select {
                        filter {
                            eq("id", itineraryId)
                        }
                    }.decodeSingle<Itinerary>()
                val friends = result.friends

                // Step 2: Append the new integer to the array
                val updatedFriends = friends + friendId

                // Step 3: Update the array in the database
                DbClient.getInstance().client
                    .from("itineraries")
                    .update(
                        mapOf("friends" to updatedFriends)
                    ) {
                        filter {
                            eq("id", itineraryId)
                        }
                    }

                Log.d("ITINERARY", "addFriend success!")
            } catch (e: Exception) {
                Log.e("ITINERARY", e.toString())
            }
        }
    }
}