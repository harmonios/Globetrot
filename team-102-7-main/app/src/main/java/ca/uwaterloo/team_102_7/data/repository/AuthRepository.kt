package ca.uwaterloo.team_102_7.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import ca.uwaterloo.team_102_7.data.DbClient
import ca.uwaterloo.team_102_7.data.getEnv
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import java.security.MessageDigest

class AuthRepository {
    fun initGoogleSSO(rawNonce: String): GetCredentialRequest {
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashNonce = digest.fold("", { str, it -> str + "%02x".format(it) })

        val serverClientId = getEnv("GOOGLE_SSO")
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setNonce(hashNonce)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun verifyUser(
        context: Context,
        req: GetCredentialRequest,
        rawNonce: String
    ): String {
        val cm = CredentialManager.create(context)
        val result = cm.getCredential(context = context, request = req)
        val credential = result.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val googleIdToken = googleIdTokenCredential.idToken

        DbClient.getInstance().client.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
            nonce = rawNonce
        }
        return googleIdToken
    }
}
