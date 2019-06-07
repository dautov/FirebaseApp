package com.example.bilal.firebaseapp.adapters

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.activity.SohbetOdaActivity
import com.example.bilal.firebaseapp.model.Kullanici
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.model.SohbetOdasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.satir_kullanici.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class KullanicilarRecylerViewAdapter (myActivity: AppCompatActivity,tumKullanicilar : ArrayList<Kullanici>) : RecyclerView.Adapter<KullanicilarRecylerViewAdapter.KullaniciHolder>() {

    var kullanicilar = tumKullanicilar
    //var mContext = context
    var mActivity = myActivity

    var  kullaniciAdi : String? = null
    var kullaniciID: String? = null

    var sohbetOdaID : String? = null

    var state : Boolean = true



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): KullaniciHolder {
        var inflater = LayoutInflater.from(p0.context)
        var tekSatirKullanicilar = inflater.inflate(R.layout.satir_kullanici,p0,false)

         return KullaniciHolder(tekSatirKullanicilar)
    }

    override fun getItemCount(): Int {
        Log.e("KullaniciSize",kullanicilar.size.toString())
        return kullanicilar.size
    }

    override fun onBindViewHolder(p0: KullaniciHolder, p1: Int) {
        var viewType = kullanicilar.get(p1)

        p0.setData(viewType,p1)
    }

    inner class KullaniciHolder(itemView : View) : RecyclerView.ViewHolder ( itemView) {
        var layout = itemView as ConstraintLayout
        var isim = layout.tvAdi
        var profilResim = layout.imgProfil




        fun setData(oAnkiKullanici : Kullanici,p1: Int) {
            var currentID = FirebaseAuth.getInstance().currentUser?.uid
            layout.setOnClickListener {

                var sorgu = FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (state == true){//oda ID null ise gir bak listeye
                                for (sohbet in p0.children){
                                    var olusturan = SohbetOdasi()
                                    var karsi = SohbetOdasi()

                                    var nesneMap = (sohbet.getValue() as HashMap<String,Object>)
                                    olusturan.olusturan_id = nesneMap.get("olusturan_id").toString()
                                    karsi.karsi_kisi_id    = nesneMap.get("karsi_kisi_id").toString()
                                    olusturan.sohbetodasi_id = nesneMap.get("sohbetodasi_id").toString()

                                    Log.e("OlusturanTest",olusturan.olusturan_id )
                                    Log.e("KarsiTest",karsi.karsi_kisi_id )


                                    Log.e("Current",currentID )
                                    Log.e("oAnki",oAnkiKullanici.kullanici_id )


                                    if ((currentID == olusturan.olusturan_id && oAnkiKullanici.kullanici_id == karsi.karsi_kisi_id) || (oAnkiKullanici.kullanici_id == olusturan.olusturan_id && currentID == karsi.karsi_kisi_id) ) {
                                        //sohbetOdasiOlustur(oAnkiKullanici.isim,oAnkiKullanici.kullanici_id)

                                        Log.e("OlusturanTestsohbet1", olusturan.sohbetodasi_id)
                                        sohbetOdaID = olusturan.sohbetodasi_id
                                        sohbetOdasinaGit(sohbetOdaID!!)
                                        state = false
                                    }

                                }
                                if (sohbetOdaID == null){
                                    Log.e("testForFor", currentID)
                                    //Toast.makeText(mActivity,"Yeni sohbet olustur", Toast.LENGTH_SHORT).show()
                                    sohbetOdasiOlustur(oAnkiKullanici.isim,oAnkiKullanici.kullanici_id)
                                    state = false
                                }



                            }else{
                                Toast.makeText(mActivity,"Yeni sohbet olustur12", Toast.LENGTH_SHORT).show()
                                //sohbetOdasiOlustur(oAnkiKullanici.isim,oAnkiKullanici.kullanici_id)
                            }
                        }

                    })

            /**/



            }

            isim.text = oAnkiKullanici.isim
            Log.e("NameInFor",isim.text.toString())
            var path = oAnkiKullanici.profil_resmi
            if (path.isNullOrEmpty() or path.isNullOrBlank()){
                Picasso.get().load(R.drawable.ic_account_circle).into(profilResim)
            }else {
                Picasso.get().load(path).into(profilResim)
            }



        }

        private fun sohbetOdasiOlustur(isim : String?,karsiID : String?) {

            var ref = FirebaseDatabase.getInstance().reference
            var sohbetOdasiID = ref.child("sohbet_odasi").push().key
            //sohbetOdaID = sohbetOdasiID

            var yeniSohbetOdasi = SohbetOdasi()
            yeniSohbetOdasi.olusturan_id = FirebaseAuth.getInstance().currentUser?.uid
            yeniSohbetOdasi.sohbetodasi_adi = isim
            yeniSohbetOdasi.sohbetodasi_id = sohbetOdasiID
            yeniSohbetOdasi.durum = "private"
            yeniSohbetOdasi.karsi_kisi_id = karsiID

            ref.child("sohbet_odasi").child(sohbetOdasiID!!).setValue(yeniSohbetOdasi)


            var metinMesajID = ref.child("sohbet_odasi").push().key
            var karsilamaMesaj = MetinMesaj()
            karsilamaMesaj.mesaj = "Sohbet Odasina hoşgeldiniz"
            karsilamaMesaj.type = 1
            karsilamaMesaj.zaman = getDate()
            karsilamaMesaj.belge_adi=""

            ref.child("sohbet_odasi")
                .child(sohbetOdasiID)
                .child("sohbet_oda_mesaj")
                .child(metinMesajID!!)
                .setValue(karsilamaMesaj)

            sohbetOdasinaGit(sohbetOdasiID)

        }

        private fun sohbetOdasinaGit(odaID : String){
            var intent = Intent(mActivity,SohbetOdaActivity::class.java)
            intent.putExtra("kullanicidanGelen",odaID)
            mActivity.startActivity(intent)

        }

        private fun getDate(): String{
            var sdf = SimpleDateFormat("HH:mm:ss", Locale("tr"))
            return sdf.format(Date( ))
        }

    }
}