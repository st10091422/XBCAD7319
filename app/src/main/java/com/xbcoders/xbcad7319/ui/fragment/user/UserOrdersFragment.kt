package com.xbcoders.xbcad7319.ui.fragment.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.api.seviceimplementations.OrderServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentUserOrdersBinding
import com.xbcoders.xbcad7319.ui.adapter.OrderAdapter
import com.xbcoders.xbcad7319.ui.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserOrdersFragment : Fragment() {

    private var _binding: FragmentUserOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderServiceImpl: OrderServiceImpl
    private lateinit var localUser: LocalUser

    private lateinit var orderAdapter: OrderAdapter

    private var orders: List<Order>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localUser = LocalUser.getInstance(requireContext()) // Initialize your LocalUser here
        orderServiceImpl = OrderServiceImpl(/* Pass your OrderService implementation here */)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserOrdersBinding.inflate(inflater, container, false)

        // Fetch user orders
        fetchUserOrders()

        return binding.root
    }

    private fun fetchUserOrders() {
        val token = localUser.getToken() // Retrieve the user's token
        val userId = localUser.getUser()?.id // Retrieve the user's ID

        Log.d("orders", "token ${token}\nuser: ${localUser.getUser()}")


        if (token != null && userId != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            orderServiceImpl.getUserOrders(tokenVal, userId).enqueue(object : Callback<List<Order>> {
                override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                    if (response.isSuccessful) {
                        orders = response.body()
                        // Handle successful response, e.g., update the UI to display orders
                        displayOrders(orders)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch orders: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()

                        Log.d("orders", "Failed to fetch orders: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                    Log.d("orders", "Error: ${t.message}")
                }
            })
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }
    }

    private fun displayOrders(orders: List<Order>?) {
        if(orders != null){
            orderAdapter = OrderAdapter(orders) { order ->
                // Handle product click event
                Toast.makeText(requireContext(), "Selected: ${order.orderNo}", Toast.LENGTH_SHORT)
                    .show()
            }
            binding.orderRecyclerView.adapter = orderAdapter
            binding.orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}