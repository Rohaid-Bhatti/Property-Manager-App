package com.hzm.sharedpreference

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.hzm.sharedpreference.propertyWork.propertyDB.LocationEntity
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyDAO
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyDatabase
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPropertyActivity : AppCompatActivity() {
    private lateinit var propertyDao: PropertyDAO
    private var propertyId: Long = -1

    private lateinit var etNumberOfRooms: EditText
    private lateinit var etNumberOfBedroom: EditText
    private lateinit var etNumberOfBathroom: EditText
    private lateinit var etNumberOfFloor: EditText
    private lateinit var etAddress: EditText
    private lateinit var etCity: EditText
    private lateinit var radioGroupType: RadioGroup
    private lateinit var radioSale: RadioButton
    private lateinit var radioRent: RadioButton
    private lateinit var radioGroupFurnished: RadioGroup
    private lateinit var radioFurnished: RadioButton
    private lateinit var radioNotFurnished: RadioButton
    private lateinit var spinnerSize: Spinner
    private lateinit var btnSave: Button
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_property)

        propertyDao = PropertyDatabase.getDatabase(this).propertyDao()

        // Retrieve the propertyId from the intent
        propertyId = intent.getLongExtra("propertyId", -1)

        if (propertyId == -1L) {
            // Invalid propertyId, handle accordingly (e.g., show an error message)
            finish()
        }

        val sharedPreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        userEmail = sharedPreference.getString("USER_EMAIL", "")!!

        // Initialize UI elements
        etNumberOfRooms = findViewById(R.id.etNumberOfRooms)
        etNumberOfBedroom = findViewById(R.id.etNumberOfBedroom)
        etNumberOfBathroom = findViewById(R.id.etNumberOfBathroom)
        etNumberOfFloor = findViewById(R.id.etNumberOfFloor)
        etAddress = findViewById(R.id.etAddress)
        etCity = findViewById(R.id.etCity)
        radioGroupType = findViewById(R.id.radioGroupType)
        radioSale = findViewById(R.id.radioSale)
        radioRent = findViewById(R.id.radioRent)
        radioGroupFurnished = findViewById(R.id.radioGroupFurnished)
        radioFurnished = findViewById(R.id.radioFurnished)
        radioNotFurnished = findViewById(R.id.radioNotFurnished)
        spinnerSize = findViewById(R.id.spinnerSize)
        btnSave = findViewById(R.id.btnSave)


        // Populate spinner
        val sizes = arrayOf("3 Marla", "5 Marla", "10 Marla", "1 Kanal")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizes)
        spinnerSize.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {

            // Load property and location details
            val property = propertyDao.getPropertyById(propertyId)
            val location = propertyDao.getLocationByHouseId(propertyId)

            if (property != null && location != null) {
                // Populate property fields
                etNumberOfRooms.setText(property.numberOfRoom)
                etNumberOfBedroom.setText(property.numberOfBedroom)
                etNumberOfBathroom.setText  (property.numberOfBathroom)
                etNumberOfFloor.setText(property.floor)

                // Populate location fields
                etAddress.setText(location.address)
                etCity.setText(location.city)

                // Populate radio buttons
                if (property.type == "Sale") {
                    radioSale.isChecked = true
                } else {
                    radioRent.isChecked = true
                }

                if (property.interior == "Furnished") {
                    radioFurnished.isChecked = true
                } else {
                    radioNotFurnished.isChecked = true
                }

                val selectedSizePosition = sizes.indexOf(property.size)
                spinnerSize.setSelection(selectedSizePosition)
            }
        }

        // Set up click listener for the Save button
        btnSave.setOnClickListener {
            saveChanges()
            finish()
        }
    }

    private fun saveChanges() {

        CoroutineScope(Dispatchers.IO).launch {
        // Get edited property details
        val editedProperty = PropertyEntity(
            houseId = propertyId,
            numberOfRoom = etNumberOfRooms.text.toString(),
            numberOfBedroom = etNumberOfBedroom.text.toString(),
            numberOfBathroom = etNumberOfBathroom.text.toString(),
            floor = etNumberOfFloor.text.toString(),
            type = if (radioSale.isChecked) "Sale" else "Rent",
            interior = if (radioFurnished.isChecked) "Furnished" else "Not Furnished",
            size = spinnerSize.selectedItem.toString(),
            userEmail = userEmail
        )

        // Get edited location details
        val editedLocation = LocationEntity(
            locationId = propertyId, // Assuming locationId is the same as houseId
            address = etAddress.text.toString(),
            city = etCity.text.toString(),
            houseId = propertyId
        )

        // Update property and location in the database

            propertyDao.updateProperty(editedProperty)
            propertyDao.updateLocation(editedLocation)
            // Optionally, you can finish the activity after updating the details
            finish()
        }
    }
}