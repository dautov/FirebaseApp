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
import android.widget.TextView

import com.example.bilal.firebaseapp.R

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
            var intent = Intent()
            intent.setType("application/pdf")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select File"),300)
        }

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

            mDosyaMesajListener.getResimYolu(galeriResimYolu)
            dismiss()
        }else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null){
            var kameradanCekilen = data.extras.get("data")as Bitmap
            mDosyaMesajListener.getResimBitmap(kameradanCekilen)
        }else if (requestCode == 300 && resultCode == Activity.RESULT_OK && data != null){
            var galeriDosyaYolu = data.data
            mDosyaMesajListener.getBelgeYolu(galeriDosyaYolu)
            dismiss()
        }
    }


}
