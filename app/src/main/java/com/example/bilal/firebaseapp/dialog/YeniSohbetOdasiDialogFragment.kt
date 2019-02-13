package com.example.bilal.firebaseapp.dialog


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.activity.MainActivity
import com.example.bilal.firebaseapp.activity.SohbetActivty
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.model.SohbetOdasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_ayarlar.*
import kotlinx.android.synthetic.main.fragment_yeni_sohbet_odasi_dialog.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class YeniSohbetOdasiDialogFragment : DialogFragment() {

    lateinit var etsohbetOdasiAdi : EditText
    lateinit var btnSohbetOdasiOlustur : Button
    lateinit var btnIptal : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_yeni_sohbet_odasi_dialog, container, false)

        etsohbetOdasiAdi = view.findViewById(R.id.etYeniSohbetOdasiAdi)
        btnSohbetOdasiOlustur =view.findViewById(R.id.btnYeniOdaOlustur)
        btnIptal = view.findViewById(R.id.btnIptal)


        btnSohbetOdasiOlustur.setOnClickListener {
            if (!etsohbetOdasiAdi.text.isNullOrEmpty()){

                var ref = FirebaseDatabase.getInstance().reference
                var sohbetOdasiID = ref.child("sohbet_odasi").push().key

                var yeniSohbetOdasi = SohbetOdasi()
                yeniSohbetOdasi.olusturan_id = FirebaseAuth.getInstance().currentUser?.uid
                yeniSohbetOdasi.sohbetodasi_adi =etsohbetOdasiAdi.text.toString()
                yeniSohbetOdasi.sohbetodasi_id = sohbetOdasiID

                ref.child("sohbet_odasi").child(sohbetOdasiID!!).setValue(yeniSohbetOdasi)


                var metinMesajID = ref.child("sohbet_odasi").push().key
                var karsilamaMesaj = MetinMesaj()
                karsilamaMesaj.mesaj = "Sohbet Odasina hoşgeldin Beybisi"
                karsilamaMesaj.zaman = getDate()

                ref.child("sohbet_odasi")
                    .child(sohbetOdasiID)
                    .child("sohbet_oda_mesaj")
                    .child(metinMesajID!!)
                    .setValue(karsilamaMesaj)


                //Toast.makeText(activity,"Helal",Toast.LENGTH_SHORT).show()

                (activity as MainActivity).init()
                 dialog.dismiss()
            }else{
                Toast.makeText(activity,"Sıkıntı var",Toast.LENGTH_SHORT).show()
            }
        }

        btnIptal.setOnClickListener {
            dialog.dismiss()
        }



        return  view
    }

    private fun getDate(): String{
        var sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("tr"))
        return sdf.format(Date( ))
    }


}
