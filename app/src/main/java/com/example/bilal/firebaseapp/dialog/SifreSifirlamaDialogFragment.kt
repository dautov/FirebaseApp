package com.example.bilal.firebaseapp.dialog


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass.
 * Use the [SifreSifirlamaDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SifreSifirlamaDialogFragment : DialogFragment() {

    lateinit var eMail:EditText
    private var mContext : FragmentActivity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_sifre_sifirlama_dialog, container, false)

        mContext= activity
        eMail= view.findViewById(R.id.tvMail)

        var btnIptal = view.findViewById<Button>(R.id.btnIptal)
        btnIptal.setOnClickListener{
            dialog.dismiss()
        }

        var btnGonder = view.findViewById<Button>(R.id.btnGonder)
        btnGonder.setOnClickListener{
            FirebaseAuth.getInstance().sendPasswordResetEmail(eMail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(mContext,"Şifre Sıfırlama Maili gönderildi",Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }else {
                        Toast.makeText(mContext,"Hata Oluştu"+task.exception?.message,Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
        }

        return view
    }


}
