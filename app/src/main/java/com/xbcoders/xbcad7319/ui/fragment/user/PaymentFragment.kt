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
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.api.seviceimplementations.OrderServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentPaymentBinding
import com.xbcoders.xbcad7319.databinding.FragmentPlaceOrderBinding
import com.xbcoders.xbcad7319.ui.fragment.user.PlaceOrderFragment.Companion
import com.xbcoders.xbcad7319.ui.fragment.user.PlaceOrderFragment.Companion.generateOrderNumber
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PaymentFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentPaymentBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var orderServiceImpl: OrderServiceImpl


    private lateinit var localUser: LocalUser


    private  var order: Order? = Order()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            order = it.getParcelable(ORDER_ARG)
        }

        localUser = LocalUser.getInstance(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)

        orderServiceImpl = OrderServiceImpl()


        binding.payBtn.setOnClickListener {
            pay()
        }

        return binding.root
    }

    private fun pay() {
        var errors = 0
        val nameOnCard = binding.nameOnCard.text.toString()
        val cardNo = binding.cardNo.text.toString()
        val cvv = binding.cvv.text.toString()
        val expiryDate = binding.expiryDate.text.toString()

        if (nameOnCard.isEmpty()) {
            binding.nameOnCard.error = "Enter name on card"
            errors++
        }

        // Check if card number is entered and is 16 digits
        val cardNoPattern = Regex("^\\d{16}$")
        if (cardNo.isEmpty()) {
            binding.cardNo.error = "Enter a card number"
            errors++

        } else if (!cardNoPattern.matches(cardNo)) {
            binding.cardNo.error = "Card number must be 16 digits"
            errors++

        }

        // Check if CVV is entered and is 3 digits
        val cvvPattern = Regex("^\\d{3}$")
        if (cvv.isEmpty()) {
            binding.cvv.error = "Enter a cvv"
            errors++

        } else if (!cvvPattern.matches(cvv)) {
            binding.cvv.error = "cvv must be 3 digits"
            errors++

        }

        // Check if expiry date is in MM/YY format
        val expiryDatePattern = Regex("^(0[1-9]|1[0-2])/\\d{2}$")
        if (expiryDate.isEmpty()) {
            binding.expiryDate.error = "Enter an expiry date"
            errors++

        } else if (!expiryDatePattern.matches(expiryDate)) {
            binding.expiryDate.error = "Expiry date format: MM/YY"
            errors++

        }

        if(errors > 0){
            return
        }

        handlePayment()
    }

    private fun handlePayment(){
        // Prepare the Order data
        val token = localUser.getToken()// Replace with actual token


        if (token != null && order != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            orderServiceImpl.placeOrder(tokenVal, order!!).enqueue(object : Callback<Order> {
                override fun onResponse(call: Call<Order>, response: Response<Order>) {
                    if (response.isSuccessful) {
                        // Handle success, navigate to order summary or show a success message
                        //Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE

                        showOrderSuccessPopup(order!!.orderNo )
                        // Optionally navigate to order summary or clear cart
                    } else {
                        binding.progressBar.visibility = View.GONE

                        // Handle failure (error response)
                        Toast.makeText(requireContext(), "Payment failed", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Order>, t: Throwable) {
                    // Handle network failure
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }
    }

    private fun showOrderSuccessPopup(orderNumber: String) {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.dialog_order_success, null)

        // Set up the text and button in the custom view
        val orderNumberTextView: TextView = view.findViewById(R.id.order_number_textview)
        val payNowButton: Button = view.findViewById(R.id.pay_now_button)

        orderNumberTextView.text = "Payment successful"
        payNowButton.text = "OK"

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
            changeCurrentFragment(UserOrdersFragment())

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

        const val CART_ITEMS_ARG = "product"
        const val ADDRESS_ARG = "product"
        const val ORDER_ARG = "order"

        @JvmStatic
        fun newInstance(order: Order) =
            PaymentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ORDER_ARG, order)
                }
            }
    }
}