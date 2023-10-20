package com.hzm.sharedpreference

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hzm.sharedpreference.dataClass.PropertyListItem
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyDatabase
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PropertyListAdapter:
    ListAdapter<PropertyListItem, PropertyListAdapter.PropertyViewHolder>(PropertyDiffCallback()) {

    private var unfilteredPropertyList: List<PropertyListItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = getItem(position)
        holder.bind(property)
    }

    fun setUnfilteredPropertyList(list: List<PropertyListItem>) {
        unfilteredPropertyList = list
        submitList(list)
    }

    /*fun filterBySearchQuery(query: String) {
        val filteredList = unfilteredPropertyList.filter { property ->
            // Check if the query matches any property or location details
            property.address.contains(query, true) ||
                    property.city.contains(query, true) ||
                    property.type.contains(query, true) ||
                    property.interior.contains(query, true) ||
                    property.size.contains(query, true)
        }
        submitList(filteredList)

        val filteredProperties = propertyDAO.searchProperties(query)
        submitList(filteredProperties)
    }

    fun filterByFurnished(criteria: String) {
        val filteredList = unfilteredPropertyList.filter { property ->
            // Filter based on Furnished or Not Furnished
            criteria.isEmpty() || property.interior.equals(criteria, ignoreCase = true)
        }
        submitList(filteredList)
    }

    fun filterByType(criteria: String) {
        val filteredList = unfilteredPropertyList.filter { property ->
            // Filter based on Sale or Rent
            criteria.isEmpty() || property.type.equals(criteria, ignoreCase = true)
        }
        submitList(filteredList)
    }*/

    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewRoom: TextView = itemView.findViewById(R.id.textViewRoom)
        private val textViewBed: TextView = itemView.findViewById(R.id.textViewBed)
        private val textViewBath: TextView = itemView.findViewById(R.id.textViewBath)
        private val textViewFloor: TextView = itemView.findViewById(R.id.textViewFloor)
        private val textViewType: TextView = itemView.findViewById(R.id.textViewType)
        private val textViewInterior: TextView = itemView.findViewById(R.id.textViewInterior)
        private val textViewSize: TextView = itemView.findViewById(R.id.textViewSize)
        private val textViewArea: TextView = itemView.findViewById(R.id.textViewArea)
        private val textViewUserEmail: TextView = itemView.findViewById(R.id.textViewUserEmail)
        val btnEdit = itemView.findViewById<Button>(R.id.btnEdit)
        val btnDelete = itemView.findViewById<Button>(R.id.btnDelete)

        fun bind(property: PropertyListItem) {
            textViewRoom.text = "Rooms: ${property.numberOfRoom}"
            textViewBed.text = "Bedrooms: ${property.numberOfBedroom}"
            textViewBath.text = "Bathrooms: ${property.numberOfBathroom}"
            textViewFloor.text = "Floor: ${property.floor}"
            textViewType.text = "Type: ${property.type}"
            textViewInterior.text = "Interior: ${property.interior}"
            textViewSize.text = "Size: ${property.size}"
            textViewArea.text = "Area: ${property.address}, ${property.city}"
            textViewUserEmail.text = "User Email: ${property.userEmail}"

            btnEdit.setOnClickListener {
                // Handle the "Edit" button click here
                onEditClick(property)
            }

            btnDelete.setOnClickListener {
                // Handle the "Delete" button click here
                onDeleteClick(property)
            }
        }
        // Function to handle "Edit" button click
        private fun onEditClick(property: PropertyListItem) {
            // Implement your edit functionality here, e.g., open an edit activity/fragment
            val intent = Intent(itemView.context, EditPropertyActivity::class.java)
            intent.putExtra("propertyId", property.houseId)
            itemView.context.startActivity(intent)
        }

        // Function to handle "Delete" button click
        private fun onDeleteClick(property: PropertyListItem) {
            // Implement your delete functionality here, e.g., show a confirmation dialog
            val alertDialog = AlertDialog.Builder(itemView.context)
            alertDialog.setTitle("Confirm Deletion")
            alertDialog.setMessage("Are you sure you want to delete this property?")

            alertDialog.setPositiveButton("Yes") { _, _ ->
                // User confirmed deletion, delete the property from both tables
                CoroutineScope(Dispatchers.IO).launch {
                    val propertyDao = PropertyDatabase.getDatabase(itemView.context).propertyDao()
                    propertyDao.deleteProperty(PropertyEntity(
                        houseId = property.houseId,
                        numberOfRoom = property.numberOfRoom,
                        numberOfBedroom = property.numberOfBedroom,
                        numberOfBathroom = property.numberOfBathroom,
                        floor = property.floor,
                        type = property.type,
                        interior = property.interior,
                        size = property.size,
                        userEmail = property.userEmail))
                    propertyDao.deleteLocationByHouseId(property.houseId)
                }
                Toast.makeText(itemView.context, "Delete Successfully", Toast.LENGTH_SHORT).show()
            }

            alertDialog.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            alertDialog.show()
        }
    }
}

class PropertyDiffCallback : DiffUtil.ItemCallback<PropertyListItem>() {
    override fun areItemsTheSame(oldItem: PropertyListItem, newItem: PropertyListItem): Boolean {
        return oldItem.houseId == newItem.houseId
    }

    override fun areContentsTheSame(oldItem: PropertyListItem, newItem: PropertyListItem): Boolean {
        return oldItem == newItem
    }
}