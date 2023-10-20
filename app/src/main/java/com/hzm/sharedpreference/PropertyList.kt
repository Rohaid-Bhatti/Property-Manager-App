package com.hzm.sharedpreference

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PropertyList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var propertyListAdapter: PropertyListAdapter
    private lateinit var etSearch: EditText
    private lateinit var radioGroupFurnished: RadioGroup
    private lateinit var radioGroupType: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_list)

        recyclerView = findViewById(R.id.recyclerViewPropertyList)
        propertyListAdapter = PropertyListAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = propertyListAdapter

        // Initialize your property DAO and location DAO
        val propertyDao = PropertyDatabase.getDatabase(this).propertyDao()

        // Search EditText
        etSearch = findViewById(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the list based on the user's input
                val searchQuery = s.toString()
                loadDataFromDatabase(searchQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Radio Groups
        radioGroupFurnished = findViewById(R.id.radioGroupFurnished)
        radioGroupType = findViewById(R.id.radioGroupType)

        // Set listeners for radio group changes
        radioGroupFurnished.setOnCheckedChangeListener { _, checkedId ->
            val filterCriteria = when (checkedId) {
                R.id.radioFurnished -> "Furnished"
                R.id.radioNotFurnished -> "Not Furnished"
                else -> "" // Handle default case or no selection
            }
            filterByFurnished(filterCriteria)
        }

        radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            val filterCriteria = when (checkedId) {
                R.id.radioSale -> "Sale"
                R.id.radioRent -> "Rent"
                else -> "" // Handle default case or no selection
            }
            filterByType(filterCriteria)
        }

        CoroutineScope(Dispatchers.IO).launch {
            // Fetch data from both tables using DAOs and combine them
            val propertiesWithLocations = propertyDao.getAllPropertiesWithLocations()

            // Update the adapter with the combined data
            propertyListAdapter.submitList(propertiesWithLocations)

            runOnUiThread {
                // Set the unfiltered property list in the adapter
                propertyListAdapter.setUnfilteredPropertyList(propertiesWithLocations)
            }
        }
    }

    private fun loadDataFromDatabase(searchQuery: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = PropertyDatabase.getDatabase(this@PropertyList)

            // Query the database for filtered data
            val propertyList = database.propertyDao().getPropertiesWithLocationsByQuery(searchQuery)

            runOnUiThread {
                // Update the adapter with the filtered data
                if (propertyList.isEmpty()) {
                    // If no matching data is found, update the adapter with an empty list
                    propertyListAdapter.submitList(emptyList())
                } else {
                    // Update the adapter with the filtered data
                    propertyListAdapter.submitList(propertyList)
                }
            }
        }
    }

    private fun filterByFurnished(criteria: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = PropertyDatabase.getDatabase(this@PropertyList)

            // Query the database for filtered data based on furnished criteria
            val filteredList = database.propertyDao().getPropertiesWithLocationsByFurnished(criteria)

            runOnUiThread {
                // Update the adapter with the filtered data
                propertyListAdapter.submitList(filteredList)
            }
        }
    }

    private fun filterByType(criteria: String) {
        // Use the adapter's filter function to filter by Sale or Rent
//        propertyListAdapter.filterByType(criteria)
        CoroutineScope(Dispatchers.IO).launch {
            val database = PropertyDatabase.getDatabase(this@PropertyList)

            // Query the database for filtered data based on type criteria
            val filteredList = database.propertyDao().getPropertiesWithLocationsByType(criteria)

            runOnUiThread {
                // Update the adapter with the filtered data
                propertyListAdapter.submitList(filteredList)
            }
        }
    }
}