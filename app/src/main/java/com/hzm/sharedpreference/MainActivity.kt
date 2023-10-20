package com.hzm.sharedpreference

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hzm.sharedpreference.databinding.ActivityMainBinding
import com.hzm.sharedpreference.roomDb.Contact
import com.hzm.sharedpreference.roomDb.ContactDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var database: ContactDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = ContactDatabase.getDatabase(this)

        var isCheck: Boolean

        val sharedPreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString()
            val mail = binding.etEmail.text.toString()
            val num = binding.etNumber.text.toString()
            val pass = binding.etPassword.text.toString()

            // Validate input fields
            if (name.isEmpty() || mail.isEmpty() || num.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ValidationUtils.isValidEmail(mail)) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate phone number: starts with "03" and is 11 digits long
            if (!ValidationUtils.isValidPhoneNumber(num)) {
                Toast.makeText(
                    this,
                    "Invalid phone number. Please enter a valid 11-digit number starting with \\'03\\'",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (pass.length < 6 || !ValidationUtils.isValidPassword(pass)) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters long and contain 1 number and 1 special character",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            isCheck = true

            // Check if the email or phone number already exists in the database
            GlobalScope.launch(Dispatchers.IO) {
                val existingUserByEmail = database.contactDao().getUserByEmail(mail)
                val existingUserByNumber = database.contactDao().getUserByNumber(num)

                withContext(Dispatchers.Main) {
                    if (existingUserByEmail != null) {
                        // Email already exists, show an error message
                        Toast.makeText(
                            this@MainActivity,
                            "Email already exists. Please use a different email.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (existingUserByNumber != null) {
                        // Phone number already exists, show an error message
                        Toast.makeText(
                            this@MainActivity,
                            "Phone number already exists. Please use a different phone number.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Email and phone number are unique, create the account
                        // Insert the user into the database here
                        val newUser = Contact(0, name, mail, pass, num)
                        database.contactDao().insertUser(newUser)

                        // Save the user's email to SharedPreferences
                        val editor = sharedPreference.edit()
                        editor.putString("USER_EMAIL", mail)
                        editor.putBoolean("ISCHECK", true)
                        editor.apply()

                        // Navigate to the DashboardActivity or the next screen
                        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        startActivity(intent)

                        Toast.makeText(
                            this@MainActivity,
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Finish the current activity (MainActivity)
                        finish()
                    }
                }
            }
        }

        binding.tvLogin.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}