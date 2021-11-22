package io.github.drbreen.appsalvager.internal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.drbreen.appsalvager.AppSalvager.createSalvageView

/**
 * Activity that shows some content on recurring crash.
 */
internal class SalvageActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashCause = runCatching {
            intent.getSerializableExtra(KEY_CRASH_CAUSE) as? Throwable
        }.getOrNull()

        setContentView(createSalvageView(this, crashCause))
    }

    override fun onBackPressed() {
        // We want to finish the whole task, otherwise it'll throw us back to main Activity,
        // which will trigger another crash
        finishAffinity()
    }

    companion object {

        private const val KEY_CRASH_CAUSE = "crash_cause"

        /**
         * Create intent for creation of [SalvageActivity].
         */
        fun createIntent(
            context: Context,
            crashCause: Throwable
        ): Intent {
            return Intent(context, SalvageActivity::class.java)
                .putExtra(KEY_CRASH_CAUSE, crashCause)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}