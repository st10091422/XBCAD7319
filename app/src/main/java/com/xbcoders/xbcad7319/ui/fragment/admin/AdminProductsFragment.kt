package com.xbcoders.xbcad7319.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.seviceimplementations.ProductServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentAdminProductsBinding
import com.xbcoders.xbcad7319.ui.adapter.CategoryAdapter
import com.xbcoders.xbcad7319.ui.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdminProductsFragment : Fragment() {
    private var _binding: FragmentAdminProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var localUser: LocalUser

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: List<Product>
    private lateinit var filteredProductList: MutableList<Product>
    private lateinit var apiService: ProductServiceImpl

    private lateinit var categoryAdapter: CategoryAdapter

    val categories = listOf(
        "All",
        "Fruits",
        "Vegetables",
        "Dairy",
        "Meat & Poultry",
        "Seafood",
        "Bakery",
        "Canned Goods",
        "Frozen Foods",
        "Snacks",
        "Beverages",
        "Condiments & Sauces",
        "Spices & Seasonings",
        "Grains & Pasta",
        "Health Foods",
        "Baby Products",
        "Organic Products",
        "Household Essentials",
        "Personal Care",
        "Pet Supplies",
        "International Foods"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using View Binding
        _binding = FragmentAdminProductsBinding.inflate(inflater, container, false)

        localUser = LocalUser.getInstance(requireContext())

        apiService = ProductServiceImpl()

        productList = listOf<Product>()

        filteredProductList = mutableListOf()

        productAdapter = ProductAdapter(mutableListOf()) { product ->
            // Handle product click event
            Toast.makeText(requireContext(), "Selected: ${product.name}", Toast.LENGTH_SHORT).show()
        }

        categoryAdapter = CategoryAdapter(categories) {
                category ->
        }



        fetchProducts()

        searchProperties()

        binding.create.setOnClickListener {
            changeCurrentFragment(CreateProductFragment())
        }

        return binding.root
    }

    private fun searchProperties(){
        binding.searchProducts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    filterProducts(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun filterProducts(query: String) {
        filteredProductList.clear() // Clear the current filtered list
        filteredProductList.addAll(productList.filter { property ->
            property.name.contains(query, ignoreCase = true) // Filter based on query
        })
        productAdapter.updateProducts(filteredProductList) // Update the existing adapter
    }
    private fun fetchProducts() {
        val token = localUser.getToken() // Replace with actual token retrieval logic

        if (token != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            apiService.getAllProducts(tokenVal).enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (response.isSuccessful) {
                        productList = response.body() ?: emptyList()
                        setupRecyclerView(productList)
                    } else {
                        // Handle error (e.g., show a Toast or a Snackbar)
                        Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    // Handle failure (e.g., show a Toast or a Snackbar)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }
    }



    private fun setupRecyclerView(products: List<Product>) {
        productAdapter = ProductAdapter(products.toMutableList()) { product ->
            // Handle product click event
            Toast.makeText(requireContext(), "Selected: ${product.name}", Toast.LENGTH_SHORT).show()

            val detailsFragment = AdminProductDetailsFragment.newInstance(product)
            changeCurrentFragment(AdminProductDetailsFragment.newInstance(product))

        }
        binding.productRecyclerView.adapter = productAdapter
        binding.productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    // Helper function to change the current fragment in the activity.
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}