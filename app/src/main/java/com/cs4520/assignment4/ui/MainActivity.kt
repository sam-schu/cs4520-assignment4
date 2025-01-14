package com.cs4520.assignment4.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs4520.assignment4.R
import com.cs4520.assignment4.model.ApiAdventuresDatabaseProvider

/**
 * The application's single activity, which can host both the login and product list fragments.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiAdventuresDatabaseProvider.setContext(applicationContext)
        setContentView(R.layout.main_activity)
    }
}