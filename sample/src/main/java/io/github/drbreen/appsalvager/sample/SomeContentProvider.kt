package io.github.drbreen.appsalvager.sample

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import java.util.*

class SomeContentProvider: ContentProvider() {

    override fun onCreate(): Boolean {
        Log.d("woodblock", "Content provider $this created")

        context?.let {
            if (it.isContentProviderBroken()) {
                throw RuntimeException("Content provider crashed: ${UUID.randomUUID()}")
            }
        }

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}