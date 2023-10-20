package com.hzm.sharedpreference

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hzm.sharedpreference.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        binding.btnUser.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        binding.btnPropertyAdd.setOnClickListener {
            val intent1 = Intent(this, PropertyActivity::class.java)
            startActivity(intent1)
        }

        binding.btnPropertyView.setOnClickListener {
            val intent = Intent(this, PropertyList::class.java)
            startActivity(intent)
        }
    }
}