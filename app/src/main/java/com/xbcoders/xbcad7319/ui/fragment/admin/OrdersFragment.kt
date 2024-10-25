package com.xbcoders.xbcad7319.ui.fragment.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.api.seviceimplementations.OrderServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentOrdersBinding
import com.xbcoders.xbcad7319.databinding.FragmentUserOrdersBinding
import com.xbcoders.xbcad7319.ui.adapter.OrderAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderServiceImpl: OrderServiceImpl
    private lateinit var localUser: LocalUser

    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localUser = LocalUser.getInstance(requireContext()) // Initialize your LocalUser here
        orderServiceImpl = OrderServiceImpl(/* Pass your OrderService implementation here */)
    }

    private var orders: List<Order>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)

        // Fetch user orders
        fetchUserOrders()

        return binding.root
    }

    private fun fetchUserOrders() {
        val token = localUser.getToken() // Retrieve the user's token

        Log.d("orders", "token ${token}\nuser: ${localUser.getUser()}")


        if (token != null) {
            val tokenVal = "Bearer ${localUser.getToken()}"
            orderServiceImpl.getOrders(tokenVal).enqueue(object :
                Callback<List<Order>> {
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
            Toast.makeText(requireContext(), "Missing token or user ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayOrders(orders: List<Order>?) {
        if(orders != null){
            orderAdapter = OrderAdapter(orders) { order ->
                // Handle product click event
                Toast.makeText(requireContext(), "Selected: ${order.orderNo}", Toast.LENGTH_SHORT)
                    .show()

                showChangeStatusDialog(order)
            }
            binding.orderRecyclerView.adapter = orderAdapter
            binding.orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        }
    }


    private fun showChangeStatusDialog(order: Order) {
        val context = binding.root.context
        val dialogView = LayoutInflater.from(context).inflate(R.layout.update_status_item, null)
        val statusSwitch = dialogView.findViewById<Switch>(R.id.statusSwitch)

        // Set the initial state of the Switch based on the order status
        statusSwitch.isChecked = order.status == "Delivered"

        // Disable the Switch if the status is "Delivered"
        if (order.status == "Delivered") {
            statusSwitch.isEnabled = false
        }

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Get the new status based on the switch state
                val newStatus = if (statusSwitch.isChecked) "Delivered" else "Pending"
                // Call the function to update the order status
                updateOrderStatus(order.id, newStatus)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }

    private fun updateOrderStatus(orderId: String, newStatus: String) {
        val token = localUser.getToken()

        Toast.makeText(requireContext(), "orderId: ${orderId}", Toast.LENGTH_LONG).show()

        if (token != null) {
            val tokenVal = "Bearer $token"

            val ord = Order(
                status= newStatus
            )
            // Call your OrderService implementation to update the order status
            orderServiceImpl.updateOrderStatus(tokenVal, orderId, ord).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Order status updated successfully", Toast.LENGTH_SHORT).show()
                        // Optionally, refresh the orders list or update the local list
                        fetchUserOrders() // Refresh orders to reflect the changes
                    } else {
                        Log.d("orders", "Failed to fetch orders: ${response.message()} ${response.code()}")

                        Toast.makeText(requireContext(), "Failed to update status: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Missing token", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}