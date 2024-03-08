package com.example.fbchat

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fbchat.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setUpActionBar()
        val database = Firebase.database
        val myRef = database.getReference("messages")
        binding.btnSend.setOnClickListener {
            myRef.child(myRef.push().key ?: "string")
                .setValue(User(auth.currentUser?.displayName, binding.edMessage.text.toString()))
        }
        onChangeListener(myRef)
        iniRcView()
    }

    private fun iniRcView() = with(binding) {
        adapter = UserAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signOut) {
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onChangeListener(dRef: DatabaseReference) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>()
                for (s in snapshot.children) {
                    val user = s.getValue(User::class.java)
                    if (user != null) list.add(user)
                }
                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar
        Thread {
            val bitMap = Picasso.get().load(auth.currentUser?.photoUrl).get()
            val draIcon = BitmapDrawable(resources, bitMap)
            runOnUiThread {
                actionBar?.setDisplayHomeAsUpEnabled(true)
                actionBar?.setHomeAsUpIndicator(draIcon)
                actionBar?.title = auth.currentUser?.displayName
            }
        }.start()
    }
}