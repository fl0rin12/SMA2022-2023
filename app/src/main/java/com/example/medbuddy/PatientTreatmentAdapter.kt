package com.example.medbuddy

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PatientTreatmentAdapter(val context: Context, private val treatmentList: ArrayList<Treatment>) :
    RecyclerView.Adapter<PatientTreatmentAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_element, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val patient = treatmentList[position]
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users/")
        patient.doctorUID?.let {
            databaseRef.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fullName = snapshot.child("fullName").getValue(String::class.java)
                    holder.textName.text = fullName
                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, PatientInteraction::class.java)
                        intent.putExtra("doctorUID", patient.doctorUID)
                        intent.putExtra("diagnostic", patient.diagnostic)
                        intent.putExtra("medication", patient.medication)
                        intent.putExtra("symptom", patient.symptom)
                        intent.putExtra("doctorFullName", fullName)
                        context.startActivity(intent)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Do nothing
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return treatmentList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.txt_name)
    }
}