package com.example.medbuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.login_screen)

        auth = FirebaseAuth.getInstance()
        val newUser = findViewById<Button>(R.id.newUser)
        val logInEmail = findViewById<TextInputLayout>(R.id.emailLogIn)
        val logInPassword = findViewById<TextInputLayout>(R.id.passwordLogIn)
        val loginButton = findViewById<Button>(R.id.loginButton)
        newUser.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = logInEmail.editText?.text.toString()
            val password = logInPassword.editText?.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    logInEmail.error = "Please enter a username"
                }
                if (password.isEmpty()) {
                    logInPassword.error = "Please enter your password"
                }
                Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailRegex.toRegex())) {
                logInEmail.error = "Please enter a valid email address"
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT)
                    .show()
            } else if (password.length < 7) {
                logInPassword.error = "Please enter a password with more than 7 characters"
                Toast.makeText(
                    this, "Please enter a password with more than 7 characters", Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { it1 ->
                    if (it1.isSuccessful) {
                        val uid = FirebaseAuth.getInstance().currentUser!!.uid
                        val databaseRef = FirebaseDatabase.getInstance().getReference("Users/")
                        val userRef = databaseRef.child(uid)
                        userRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val snapshot = task.result
                                val role = snapshot.child("role").getValue(String::class.java)
                                val fullName =
                                    snapshot.child("fullName").getValue(String::class.java)
                                val phoneNumber =
                                    snapshot.child("phoneNumber").getValue(String::class.java)
                                Log.d("TAG", "role: $role\n")
                                if (role.equals("Medic")) {
                                    val intent = Intent(this, DoctorDashboard::class.java)
                                    intent.putExtra("fullName", fullName)
                                    intent.putExtra("phoneNumber", phoneNumber)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this, PatientDashboard::class.java)
                                    intent.putExtra("fullName", fullName)
                                    intent.putExtra("phoneNumber", phoneNumber)
                                    startActivity(intent)
                                }
                            } else {
                                Log.d(
                                    "TAG", task.exception!!.message!!
                                ) //Don't ignore potential errors!
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
