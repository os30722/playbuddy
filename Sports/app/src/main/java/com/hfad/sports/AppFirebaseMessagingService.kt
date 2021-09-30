package com.hfad.sports

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.hfad.sports.util.TokenToolkit

class AppFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        if(TokenToolkit.containsToken()){

        }
        Log.d("debug64","wjoiwjfoq")
        super.onNewToken(token)

    }
}