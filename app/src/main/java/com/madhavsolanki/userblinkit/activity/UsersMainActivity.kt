package com.madhavsolanki.userblinkit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.madhavsolanki.userblinkit.CartListener
import com.madhavsolanki.userblinkit.R

class UsersMainActivity : AppCompatActivity(), CartListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_main)

    }
}