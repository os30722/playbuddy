package com.hfad.sports

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.hfad.sports.util.TokenToolkit

class AppFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {

        super.onNewToken(token)

    }
}