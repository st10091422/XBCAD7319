package com.xbcoders.xbcad7319.ui.fragment.user

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.seviceimplementations.ProductServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentProductsBinding
import com.xbcoders.xbcad7319.ui.adapter.BannerAdapter
import com.xbcoders.xbcad7319.ui.adapter.CategoryAdapter
import com.xbcoders.xbcad7319.ui.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductsFragment : Fragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var localUser: LocalUser

    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productList: List<Product>
    private lateinit var filteredProductList: MutableList<Product>
    private lateinit var apiService: ProductServiceImpl

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentProductsBinding.inflate(inflater, container, false)

        localUser = LocalUser.getInstance(requireContext())

        apiService = ProductServiceImpl()

        categoryAdapter = CategoryAdapter(categories) { category ->
            if (category == "All") {
                productAdapter.updateProducts(productList) // Update the existing adapter
            } else {
                filterByCategory(category)
            }
        }

        productAdapter = ProductAdapter(mutableListOf()) { product ->
            // Handle product click event
            Toast.makeText(requireContext(), "Selected: ${product.name}", Toast.LENGTH_SHORT).show()
        }

        productList = listOf<Product>()

        filteredProductList = mutableListOf()

        binding.categoryRecyclerView.adapter = categoryAdapter
        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        fetchProducts()

        searchProducts()

        return binding.root
    }

    private fun searchProducts() {
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


    private fun filterByCategory(category: String) {
        filteredProductList.clear() // Clear the current filtered list
        filteredProductList.addAll(productList.filter { it.category == category })
        productAdapter.updateProducts(filteredProductList) // Update the existing adapter
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
                override fun onResponse(
                    call: Call<List<Product>>,
                    response: Response<List<Product>>
                ) {
                    if (response.isSuccessful) {
                        productList = response.body() ?: emptyList()
                        setupRecyclerView(productList.toMutableList())
                    } else {
                        // Handle error (e.g., show a Toast or a Snackbar)
                        Toast.makeText(
                            requireContext(),
                            "Failed to load products",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    // Handle failure (e.g., show a Toast or a Snackbar)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                )
            ) // Restart the MainActivity
        }
    }

    private var autoScrollHandler: Handler? = null
    private var autoScrollRunnable: Runnable? = null

    private fun setupRecyclerView(products: MutableList<Product>) {
        productAdapter = ProductAdapter(products) { product ->
            // Handle product click event
            Toast.makeText(requireContext(), "Selected: ${product.name}", Toast.LENGTH_SHORT).show()

            val paymentFragment = ProductDetailsFragment.newInstance(product)
            changeCurrentFragment(paymentFragment)

        }
        binding.productRecyclerView.adapter = productAdapter
        binding.flashSaleRecyclerView.adapter = productAdapter
        binding.productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.flashSaleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // Set layout manager for flashSaleRecyclerView

        // Slideshow setup
        val bannerImages = listOf( R.drawable.banner2, R.drawable.banner3)
        val bannerAdapter = BannerAdapter(bannerImages)
        binding.imageSlider.adapter = bannerAdapter

        binding.dotsIndicator.attachTo(binding.imageSlider)

        // Auto-scrolling
        autoScrollHandler = Handler(Looper.getMainLooper())
        autoScrollRunnable = Runnable {
            val currentItem = binding.imageSlider.currentItem
            binding.imageSlider.currentItem = (currentItem + 1) % bannerImages.size
        }
        startAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    private fun startAutoScroll() {
        autoScrollHandler?.postDelayed(autoScrollRunnable!!, 8000) // Scroll every 8 seconds
    }

    private fun stopAutoScroll() {
        autoScrollHandler?.removeCallbacks(autoScrollRunnable!!)
    }

    // Helper function to change the current fragment in the activity.
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment).addToBackStack(null).commit()
    }
}