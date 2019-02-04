package myoo.votingapp.Utils

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import myoo.votingapp.Utils.*
import org.koin.android.ext.android.startKoin


class App : Application(){

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        startKoin(this, listOf(loginModule, firebaseModule, otpModule, appModule, meetingModule))
    }

}