package com.example.gamefest

import android.app.Application
import com.example.gamefest.di.AppContainer
import com.example.gamefest.di.DefaultAppContainer

class GameFestApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}