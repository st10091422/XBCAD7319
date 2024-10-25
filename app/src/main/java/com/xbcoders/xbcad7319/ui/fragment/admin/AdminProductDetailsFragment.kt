package com.xbcoders.xbcad7319.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.seviceimplementations.CartItemServiceImpl
import com.xbcoders.xbcad7319.api.seviceimplementations.ProductServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentAdminProductDetailsBinding
import com.xbcoders.xbcad7319.databinding.FragmentAdminProductsBinding
import com.xbcoders.xbcad7319.databinding.FragmentProductDetailsBinding
import com.xbcoders.xbcad7319.ui.fragment.user.PaymentFragment
import com.xbcoders.xbcad7319.ui.fragment.user.ProductDetailsFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdminProductDetailsFragment : Fragment() {
    private var product: Product? = Product()

    // View binding object for accessing views in the layout
    private var _binding: FragmentAdminProductDetailsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var productServiceImpl: ProductServiceImpl

    private lateinit var localUser: LocalUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getParcelable(PRODUCT_ARG)
        }
        localUser = LocalUser.getInstance(requireContext())

        productServiceImpl = ProductServiceImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminProductDetailsBinding.inflate(inflater, container, false)

        binding.productName.text = product?.name

        binding.productPrice.text = "R ${product?.price}"

        Glide.with(requireContext())
            .load(product?.imageUrl) // Load the image from the URL
            .placeholder(R.drawable.baseline_image_24) // Optional: Add a placeholder
            //.error(R.drawable.error_image) // Optional: Add an error image
            .into(binding.productImage)

        binding.propertyDescription.text = product?.description

        // Add to cart button click listener
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()

        }

        binding.updateButton.setOnClickListener {
            proceedToUpdate()
        }

        return binding.root
    }
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete this product?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            // Handle the delete action here
            deleteProduct(product?.id)
            Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteProduct(productId: String?) {
        val authToken = localUser.getToken()
        val userId = localUser.getUser()?.id

        if (authToken != null && userId != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"

            productId?.let {
                productServiceImpl.deleteProduct(tokenVal, it).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
                            // Navigate back or update the UI as needed
                            changeCurrentFragment(AdminProductsFragment())
                        } else {
                            Toast.makeText(requireContext(), "Failed to delete product", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("AdminProductDetails", "Error deleting product: ${t.message}")
                        Toast.makeText(requireContext(), "Error deleting product", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }

    }

    private fun proceedToUpdate() {
        Log.d("product", "product: $product")
        val updateFragment = product?.let { UpdateProductFragment.newInstance(it) }
        if (updateFragment != null) {
            changeCurrentFragment(updateFragment)
        }

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

    companion object {
        const val PRODUCT_ARG = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            AdminProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCT_ARG, product)
                }
            }
    }
}