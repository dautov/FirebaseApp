package com.example.bilal.firebaseapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import kotlinx.android.synthetic.main.activity_belge_upload.*

class BelgeUploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_belge_upload)

        var secilenOdaID = intent.getStringExtra("OdaID")
        Log.e("BelgeOdaID",secilenOdaID)

        Init()
    }

    fun Init(){
        btnImage.setOnClickListener{
            Toast.makeText(this@BelgeUploadActivity,"Image",Toast.LENGTH_SHORT).show()
        }


    }
}
