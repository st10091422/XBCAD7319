package com.xbcoders.xbcad7319.ui.fragment.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.seviceimplementations.CartItemServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentProductDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductDetailsFragment : Fragment() {
    private var product: Product? = Product()

    // View binding object for accessing views in the layout
    private var _binding: FragmentProductDetailsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private var quantity = 1

    private lateinit var cartItemServiceImpl: CartItemServiceImpl

    private lateinit var localUser: LocalUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getParcelable(PRODUCT_ARG)
        }
        localUser = LocalUser.getInstance(requireContext())

        cartItemServiceImpl = CartItemServiceImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)

        binding.productName.text = product?.name

        binding.productPrice.text = "R ${product?.price}"

        Glide.with(requireContext())
            .load(product?.imageUrl) // Load the image from the URL
            .placeholder(R.drawable.baseline_image_24) // Optional: Add a placeholder
            //.error(R.drawable.error_image) // Optional: Add an error image
            .into(binding.productImage)

        binding.propertyDescription.text = product?.description
        // Set click listeners
        binding.decreaseImage.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.quantityText.text = quantity.toString()
            }
        }

        binding.increaseImage.setOnClickListener {
            quantity++
            binding.quantityText.text = quantity.toString()
        }
        // Add to cart button click listener
        binding.submitButton.setOnClickListener {
            product?.let {
                val cartItem = CartItem(
                    productId = it.id, // Assuming Product has an `id` property
                    quantity = quantity,
                )
                addItemToCart(cartItem)
            }
        }

        return binding.root
    }

    private fun addItemToCart(cartItem: CartItem) {
        val authToken = localUser.getToken()
        val userId = localUser.getUser()?.id

        if (authToken != null && userId != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"

//            val newCart = CartItem(
//                productId = cartItem.productId,
//                quantity = cartItem.quantity,
//                userId = userId,
//
//            )
            cartItemServiceImpl.addItemToCart(tokenVal, cartItem).enqueue(object : Callback<CartItem> {
                override fun onResponse(call: Call<CartItem>, response: Response<CartItem>) {
                    if (response.isSuccessful) {
                        // Handle success, e.g., show a toast or update UI
                        Toast.makeText(requireContext(), "Item added to cart", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle error response
                        Toast.makeText(requireContext(), "Failed to add item to cart", Toast.LENGTH_SHORT).show()
                        Log.d("", "Failed to add item to cart: ${response.message()} ${response.code()}")

                    }
                }

                override fun onFailure(call: Call<CartItem>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    companion object {
        const val PRODUCT_ARG = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCT_ARG, product)
                }
            }
    }
}