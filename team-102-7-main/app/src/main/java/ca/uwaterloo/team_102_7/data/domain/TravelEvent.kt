package ca.uwaterloo.team_102_7.data.domain

import android.util.Log
import ca.uwaterloo.team_102_7.data.DbClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

// A [TravelEvent] is either a Travel item or
// an Event item.
//
// Travel item: Flights
// Event item: Going to a restaurant
@Serializable
data class TravelEvent(
    val id: Int? = 0,
    val nameText: String,
    val travelType: TravelType,

    // TODO: all these times are wrapped in String, maybe change that in the future?
    val date: String, // LocalDate YYYY-MM-DD
    val startTime: String,
    val endTime: String,
    val travelTime: String, // a.k.a transitTime, you might want this to be String

    var price: Double? = 0.0,
    val latitude: Double,
    val longitude: Double,

    // Optional, only for eventItems
    var descriptionText: String? = null,
    val imageResId: Int? = null,

    val createdByUserId: Int, //Foreign Key to user[id]
    val itineraryId: Int,
) {
    companion object {
        suspend fun getTravelEvents(itineraryId: Int): List<TravelEvent> {
            try {
                val res = DbClient.getInstance().client.from("travelEvents")
                    .select {
                        filter {
                            eq("itineraryId", itineraryId)
                        }
                    }.decodeList<TravelEvent>()

                Log.d("TRAVEL EVENT", "getTravelEvents success!")
                return res
            } catch (e: Exception) {
                Log.e("TRAVEL EVENT", e.toString())
                return emptyList()
            }
        }

        suspend fun appendToDB(travelEvent: TravelEvent): TravelEvent? {
            try {
                val res = DbClient.getInstance().client.from("travelEvents").insert(travelEvent) {
                    select()
                }.decodeSingle<TravelEvent>()
                Log.d("TRAVEL EVENT", "Travel Event has been added to the database successfully")
                return res
            } catch (e: Exception) {
                Log.e("TRAVEL EVENT", e.toString())
                return null
            }
        }

        suspend fun updateTravelEventDate(id: Int, date: String) {
            try {
                DbClient.getInstance().client.from("travelEvents").update(
                    {
                        set("date", date)
                    }
                ) {
                    filter {
                        eq("id", id)
                    }
                }
                Log.d("TRAVEL EVENT", "updateTravelEventDate success!")
            } catch (e: Exception) {
                Log.e("TRAVEL EVENT", e.toString())
            }
        }

        suspend fun updateTravelEventPrice(id: Int, price: Double) {
            try {
                DbClient.getInstance().client.from("travelEvents").update(
                    {
                        set("price", price)
                    }
                ) {
                    filter {
                        eq("id", id)
                    }
                }
                Log.d("TRAVEL EVENT", "updateTravelEventPrice success!")
            } catch (e: Exception) {
                Log.e("TRAVEL EVENT", e.toString())
            }
        }

        suspend fun getTravelEventById(id: Int): TravelEvent? {
            return try {
                val res = DbClient.getInstance().client.from("travelEvents")
                    .select {
                        filter {
                            eq("id", id)
                        }
                    }.decodeSingleOrNull<TravelEvent>()
                Log.d("TRAVEL EVENT", "getTravelEventById success!")
                res
            } catch (e: Exception) {
                Log.e("TRAVEL EVENT", e.toString())
                null
            }
        }

        suspend fun updateTravelEventDescription(id: Int, description: String) {
            try {
                DbClient.getInstance().client.from("travelEvents").update(
                    {
                        set("descriptionText", description)
                    }
                ) {
                    filter {
                        eq("id", id)
                    }
                }
                Log.d("TRAVEL EVENT", "updateTravelEventDescription success!")
            } catch (e: Exception) {
                Log.e("TRAVEL EVENT", e.toString())
            }
        }
    }
}