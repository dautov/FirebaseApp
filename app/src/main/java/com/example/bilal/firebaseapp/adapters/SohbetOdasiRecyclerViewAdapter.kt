package com.example.bilal.firebaseapp.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bilal.firebaseapp.model.SohbetOdasi
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.model.Kullanici
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.satir_sohbet_oda.view.*
import android.support.v7.app.AppCompatActivity
import com.example.bilal.firebaseapp.activity.MainActivity
import com.example.bilal.firebaseapp.activity.SohbetActivty
import com.example.bilal.firebaseapp.activity.SohbetOdaActivity
import java.text.FieldPosition

class SohbetOdasiRecyclerViewAdapter(mActivity: AppCompatActivity,tumSohbetOdalari:ArrayList<SohbetOdasi>) : RecyclerView.Adapter<SohbetOdasiRecyclerViewAdapter.SohbetOdasiHolder>() {

    var sohbetOdalari = tumSohbetOdalari
    var myActivity = mActivity

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SohbetOdasiHolder {
        var inflater = LayoutInflater.from(p0.context)
        var tekSatirSohbetOdalari = inflater.inflate(R.layout.satir_sohbet_oda,p0,false)

        return SohbetOdasiHolder(tekSatirSohbetOdalari)
    }

    override fun getItemCount(): Int {
        Log.e("ItemCount Test",sohbetOdalari.size.toString())
        return sohbetOdalari.size
    }

    override fun onBindViewHolder(p0: SohbetOdasiHolder, p1: Int) {
        var oAnOlusturulanSohbetOdasi = sohbetOdalari.get(p1)
        p0.setData(oAnOlusturulanSohbetOdasi,p1)
    }

    inner class SohbetOdasiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tekSatirSohbetOdasi = itemView as ConstraintLayout
        var sohbetOdasiOlusturan = tekSatirSohbetOdasi.tvOlusturanAdi
        var sohbetOdasiAdi = tekSatirSohbetOdasi.tvSohbetAdi
        var sohbetOdasiMesajSayisi = tekSatirSohbetOdasi.tvMesajSayisi
        var sohbetOdasiResim = tekSatirSohbetOdasi.imgProfilResim
        var sohbetOdasiSil= tekSatirSohbetOdasi.imgSohbetSil

        fun setData(oAnOlusturulanSohbetOdasi: SohbetOdasi, p1: Int) {
            sohbetOdasiAdi.text = oAnOlusturulanSohbetOdasi.sohbetodasi_adi

            sohbetOdasiMesajSayisi.text = (oAnOlusturulanSohbetOdasi.sohbet_oda_mesaj)?.size.toString()

            sohbetOdasiSil.setOnClickListener {
                if (oAnOlusturulanSohbetOdasi.olusturan_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    var dialog = AlertDialog.Builder(itemView.context)
                    dialog.setTitle("Sohbet Sil")
                    dialog.setMessage("Sohbet Odasını Silmek Üzeresiniz ")
                    dialog.setCancelable(true)
                    dialog.setPositiveButton("Evet",object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            (myActivity as MainActivity).sohbetOdasiSil(oAnOlusturulanSohbetOdasi.sohbetodasi_id.toString())

                        }
                    })
                    dialog.setNegativeButton("Hayır",object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                        }

                    })
                    dialog.show()

                }else {
                    Toast.makeText(itemView.context," Silmeye Yetkiniz Yok",Toast.LENGTH_SHORT).show()
                }

            }

            tekSatirSohbetOdasi.setOnClickListener {
                var intent = Intent(myActivity,SohbetOdaActivity::class.java)
                //tıklanan odanın ID sini SohbetOdaActvity ye yollar
                intent.putExtra("sohbet_odasi_id",oAnOlusturulanSohbetOdasi.sohbetodasi_id)
                myActivity.startActivity(intent)

            }



            var ref = FirebaseDatabase.getInstance().reference
            var sorgu = ref.child("kullanici")
                .orderByKey()
                .equalTo(oAnOlusturulanSohbetOdasi.olusturan_id).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (kullanici in p0.children){
                            sohbetOdasiOlusturan.text = kullanici.getValue(Kullanici::class.java)!!.isim.toString()

                            var profil = kullanici.getValue(Kullanici::class.java)!!.profil_resmi.toString()
                            Picasso.get().load(profil).into(sohbetOdasiResim)


                        }
                    }

                })
        }

    }


}