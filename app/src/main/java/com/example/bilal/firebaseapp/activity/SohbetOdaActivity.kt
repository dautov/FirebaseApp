package com.example.bilal.firebaseapp.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.adapters.SohbetMesajRecyclerViewAdapter
import com.example.bilal.firebaseapp.dialog.BelgePaylasimiDialogFragment
import com.example.bilal.firebaseapp.dialog.fragment_belge
import com.example.bilal.firebaseapp.model.Kullanici
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sohbet_oda.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

class SohbetOdaActivity : AppCompatActivity() {


    var izinler =false
    var tumMesajlar : ArrayList<MetinMesaj>?  = null
    var mAuthListener : FirebaseAuth.AuthStateListener? = null
    //var mStorageRef: StorageReference? = null
    var secilenSohbetOdaID : String = ""
    var mMesajReferans : DatabaseReference? = null
    var mAdapter : SohbetMesajRecyclerViewAdapter? = null
    var mesajIDSet : HashSet<String>? = null

    var resimYolu:Uri? = null
    var kameraBitmap:Bitmap? = null
    var belgeYolu:Uri? = null
    var dosyaAdi:String? = null
    var resimAdi:String?=null

    val Text : Int = 1
    val Image : Int = 2
    val PDF : Int = 3



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_oda)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //kullanici giriş çıkışları dinler
        baslatFirebaseAuthListener()

        //tıklanan odanın ID si
        SohbetOdasiIDGetir()
        init()
        initBelge()
        //rvMesaj.smoothScrollToPosition(mAdapter!!.itemCount-1)




    }



    private fun initBelge(){
        btnBelgeYolla.setOnClickListener(){
            rvMesaj.scrollToPosition(mAdapter!!.itemCount-1)
            var items = arrayOf<CharSequence>(
                "Fotograf Seç",
                "Pdf Seç"
            )

            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("Belge Türü Seçiniz")
            dialog.setItems(items,object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (which ==0){
                        Toast.makeText(this@SohbetOdaActivity,"Resim Seçtiniz",Toast.LENGTH_SHORT).show()
                        var intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        startActivityForResult(intent,100)
                    }else{
                        Toast.makeText(this@SohbetOdaActivity,"pdf Seçtiniz",Toast.LENGTH_SHORT).show()
                        var intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.setType("application/pdf")
                        startActivityForResult(Intent.createChooser(intent,"Select File"),300)
                    }
                }

            })

            dialog.setNegativeButton("Hayır",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.cancel()
                }

            })

            dialog.show()




        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==100 && resultCode == Activity.RESULT_OK && data != null){
            var galeriResimYolu = data.data
            resimYolu = galeriResimYolu
            Log.e("URIYoluTestResim",galeriResimYolu.toString())
            var dosyAdi = galeriResimYolu.lastPathSegment
            //tvDosyaAdi.text = dosyAdi
            uploadResim()

        }else{
            var PdfYolu = data!!.data
            Log.e("URIYoluTestPDF",PdfYolu.toString())
            belgeYolu = PdfYolu
            //tvDosyaAdi.text = PdfYolu.lastPathSegment


            uploadPdf()
        }
    }

    private fun uploadResim(){
        var storageReference = FirebaseStorage.getInstance().getReference()

        Log.e("UploadTestUR",resimYolu.toString())

        var ref = FirebaseDatabase.getInstance().reference
        var key = ref.key
        var yol = storageReference.child("messages/users"+ FirebaseAuth.getInstance().currentUser!!.uid + "/images" )

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
                Toast.makeText(this@SohbetOdaActivity,"Mesaj GİDECEKKK",Toast.LENGTH_SHORT).show()
                Log.e("DownloadURLRES",task.result.toString())

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
            }
        }
    }

    private fun uploadPdf(){
        var storageReference = FirebaseStorage.getInstance().getReference()

        Log.e("UploadTestUR",belgeYolu.toString())

        var ref = FirebaseDatabase.getInstance().reference
        var key = ref.key
        var yol = storageReference.child("messages/users"+ FirebaseAuth.getInstance().currentUser!!.uid + "/pdf" )

        var upload = yol.putFile(belgeYolu!!)

        var UrlTask = upload.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation yol.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this@SohbetOdaActivity,"Mesaj GİDECEKKK",Toast.LENGTH_SHORT).show()
                Log.e("DownloadURLRES",task.result.toString())
            }
        }
    }



    private fun init(){
        etMesaj.setOnClickListener{
            rvMesaj.smoothScrollToPosition(mAdapter!!.itemCount-1)
        }

        imgMesajYolla.setOnClickListener {
            if (!etMesaj.text.toString().equals("")){
                var yazilanMesaj = etMesaj.text.toString()
                var kaydedilecekMesaj = MetinMesaj()
                kaydedilecekMesaj.mesaj = yazilanMesaj
                kaydedilecekMesaj.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                kaydedilecekMesaj.zaman=getMesajTarih()
                kaydedilecekMesaj.type = Text

                var ref = FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi").child(secilenSohbetOdaID)
                    .child("sohbet_oda_mesaj")

                var yenimesajID = ref.push().key
                ref.child(yenimesajID!!)
                    .setValue(kaydedilecekMesaj)

                etMesaj.setText("")
            }
        }
    }

    private fun getMesajTarih(): String? {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())
    }

    private fun SohbetOdasiIDGetir() {
        var mainAct = intent.getStringExtra("sohbetodasi_id")




        if (mainAct != null){
            secilenSohbetOdaID = intent.getStringExtra("sohbetodasi_id")
        }else if (intent.hasExtra("kullanicidanGelen") != null ){
            secilenSohbetOdaID =  intent.getStringExtra("kullanicidanGelen")
        }

        Log.e("IDdogTest",secilenSohbetOdaID)
        MesajListener()
    }

    private fun SohbetodasiIDgetir(){

    }

    //değişiklikleri dinleyen kısım
    var mValueEventListener : ValueEventListener = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            sohbetOdaMesajlarGetir()
        }

    }

    private fun sohbetOdaMesajlarGetir() {
        //burda bellekte yer aaçıyorum
        if (tumMesajlar == null){
            tumMesajlar=ArrayList<MetinMesaj>()
            mesajIDSet = HashSet<String>()
        }

        mMesajReferans = FirebaseDatabase.getInstance().reference
        var sorgu = mMesajReferans?.child("sohbet_odasi")?.child(secilenSohbetOdaID)?.child("sohbet_oda_mesaj")
            ?.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (mesaj in p0.children){
                        var yeniMesaj = MetinMesaj()
                        var kullaniciID = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id


                        if (!mesajIDSet!!.contains(mesaj.key)){
                            mesajIDSet!!.add(mesaj.key!!)
                            if (kullaniciID != null){
                                yeniMesaj.kullanici_id = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id
                                yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                                yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman
                                yeniMesaj.type = mesaj.getValue(MetinMesaj::class.java)!!.type
                                yeniMesaj.belge_adi = mesaj.getValue(MetinMesaj::class.java)!!.belge_adi

                                var kullaniciBilgileri = mMesajReferans?.child("kullanici")?.orderByKey()?.equalTo(kullaniciID)
                                kullaniciBilgileri?.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        //bulunan kullanici
                                        if (p0.exists()){
                                            var kulllanici = p0?.children.iterator().next()
                                            yeniMesaj.profil_resmi = kulllanici.getValue(Kullanici::class.java)!!.profil_resmi
                                            yeniMesaj.adi = kulllanici.getValue(Kullanici::class.java)!!.isim

                                            mAdapter!!.notifyDataSetChanged()
                                        }


                                    }

                                })


                                tumMesajlar?.add(yeniMesaj)

                                mAdapter?.notifyDataSetChanged()

                                rvMesaj.scrollToPosition(mAdapter!!.itemCount-1)


                            }else {
                                yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                                yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman
                                yeniMesaj.type = mesaj.getValue(MetinMesaj::class.java)!!.type
                                yeniMesaj.profil_resmi = ""
                                yeniMesaj.adi = ""
                                tumMesajlar?.add(yeniMesaj)
                                mAdapter!!.notifyDataSetChanged()
                            }
                        }


                    }
                }

            })
        if (mAdapter == null){
            initMesajListesi()

        }
    }

    private fun initMesajListesi() {
        mAdapter = SohbetMesajRecyclerViewAdapter(this,tumMesajlar!!)
        rvMesaj.adapter = mAdapter
        rvMesaj.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvMesaj.scrollToPosition(mAdapter?.itemCount!! - 1)
    }

    private fun MesajListener() {
        mMesajReferans = FirebaseDatabase.getInstance().reference.child("sohbet_odasi")
            .child(secilenSohbetOdaID).child("sohbet_oda_mesaj")
        //değişiklikleri datachange yolladık
        mMesajReferans?.addValueEventListener(mValueEventListener)
    }

    private fun baslatFirebaseAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser
                if (kullanici == null){
                    var intent = Intent(this@SohbetOdaActivity,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener { mAuthListener!! }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener { mAuthListener!! }
        }
    }

    override fun onResume() {
        super.onResume()
        UserControl()
    }

    private fun UserControl() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici == null){
            var intent = Intent(this@SohbetOdaActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
    /* private fun SohbetOdasiMesajlarGetir(){
        var secilenSohbetOdaID = intent.getStringExtra("sohbet_odasi_id")
        //Log.e("SOhbetTest",secilenSohbetOdaID)

        tumMesajlar= ArrayList<MetinMesaj>()

        var ref = FirebaseDatabase.getInstance().reference
            ref.child("sohbet_odasi")
                .child(secilenSohbetOdaID).child("sohbet_oda_mesaj")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (mesaj in p0.children){
                            var yeniMesaj = MetinMesaj()
                            var kullaniciID = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id
                            if (kullaniciID != null){
                                yeniMesaj.kullanici_id = mesaj.getValue(MetinMesaj::class.java)!!.kullanici_id
                                yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                                yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman

                                tumMesajlar.add(yeniMesaj)

                            }else {
                                yeniMesaj.mesaj = mesaj.getValue(MetinMesaj::class.java)!!.mesaj
                                yeniMesaj.zaman = mesaj.getValue(MetinMesaj::class.java)!!.zaman

                                tumMesajlar.add(yeniMesaj)
                            }
                        }
                    }

                })
    }
    * */