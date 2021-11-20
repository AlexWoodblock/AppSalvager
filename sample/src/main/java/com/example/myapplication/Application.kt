package com.example.myapplication

import android.app.Application
import android.content.Context
import android.util.Log
import com.woodblockwithoutco.appsalvager.AppSalvager
import com.woodblockwithoutco.appsalvager.policy.ExceptionNumberPolicy
import java.util.*

class TheApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        if (base != null) {
            AppSalvager.install(
                AppSalvager.Config(
                    context = base,
                    installExceptionHandler = true,
                    policy = ExceptionNumberPolicy(),
                    createSalvageView = ::createSalvageModeView
                )
            )
        }

        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        if (AppSalvager.inSalvageMode) {
            Log.d("woodblock", "Created in salvage mode")
            return
        } else {
            Log.d("woodblock", "Created in normal mode")
        }

        val shouldCrash = isApplicationBroken()
        if (shouldCrash) {
            throw RuntimeException(UUID.randomUUID().toString())
        }
    }
}