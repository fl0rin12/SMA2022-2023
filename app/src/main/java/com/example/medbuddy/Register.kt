package com.example.medbuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

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
        val fullname = findViewById<TextInputLayout>(R.id.fullName)
        val phoneNumber = findViewById<TextInputLayout>(R.id.phoneNumber)
        val email = findViewById<TextInputLayout>(R.id.Email)
        val confirmPassword = findViewById<TextInputLayout>(R.id.confirmPassword)
        val saveData = findViewById<Button>(R.id.saveData)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_items,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        // val myRef = database.getReference("items")
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection
                val selectedItem = parent?.getItemAtPosition(position).toString()
                //   myRef.push().setValue(selectedItem)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        saveData.setOnClickListener {
            //val database = FirebaseDatabase.getInstance()


            val sdusername = username.editText?.text.toString()
            val sdpassword = password.editText?.text.toString()
            val sdfullname = fullname.editText?.text.toString()
            val sdphoneNumber = phoneNumber.editText?.text.toString()
            val sdemail = email.editText?.text.toString()
            val sdconfirmPassword = confirmPassword.editText?.text.toString()
            val sdrole = spinner.selectedItem.toString()
            if (sdusername.isEmpty() || sdemail.isEmpty() || sdpassword.isEmpty() || sdfullname.isEmpty() || sdphoneNumber.isEmpty() || sdconfirmPassword.isEmpty()) {
                if (sdusername.isEmpty()) {
                    username.error = "Please enter your Username"
                }
                if (sdemail.isEmpty()) {
                    email.error = "Please enter your Email"
                }
                if (sdpassword.isEmpty()) {
                    password.error = "Please enter your Password"
                }
                if (sdfullname.isEmpty()) {
                    fullname.error = "Please enter your Full Name"
                }
                if (sdphoneNumber.isEmpty()) {
                    phoneNumber.error = "Please enter your phone number"
                }
                if (sdconfirmPassword.isEmpty()) {
                    confirmPassword.error = "Please confirm your Password"
                }
                Toast.makeText(this, "Please check your fields", Toast.LENGTH_SHORT).show()

            } else if (!sdemail.matches(emailRegex.toRegex())) {
                email.error = "Please enter a valid email adress"
                Toast.makeText(this, "Please enter a valid email adress", Toast.LENGTH_SHORT)
                    .show()
            } else if (sdphoneNumber.length != 10) {
                phoneNumber.error = "Please enter 10 digits"
                Toast.makeText(this, "Please enter 10 digits ", Toast.LENGTH_SHORT).show()
            } else if (sdpassword.length < 7) {
                password.error = "Please enter a password with more than 7 characters"
                Toast.makeText(
                    this,
                    "Please enter a password with more than 7 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sdpassword != sdconfirmPassword) {
                confirmPassword.error = "Passwords don't match"
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(sdemail, sdpassword).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val databaseRef =
                            database.reference.child(sdrole).child(auth.currentUser!!.uid)
                        val users: Users =
                            Users(
                                sdusername,
                                sdpassword,
                                sdfullname,
                                sdphoneNumber,
                                sdrole,
                                auth.currentUser!!.uid
                            )
                        databaseRef.setValue(users).addOnCompleteListener {
                            if(it.isSuccessful) {
                                val intent= Intent(this, Login::class.java)
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(this, "Something wrong with intent", Toast.LENGTH_SHORT).show()
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