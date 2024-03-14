package com.cs4520.assignment4.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room

class ApiAdventuresDatabaseProvider {
    companion object {
        var db: ApiAdventuresDatabase? = null

        // as done in class, since we are not using dependency injection

        @SuppressLint("StaticFieldLeak")
        var databaseContext: Context? = null

        fun getDatabase(): ApiAdventuresDatabase? {
            if (db == null) {
                databaseContext?.let {
                    db = Room.databaseBuilder(
                        it,
                        ApiAdventuresDatabase::class.java,
                        "ApiAdventuresDatabase"
                    ).build()
                }
            }
            return db
        }

        fun setContext(context: Context) {
            databaseContext = context
        }
    }
}