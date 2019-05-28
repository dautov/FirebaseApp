package com.example.bilal.firebaseapp.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.img_mesaj.view.*
import kotlinx.android.synthetic.main.pdf_mesaj.view.*
import kotlinx.android.synthetic.main.tek_satir_mesaj.view.*
import java.lang.IllegalArgumentException


class SohbetMesajRecyclerViewAdapter(context: Context,tumMejlar : ArrayList<MetinMesaj>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mContext = context
    var mTumMesajlar = tumMejlar

    companion object {
         val TXT = 1
         val IMG = 2
         val DOC = 3
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        var inflater = LayoutInflater.from(mContext)
        Log.e("createviewtest",p1.toString())
        when(p1){
            TXT -> return  MesajViewHolder(inflater.inflate(R.layout.tek_satir_mesaj,p0,false))
            IMG -> return  ImageViewHolder(inflater.inflate(R.layout.img_mesaj,p0,false))
            DOC -> return PDFMesajViewHolder(inflater.inflate(R.layout.pdf_mesaj,p0,false))
             else -> throw IllegalArgumentException("Invalid View Type") as Throwable

        }

        //return object : RecyclerView.ViewHolder(View(mContext)){}

    }

    override fun getItemCount(): Int {
        Log.e("MesajCount Test",mTumMesajlar.size.toString())
        return mTumMesajlar.size

    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        var viewtype = mTumMesajlar.get(p1)
        Log.e("bindviewTest",p0.toString())

        /*when(viewtype.type){
            TXT -> (p0 as MesajViewHolder).setData(viewtype,p1)
            IMG -> (p0 as ImageViewHolder).setIMG(viewtype,p1)
            DOC -> (p0 as PDFMesajViewHolder).setPDF(viewtype,p1)


        }*/
        when(p0){
            is MesajViewHolder -> p0.setData(viewtype,p1)
            is ImageViewHolder -> p0.setIMG(viewtype,p1)
            is PDFMesajViewHolder -> p0.setPDF(viewtype,p1)
        }


    }


    override fun getItemViewType(position: Int): Int {
        var view = mTumMesajlar.get(position)
        Log.e("viewtypetest",view.type.toString())
        return view.type!!
        /*if (view.type != null){

        }else {
            return 1
        }*/

        /*return when(view){
            is MetinMesaj ->  TXT
            is MetinMesaj -> IMG
            is MetinMesaj -> DOC
            else -> throw IllegalArgumentException("Invalid view type")
        }*/
        //Log.e("returntest",return )


    }



    class MesajViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tekSatirMesaj = itemView as ConstraintLayout
        var mesaj = tekSatirMesaj.tvMesaj
        var kullanici = tekSatirMesaj.tvKullaniciAd2
        var tarih = tekSatirMesaj.tvTarih
        fun setData(oAnkiMesaj: MetinMesaj,p1: Int){
            mesaj.text = oAnkiMesaj.mesaj
            tarih.text = oAnkiMesaj.zaman
            kullanici.text = oAnkiMesaj.adi
        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var resim = itemView as RelativeLayout
        var resimMesaj = resim.tvResimRel

        fun setIMG(oAnkiMesaj : MetinMesaj, p1: Int){
            resimMesaj.text = "RESimli Mesaj"


        }

    }

    class PDFMesajViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var belge = itemView as ConstraintLayout
        var pdf = belge.txtPDF

        fun setPDF(oAnkiMesaj: MetinMesaj,p1: Int){
            pdf.text = oAnkiMesaj.mesaj

        }

    }

}
