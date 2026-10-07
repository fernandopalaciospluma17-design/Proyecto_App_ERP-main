package com.nodara.erp

import android.app.Application
import com.nodara.erp.data.local.SecureSessionManager

class NodaraApp : Application() {

    val sessionManager: SecureSessionManager by lazy {
        SecureSessionManager(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: NodaraApp
            private set
    }
}
