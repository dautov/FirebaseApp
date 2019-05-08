package com.example.bilal.firebaseapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SohbetOdaActivity : AppCompatActivity() {
    var tumMesajlar : ArrayList<MetinMesaj>? = null

    var mAuthListener : FirebaseAuth.AuthStateListener? = null
    var secilenSohbetOdaID:String = ""
    var mMesajReferans : DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_oda)
        SohbetOdasiIDGetir()
        //kullanici giriş çıkışları dinler
        baslatFirebaseAuthListener()
    }

    private fun SohbetOdasiIDGetir() {
        var secilenSohbetOdaID = intent.getStringExtra("sohbet_odasi_id")
        MesajListener()
    }

    //değişiklikleri dinleyen kısım
    var mValueEventListener : ValueEventListener = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            sohbetOdaMesajlarGetir()
        }

    }

    private fun sohbetOdaMesajlarGetir() {
        //burda bellekte yer aaçıyorum
        if (tumMesajlar == null){
            tumMesajlar=ArrayList<MetinMesaj>()
        }

        mMesajReferans = FirebaseDatabase.getInstance().reference
        var sorgu = mMesajReferans?.child("sohbet_odasi")?.child(secilenSohbetOdaID)?.child("sohbet_oda_mesaj")
            ?.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (mesaj in p0.children){
                        var yeniMesaj = MetinMesaj()
                        var kullaniciID = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id
                        if (kullaniciID != null){
                            yeniMesaj.kullanici_id = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id
                            yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                            yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman
                            yeniMesaj.profil_resmi = ""
                            yeniMesaj.adi = ""

                            tumMesajlar?.add(yeniMesaj)

                        }else {
                            yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                            yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman
                            yeniMesaj.profil_resmi = ""
                            yeniMesaj.adi = ""
                            tumMesajlar?.add(yeniMesaj)
                        }
                    }
                }

            })

    }

    private fun MesajListener() {
        mMesajReferans = FirebaseDatabase.getInstance().reference.child("sohbet_odasi")
            .child(secilenSohbetOdaID).child("sohbet_oda_mesaj")
        //değişiklikleri datachange yolladık
        mMesajReferans?.addValueEventListener(mValueEventListener)
    }

    private fun baslatFirebaseAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser
                if (kullanici == null){
                    var intent = Intent(this@SohbetOdaActivity,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener { mAuthListener }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener { mAuthListener }
        }
    }

    override fun onResume() {
        super.onResume()
        UserControl()
    }

    private fun UserControl() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici == null){
            var intent = Intent(this@SohbetOdaActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
    /* private fun SohbetOdasiMesajlarGetir(){
        var secilenSohbetOdaID = intent.getStringExtra("sohbet_odasi_id")
        //Log.e("SOhbetTest",secilenSohbetOdaID)

        tumMesajlar= ArrayList<MetinMesaj>()

        var ref = FirebaseDatabase.getInstance().reference
            ref.child("sohbet_odasi")
                .child(secilenSohbetOdaID).child("sohbet_oda_mesaj")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (mesaj in p0.children){
                            var yeniMesaj = MetinMesaj()
                            var kullaniciID = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id
                            if (kullaniciID != null){
                                yeniMesaj.kullanici_id = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id
                                yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                                yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman

                                tumMesajlar.add(yeniMesaj)

                            }else {
                                yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                                yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman

                                tumMesajlar.add(yeniMesaj)
                            }
                        }
                    }

                })
    }
    * */