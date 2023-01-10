package com.example.medbuddy

import android.app.*
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DoctorDashboard : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var treatmentList: ArrayList<Treatment>
    private lateinit var adapter: DoctorTreatmentAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var mDialog: Dialog
    private lateinit var mnDialog: Dialog

    private lateinit var title: TextView
    private lateinit var appointments: LinearLayout
    private lateinit var requests: LinearLayout
    private lateinit var patientHistory: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.doctor_dashboard)

        title = findViewById(R.id.doctorDashboardTitle)
        title.text = intent.getStringExtra("fullName")

        appointments = findViewById(R.id.layoutAppointments)
        appointments.setOnClickListener{
            val intent = Intent(this, Appointments::class.java)
            startActivity(intent)
        }

        requests = findViewById(R.id.layoutNeedDoctor)
        requests.setOnClickListener{
            val intent = Intent(this, RequestsList::class.java)
            startActivity(intent)
        }

        patientHistory = findViewById(R.id.layoutPatientsHistory)
        patientHistory.setOnClickListener{
            val intent = Intent(this, PatientsHistory::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        treatmentList = ArrayList()
        adapter = DoctorTreatmentAdapter(this, treatmentList)
        userRecyclerView = findViewById(R.id.treatmentRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mDbRef.child("Treatment").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                treatmentList.clear()
                for (postSnapshot in snapshot.children) {
                    val treatment = postSnapshot.getValue(Treatment::class.java)
                    if (treatment != null) {
                        if ((mAuth.currentUser?.uid == treatment.doctorUID) &&
                            (treatment.accepted == true) && (treatment.active == true)
                        ) {
                            treatmentList.add(treatment)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        val settingsButton = findViewById<ImageView>(R.id.settingsDoctor)
        settingsButton.setOnClickListener {
            mDialog = Dialog(this)
            mDialog.setContentView(R.layout.popup_settings)
            mDialog.setTitle("Pop-up Window")
            mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val logoutButton = mDialog.findViewById<Button>(R.id.btnLogout)
            logoutButton.setOnClickListener {
                startActivity(Intent(this, Login::class.java))
                finish()
                mDialog.dismiss()
            }
            val editProfileButton = mDialog.findViewById<Button>(R.id.btnEditProfile)
            editProfileButton.setOnClickListener {
                mDialog.dismiss()

                mnDialog = Dialog(this)
                mnDialog.setContentView(R.layout.edit_profile_doctor)
                mnDialog.setTitle("Pop-up Window")
                mnDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mnDialog.window!!.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val fullName = mnDialog.findViewById<TextInputLayout>(R.id.editfullName)
                val phoneNumber = mnDialog.findViewById<TextInputLayout>(R.id.editphoneNumber)
                val editData = mnDialog.findViewById<Button>(R.id.editData)
                val spinner = mnDialog.findViewById<Spinner>(R.id.editspinner)
                val adapter = ArrayAdapter.createFromResource(
                    this, R.array.medical_specialties, android.R.layout.simple_spinner_item
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }
                editData.setOnClickListener {
                    val sdFullName = fullName.editText?.text.toString()
                    val sdSpeciality = spinner.selectedItem.toString()
                    val sdPhoneNumber = phoneNumber.editText?.text.toString()
                    if (sdFullName.isEmpty() || sdPhoneNumber.isEmpty()) {
                        if (sdFullName.isEmpty()) {
                            fullName.error = "Please enter your Full Name"
                        }
                        if (sdPhoneNumber.isEmpty()) {
                            phoneNumber.error = "Please enter your phone number"
                        }
                        Toast.makeText(this, "Please check your fields", Toast.LENGTH_SHORT).show()
                    } else if (sdPhoneNumber.length != 10) {
                        phoneNumber.error = "Please enter 10 digits"
                        Toast.makeText(this, "Please enter 10 digits ", Toast.LENGTH_SHORT).show()
                    } else {
                        val userReference =
                            database.reference.child("Users").child(auth.currentUser!!.uid)
                        val updates = HashMap<String, Any>()
                        updates["fullName"] = sdFullName
                        updates["phoneNumber"] = sdPhoneNumber
                        updates["speciality"] = sdSpeciality
                        userReference.updateChildren(updates)
                    }
                }
                mnDialog.show()
            }
            mDialog.show()
        }
    }
}


