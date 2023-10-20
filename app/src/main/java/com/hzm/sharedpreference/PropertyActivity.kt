package com.hzm.sharedpreference

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.hzm.sharedpreference.propertyWork.propertyDB.LocationEntity
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyDatabase
import com.hzm.sharedpreference.propertyWork.propertyDB.PropertyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PropertyActivity : AppCompatActivity() {
    private lateinit var etNumberOfRoom: EditText
    private lateinit var etNumberOfBedroom: EditText
    private lateinit var etNumberOfBathroom: EditText
    private lateinit var etFloor: EditText
    private lateinit var radioGroupType: RadioGroup
    private lateinit var radioSale: RadioButton
    private lateinit var radioRent: RadioButton
    private lateinit var radioGroupFurnished: RadioGroup
    private lateinit var radioFurnished: RadioButton
    private lateinit var radioNotFurnished: RadioButton
    private lateinit var spinnerSize: Spinner
    private lateinit var etArea: EditText
    private lateinit var etLocation: EditText

    private lateinit var database: PropertyDatabase
//    private lateinit var locationDatabase: LocationDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property)

        // Retrieve the user's email from shared preferences
        val sharedPreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        val userEmail = sharedPreference.getString("USER_EMAIL", "")

        etNumberOfRoom = findViewById(R.id.etNumberOfRoom)
        etNumberOfBedroom = findViewById(R.id.etNumberOfBedroom)
        etNumberOfBathroom = findViewById(R.id.etNumberOfBathroom)
        etFloor = findViewById(R.id.etFloor)
        radioGroupType = findViewById(R.id.radioGroupType)
        radioSale = findViewById(R.id.radioSale)
        radioRent = findViewById(R.id.radioRent)
        radioGroupFurnished = findViewById(R.id.radioGroupFurnished)
        radioFurnished = findViewById(R.id.radioFurnished)
        radioNotFurnished = findViewById(R.id.radioNotFurnished)
        spinnerSize = findViewById(R.id.spinnerSize)
        etArea = findViewById(R.id.etArea)
        etLocation = findViewById(R.id.etLocation)

        // Initialize the Spinner with values
        val sizeOptions = arrayOf("3 Marla", "5 Marla", "10 Marla", "1 Kanal")
        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizeOptions)
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSize.adapter = sizeAdapter

        database = PropertyDatabase.getDatabase(this)
//        locationDatabase = LocationDatabase.getDatabase(this)

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            saveProperty(userEmail.toString())
        }
    }

    private fun saveProperty(userEmail: String) {
        val numberOfRoom = etNumberOfRoom.text.toString()
        val numberOfBedroom = etNumberOfBedroom.text.toString()
        val numberOfBathroom = etNumberOfBathroom.text.toString()
        val floor = etFloor.text.toString()
        val size = spinnerSize.selectedItem.toString()
        val typeId = when (radioGroupType.checkedRadioButtonId) {
            R.id.radioSale -> "Sale"
            R.id.radioRent -> "Rent"
            else -> ""
        }

        val furnishedStatus = when (radioGroupFurnished.checkedRadioButtonId) {
            R.id.radioFurnished -> "Furnished"
            R.id.radioNotFurnished -> "Not Furnished"
            else -> ""
        }
        val area = etArea.text.toString()
        val locationName = etLocation.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val property = PropertyEntity(
                0,
                numberOfRoom,
                numberOfBedroom,
                numberOfBathroom,
                floor,
                typeId,
                furnishedStatus,
                size,
                userEmail
            )

            val propertyId = database.propertyDao().insertProperty(property) // Insert property and get its ID

            val location = LocationEntity(0, area, locationName, propertyId) // Use the propertyId

//            locationDatabase.locationDao().insertLocation(location)
            database.propertyDao().insertLocation(location)
        }
        Toast.makeText(this@PropertyActivity, "Property Added successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}