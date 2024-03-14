package com.cs4520.assignment4.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert
    fun addProduct(product: Product)

    @Query("SELECT * FROM Product")
    fun getAllProducts(): List<Product>
}