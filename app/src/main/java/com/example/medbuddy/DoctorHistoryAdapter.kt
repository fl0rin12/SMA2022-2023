package com.example.medbuddy

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoctorHistoryAdapter(val context: Context, private val treatmentList: ArrayList<Treatment>) :
    RecyclerView.Adapter<DoctorHistoryAdapter.UserViewHolder>() {

    private lateinit var mDialog: Dialog

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
                        mDialog = Dialog(context)
                        mDialog.setContentView(R.layout.pop_up_doctor_history)
                        mDialog.setTitle("Pop-up Window")
                        mDialog.findViewById<TextView>(R.id.fullName).text = fullName
                        mDialog.findViewById<TextView>(R.id.diagnostic).text = patient.diagnostic
                        mDialog.findViewById<TextView>(R.id.medication).text = patient.medication
                        mDialog.findViewById<TextView>(R.id.review).text = "5/5"
                        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        mDialog.window!!.setLayout(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        mDialog.show()
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