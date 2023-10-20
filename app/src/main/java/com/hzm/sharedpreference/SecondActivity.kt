package com.hzm.sharedpreference

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.widget.Toast
import com.hzm.sharedpreference.databinding.ActivitySecondBinding
import com.hzm.sharedpreference.roomDb.ContactDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var database: ContactDatabase
    private lateinit var number1: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        // Initialize your Room database
        database = ContactDatabase.getDatabase(this)

        // Retrieve the user's email from SharedPreferences
        val sharedPreferences = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        val currentUserEmail = sharedPreferences.getString("USER_EMAIL", "")

        if (currentUserEmail!!.isNotEmpty()) {
            GlobalScope.launch {
                val user = database.contactDao().getUserByEmail(currentUserEmail.toString())

                // Update the TextViews with user data if found
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        binding.tvName.text = user.name
                        binding.tvEmail.text = user.email
                        binding.tvNumber.text = user.number
                        number1 = user.number
                    }
                }
            }
        }

        val calendar = Calendar.getInstance()
        binding.tvAgeGiven.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    // Update the TextView with the selected date
                    calendar.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.tvAgeGiven.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        binding.btnCalcute.setOnClickListener {
            // Calculate age based on the selected date
            val selectedDate = calendar.time
            val currentDate = Calendar.getInstance().time

            val dobCalendar = Calendar.getInstance()
            dobCalendar.time = selectedDate
            val currentDateCalendar = Calendar.getInstance()
            currentDateCalendar.time = currentDate

            var years = currentDateCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
            var months = currentDateCalendar.get(Calendar.MONTH) - dobCalendar.get(Calendar.MONTH)
            var days = currentDateCalendar.get(Calendar.DAY_OF_MONTH) - dobCalendar.get(Calendar.DAY_OF_MONTH)

            if (days < 0) {
                months -= 1
                days += dobCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            }

            if (months < 0) {
                years -= 1
                months += 12
            }

            val ageString = resources.getString(
                R.string.age_result,
                years,
                months,
                days
            )
            binding.tvAge.text = ageString
        }

        binding.btnCalculateBMI.setOnClickListener {
            // Get the height and weight input from EditText fields
            val heightStr = binding.etHeight.text.toString()
            val weightStr = binding.etWeight.text.toString()

            if (heightStr.isNotEmpty() && weightStr.isNotEmpty()) {
                val height = heightStr.toFloat()
                val weight = weightStr.toFloat()

                // Calculate BMI
                val bmi = calculateBMI(height, weight)

                // Display the BMI result
                val bmiResult = getString(R.string.bmi_result, bmi)
                Toast.makeText(this, bmiResult, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter valid height and weight", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            val isCheck = false
            editor.putBoolean("ISCHECK", isCheck).apply()

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

            // Finish the current activity (SecondActivity)
            finish()
        }

        binding.btnDialer.setOnClickListener {
            //Dialer intent
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode(number1))
            )
            startActivity(intent)
        }
    }

    private fun calculateBMI(height: Float, weight: Float): Float {
        // Calculate BMI using the formula: BMI = weight (kg) / (height (m) * height (m))
        return weight / (height.pow(2))
    }
}