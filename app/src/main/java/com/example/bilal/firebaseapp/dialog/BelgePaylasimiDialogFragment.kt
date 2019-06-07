package com.example.bilal.firebaseapp.dialog


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class BelgePaylasimiDialogFragment : DialogFragment() {


    lateinit var tvResimSec : TextView
    lateinit var tvResimCek : TextView
    lateinit var tvPdfSec : TextView
    lateinit var tvDosyaAdi : TextView
    lateinit var btnIslemIptal : Button
    lateinit var btnMesajGonder : Button

    lateinit var secilenSohbetOdaID : String




    var belgeYolu : Uri? = null
    var resimYolu : Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        secilenSohbetOdaID = arguments!!.getString("odaID","")
        Log.e("BelgeSohbetOdaID",secilenSohbetOdaID)

        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_belge_paylasimi_dialog, container, false)

        tvResimSec = view.findViewById(R.id.tvFotografSec)
        tvResimCek = view.findViewById(R.id.tvResimCek)
        tvPdfSec = view.findViewById(R.id.tvPdfSec)
        tvDosyaAdi = view.findViewById(R.id.tvDosyaAdi)
        btnIslemIptal = view.findViewById(R.id.btnIslemIptal)
        btnMesajGonder = view.findViewById(R.id.btnMesajBelge)

        tvResimSec.setOnClickListener {
            //Toast.makeText(activity,"Resim Seçtin",Toast.LENGTH_SHORT).show()
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,100)

        }

        tvResimCek.setOnClickListener {
            Toast.makeText(activity,"Kamera Seçtin",Toast.LENGTH_SHORT).show()
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 200)

        }

        tvPdfSec.setOnClickListener {
            //Toast.makeText(activity,"PDF Seçtin",Toast.LENGTH_SHORT).show()
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("application/pdf")
            startActivityForResult(Intent.createChooser(intent,"Select File"),300)

        }

        btnIslemIptal.setOnClickListener {
            dialog.dismiss()

        }

        btnMesajGonder.setOnClickListener {


            if (resimYolu != null  ){
                Log.e("URIYoluTest",belgeYolu.toString())
                uploadResim()
                dialog.dismiss()
            }else if(belgeYolu != null){
               uploadPdf()
                dialog.dismiss()
            }else{
                Toast.makeText(activity,"Belge Seçilmedi",Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==100 && resultCode == Activity.RESULT_OK && data != null){
            var galeriResimYolu = data.data
            resimYolu = galeriResimYolu
            Log.e("URIYoluTest1",galeriResimYolu.toString())
            var dosyAdi = galeriResimYolu.lastPathSegment
            tvDosyaAdi.text = dosyAdi


           // mDosyaMesajListener.getResimYolu(galeriResimYolu)

        }else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null){
            var kameradanCekilen : Bitmap
            kameradanCekilen  = data.extras.get("data") as Bitmap

            tvDosyaAdi.text= "Kamera"
            //mDosyaMesajListener.getResimBitmap(kameradanCekilen)
        }else{
            var PdfYolu = data!!.data
            belgeYolu = PdfYolu
            tvDosyaAdi.text = PdfYolu.lastPathSegment

            //mDosyaMesajListener.getBelgeYolu(galeriDosyaYolu)

        }

    }

    private fun uploadResim(){
        var storageReference = FirebaseStorage.getInstance().getReference()

        Log.e("UploadTestUR",resimYolu.toString())

        var ref = FirebaseDatabase.getInstance().reference
        var key = ref.key
        var yol = storageReference.child("messages/users"+ FirebaseAuth.getInstance().currentUser!!.uid + "/" + tvDosyaAdi.text)

        var upload = yol.putFile(resimYolu!!)

        var UrlTask = upload.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation yol.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful){
                var downloadLink = task.result

                var yazilanMesaj = downloadLink.toString()
                var kaydedilecekMesaj = MetinMesaj()
                kaydedilecekMesaj.mesaj = yazilanMesaj
                kaydedilecekMesaj.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                kaydedilecekMesaj.zaman = getMesajTarih()
                kaydedilecekMesaj.type = 2

                var ref = FirebaseDatabase.getInstance().reference
                                         .child("sohbet_odasi")
                                         .child(secilenSohbetOdaID)
                                         .child("sohbet_oda_mesaj")

                var yenimesajID = ref.push().key
                ref.child(yenimesajID!!)
                    .setValue(kaydedilecekMesaj)
                Toast.makeText(activity,"Mesaj Gitti",Toast.LENGTH_SHORT).show()

                resimYolu = null
            }
        }

    }

    private fun uploadPdf(){}


    private fun getMesajTarih(): String? {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())
    }



}
