package com.example.bilal.firebaseapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.dialog.YeniSohbetOdasiDialogFragment
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.model.SohbetOdasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    lateinit var tumSohbetOdalari:ArrayList<SohbetOdasi>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMyAuthStateListener()
        init()


    }

    fun init(){
        tumSohbetOdalariniGetir()
        //Toast.makeText(this@MainActivity,"Selam Bro",Toast.LENGTH_SHORT).show()
        floatingActionButton.setOnClickListener {
            var dialog = YeniSohbetOdasiDialogFragment()
            dialog.show(supportFragmentManager,"gostrer")
        }
    }

    private fun tumSohbetOdalariniGetir(){

        tumSohbetOdalari = ArrayList<SohbetOdasi>() //boş array list

        var ref = FirebaseDatabase.getInstance().reference

        var sorgu = ref.child("sohbet_odasi").addListenerForSingleValueEvent(object : ValueEventListener {
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
                        var okunanMesaj = MetinMesaj()
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

                Toast.makeText(this@MainActivity,"Tum SohbetOdalari Sayısı : "+tumSohbetOdalari.size,Toast.LENGTH_SHORT).show()

            }

        })

    }


    private fun initMyAuthStateListener (){
        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser

                if (kullanici != null){

                }else{
                    var intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.menuExit -> {
                cikisyap()
                return true
            }
            R.id.menuAyarlar ->{
                ayarlaragit()
                return true
            }
            R.id.menuSohbetOdasi ->{
                sohbetleregit()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }
    private fun  ayarlaragit(){
        var intent = Intent(this, AyarlarActivity::class.java)
        startActivity(intent)

    }
    private fun cikisyap() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun sohbetleregit(){
        var intent=Intent(this,SohbetActivty::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        ControlUser()
    }

    private fun ControlUser() {
        var user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            var intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener (mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener (mAuthStateListener)
        }

    }
}
