package com.example.bilal.firebaseapp.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tek_satir_mesaj.view.*


class SohbetMesajRecyclerViewAdapter(context: Context,tumMesajlar:ArrayList<MetinMesaj>): RecyclerView.Adapter<SohbetMesajRecyclerViewAdapter.SohbetMesajViewHolder>() {

    var myContext = context
    var myTumMesajlar = tumMesajlar

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SohbetMesajViewHolder {
        var inflater = LayoutInflater.from(myContext)
        var view = inflater.inflate(R.layout.tek_satir_mesaj,p0,false)

        return SohbetMesajViewHolder(view)
    }

    override fun getItemCount(): Int {

        return myTumMesajlar.size
    }

    override fun onBindViewHolder(p0: SohbetMesajViewHolder, p1: Int) {
        var oAnkiMesaj = myTumMesajlar.get(p1)
        p0.setData(oAnkiMesaj,p1)

    }

    inner class SohbetMesajViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var tekSatirMesaj = itemView as ConstraintLayout
        var profilResim = tekSatirMesaj.imgMesajProfil
        var mesaj = tekSatirMesaj.tvMesaj
        var kullanici = tekSatirMesaj.tvKullaniciAd
        var tarih = tekSatirMesaj.tvTarih


        fun setData(oAnkiMesaj: MetinMesaj, p1: Int) {
            mesaj.text = oAnkiMesaj.mesaj
            tarih.text = oAnkiMesaj.zaman
            kullanici.text = oAnkiMesaj.adi

            if (!oAnkiMesaj.profil_resmi.isNullOrEmpty()){
                Picasso.get().load(oAnkiMesaj.profil_resmi).resize(48,48).into(profilResim)
            }



        }


    }
}