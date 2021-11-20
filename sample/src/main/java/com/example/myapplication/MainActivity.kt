package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.breakContentProvider).setOnClickListener {
            setContentProviderBroken(true)
        }

        findViewById<Button>(R.id.breakApplication).setOnClickListener {
            setApplicationBroken(true)
        }

        findViewById<Button>(R.id.justCrash).setOnClickListener {
            throw RuntimeException("I was requested to crash!")
        }
    }


}