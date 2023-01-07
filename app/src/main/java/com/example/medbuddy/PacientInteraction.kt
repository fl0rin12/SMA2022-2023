package com.example.medbuddy

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PacientInteraction : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Users>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.pacient_interaction)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth=FirebaseAuth.getInstance()
        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(Users::class.java)
                   // if(mAuth.currentUser?.uid !=currentUser?.uid){
                        userList.add(currentUser!!)
                    //}

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}