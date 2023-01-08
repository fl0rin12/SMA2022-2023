package com.example.medbuddy

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TreatmentAdapter(val context: Context, val treatmentList: ArrayList<Treatment>) :
    RecyclerView.Adapter<TreatmentAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.show_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val pacient = treatmentList[position]
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users/")
        pacient.pacientUID?.let {
            databaseRef.child(it).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fullname = snapshot.child("fullname").getValue(String::class.java)
                    holder.textName.text = fullname
                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, PacientInteraction::class.java)
                        intent.putExtra("pacientUID", pacient.pacientUID)
                        intent.putExtra("diagnostic", pacient.diagnostic)
                        intent.putExtra("medication", pacient.medication)
                        intent.putExtra("symptom", pacient.symptom)
                        intent.putExtra("pacientFullname", fullname)
                        context.startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    override fun getItemCount(): Int {
        return treatmentList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }
}