package com.cs4520.assignment4.logic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs4520.assignment4.ApiService
import com.cs4520.assignment4.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Represents a single product (equipment or food).
 *
 * Contains a name, a price, and possibly an expiry date.
 */
sealed class CategorizedProduct(
    open val name: String, open val expiryDate: String?, open val price: Double
) {

    companion object {
        /**
         * Returns a Product generated from the given list with the following elements:
         * 0: product name (String)
         * 1: product type ("Equipment" or "Food")
         * 2: product expiry date (String), or null if none
         * 3: product price (Int)
         *
         * The returned Product will be either an EquipmentProduct or a FoodProduct, depending on
         * the product type.
         *
         * @throws IllegalArgumentException if the aforementioned list structure is not followed.
         */
        fun fromDataList(data: List<Any?>): CategorizedProduct {
            if (data.size != 4) {
                throw IllegalArgumentException(
                    "Exactly 4 data items must be provided to initialize a product"
                )
            }

            val name = data[0] as? String ?: throw IllegalArgumentException(
                "The first data item (the product name) must be a String"
            )

            val subclassConstructor = when (data[1]) {
                "Equipment" -> ::Equipment
                "Food" -> ::Food
                else -> throw IllegalArgumentException(
                    "The second data item (the product type) must be either \"Equipment\" or "
                    + "\"Food\""
                )
            }

            val expiryDate: String? = data[2].let {
                if (it is String?) it else throw IllegalArgumentException(
                    "The third data item (the expiry date) must be either a String or null"
                )
            }

            val price = data[3] as? Int ?: throw IllegalArgumentException(
                "The fourth data item (the price) must be an Int"
            )

            return subclassConstructor(name, expiryDate, price.toDouble())
        }
    }

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

    /**
     * Replaces the list of products using data from the given list. Each list element
     * should be a list representing a single product and containing the following elements:
     * 0: product name (String)
     * 1: product type ("Equipment" or "Food")
     * 2: product expiry date (String), or null if none
     * 3: product price (Int)
     *
     * @throws IllegalArgumentException if the aforementioned list structure is not followed for all
     *     products.
     */
    fun importProductData(data: List<List<Any?>>) {
        _displayProducts.value = DisplayProducts.ProductList(
            data.map { CategorizedProduct.fromDataList(it) }
        )
    }

    fun loadProductData() {
        val api = RetrofitBuilder.getRetrofit().create(ApiService::class.java)
        viewModelScope.launch(Dispatchers.IO) {
            val products = api.getAllProducts().map { it.toCategorizedProduct() }
            Log.e("products", products.toString())
        }
    }
}