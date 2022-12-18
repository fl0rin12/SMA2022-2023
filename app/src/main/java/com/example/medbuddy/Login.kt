package com.example.medbuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    companion object {
        private const val TAG = "KotlinActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.login_screen)
        auth = FirebaseAuth.getInstance()
        val newUser = findViewById<Button>(R.id.newUser)
        val LogInEmail = findViewById<TextInputLayout>(R.id.emailLogIn)
        val LogInPassword = findViewById<TextInputLayout>(R.id.passwordLogIn)
        val loginButton = findViewById<Button>(R.id.loginButton)
        newUser.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener() {
            val email = LogInEmail.editText?.text.toString()
            val password = LogInPassword.editText?.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    LogInEmail.error = "Please enter a username"
                }
                if (password.isEmpty()) {
                    LogInPassword.error = "Please enter your paswword"
                }
                Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailRegex.toRegex())) {
                LogInEmail.error = "Please enter a valid email adress"
                Toast.makeText(this, "Please enter a valid email adress", Toast.LENGTH_SHORT)
                    .show()
            } else if (password.length < 7) {
                LogInPassword.error = "Please enter a password with more than 7 characters"
                Toast.makeText(
                    this,
                    "Please enter a password with more than 7 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Somewthing wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    fun basicReadWrite(view: View) {
        // [START write_message]
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")

        //myRef.setValue("Hello, World!")
        // [END write_message]

        // [START read_message]
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(Login.TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(Login.TAG, "Failed to read value.", error.toException())
            }
        })
        // [END read_message]
    }
}
