package com.example.bilal.firebaseapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.dialog.ConfirmMailFragment
import com.example.bilal.firebaseapp.dialog.SifreSifirlamaDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initMyAuthStateListener()

        txtRegister.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvConfirmMail.setOnClickListener {
            var dialogGoster = ConfirmMailFragment()
            dialogGoster.show(supportFragmentManager,"dialogmailonay")
        }

        tvSifreunuttum.setOnClickListener(){
            var dialogSifreUnuttum = SifreSifirlamaDialogFragment()
            dialogSifreUnuttum.show(supportFragmentManager,"dialogsifreunuttum")
        }

        btnLogin.setOnClickListener {

            if (etEmail.text.isNotEmpty() && etpass.text.isNotEmpty()) {
                progressBarGoster()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(etEmail.text.toString(), etpass.text.toString())
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {
                                progressBarGosterme()

                                if (!p0.result.user.isEmailVerified){
                                    FirebaseAuth.getInstance().signOut()
                                }
                            } else {
                                progressBarGosterme()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Hatalı Giriş" + p0.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()


                            }
                        }

                    })

            } else {
                Toast.makeText(this@LoginActivity, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun progressBarGoster() {
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarGosterme() {
        progressBarLogin.visibility = View.INVISIBLE
    }

    private fun initMyAuthStateListener() {
        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = p0.currentUser
                if (user != null) {
                    if (user.isEmailVerified) {
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Mail adresinizi onaylayın", Toast.LENGTH_SHORT).show()

                    }
                }
            }

        }
    }


    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }
}
