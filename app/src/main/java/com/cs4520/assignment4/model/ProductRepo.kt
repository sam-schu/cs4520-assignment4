package com.cs4520.assignment4.model

/**
 * Defines methods that can be used to access/update the locally stored products.
 */
interface ProductRepo {
    /**
     * Replaces all stored products with the given products.
     */
    fun replaceStoredProducts(products: List<Product>)

    /**
     * Gets all stored products. Returns null if the database object cannot be obtained.
     */
    fun getStoredProducts(): List<Product>?
}