package com.demo.chatapp.services

import android.util.Log
import com.demo.chatapp.utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
//        super.onMessageReceived(p0
        if (p0.notification != null) {
            Log.d("FCM", p0.data.toString())
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(p0)

    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            FirestoreUtil.getFCMRegistrationTokens { tokens ->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationTokens(tokens)
            }
        }

    }
}