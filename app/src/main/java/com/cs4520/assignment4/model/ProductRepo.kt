package com.cs4520.assignment4.model

interface ProductRepo {
    fun cacheProducts(products: List<Product>)

    fun getCachedProducts(): List<Product>?
}