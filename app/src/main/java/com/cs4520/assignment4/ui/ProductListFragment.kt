package com.cs4520.assignment4.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs4520.assignment4.R
import com.cs4520.assignment4.databinding.ProductListFragmentBinding
import com.cs4520.assignment4.databinding.ProductListItemBinding
import com.cs4520.assignment4.logic.CategorizedProduct
import com.cs4520.assignment4.logic.ProductsViewModel
import com.cs4520.assignment4.productsDataset

/**
 * The fragment displaying the list of products.
 */
class ProductListFragment : Fragment() {
    private lateinit var binding: ProductListFragmentBinding
    private lateinit var viewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ProductListFragmentBinding.inflate(inflater).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        viewModel.products.observe(viewLifecycleOwner) {products ->
            with (binding.recyclerView) {
                layoutManager = LinearLayoutManager(activity)
                adapter = RecyclerViewAdapter(products)
            }
        }

        viewModel.importProductData(productsDataset)
        viewModel.loadProductData()
    }
}

/**
 * The adapter for the RecyclerView displaying the list of products.
 */
private class RecyclerViewAdapter(private val products: List<CategorizedProduct>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    /**
     * The ViewHolder for each item in the RecyclerView.
     */
    class ViewHolder(val view: View, val binding: ProductListItemBinding) :
        RecyclerView.ViewHolder(view) {

        /**
         * Updates the view to represent the given product's information.
         */
        fun bind(product: CategorizedProduct) {
            with (binding) {
                when (product) {
                    is CategorizedProduct.Equipment -> {
                        productImage.setImageResource(R.drawable.equipment)
                        constraintLayout.setBackgroundResource(R.color.product_equipment_background)
                    }
                    is CategorizedProduct.Food -> {
                        productImage.setImageResource(R.drawable.food)
                        constraintLayout.setBackgroundResource(R.color.product_food_background)
                    }
                }

                productName.text = product.name

                if (product.expiryDate == null) {
                    productExpiryDate.visibility = View.GONE
                } else {
                    productExpiryDate.visibility = View.VISIBLE
                    productExpiryDate.text = product.expiryDate
                }

                productPrice.text = view.context.getString(
                    R.string.product_price_text, product.price
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).let {
            return ViewHolder(it.root, it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}