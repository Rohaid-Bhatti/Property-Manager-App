package com.hzm.sharedpreference

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.hzm.sharedpreference.roomDb.ContactDAO
import com.hzm.sharedpreference.roomDb.ContactDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private lateinit var contactDao: ContactDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        var isCheck: Boolean

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        val database = ContactDatabase.getDatabase(this)
        contactDao = database.contactDao()

        btnLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            isCheck = true
            val sharedPreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString("USER_EMAIL", email)
            editor.putBoolean("ISCHECK", isCheck)
            editor.apply()

            // Query the database to check if the user exists
            validateUser(email, password)
        }

// Handle sign-up TextView click event
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        tvSignUp.setOnClickListener {
            // Navigate to the sign-up screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUser(email: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val user = contactDao.getUserByEmailAndPassword(email, password)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    // User authenticated successfully, navigate to the next screen
                    val intent = Intent(this@SignInActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish() // Finish the SignInActivity
                } else {
                    // Authentication failed, show an error message
                    Toast.makeText(
                        this@SignInActivity,
                        "Incorrect email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}