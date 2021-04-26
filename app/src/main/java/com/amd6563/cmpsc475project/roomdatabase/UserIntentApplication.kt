package com.amd6563.cmpsc475project.roomdatabase

import android.app.Application

class UserIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserRepository.initialize(this)
    }
}