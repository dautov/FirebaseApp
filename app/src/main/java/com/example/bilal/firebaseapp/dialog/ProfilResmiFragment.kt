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


/**
 * A simple [Fragment] subclass.
 *
 */
class ProfilResmiFragment : DialogFragment() {

    lateinit var tvGaleridenSec: TextView
    lateinit var tvKameraCek: TextView
    //interface her resim değiştiğinde dinleyecek
    interface onProfilResimListener {
        //galeriden seçerken
        fun getResimYol(resimPath:Uri?)
        //kameradan çekerken
        fun getResimBitmap(bitmap: Bitmap)
    }

    lateinit var myProfilResimListener : onProfilResimListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_profil_resmi, container, false)

        tvGaleridenSec = v.findViewById(R.id.tvGaleri)
        tvKameraCek = v.findViewById(R.id.tvKamera)

        tvGaleridenSec.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }
        tvKameraCek.setOnClickListener {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 200)
        }

        return v
    }

    //yukarıda kullanıcının seçtiklerini dinleyecek
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            var galeridenSecilen = data.data
            myProfilResimListener.getResimYol(galeridenSecilen)
            dismiss()
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            var kameradanCekilen = data.extras.get("data")as Bitmap
            myProfilResimListener.getResimBitmap(kameradanCekilen)
            dialog.dismiss()
        }
    }

    override fun onAttach(context: Context?) {

        myProfilResimListener = activity as onProfilResimListener
        super.onAttach(context)
    }

}
