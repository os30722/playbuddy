package com.hfad.sports

import android.app.Application
import com.hfad.sports.util.TokenToolkit
import dagger.hilt.android.HiltAndroidApp

/*
    Set the api server URL here
    
*/
const val API_BASE_URL = ""

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {


        // Class Responsible for managing token session for the app

        // Class Responsible for managing token session for the app
        TokenToolkit.init(this)
        super.onCreate()
    }
}