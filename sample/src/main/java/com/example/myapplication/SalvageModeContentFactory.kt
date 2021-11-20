package com.example.myapplication

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.woodblockwithoutco.appsalvager.AppSalvager

fun createSalvageModeView(activity: Activity): View {
    val root = LayoutInflater.from(activity).inflate(R.layout.salvage_activity_layout, null)

    root.findViewById<Button>(R.id.fixContentProvider).setOnClickListener {
        activity.setContentProviderBroken(false)
        AppSalvager.onProblemFixed()
    }

    root.findViewById<Button>(R.id.fixApplication).setOnClickListener {
        activity.setApplicationBroken(false)
        AppSalvager.onProblemFixed()
    }

    root.findViewById<Button>(R.id.throwAnotherCrash).setOnClickListener {
        throw RuntimeException("Oops! Another crash!")
    }

    return root
}