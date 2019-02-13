package com.example.bilal.firebaseapp.dialog


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class ConfirmMailFragment : DialogFragment() {

    lateinit var emailEditText: EditText
    lateinit var passwEditText: EditText
    //lateinit var mContext: FragmentActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_confirm_mail, container, false)

        emailEditText = view.findViewById(R.id.etConfirmMail)
        passwEditText = view.findViewById(R.id.etConfirmPass)




        // Inflate the layout for this fragment
        var btnExit = view.findViewById<Button>(R.id.btnExit)
        btnExit.setOnClickListener {
            dialog.dismiss()
        }
        var btnSend = view.findViewById<Button>(R.id.btnSend)
        btnSend.setOnClickListener {

            if (emailEditText.text.toString().isNotEmpty() && passwEditText.text.toString().isNotEmpty()) {
                LoginToSendConfirmMail(emailEditText.text.toString(), passwEditText.text.toString())
            } else {
                Toast.makeText(activity, "Alanlar boş bırkılamaz", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun LoginToSendConfirmMail(mail: String, pasword: String) {
        var credential = EmailAuthProvider.getCredential(mail, pasword)
        FirebaseAuth.getInstance().signInWithCredential(credential)//kullanıcı onay maili istedi ve biz anlık olarak sisteme dahil ettik ve authstatelistener çalıştı
            .addOnCompleteListener { task ->
                //tek bir fonk var ve bu fonka task diye parametre giriyor daha önce p0 a denk geliyordu
                if (task.isSuccessful) {
                    sendConfirmMail()
                    dialog.dismiss()
                } else {
                    Toast.makeText(activity, "Email veya şifre hatalı", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun sendConfirmMail() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici != null) {
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if (p0.isSuccessful) {
                            //Toast.makeText(mContext, "Mail bilgilerinizi doğrulayın", Toast.LENGTH_SHORT).show()
                        } else {
                            //Toast.makeText(mContext, "Mail göderilemedi" + p0.exception?.message, Toast.LENGTH_SHORT) .show()
                        }
                    }

                })
        }
    }


}
