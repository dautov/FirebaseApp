package com.example.bilal.firebaseapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.adapters.KullanicilarRecylerViewAdapter
import com.example.bilal.firebaseapp.model.Kullanici
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_kullanicilar.*

class KullanicilarActivity : AppCompatActivity() {

    var tumKullanicilar:ArrayList<Kullanici>? = null
    var mAdapter : KullanicilarRecylerViewAdapter? = null
    var mKullaniciReferans : DatabaseReference? = null
    var kullaniciIDSet: HashSet<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanicilar)
        supportActionBar?.setTitle("Kullanıcılar")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        kullaniciListener()
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
            mAdapter?.notifyDataSetChanged()
            tumKullanicilarigetir()

        }

    }

    //listener
    private fun kullaniciListener(){
        mKullaniciReferans = FirebaseDatabase.getInstance().reference.child("kullanici")

        mKullaniciReferans!!.addValueEventListener(mValueEventListener)
    }

    private fun tumKullanicilarigetir() {
        if (tumKullanicilar == null){
            tumKullanicilar = ArrayList<Kullanici>()
            kullaniciIDSet = HashSet<String>()

        }
        var ref = FirebaseDatabase.getInstance().reference

        var sorgu = ref.child("kullanici").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (tekKullanici in p0.children){
                    if (!kullaniciIDSet!!.contains(tekKullanici.key)){
                        kullaniciIDSet!!.add(tekKullanici.key!!)
                        Log.e("TekKullaniciTest",tekKullanici.key)
                        if (!tekKullanici.key.equals(FirebaseAuth.getInstance().currentUser?.uid)){
                            var oAnkiKullanici = Kullanici()
                            var nesneMap = (tekKullanici.getValue() as HashMap<String,Object>)
                            Log.e("UserEmptyCOntrol",nesneMap.get("kullanici_id").toString())

                            oAnkiKullanici.isim = nesneMap.get("isim").toString()
                            oAnkiKullanici.kullanici_id = nesneMap.get("kullanici_id").toString()
                            oAnkiKullanici.profil_resmi = nesneMap.get("profil_resmi").toString()


                            tumKullanicilar!!.add(oAnkiKullanici)

                            mAdapter?.notifyDataSetChanged()
                        }


                    }

                }
                if (mAdapter==null){
                    kullanicilariListele()
                }
            }

        })
    }
    private fun kullanicilariListele(){
        mAdapter = KullanicilarRecylerViewAdapter(this,tumKullanicilar!!)
        rvKullanicilar.adapter = mAdapter
        rvKullanicilar.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }
}
