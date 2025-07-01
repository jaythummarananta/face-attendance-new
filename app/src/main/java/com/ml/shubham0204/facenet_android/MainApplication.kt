package com.ananta.faceapp

import android.app.Application
import com.ananta.faceapp.data.ObjectBoxStore
import com.ananta.faceapp.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(AppModule().module)
        }
        ObjectBoxStore.init(this)
    }
}
