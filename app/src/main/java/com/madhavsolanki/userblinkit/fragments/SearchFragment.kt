package com.madhavsolanki.userblinkit.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madhavsolanki.userblinkit.R
import com.madhavsolanki.userblinkit.adapters.AdapterProduct
import com.madhavsolanki.userblinkit.databinding.FragmentSearchBinding
import com.madhavsolanki.userblinkit.models.Product
import com.madhavsolanki.userblinkit.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    val viewModel: UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)


        getAllTheProducts()

        backToHomeFragment()

        searchProduct()
        return binding.root
    }

    private fun searchProduct() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                adapterProduct.filter.filter(query)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun backToHomeFragment() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun getAllTheProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts().collect {
                if (it.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                    binding.shimmerViewContainer.visibility = View.GONE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE

                    adapterProduct = AdapterProduct(::onAddProductClicked)
                    binding.rvProducts.adapter = adapterProduct
                    adapterProduct.differ.submitList(it)
                    adapterProduct.originalList = it as ArrayList<Product>
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }
    }

}