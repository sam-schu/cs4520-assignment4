package com.cs4520.assignment4.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1)
abstract class ApiAdventuresDatabase : RoomDatabase() {
    abstract fun getProductDao(): ProductDao
}