package com.xbcoders.xbcad7319.ui.fragment.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.api.seviceimplementations.OrderServiceImpl
import com.xbcoders.xbcad7319.databinding.FragmentPaymentBinding
import com.xbcoders.xbcad7319.databinding.FragmentPlaceOrderBinding
import com.xbcoders.xbcad7319.ui.fragment.user.PlaceOrderFragment.Companion
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

    private var cartItems: List<CartItem>? = mutableListOf()

    private var address: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cartItems = it.getParcelableArrayList(CART_ITEMS_ARG)
            address = it.getString(ADDRESS_ARG).toString()
        }

        localUser = LocalUser.getInstance(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)

        orderServiceImpl = OrderServiceImpl()



        return binding.root
    }

    companion object {

        const val CART_ITEMS_ARG = "product"
        const val ADDRESS_ARG = "product"
        @JvmStatic
        fun newInstance(cartItems: MutableList<CartItem>, address: String) =
            PaymentFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(CART_ITEMS_ARG, ArrayList(cartItems))
                    putString(ADDRESS_ARG, address)
                }
            }
    }
}