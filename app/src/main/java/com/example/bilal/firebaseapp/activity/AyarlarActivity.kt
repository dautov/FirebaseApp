package com.example.bilal.firebaseapp.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.dialog.ProfilResmiFragment
import com.example.bilal.firebaseapp.dialog.SifreSifirlamaDialogFragment
import com.example.bilal.firebaseapp.model.Kullanici
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_ayarlar.*
import java.io.ByteArrayOutputStream

class AyarlarActivity : AppCompatActivity(), ProfilResmiFragment.onProfilResimListener {

    var izinDurumu = false
    var galeridenGlenUri: Uri? = null
    var kamerdanGelenBmap: Bitmap? = null
    val MEGABYTE = 1000000.toDouble()

    override fun getResimYol(resimPath: Uri?) {
        galeridenGlenUri = resimPath
        Picasso.get().load(galeridenGlenUri).resize(100,100).into(imgProfil)

    }

    override fun getResimBitmap(bitmap: Bitmap) {
        kamerdanGelenBmap =bitmap
        imgProfil.setImageBitmap(bitmap)
        //Picasso.
    }

    inner class BackgroundImageCompress : AsyncTask<Uri,Void,ByteArray?>{

        var myBitmap : Bitmap? = null
        constructor(){

        }
        constructor(bmap:Bitmap){
            if (bmap != null){
                myBitmap=bmap
            }
        }


        //main threade çalışıyor yukarıdan aşağıya sıra ile tetikleniyor
        override fun onPreExecute() {
            super.onPreExecute()
        }
        //worker thread de çalışıyor
        override fun doInBackground(vararg params: Uri?): ByteArray? {
            //publishProgress()//işlmel bilgileri progress update e yollar
            //galeriden resim çekilmiş
            if (myBitmap ==null){
                myBitmap = MediaStore.Images.Media.getBitmap(this@AyarlarActivity.contentResolver,params[0])//contentResolver telefonda ki yani uri dan ilgili resmi elde etmek için
                //ve bunu myBitmap e atadım ve sonra bmape çevirdim
                Log.e("Test","orjinal boyut: " + (myBitmap!!.byteCount).toDouble()/MEGABYTE)
            }

            var resimBytes:ByteArray? = null
            for (i in 1..5){
                resimBytes=convertBitmaptoByte(myBitmap,100/i)
            }
            return resimBytes
        }

        private fun convertBitmaptoByte(myBitmap: Bitmap?,i:Int):ByteArray?{
            var stream = ByteArrayOutputStream()
            myBitmap?.compress(Bitmap.CompressFormat.JPEG,i,stream)
            return stream.toByteArray()

        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }
        // main threadde çalışıyor
        //result ta ise doinBackground da donderdğimiz değer gelecek
        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadToFirebase(result)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayarlar)

        var user = FirebaseAuth.getInstance().currentUser!!

        //etKullaniciAdi.setText(user.displayName.toString())
        //etSifre.setText(user.email.toString())
        kullaniciBilgileri()


        tvSifreResetle.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser?.email.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var dialog = SifreSifirlamaDialogFragment()
                        dialog.show(supportFragmentManager,"şifresifirlama")
                        Toast.makeText(this@AyarlarActivity, "asdsad", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AyarlarActivity, "asd", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnDegisiklikler.setOnClickListener {
            var ref =FirebaseDatabase.getInstance().reference
            if (etKullaniciAdi.text.toString().isNotEmpty()) {

                //if (!etKullaniciAdi.text.toString().equals(user.displayName.toString())) {
                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                        .setDisplayName(etKullaniciAdi.text.toString())
                        .build()
                    user.updateProfile(bilgileriGuncelle)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                    ref.child("kullanici")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                                    .child("isim")
                                    .setValue(etKullaniciAdi.text.toString())
                                //Toast.makeText(this@AyarlarActivity, "Değişiklikler yapıldı", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    this@AyarlarActivity,
                                    "Hata" + task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                //}
            } else {
                Toast.makeText(this@AyarlarActivity, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show()
            }
            if (etTelefon.text.isNotEmpty()){
            ref.child("kullanici")
                .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                .child("telefon")
                .setValue(etTelefon.text.toString())
            }
            if (galeridenGlenUri != null){
                photoCompress(galeridenGlenUri!!)
            }else if (kamerdanGelenBmap != null){
                photoCompress(kamerdanGelenBmap!!)
            }
        }

        tvSifreMailGuncelle.setOnClickListener {
            if (etSifre.text.toString().isNotEmpty()) {
                var credential = EmailAuthProvider.getCredential(user.email.toString(), etSifre.text.toString())

                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            SifreMailGuncelleLayout.visibility = View.VISIBLE

                            btnMailKaydet.setOnClickListener {
                                yeniMailGuncelle()
                            }
                            btnSifreKaydet.setOnClickListener {
                                yeniSifreGuncelle()
                            }
                        } else {
                            Toast.makeText(this@AyarlarActivity, "Mevcut şifrenizi doğru giriniz", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

            } else {
                Toast.makeText(this@AyarlarActivity, "Güncelleme için var olan şifrenizi giriniz", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        imgProfil.setOnClickListener {
            if (izinDurumu) {
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager, "Foto seç")
            } else {
                izinIste()
            }

        }
    }

    private fun uploadToFirebase(result: ByteArray?){
        progressBarGoster()
        var storageReferans = FirebaseStorage.getInstance().getReference()
        var resimDizin = storageReferans.child("image/users" + FirebaseAuth.getInstance().currentUser?.uid +"/profil_resim")

        var upload = resimDizin.putBytes(result!!)

        var urlTask = upload.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>>{ task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            //Log.e("Resim1","dizini: " + storageReferans.downloadUrl.toString())
            //Toast.makeText(this,"Uzantu111 == "+resimDizin.downloadUrl.toString(),Toast.LENGTH_SHORT).show()
            return@Continuation resimDizin.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful){
                var downloadUri = task.result
                FirebaseDatabase.getInstance().reference
                    .child("kullanici")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("profil_resmi")
                    .setValue(downloadUri.toString())
                progressBarGosterme()
                Toast.makeText(this,"Profil Resmi Başarıyla Güncellendi",Toast.LENGTH_SHORT).show()

            }else{
                progressBarGosterme()
                Toast.makeText(this,"Hata" + task.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun photoCompress(galeridenGlenUri: Uri) {
        var compress = BackgroundImageCompress()
        compress.execute(galeridenGlenUri)
    }
    private fun photoCompress(kamerdanGelenBmap : Bitmap) {
        var compress=BackgroundImageCompress(kamerdanGelenBmap)
        var uri:Uri? = null
        compress.execute(uri)
    }

    private fun izinIste() {
        var izin = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )
        if (ContextCompat.checkSelfPermission(this, izin[0]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, izin[1]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, izin[2]) == PackageManager.PERMISSION_GRANTED
        ) {
            izinDurumu = true
        } else {
            ActivityCompat.requestPermissions(this, izin, 150)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 150) {
            //grantResult benim permission bilgileri tutar
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager,"fotosec")
            }else{
                Toast.makeText(this,"Bütün izinleri onaylamalısınız",Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun kullaniciBilgileri() {
        var referans = FirebaseDatabase.getInstance().reference
        var kullanici = FirebaseAuth.getInstance().currentUser

        //Database sorgub
        var sorgu = referans.child("kullanici")
            .orderByKey()
            .equalTo(kullanici?.uid)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    var bilgi = data.getValue(Kullanici::class.java)
                    etKullaniciAdi.setText(bilgi?.isim)
                    etEmail.setText(bilgi?.mail)
                    etTelefon.setText(bilgi?.telefon)
                    if (bilgi?.kullanici_id.isNullOrEmpty()){
                        Log.e("KullaniciAyarlarUID","boş")
                    }else{
                        Log.e("KullaniciAyarlarUID",bilgi?.kullanici_id)
                    }
                    Log.e("KullaniciAyarlarUID",bilgi?.kullanici_id)
                    if (!bilgi?.profil_resmi.isNullOrEmpty()){
                        Picasso.get().load(bilgi?.profil_resmi).resize(100,100).into(imgProfil)
                    }else {
                        Picasso.get().load(R.drawable.ic_action_user).resize(100,100).into(imgProfil)
                    }

                }
            }

        })
    }

    private fun yeniMailGuncelle() {
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseAuth.getInstance().fetchProvidersForEmail(etMailGuncelle.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.getResult().providers?.size == 1) {
                            Toast.makeText(this@AyarlarActivity, "Email kullanımda", Toast.LENGTH_SHORT).show()
                        } else {
                            user.updateEmail(etSifreGuncelle.text.toString())
                                .addOnCompleteListener { task ->
                                    FirebaseAuth.getInstance().signOut()
                                    Toast.makeText(
                                        this@AyarlarActivity,
                                        "Mail bilgileriniz güncellendi tekrar giriş yapınız",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loginSayfasinaYonlendir()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this@AyarlarActivity,
                            "Hata oluştu" + task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
        }
    }

    private fun yeniSifreGuncelle() {
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.updatePassword(etSifreGuncelle.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@AyarlarActivity,
                            "Şifreniz başarı ile güncellendi tekrar giriş yapınız",
                            Toast.LENGTH_SHORT
                        ).show()
                        FirebaseAuth.getInstance().signOut()
                        loginSayfasinaYonlendir()
                    } else {
                        Toast.makeText(
                            this@AyarlarActivity,
                            "Hata oluştu" + task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun loginSayfasinaYonlendir() {
        var intent = Intent(this@AyarlarActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun progressBarGoster() {
        progressBarResim.visibility = View.VISIBLE
    }

    private fun progressBarGosterme() {
        progressBarResim.visibility = View.INVISIBLE
    }
}
