package ca.uwaterloo.team_102_7.data.repository

import ca.uwaterloo.team_102_7.data.DbClient
import ca.uwaterloo.team_102_7.data.domain.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class UserRepository {
    suspend fun getUser(userId: Int): User {
        return DbClient.getInstance().client.from("users")
            .select(columns = Columns.list("id, first, last, email, theme, language, currency")){
                filter { eq("id", userId) }
            }.decodeSingle<User>()
    }

    suspend fun updateFName(userId: Int, fName: String) {
        DbClient.getInstance().client.from("users").update(mapOf(
            "first" to fName
        )) {
            filter { eq("id", userId) }
        }
    }

    suspend fun updateLName(userId: Int, lName: String) {
        DbClient.getInstance().client.from("users").update(mapOf(
            "last" to lName
        )) {
            filter { eq("id", userId) }
        }
    }

    suspend fun updateTheme(userId: Int, theme: String) {
        DbClient.getInstance().client.from("users").update(mapOf(
            "theme" to theme
        )) {
            filter { eq("id", userId) }
        }
    }

    suspend fun updateLanguage(userId: Int, newLanguage: String) {
        DbClient.getInstance().client.from("users").update(mapOf(
            "language" to newLanguage
        )) {
            filter { eq("id", userId) }
        }
    }

    suspend fun updateCurrency(userId: Int, newCurrency: String) {
        DbClient.getInstance().client.from("users").update(mapOf(
            "currency" to newCurrency
        )) {
            filter { eq("id", userId) }
        }
    }

}