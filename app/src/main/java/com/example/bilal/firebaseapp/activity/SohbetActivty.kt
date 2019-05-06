package com.example.bilal.firebaseapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SohbetActivty : AppCompatActivity() {

    lateinit var tumSohbetOdalari:ArrayList<SohbetOdasi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_sohbet_activty)

       // init()

    }

    fun init(){
        tumSohbetOdalariniGetir()
        //Toast.makeText(this@SohbetActivty,"Selam Bro",Toast.LENGTH_SHORT).show()
        fbtnSohbetOlustur.setOnClickListener {
            var dialog =YeniSohbetOdasiDialogFragment()
            dialog.show(supportFragmentManager,"goster")
        }


    }

    private fun tumSohbetOdalariniGetir(){
        tumSohbetOdalari = ArrayList<SohbetOdasi>() //boş array list

        var ref = FirebaseDatabase.getInstance().reference

        var sorgu = ref.child("sohbet_odasi").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {



                for (tekSohbetOdasi in p0.children){

                    var oAnkiSohbetOdasi = SohbetOdasi()
                    var nesneMap = (tekSohbetOdasi.getValue() as HashMap<String,Object>)
                    oAnkiSohbetOdasi.olusturan_id = nesneMap.get("olusturan_id").toString()
                    oAnkiSohbetOdasi.sohbetodasi_adi= nesneMap.get("sohbetodasi_adi").toString()
                    oAnkiSohbetOdasi.sohbetodasi_id = nesneMap.get("sohbetodasi_id").toString()

                    var tumMesajlar = ArrayList<MetinMesaj>()
                    for (mesajlar in tekSohbetOdasi.child("sohbet_oda_mesaj").children){
                        var okunanMesaj =MetinMesaj()
                        okunanMesaj.zaman = mesajlar.getValue(MetinMesaj::class.java)?.zaman
                        okunanMesaj.kullanici_id = mesajlar.getValue(MetinMesaj::class.java)?.kullanici_id
                        okunanMesaj.adi = mesajlar.getValue(MetinMesaj::class.java)?.adi
                        okunanMesaj.profil_resmi =mesajlar.getValue(MetinMesaj::class.java)?.profil_resmi
                        okunanMesaj.mesaj = mesajlar.getValue(MetinMesaj::class.java)?.mesaj

                        tumMesajlar.add(okunanMesaj)

                    }

                    oAnkiSohbetOdasi.sohbet_oda_mesaj= tumMesajlar
                    tumSohbetOdalari.add(oAnkiSohbetOdasi)
                    Log.e("TestSohbetOdalar",oAnkiSohbetOdasi.sohbetodasi_adi)

                }

               //Toast.makeText(this@SohbetActivty,"Tum SohbetOdalari Sayısı : "+tumSohbetOdalari.size,Toast.LENGTH_SHORT).show()

               sohbetOdalariListele()

            }

        })

    }

    private fun sohbetOdalariListele(){
        //Toast.makeText(this@SohbetActivty,"Listeleme Yapiliyor : " + tumSohbetOdalari.size,Toast.LENGTH_SHORT).show()
        var MyAdapter = SohbetOdasiRecyclerViewAdapter(this@SohbetActivty,tumSohbetOdalari)
        rvSohbetler.adapter = MyAdapter
        rvSohbetler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }


     /*fun sohbetOdasiSil(silinecekSohbetOdaID : String){
         var ref = FirebaseDatabase.getInstance().reference
          ref.child("sohbet_odasi")
              .child(silinecekSohbetOdaID).removeValue()

    }*/



}



