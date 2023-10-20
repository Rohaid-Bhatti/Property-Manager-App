package com.hzm.sharedpreference

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzm.sharedpreference.roomDb.Contact

class UserListActivity : AppCompatActivity(), UserListAdapter.OnItemClickListener {
    private lateinit var contactViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val adapter = UserListAdapter(this) // Pass 'this' (activity) as the click listener
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the ViewModel
        contactViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Observe the LiveData from ViewModel
        contactViewModel.getAllContacts().observe(this, Observer { contacts ->
            // Update the cached copy of the contacts in the adapter
            adapter.setContacts(contacts)
        })
    }

    override fun onItemClick(contact: Contact) {
        // Handle item click here
        // For example, you can open a new activity and pass the clicked contact's data
        val phoneNumber = contact.number // Retrieve the phone number

        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:" + Uri.encode(phoneNumber))
        )
        startActivity(intent)
    }
}