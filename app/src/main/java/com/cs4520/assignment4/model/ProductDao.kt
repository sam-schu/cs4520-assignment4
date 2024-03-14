package com.cs4520.assignment4.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ProductDao {
    @Insert
    fun addProducts(products: List<Product>)

    @Query("DELETE FROM Product")
    fun clearProducts()

    @Transaction
    fun replaceCache(products: List<Product>) {
        clearProducts()
        addProducts(products)
    }

    @Query("SELECT * FROM Product")
    fun getAllProducts(): List<Product>
}