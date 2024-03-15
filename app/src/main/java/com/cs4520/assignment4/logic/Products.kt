package com.cs4520.assignment4.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs4520.assignment4.ApiService
import com.cs4520.assignment4.RetrofitBuilder
import com.cs4520.assignment4.model.Product
import com.cs4520.assignment4.model.ProductRepo
import com.cs4520.assignment4.model.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Represents a single product (equipment or food).
 *
 * Contains a name, a price, and possibly an expiry date.
 */
sealed class CategorizedProduct(
    open val name: String, open val expiryDate: String?, open val price: Double
) {
    /**
     * Represents a product of the "Equipment" type.
     *
     * Contains a name, a price, and possibly an expiry date.
     */
    data class Equipment(
        override val name: String, override val expiryDate: String?, override val price: Double
    ) : CategorizedProduct(name, expiryDate, price)

    /**
     * Represents a product of the "Food" type.
     *
     * Contains a name, a price, and possibly an expiry date.
     */
    data class Food(
        override val name: String, override val expiryDate: String?, override val price: Double
    ) : CategorizedProduct(name, expiryDate, price)
}

private fun Product.toCategorizedProduct(): CategorizedProduct {
    return when (type) {
        "Equipment" -> CategorizedProduct.Equipment(name, expiryDate, price)
        "Food" -> CategorizedProduct.Food(name, expiryDate, price)
        else -> throw IllegalStateException(
            "The product type must be either \"Equipment\" or \"Food\" in order for it to be "
                    + "converted to a CategorizedProduct"
        )
    }
}

sealed interface DisplayProducts {
    data object ServerError : DisplayProducts

    data object ServerNoProducts : DisplayProducts

    data object OfflineNoProducts : DisplayProducts

    data class ProductList(val products: List<CategorizedProduct>) : DisplayProducts
}

/**
 * Manages a list of products and allows products to be mass imported from a list.
 */
class ProductsViewModel(private val repo: ProductRepo = Repo()) : ViewModel() {
    private val _displayProducts = MutableLiveData<DisplayProducts>()

    val displayProducts: LiveData<DisplayProducts> = _displayProducts

    fun loadProductData() {
        val api = RetrofitBuilder.getRetrofit().create(ApiService::class.java)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val products = api.getAllProducts()
                if (products.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.ServerNoProducts
                    }
                } else {
                    val categorizedProducts = products.map { it.toCategorizedProduct() }
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.ProductList(categorizedProducts)
                    }
                    repo.cacheProducts(products)
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    _displayProducts.value = DisplayProducts.ServerError
                }
            } catch (e: UnknownHostException) {
                // handle device offline
                val products = repo.getCachedProducts()
                if (products.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.OfflineNoProducts
                    }
                } else {
                    val categorizedProducts = products.map { it.toCategorizedProduct() }
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.ProductList(categorizedProducts)
                    }
                }
            }
        }
    }
}