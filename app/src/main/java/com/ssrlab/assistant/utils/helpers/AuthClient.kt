package com.ssrlab.assistant.utils.helpers

import android.content.IntentSender
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ssrlab.assistant.R
import com.ssrlab.assistant.app.MainApplication
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class AuthClient {

    private val fireAuth = Firebase.auth
    private val emailRegex = Regex("^[a-zA-Z0-9]@[a-zA-Z0-9]+\\.[a-zA-Z0-9][a-zA-Z0-9]+")

    fun signIn(login: String, password: String, onSuccess: (AuthResult) -> Unit, onFailure: (String, Int) -> Unit) {
        if (login.matches(emailRegex)) {
            if (password.length >= 8) {
                fireAuth.signInWithEmailAndPassword(login, password)
                    .addOnSuccessListener { onSuccess(it) }
                    .addOnFailureListener { onFailure(it.message!!, 0) }
            } else {
//                val errMsg = ContextCompat.getString(MainApplication().getContext(), R.string.password_length_error)
//                onFailure(errMsg, 2)
            }
        } else {
//            val errMsg = ContextCompat.getString(MainApplication().getContext(), R.string.email_type_error)
//            onFailure(errMsg, 1)
        }
    }

    suspend fun signIn(oneTapClient: SignInClient) : IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildGoogleSignIn()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }

        return result?.pendingIntent?.intentSender
    }

    private fun buildGoogleSignIn() : BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(ContextCompat.getString(MainApplication().getContext(), R.string.google_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }
}