package com.cs4520.assignment4.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs4520.assignment4.ApiService
import com.cs4520.assignment4.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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

data class ApiProduct(
    val name: String, val type: String, val expiryDate: String?, val price: Double
) {
    fun toCategorizedProduct(): CategorizedProduct {
        return when (type) {
            "Equipment" -> CategorizedProduct.Equipment(name, expiryDate, price)
            "Food" -> CategorizedProduct.Food(name, expiryDate, price)
            else -> throw IllegalStateException(
                "The product type must be either \"Equipment\" or \"Food\" in order for it to be "
                + "converted to a CategorizedProduct"
            )
        }
    }
}

sealed interface DisplayProducts {
    data object Error : DisplayProducts

    data class ProductList(val products: List<CategorizedProduct>) : DisplayProducts
}

/**
 * Manages a list of products and allows products to be mass imported from a list.
 */
class ProductsViewModel : ViewModel() {
    private val _displayProducts = MutableLiveData<DisplayProducts>()

    val displayProducts: LiveData<DisplayProducts> = _displayProducts

    fun loadProductData() {
        val api = RetrofitBuilder.getRetrofit().create(ApiService::class.java)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val products = api.getAllProducts().map { it.toCategorizedProduct() }
                withContext(Dispatchers.Main) {
                    _displayProducts.value = DisplayProducts.ProductList(products)
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    _displayProducts.value = DisplayProducts.Error
                }
            }
        }
    }
}