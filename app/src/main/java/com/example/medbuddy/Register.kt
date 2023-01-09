package com.example.medbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val username = findViewById<TextInputLayout>(R.id.usernameRegister)
        val password = findViewById<TextInputLayout>(R.id.passwordRegister)
        val fullName = findViewById<TextInputLayout>(R.id.fullName)
        val phoneNumber = findViewById<TextInputLayout>(R.id.phoneNumber)
        val email = findViewById<TextInputLayout>(R.id.Email)
        val confirmPassword = findViewById<TextInputLayout>(R.id.confirmPassword)
        val saveData = findViewById<Button>(R.id.saveData)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.spinner_items, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        // val myRef = database.getReference("items")
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        saveData.setOnClickListener {
            val sdUsername = username.editText?.text.toString()
            val sdPassword = password.editText?.text.toString()
            val sdFullName = fullName.editText?.text.toString()
            val sdPhoneNumber = phoneNumber.editText?.text.toString()
            val sdEmail = email.editText?.text.toString()
            val sdConfirmPassword = confirmPassword.editText?.text.toString()
            val sdRole = spinner.selectedItem.toString()
            if (sdUsername.isEmpty() || sdEmail.isEmpty() || sdPassword.isEmpty() || sdFullName.isEmpty() || sdPhoneNumber.isEmpty() || sdConfirmPassword.isEmpty()) {
                if (sdUsername.isEmpty()) {
                    username.error = "Please enter your Username"
                }
                if (sdEmail.isEmpty()) {
                    email.error = "Please enter your Email"
                }
                if (sdPassword.isEmpty()) {
                    password.error = "Please enter your Password"
                }
                if (sdFullName.isEmpty()) {
                    fullName.error = "Please enter your Full Name"
                }
                if (sdPhoneNumber.isEmpty()) {
                    phoneNumber.error = "Please enter your phone number"
                }
                if (sdConfirmPassword.isEmpty()) {
                    confirmPassword.error = "Please confirm your Password"
                }
                Toast.makeText(this, "Please check your fields", Toast.LENGTH_SHORT).show()

            } else if (!sdEmail.matches(emailRegex.toRegex())) {
                email.error = "Please enter a valid email address"
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT)
                    .show()
            } else if (sdPhoneNumber.length != 10) {
                phoneNumber.error = "Please enter 10 digits"
                Toast.makeText(this, "Please enter 10 digits ", Toast.LENGTH_SHORT).show()
            } else if (sdPassword.length < 7) {
                password.error = "Please enter a password with more than 7 characters"
                Toast.makeText(
                    this, "Please enter a password with more than 7 characters", Toast.LENGTH_SHORT
                ).show()
            } else if (sdPassword != sdConfirmPassword) {
                confirmPassword.error = "Passwords don't match"
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(sdEmail, sdPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val databaseRef =
                                database.reference.child("Users").child(auth.currentUser!!.uid)
                            val users = Users(
                                sdUsername,
                                sdPassword,
                                sdFullName,
                                sdPhoneNumber,
                                sdRole,
                                auth.currentUser!!.uid
                            )
                            databaseRef.setValue(users).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this, "Something wrong with intent", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}