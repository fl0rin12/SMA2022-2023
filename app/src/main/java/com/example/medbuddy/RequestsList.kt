package com.example.medbuddy

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RequestsList : AppCompatActivity() {

    private lateinit var treatmentRecyclerView: RecyclerView
    private lateinit var treatmentList: ArrayList<Treatment>
    private lateinit var adapter: RequestTreatmentAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.requests_list)

        back = findViewById(R.id.backButton)
        back.setOnClickListener {
            val intent = Intent(this, DoctorDashboard::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        treatmentList = ArrayList()
        adapter = RequestTreatmentAdapter(this, treatmentList)
        treatmentRecyclerView = findViewById(R.id.requestsRecyclerView)
        treatmentRecyclerView.layoutManager = LinearLayoutManager(this)
        treatmentRecyclerView.adapter = adapter
        mDbRef.child("Treatment").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                treatmentList.clear()
                for (postSnapshot in snapshot.children) {
                    val treatment = postSnapshot.getValue(Treatment::class.java)
                    if (treatment != null) {
                        if (
                            (treatment.accepted == false) && (treatment.active == true)
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