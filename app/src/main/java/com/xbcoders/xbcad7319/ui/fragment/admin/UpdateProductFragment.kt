package com.xbcoders.xbcad7319.ui.fragment.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.seviceimplementations.ProductServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentUpdateProductBinding
import com.xbcoders.xbcad7319.ui.adapter.CategoryAdapter
import com.xbcoders.xbcad7319.ui.fragment.admin.AdminProductDetailsFragment.Companion
import com.xbcoders.xbcad7319.ui.fragment.user.ProductDetailsFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class UpdateProductFragment : Fragment() {
    private var product: Product? = Product()

    // View binding object for accessing views in the layout
    private var _binding: FragmentUpdateProductBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!


    private lateinit var prodServiceImpl: ProductServiceImpl

    private lateinit var localUser: LocalUser

    private lateinit var imageUri: Uri
    private lateinit var imageUploadLauncher: ActivityResultLauncher<Intent>
    private val productServiceImpl = ProductServiceImpl() // Initialize your ProductServiceImpl

    private lateinit var categoryAdapter: CategoryAdapter

    private var selectedCategory: String = ""

    val categories = listOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the image upload launcher
        imageUploadLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data ?: Uri.EMPTY
                binding.imageName.text = imageUri.lastPathSegment?.substringAfterLast("/") ?: "Selected Image" // Display the image name
                binding.selectedImage.setImageURI(imageUri) // Set the selected image to ImageView
            }
        }

        arguments?.let {
            product = it.getParcelable(AdminProductDetailsFragment.PRODUCT_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateProductBinding.inflate(inflater, container, false)

        localUser = LocalUser.getInstance(requireContext())

        categoryAdapter = CategoryAdapter {
                category -> selectedCategory = category
        }

        categoryAdapter.updateCategories(categories)

        binding.categoryList.adapter = categoryAdapter
        binding.categoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        loadProductData()

        return binding.root
    }

    private fun loadProductData(){
        binding.propertyName.setText(product?.name)
        binding.productDescription.setText(product?.description)
        binding.propertyPrice.setText(product?.price.toString())

        binding.imageName.setText(product?.name)
        Glide.with(requireContext())
            .load(product?.imageUrl) // Load the image from the URL
            .placeholder(R.drawable.baseline_image_24) // Optional: Add a placeholder
            //.error(R.drawable.error_image) // Optional: Add an error image
            .into(binding.selectedImage)
        binding.productQuantity.setText(product?.quantity.toString())
        Log.d("setSelectedItem", "${categories.indexOf(product?.category)}")
        categoryAdapter.setSelectedItem(categories.indexOf(product?.category))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ownerRedirect.setOnClickListener {
            selectImage()
        }

        binding.updateButton.setOnClickListener {
            updateProduct()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imageUploadLauncher.launch(intent)
    }

    private fun updateProduct() {
        val productName = binding.propertyName.text.toString().trim()
        val productDescription = binding.productDescription.text.toString().trim()
        val productPrice = binding.propertyPrice.text.toString().trim()
        val productQuantity = binding.productQuantity.text.toString().trim()

        if (productName.isEmpty() || productDescription.isEmpty() || productPrice.isEmpty() || productQuantity.isEmpty() || selectedCategory.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare the product object with updated values
        val updatedProduct = product!!.copy(
            name = productName,
            description = productDescription,
            price = productPrice.toDouble(),
            quantity = productQuantity.toInt(),
            category = selectedCategory,
            imageUrl = product!!.imageUrl // Use existing image URL or update if a new image is uploaded
        )

        // Check if an image was selected to upload
        if (::imageUri.isInitialized) {
            uploadImageAndUpdateProduct(updatedProduct)
        } else {
            updateProductWithoutImage(updatedProduct)
        }
    }

    private fun uploadImageAndUpdateProduct(product: Product) {
        // Similar image upload code as in the create product
        val tempFile = getFileFromUri(imageUri)

        if (tempFile == null || !tempFile.exists()) {
            Toast.makeText(requireContext(), "Failed to process selected image", Toast.LENGTH_SHORT).show()
            return
        }

        val filePart = MultipartBody.Part.createFormData(
            "image",
            tempFile.name,
            RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
        )

        binding.progressBar.visibility = View.VISIBLE


        val productJson = Gson().toJson(product)
        val productRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), productJson)

        val token = localUser.getToken()
        if (token != null) {
            val tokenVal = "Bearer $token"
            productServiceImpl.updateProduct(tokenVal, product.id, productRequestBody, filePart).enqueue(object :
                Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful) {
                        binding.progressBar.visibility = View.GONE

                        Toast.makeText(requireContext(), "Product updated: ${response.body()?.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.progressBar.visibility = View.GONE

                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.d("UpdateProductFragment", "Failed to update product: ${response.message()} ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to update product: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProductFragment", "Failed to update product: ${t.message}")
                }
            })
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun updateProductWithoutImage(product: Product) {
        val productJson = Gson().toJson(product)
        val productRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), productJson)

        val token = localUser.getToken()
        if (token != null) {
            val tokenVal = "Bearer $token"
            productServiceImpl.updateProduct(tokenVal, product.id, productRequestBody, null).enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Product updated: ${response.body()?.name}", Toast.LENGTH_SHORT).show()
                        Log.d("UpdateProductFragment", "Product updated: ${response.body()?.name}} ${response.code()}")
                        changeCurrentFragment(AdminProductsFragment())
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.d("UpdateProductFragment", "Failed to update product: ${response.message()} ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to update product: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProductFragment", "Failed to update product: ${t.message}")
                }
            })
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            inputStream?.let {
                val tempFile = File.createTempFile("image", ".jpg", context?.cacheDir)
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile
            }
        } catch (e: Exception) {
            null
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PRODUCT_ARG = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            UpdateProductFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCT_ARG, product)
                }
            }
    }
}