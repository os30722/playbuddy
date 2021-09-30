package com.hfad.sports

import android.app.Application
import com.hfad.sports.util.TokenToolkit
import dagger.hilt.android.HiltAndroidApp

const val API_BASE_URL = "http://192.168.0.110:3000/"

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {


        // Class Responsible for managing token session for the app

        // Class Responsible for managing token session for the app
        TokenToolkit.init(this)
        super.onCreate()
    }
}