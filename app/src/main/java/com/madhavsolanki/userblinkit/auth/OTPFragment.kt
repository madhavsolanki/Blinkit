package com.madhavsolanki.userblinkit.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madhavsolanki.userblinkit.R
import com.madhavsolanki.userblinkit.Utils
import com.madhavsolanki.userblinkit.activity.UsersMainActivity
import com.madhavsolanki.userblinkit.databinding.FragmentOTPBinding
import com.madhavsolanki.userblinkit.models.Users
import com.madhavsolanki.userblinkit.viewmodels.AuthViewModel
import kotlinx.coroutines.launch


class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding

    private lateinit var userNumber:String

    private val viewModel:AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOTPBinding.inflate(inflater, container, false)
        setStatusBarColor()

        getUserNumber()

        customizingEnteringOTP()

        sentOTP()

        onLoginButtonClicked()

        onBackButtonClicked()

        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing you...")
            val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)
           val otp =  editTexts.joinToString("") {it.text.toString() }

            if (otp.length < editTexts.size){
                Utils.showToast(requireContext(),"Please enter right OTP")
            }else{
                editTexts.forEach { it.text?.clear(); it.clearFocus() }
                verifyOtp(otp)
            }
        }
    }

    private fun verifyOtp(otp: String) {

        val user  =Users(uid = Utils.getCurrentUserId(), userPhoneNumber = userNumber, userAddress = null)

        viewModel.signInWithPhoneAuthCredential(otp,userNumber, user)

        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect{
                if (it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext(),"Logged in")
                    startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sentOTP() {
        Utils.showDialog(requireContext(), "Sending OTP...")
        viewModel.apply {
            sendOTP(userNumber,requireActivity())
            lifecycleScope.launch {
                otpSent.collect{otpSent->
                    if (otpSent == true){
                        Utils.hideDialog()
                        Utils.showToast(requireContext(),"Otp Sent..")
                    }
                }
            }

        }

    }

    private fun onBackButtonClicked() {

        binding.tbOtpFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }
    }

    private fun customizingEnteringOTP() {

        val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)

        for (i in editTexts.indices){
            editTexts[i].addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun afterTextChanged(s: Editable?) {

                    if (s?.length == 1){
                        if (i < editTexts.size-1){
                            editTexts[i+1].requestFocus()
                        }
                    }else{
                        if (i > 0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }
            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments

        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}