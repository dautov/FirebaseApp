package com.example.bilal.firebaseapp.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.img_mesaj_sag.view.*
import kotlinx.android.synthetic.main.img_mesaj_sol.view.*
import kotlinx.android.synthetic.main.text_mesaj_sag.view.*
import kotlinx.android.synthetic.main.pdf_mesaj.view.*
import kotlinx.android.synthetic.main.text_mesaj_sol.view.*
import java.lang.IllegalArgumentException


class SohbetMesajRecyclerViewAdapter(context : Context, tumMejlar : ArrayList<MetinMesaj>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mContext = context
    var mTumMesajlar = tumMejlar
    //var myActivity = mActivity


    companion object {
         val TXT = 1
         val IMG = 2
         val DOC = 3
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        var inflater = LayoutInflater.from(mContext)

        Log.e("createviewtest",p1.toString())

        when(p1){
            1 -> return  MesajViewHolder(inflater.inflate(R.layout.text_mesaj_sag,p0,false))
            2 -> return  MesajViewHolder2(inflater.inflate(R.layout.text_mesaj_sol,p0,false))
            3 -> return  ImageViewHolder(inflater.inflate(R.layout.img_mesaj_sag,p0,false))
            4 -> return  ImageViewHolder2(inflater.inflate(R.layout.img_mesaj_sol,p0,false))
            5 -> return  PDFMesajViewHolder(inflater.inflate(R.layout.pdf_mesaj,p0,false))
            6 -> return  PDFMesajViewHolder2(inflater.inflate(R.layout.pdf_mesaj,p0,false))
             else -> throw IllegalArgumentException("Invalid View Type") as Throwable

        }

        return object : RecyclerView.ViewHolder(View(mContext)){}

    }

    override fun getItemCount(): Int {
        Log.e("MesajCount Test",mTumMesajlar.size.toString())
        return mTumMesajlar.size

    }


    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        var viewtype = mTumMesajlar.get(p1)
        Log.e("bindviewTest",p0.toString())

        when(p0){
            is MesajViewHolder -> p0.setData(viewtype,p1)
            is MesajViewHolder2 -> p0.setData(viewtype,p1)
            is ImageViewHolder -> p0.setIMG(viewtype,p1)
            is ImageViewHolder2 -> p0.setIMG(viewtype,p1)
            is PDFMesajViewHolder -> p0.setPDF(viewtype,p1)
            is PDFMesajViewHolder2 -> p0.setPDF(viewtype,p1)
        }


    }


    override fun getItemViewType(position: Int): Int {
        var view = mTumMesajlar.get(position)
        Log.e("viewtypetest",view.type.toString())
        if ((view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == TXT)){
            return 1
        }else if (!(view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == TXT)){
            return 2
        }else if ((view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == IMG)){
            return 3
        }else if ((!view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == IMG)){
            return 4
        }else if ((view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == DOC)){
            return 5
        }else{
            return 6
        }

    }



    class MesajViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var layout = itemView as FrameLayout
        var mesaj = layout.tvMesajSag
        var isim = layout.tvAdi
        var zaman = layout.message_time_sag

        fun setData(oAnkiMesaj: MetinMesaj,p1: Int){


            mesaj.text = oAnkiMesaj.mesaj
            isim.text = oAnkiMesaj.adi
            zaman.text = oAnkiMesaj.zaman


        }


    }class MesajViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var layout = itemView as FrameLayout
        var mesaj = layout.tvMesajSol
        var isim = layout.tvAdiSol
        var zaman = layout.message_time_sol

        fun setData(oAnkiMesaj: MetinMesaj,p1: Int){

            mesaj.text = oAnkiMesaj.mesaj
            isim.text = oAnkiMesaj.adi
            zaman.text = oAnkiMesaj.zaman


        }


    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout = itemView as FrameLayout
        var profilResim = layout.imgResimMesajSag
        var zaman = layout.textView_message_time_sag



        fun setIMG(oAnkiMesaj : MetinMesaj, p1: Int){
            var path = oAnkiMesaj.mesaj
            if (path.isNullOrEmpty() or path.isNullOrBlank()){
                Picasso.get().load(R.drawable.ic_account_circle).into(profilResim)
            }else {
                Picasso.get().load(path).into(profilResim)
            }
            zaman.text = oAnkiMesaj.zaman



        }

    }class ImageViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout = itemView as FrameLayout
        var profilResim = layout.imgResimMesajSol
        var zaman = layout.textView_message_time_sol



        fun setIMG(oAnkiMesaj : MetinMesaj, p1: Int){
            var path = oAnkiMesaj.mesaj
            if (path.isNullOrEmpty() or path.isNullOrBlank()){
                Picasso.get().load(R.drawable.ic_account_circle).into(profilResim)
            }else {
                Picasso.get().load(path).into(profilResim)
            }
            zaman.text = oAnkiMesaj.zaman



        }

    }

    class PDFMesajViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var belge = itemView as ConstraintLayout
        var pdf = belge.txtPDF
        var pdf_view = belge.pdfView

        fun setPDF(oAnkiMesaj: MetinMesaj,p1: Int){
            pdf.text = oAnkiMesaj.mesaj


        }

    }

    class PDFMesajViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView) {
        var belge = itemView as ConstraintLayout
        var pdf = belge.txtPDF

        fun setPDF(oAnkiMesaj: MetinMesaj,p1: Int){
            pdf.text = oAnkiMesaj.mesaj

        }

    }

}

