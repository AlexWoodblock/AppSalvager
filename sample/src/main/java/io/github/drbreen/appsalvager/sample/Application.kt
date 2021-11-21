package io.github.drbreen.appsalvager.sample

import android.app.Application
import android.content.Context
import android.util.Log
import io.github.drbreen.appsalvager.AppSalvager
import io.github.drbreen.appsalvager.policy.SameExceptionPolicy
import java.util.*

class TheApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        AppSalvager.install(
            context = this,
            configuration = AppSalvager.Configuration(
                policy = SameExceptionPolicy(configuration = SameExceptionPolicy.Configuration(compareMessage = false)),
                createSalvageView = ::createSalvageModeView
            )
        )
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