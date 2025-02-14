package ca.uwaterloo.team_102_7.data

import io.github.cdimascio.dotenv.dotenv

fun getEnv(name: String): String {
    val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    return try {
        dotenv[name]
    } catch (e: Exception) {
        ""
    } catch (e: NullPointerException) {
        ""
    }
}
