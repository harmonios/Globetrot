package ca.uwaterloo.team_102_7.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import ca.uwaterloo.team_102_7.data.repository.AuthRepository
import ca.uwaterloo.team_102_7.data.domain.User
import kotlinx.coroutines.launch
import android.content.Context
import android.util.Base64
import android.widget.Toast
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import ca.uwaterloo.team_102_7.data.repository.LocalStorageRepository
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.auth.exception.AuthRestException
import org.json.JSONObject
import java.util.*

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val localStorageRepository: LocalStorageRepository
) : ViewModel() {
    fun googleSignIn(context: Context, navController: NavController) {
        viewModelScope.launch {
            try {
                val rawNonce = UUID.randomUUID().toString()
                val request = authRepository.initGoogleSSO(rawNonce)

                val googleIdToken = authRepository.verifyUser(context, request, rawNonce).split(".")
                val payload = String(Base64.decode(googleIdToken[1], Base64.URL_SAFE))
                val payloadJson = JSONObject(payload)
                val first = payloadJson.optString("given_name")
                val last = payloadJson.optString("family_name")
                val email = payloadJson.optString("email")

                val userId = User.appendToDB(first, last, email)
                if (userId != null) {
                    localStorageRepository.saveUserUUID(userId)
                }
                showToast(context, "Successfully signed with Google!")

                navController.navigate("home")
            } catch (e: Exception) {
                handleSignInError(context, e)
            }
        }
    }

    private fun handleSignInError(context: Context, e: Exception) {
        when (e) {
            is NoCredentialException, is GetCredentialException -> showToast(context, "Please sign into Google on your device!")
            is GoogleIdTokenParsingException -> showToast(context, "Failed to parse id_token!")
            is AuthRestException -> showToast(context, "Authentication failed! ${e.message}")
            else -> showToast(context, "Unknown error occurred")
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
