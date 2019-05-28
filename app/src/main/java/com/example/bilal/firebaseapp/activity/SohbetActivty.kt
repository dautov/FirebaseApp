package com.example.bilal.firebaseapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.adapters.SohbetOdasiRecyclerViewAdapter
import com.example.bilal.firebaseapp.dialog.YeniSohbetOdasiDialogFragment
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.model.SohbetOdasi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sohbet_activty.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SohbetActivty : AppCompatActivity() {

    lateinit var tumSohbetOdalari:ArrayList<SohbetOdasi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_activty)

        init()

    }

    private fun init(){
        tumKullanicilarigetir()
    }

    var mValueEventListener : ValueEventListener = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(p0: DataSnapshot) {

        }

    }

    private fun tumKullanicilarigetir() {
        var ref = FirebaseDatabase.getInstance().reference

        var sorgu = ref.child("kullanici").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

}



