package com.example.medbuddy

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PatientDashboard : AppCompatActivity() {
    private lateinit var treatmentRecyclerView: RecyclerView
    private lateinit var treatmentList: ArrayList<Treatment>
    private lateinit var adapter: PatientTreatmentAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var pDialog: Dialog
    private lateinit var pnDialog: Dialog
    private lateinit var title: TextView
    private lateinit var needDoctor: LinearLayout
    private lateinit var appointment: LinearLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.patient_dashboard)

        title = findViewById(R.id.patientDashboardTitle)
        title.text = intent.getStringExtra("fullName")
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        needDoctor = findViewById(R.id.layoutNeedDoctor)
        needDoctor.setOnClickListener {
            val intent = Intent(this, RequestCreate::class.java)
            startActivity(intent)
        }
        appointment=findViewById(R.id.layoutAppointments)
        appointment.setOnClickListener{
            Toast.makeText(this, "To be implemented, stay soon!", Toast.LENGTH_SHORT).show()
        }
        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        treatmentList = ArrayList()
        adapter = PatientTreatmentAdapter(this, treatmentList)
        treatmentRecyclerView = findViewById(R.id.treatmentRecyclerView)
        treatmentRecyclerView.layoutManager = LinearLayoutManager(this)
        treatmentRecyclerView.adapter = adapter
        val settingsButton = findViewById<ImageView>(R.id.settingsPatient)

        settingsButton.setOnClickListener{
            pDialog= Dialog(this)
            pDialog.setContentView(R.layout.pop_up_settings)
            pDialog.setTitle("Pop-up Window")
            pDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            pDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val logoutButton = pDialog.findViewById<Button>(R.id.btnLogout)
            logoutButton.setOnClickListener{
                startActivity(Intent(this, Login::class.java))

                finish()

                pDialog.dismiss()
            }
            val editProfileButton = pDialog.findViewById<Button>(R.id.btnEditProfile)
            editProfileButton.setOnClickListener{
                pDialog.dismiss()

                pnDialog= Dialog(this)
                pnDialog.setContentView(R.layout.patient_edit_profile)
                pnDialog.setTitle("Pop-up Window")
                pnDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                pnDialog.window!!.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val fullName = pnDialog.findViewById<TextInputLayout>(R.id.pfullName)
                val phoneNumber = pnDialog.findViewById<TextInputLayout>(R.id.pphoneNumber)
                val age = pnDialog.findViewById<TextInputLayout>(R.id.editAge)
                val weight = pnDialog.findViewById<TextInputLayout>(R.id.editWeight)
                val editData = pnDialog.findViewById<Button>(R.id.peditData)
                val spinner = pnDialog.findViewById<Spinner>(R.id.genderspinner)
                val adapter = ArrayAdapter.createFromResource(
                    this, R.array.gender_type, android.R.layout.simple_spinner_item
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        // Do nothing
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }
                editData.setOnClickListener {
                    val sdFullName = fullName.editText?.text.toString()
                    val sdGender = spinner.selectedItem.toString()
                    val sdPhoneNumber = phoneNumber.editText?.text.toString()
                    val sdAge=age.editText?.text.toString()
                    val sdWeight=weight.editText?.text.toString()
                    if(sdFullName.isEmpty() || sdPhoneNumber.isEmpty()|| sdAge.isEmpty() || sdWeight.isEmpty()){
                        if (sdFullName.isEmpty()) {
                            fullName.error = "Please enter your Full Name"
                        }
                        if (sdPhoneNumber.isEmpty()) {
                            phoneNumber.error = "Please enter your phone number"
                        }
                        if (sdAge.isEmpty()) {
                            age.error = "Please enter your age"
                        }
                        if (sdWeight.isEmpty()) {
                            weight.error = "Please enter your weight"
                        }
                        Toast.makeText(this, "Please check your fields", Toast.LENGTH_SHORT).show()
                    }
                    else if (sdPhoneNumber.length != 10) {
                        phoneNumber.error = "Please enter 10 digits"
                        Toast.makeText(this, "Please enter 10 digits ", Toast.LENGTH_SHORT).show()
                    }
                    else if (sdAge.length >2) {
                        phoneNumber.error = "Please introduce an age between 18 and 99 "
                        Toast.makeText(this, "Please introduce an age between 18 and 99 ", Toast.LENGTH_SHORT).show()
                    }
                    else if (sdWeight.length > 4) {
                        phoneNumber.error = "Please enter you weight in cm "
                        Toast.makeText(this, "Please enter you weight in cm ", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val userReference = database.reference.child("Users").child(auth.currentUser!!.uid)
                        val updates = HashMap<String, Any>()
                        updates["fullName"] = sdFullName
                        updates["phoneNumber"] = sdPhoneNumber
                        updates["age"]=sdAge
                        updates["weight"]=sdWeight
                        updates["gender"]=sdGender
                        userReference.updateChildren(updates)
                        pnDialog.dismiss()
                        Toast.makeText(this, "Your data has been changed", Toast.LENGTH_SHORT).show()

                    }
                }

                pnDialog.show()
            }
            pDialog.show()
        }
        mDbRef.child("Treatment").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                treatmentList.clear()
                for (postSnapshot in snapshot.children) {
                    val treatment = postSnapshot.getValue(Treatment::class.java)
                    if (treatment != null) {
                        if ((mAuth.currentUser?.uid == treatment.patientUID) &&
                            (treatment.accepted == true) && (treatment.active == true)
                        ) {
                            treatmentList.add(treatment)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }

        })


    }
}