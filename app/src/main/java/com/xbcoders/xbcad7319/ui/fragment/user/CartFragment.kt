package com.xbcoders.xbcad7319.ui.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.api.seviceimplementations.CartItemServiceImpl
import com.xbcoders.xbcad7319.api.seviceimplementations.OrderServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentCartBinding
import com.xbcoders.xbcad7319.ui.CartAction
import com.xbcoders.xbcad7319.ui.adapter.CartItemAdapter
import com.xbcoders.xbcad7319.ui.adapter.OrderAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartFragment : Fragment() {
    private lateinit var cartAdapter: CartItemAdapter
    private lateinit var cartItemService: CartItemServiceImpl
    private lateinit var orderServiceImpl: OrderServiceImpl


    private lateinit var localUser: LocalUser

    // View binding object for accessing views in the layout
    private var _binding: FragmentCartBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!


    private lateinit var cartItems: MutableList<CartItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        cartItems = mutableListOf()

        localUser = LocalUser.getInstance(requireContext())

        cartItemService = CartItemServiceImpl()
        orderServiceImpl = OrderServiceImpl()

        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        cartAdapter = CartItemAdapter(cartItems) { cartItem, action -> handleCartItemAction(cartItem, action) }

        binding.cartRecyclerView.adapter = cartAdapter

        fetchCartItems()

        binding.checkout.setOnClickListener {
            placeOrder()
        }

        return binding.root
    }

    private fun fetchCartItems() {
        val token = localUser.getToken()
        val userId = localUser.getUser()?.id

        if (token != null && userId != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            cartItemService.getUserCartItems(tokenVal, userId).enqueue(object : Callback<List<CartItem>> {
                override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                    if (response.isSuccessful && response.body() != null) {
                        cartAdapter.updateCartItems(response.body()!!)
                        cartItems.clear()
                        cartItems.addAll(response.body()!!)
                    } else {
                        Log.d("", "Failed to add item to cart: ${response.message()} ${response.code()}")
                        Toast.makeText(requireContext(), "Failed to load cart items ${response.message()} ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                    Log.d("error", "Error: ${t.message}")

                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun handleCartItemAction(cartItem: CartItem, action: CartAction) {
        when (action) {
            CartAction.INCREASE -> {
                cartItem.quantity++
                updateCartItem(cartItem)
            }
            CartAction.DECREASE -> {
                if (cartItem.quantity > 1) {
                    cartItem.quantity--
                    updateCartItem(cartItem)
                }
            }
            CartAction.REMOVE -> {
                removeCartItem(cartItem.id)
            }
            CartAction.VIEW -> {
                redirectToDetails(cartItem)
            }
        }
    }

    private fun updateCartItem(cartItem: CartItem) {
        val token = localUser.getToken()
        if (token != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            cartItemService.updateCartItem(tokenVal, cartItem.id, cartItem).enqueue(object :
                Callback<List<CartItem>> {
                override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                    if (!response.isSuccessful) {
                        Log.d("", "Failed to add item to cart: ${response.message()} ${response.code()}")
                        Toast.makeText(requireContext(), "Failed to update cart item", Toast.LENGTH_SHORT).show()
                    } else {
                        displayCart(response.body())
                    }
                }

                override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun removeCartItem(cartItemId: String) {
        val token = localUser.getToken()
        if (token != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            cartItemService.removeItemFromCart(tokenVal, cartItemId).enqueue(object : Callback<List<CartItem>> {
                override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                    if (response.isSuccessful) {
                        cartAdapter.removeCartItem(cartItemId)
                        Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("", "Failed to add item to cart: ${response.message()} ${response.code()}")
                        Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun redirectToDetails(cartItem: CartItem){

    }

    private fun displayCart(cart: List<CartItem>?) {
        if(cart != null){
            cartAdapter = CartItemAdapter(cart.toMutableList()) { cartItem, action -> handleCartItemAction(cartItem, action)
                // Handle product click event

            }
            binding.cartRecyclerView.adapter = cartAdapter
            binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        }
    }

    private fun placeOrder() {
        // Pass the sorted messages and chatId to the MessagesFragment
        val placeOrderFragment = PlaceOrderFragment.newInstance(cartItems)
        changeCurrentFragment(placeOrderFragment)
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