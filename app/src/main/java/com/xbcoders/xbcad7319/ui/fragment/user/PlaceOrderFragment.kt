package com.xbcoders.xbcad7319.ui.fragment.user

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.api.seviceimplementations.OrderServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentPlaceOrderBinding
import com.xbcoders.xbcad7319.ui.adapter.CartItemAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


class PlaceOrderFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentPlaceOrderBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var orderServiceImpl: OrderServiceImpl


    private lateinit var localUser: LocalUser

    private var cartItems: MutableList<CartItem>? = mutableListOf()

    private lateinit var cartAdapter: CartItemAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cartItems = it.getParcelableArrayList(CART_ITEMS_ARG)
        }

        localUser = LocalUser.getInstance(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceOrderBinding.inflate(inflater, container, false)

        orderServiceImpl = OrderServiceImpl()

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartItemAdapter { cartItem, action ->  }
        cartItems?.let { cartAdapter.updateCartItems(it) }

        binding.productsRecyclerView.adapter = cartAdapter

        loadOrderInfo()

        binding.submitButton.setOnClickListener {
            placeOrder()
        }
        return binding.root
    }

    private fun loadOrderInfo(){
        val total = cartItems!!.sumOf { it.product.price * it.quantity }
        binding.totalPrice.text = "R ${total}"

        binding.vat.text = "15%"

        val subTotal = (total * 85)/100

        binding.subTotal.text = "R ${subTotal}"

        val username = localUser.getUser()?.username

        val email = localUser.getUser()?.email

        binding.username.text = username

        binding.email.text = email
    }

    private fun proceedToPayment(order: Order) {


        val paymentFragment = order.let { PaymentFragment.newInstance(it) }
        changeCurrentFragment(paymentFragment)

    }

    private fun placeOrder() {
        val userId = localUser.getUser()?.id // Replace with actual user ID
        binding.progressBar.visibility = View.VISIBLE

        val address = binding.address.text.toString()

        if(address.isEmpty()){
            binding.progressBar.visibility = View.GONE

            Toast.makeText(requireContext(), "Enter an address", Toast.LENGTH_SHORT).show()
            binding.address.error = "Address is required"
            return
        }

        val totalPrice = cartItems!!.sumOf { it.product.price * it.quantity } // Calculate total price
        val order = userId?.let {
            Order(
                userId = it,
                items = cartItems!!,
                totalPrice = totalPrice,
                address = address,
                orderNo = generateOrderNumber()
            )
        }

        showOrderSuccessPopup(order!!)

    }

    private fun showOrderSuccessPopup(order: Order) {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.dialog_order_success, null)

        // Set up the text and button in the custom view
        val orderNumberTextView: TextView = view.findViewById(R.id.order_number_textview)
        val payNowButton: Button = view.findViewById(R.id.pay_now_button)

        orderNumberTextView.text = "Your order number is : ${order.orderNo}"

        // Create an AlertDialog and set the custom view
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(true)
            .create()

        // Show the dialog
        dialog.show()

        // Set the onClick action for the "Pay Now" button
        payNowButton.setOnClickListener {
            // Handle pay now action
            dialog.dismiss()
            proceedToPayment(order)
            // Navigate to payment or perform necessary action
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

        const val CART_ITEMS_ARG = "cartItems"
        @JvmStatic
        fun newInstance(cartItems: List<CartItem>) =
            PlaceOrderFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(CART_ITEMS_ARG, ArrayList(cartItems))
                }
            }

        fun generateOrderNumber(): String {
            val uuid = UUID.randomUUID().toString().substring(0, 8) // Get the first 8 characters of UUID
            return "ORD$uuid"
        }
    }
}