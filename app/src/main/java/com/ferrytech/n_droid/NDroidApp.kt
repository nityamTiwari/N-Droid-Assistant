package com.ferrytech.n_droid

import android.app.Application
import com.ferrytech.n_droid.data.local.DatabaseProvider

class NDroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        DatabaseProvider.init(this)
    }
}