package com.woodblockwithoutco.appsalvager.internal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.woodblockwithoutco.appsalvager.AppSalvager.createSalvageView

/**
 * Activity that shows some content on recurring crash.
 */
internal class SalvageActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createSalvageView(this))
    }

    companion object {

        /**
         * Create intent for creation of [SalvageActivity].
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, SalvageActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}