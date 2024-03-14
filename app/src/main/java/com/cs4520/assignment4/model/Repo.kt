package com.cs4520.assignment4.model

class Repo : ProductRepo {
    override fun cacheProducts(products: List<Product>) {
        getDao()?.replaceCache(products)
    }

    override fun getCachedProducts(): List<Product>? {
        return getDao()?.getAllProducts()
    }

    private fun getDao(): ProductDao? =
        ApiAdventuresDatabaseProvider
            .getDatabase()
            ?.getProductDao()
}