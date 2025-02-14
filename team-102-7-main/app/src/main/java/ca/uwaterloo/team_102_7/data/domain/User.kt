package ca.uwaterloo.team_102_7.data.domain

import android.util.Log
import ca.uwaterloo.team_102_7.data.DbClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,

    val first: String,
    val last: String,
    val email: String,

    val theme: String? = "",
    val language: String? = "",
    val currency: String? = "",
) {
    companion object {
        suspend fun appendToDB(first: String, last: String, email: String): Int? {
            //check if user already exists in DB
            val checkUser = DbClient.getInstance().client.from("users")
                .select(columns = Columns.list("id, first, last, email, theme, language, currency")){
                filter { eq("email", email) }
            }.decodeSingleOrNull<User>()

            if (checkUser != null) {
                Log.d("db", "User is already in the database")
                return checkUser.id
            }

            val defaultTheme = "Classic"
            val defaultLanguage = "English"
            val defaultCurrency = "CAD"

            DbClient.getInstance().client.from("users").insert(mapOf(
                "first" to first,
                "last" to last,
                "email" to email,
                "theme" to defaultTheme,
                "language" to defaultLanguage,
                "currency" to defaultCurrency
            )) {
                select()
                single()
            }
            Log.d("db", "User has been added to the database successfully")

            // Return back userID so we can write to local storage
            return DbClient.getInstance().client.from("users")
                .select(columns = Columns.list("id, first, last, email, theme, language, currency")){
                    filter { eq("email", email) }
                }.decodeSingle<User>().id
        }
    }
}