package com.example.bilal.firebaseapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.model.Kullanici
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPass1.text.isNotEmpty() && etPass2.text.isNotEmpty()) {
                if (etPass1.text.toString().equals(etPass2.text.toString())) {
                    newMember(etEmail.text.toString(), etPass1.text.toString())
                } else {
                    Toast.makeText(this, "şifre aynı değil", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Boş alanları doldurunuz ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun newMember(mail: String, passw: String) {

        progressBarGoster()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, passw)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {

                    if (p0.isSuccessful) {
                        progressBarGosterme()
                        onayMail()
                        var dbUyeKayit = Kullanici()
                        dbUyeKayit.isim = etName.text.toString()
                        dbUyeKayit.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                        dbUyeKayit.seviye = "1"
                        dbUyeKayit.mail = etEmail.text.toString()
                        dbUyeKayit.profil_resmi = ""
                        dbUyeKayit.kullanici_id = ""
                        dbUyeKayit.telefon = ""


                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                            .setValue(dbUyeKayit).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@RegisterActivity, "Kayıt Olundu ", Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    loginsayfasinayonlendir()
                                }
                            }

                    } else {
                        progressBarGosterme()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Kayıt Başarısız " + p0.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

    }

    private fun onayMail() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici != null) {
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if (p0.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Mail bilgilerinizi doğrulayın", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Mail göderilemedi" + p0.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
        }
    }

    private fun progressBarGoster() {
        progressBar.visibility = View.VISIBLE
    }

    private fun progressBarGosterme() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun loginsayfasinayonlendir() {
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }
    /*".read": false,
    ".write": false*/
}
