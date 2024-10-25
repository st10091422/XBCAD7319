package com.xbcoders.xbcad7319.ui.fragment.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.seviceimplementations.ProductServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentCreateProductBinding
import com.xbcoders.xbcad7319.ui.adapter.CategoryAdapter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class CreateProductFragment : Fragment() {
    private var _binding: FragmentCreateProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageUri: Uri
    private lateinit var imageUploadLauncher: ActivityResultLauncher<Intent>
    private val productServiceImpl = ProductServiceImpl() // Initialize your ProductServiceImpl

    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var localUser: LocalUser

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentCreateProductBinding.inflate(inflater, container, false)

        localUser = LocalUser.getInstance(requireContext())

        categoryAdapter = CategoryAdapter(categories) {
            category -> selectedCategory = category
        }

        binding.categoryList.adapter = categoryAdapter
        binding.categoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Image selection on click
        binding.ownerRedirect.setOnClickListener {
            selectImage()
        }

        binding.submitButton.setOnClickListener {
            addProduct()
        }
    }

    private fun selectImage() {
        // Intent to pick an image from the gallery
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imageUploadLauncher.launch(intent)
    }

    private fun addProduct() {
        // Retrieve input data from EditTexts
        val productName = binding.propertyName.text.toString().trim()
        val productDescription = binding.productDescription.text.toString().trim()
        val productPrice = binding.propertyPrice.text.toString().trim()
        val productQuantity = binding.productQuantity.text.toString().trim()

        // Validate inputs
        if (productName.isEmpty() || productDescription.isEmpty() || productPrice.isEmpty() || productQuantity.isEmpty() || selectedCategory.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare the product object
        val product = Product(
            name = productName,
            description = productDescription,
            price = productPrice.toDouble(),
            quantity = productQuantity.toInt(),
            category = selectedCategory,
            imageUrl = "" // We will fill this later if needed
        )

        // Upload the image and create the product
        uploadImageAndCreateProduct(product)
    }

    private fun uploadImageAndCreateProduct(product: Product) {
        if (!::imageUri.isInitialized) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Use the helper function to get a file from the Uri
        val tempFile = getFileFromUri(imageUri)

        if (tempFile == null || !tempFile.exists()) {
            Toast.makeText(requireContext(), "Failed to process selected image", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a RequestBody instance from the image file
        val filePart = MultipartBody.Part.createFormData(
            "image", // API parameter name
            tempFile.name,
            RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
        )

        // Prepare the product JSON request body
        val productJson = Gson().toJson(product)
        val productRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), productJson)

        // Call the createProduct method from ProductServiceImpl
        val token = localUser.getToken() // Get the token

        if (token != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            productServiceImpl.createProduct(tokenVal, productRequestBody, filePart).enqueue(object :
                Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Product added: ${response.body()?.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.d("", "Failed to create product: ${response.message()} ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to create product: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("", "Failed to create product: ${t.message}")
                }
            })
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity if token is not available
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to prevent memory leaks
    }
}