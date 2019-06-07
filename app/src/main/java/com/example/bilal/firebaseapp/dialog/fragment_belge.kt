package com.example.bilal.firebaseapp.dialog


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.example.bilal.firebaseapp.R
import kotlinx.android.synthetic.main.fragment_fragment_belge.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class fragment_belge : DialogFragment() {

    lateinit var tvGaleri:TextView
    lateinit var tvKamera:TextView
    lateinit var tvPDF:TextView
    lateinit var tvDosyaAdi:TextView
    lateinit var btnBelgeGonder : Button
    lateinit var btnIptal : Button



    interface onDosyaMesajListener {
        //telefonuuzda bulunan yolu seçiyoruz
        fun getResimYolu (resimPath:Uri?)

        fun getBelgeYolu(belgePath:Uri)
        //kamera
        fun getResimBitmap(bitmap: Bitmap)
    }

    lateinit var mDosyaMesajListener: onDosyaMesajListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater!!.inflate(R.layout.fragment_fragment_belge, container, false)
        tvGaleri = v.findViewById(R.id.tvGaleriden)
        tvKamera = v.findViewById(R.id.tvFotografCek)
        tvPDF = v.findViewById(R.id.tvPdf)
        tvDosyaAdi = v.findViewById(R.id.tvDosyaAdi)
        btnBelgeGonder = v.findViewById(R.id.btnBelgeMesaj)
        btnIptal = v.findViewById(R.id.btnGonderiIptal)



        tvGaleri.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,100)
        }
        tvKamera.setOnClickListener {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 200)
        }

        tvPDF.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("application/pdf")
            startActivityForResult(Intent.createChooser(intent,"Select File"),300)
        }
        btnBelgeGonder.setOnClickListener{

            Toast.makeText(activity,"Belge Upload edilecek", Toast.LENGTH_SHORT).show()
        }
/*
        btnIptal.setOnClickListener {
            dialog.dismiss()
        }*/


        return v
    }

    override fun onAttach(context: Context?) {
        mDosyaMesajListener = activity as onDosyaMesajListener
        super.onAttach(context)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==100 && resultCode == Activity.RESULT_OK && data != null){
            var galeriResimYolu = data.data
            var dosyAdi = galeriResimYolu.lastPathSegment
            tvDosyaAdi.text = dosyAdi


            mDosyaMesajListener.getResimYolu(galeriResimYolu)

        }else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null){
            var kameradanCekilen : Bitmap
            kameradanCekilen  = data.extras.get("data") as Bitmap

            tvDosyaAdi.text= "Kamera"
            mDosyaMesajListener.getResimBitmap(kameradanCekilen)
        }else{
            var galeriDosyaYolu = data!!.data
            var asd = galeriDosyaYolu.lastPathSegment
            mDosyaMesajListener.getBelgeYolu(galeriDosyaYolu)

        }
    }

    private fun uploadImage(){

    }


}
