package com.example.bilal.firebaseapp.activity

import android.content.Context
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
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.adapters.SohbetMesajRecyclerViewAdapter
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

class SohbetOdaActivity : AppCompatActivity(), fragment_belge.onDosyaMesajListener {


    var izinler =false
    var tumMesajlar : ArrayList<MetinMesaj>?  = null
    var mAuthListener : FirebaseAuth.AuthStateListener? = null
    //var mStorageRef: StorageReference? = null
    var secilenSohbetOdaID : String = ""
    var mMesajReferans : DatabaseReference? = null
    var mAdapter : SohbetMesajRecyclerViewAdapter? = null
    var mesajIDSet : HashSet<String>? = null

    var galeriUri:Uri? = null
    var kameraBitmap:Bitmap? = null
    var belgeUri:Uri? = null
    var dosyaAdi:String? = null
    var resimAdi:String?=null

    val Text : Int = 1
    val Image : Int = 2
    val PDF : Int = 3


    override fun getResimYolu(resimPath: Uri?) {
        galeriUri = resimPath
        resimAdi = resimPath!!.lastPathSegment
    }

    override fun getBelgeYolu(belgePath: Uri) {
        belgeUri = belgePath
        dosyaAdi=belgePath.lastPathSegment

    }

    override fun getResimBitmap(bitmap: Bitmap) {
        kameraBitmap = bitmap
    }

    inner class BackgroundaCompress : AsyncTask<Uri,Void,ByteArray>{
        var mBitmap : Bitmap? = null

        constructor(){}
        constructor(bMap:Bitmap){
            if (bMap != null){
                mBitmap = bMap
            }
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg params: Uri?): ByteArray {
            if (mBitmap == null) {
                mBitmap = MediaStore.Images.Media.getBitmap(this@SohbetOdaActivity.contentResolver,params[0])
            }
            var resimBytes : ByteArray? = null
            for (i in 1..7){
                resimBytes = BitmapToByte(mBitmap,i) //sıkıştırılmış veri
            }
            return  resimBytes!!
        }

        private fun BitmapToByte(mBitmap: Bitmap?, i: Int): ByteArray? {
            var stream = ByteArrayOutputStream()
            mBitmap?.compress(Bitmap.CompressFormat.JPEG,i,stream)
            return stream.toByteArray()
        }


        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)

            uplodImageToFirebase(result)
        }

    }

    private fun uplodImageToFirebase(result: ByteArray?){
        var ref = FirebaseDatabase.getInstance().reference
        var key = ref.key
        var refStorage = FirebaseStorage.getInstance().getReference()
        var yol = refStorage.child("messages/users"+ FirebaseAuth.getInstance().currentUser!!.uid + "/" + resimAdi)

        var upload = yol.putBytes(result!!)

        var UrlTask = upload.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> {task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            Toast.makeText(this@SohbetOdaActivity,"Yüklendi ",Toast.LENGTH_SHORT).show()
            return@Continuation yol.downloadUrl
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_oda)
        //kullanici giriş çıkışları dinler
        baslatFirebaseAuthListener()

        //tıklanan odanın ID si
        SohbetOdasiIDGetir()
        init()
        initBelge()
        //rvMesaj.smoothScrollToPosition(mAdapter!!.itemCount-1)



    }

    private fun initBelge(){

        btnBelgeYolla.setOnClickListener{
            if (izinler){
                var dialog = fragment_belge()
                dialog.show(supportFragmentManager,"Belge Seç")
            }else{
                izinAl()
            }

            //overload
            if (galeriUri != null){
                photoCompress(galeriUri!!)
            }else if (kameraBitmap != null){
                photoCompress(kameraBitmap!!)
            }

        }
    }
    // bu kısımlar working thread tarafında çalışacak
    private fun photoCompress(galeriUri: Uri) {
        var compress = BackgroundaCompress()
        compress.execute(galeriUri)//doing background çalışmaya başlıyor
    }
    private fun photoCompress(kameraBitmap: Bitmap) {
        var compress = BackgroundaCompress(kameraBitmap)
        var uri : Uri? = null
        compress.execute(uri)
    }

    private fun izinAl() {
        var izin = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )
        if (ContextCompat.checkSelfPermission(this, izin[0]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, izin[1]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, izin[2]) == PackageManager.PERMISSION_GRANTED
        ) {
            izinler = true
        }else{
            ActivityCompat.requestPermissions(this, izin, 150)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 150) {
            //grantResult benim permission bilgileri tutar
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                var dialog = fragment_belge()
                dialog.show(supportFragmentManager,"fotosec")
            }else{
                Toast.makeText(this,"Bütün izinleri onaylamalısınız", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                    .child("sohbet_odasi").child(secilenSohbetOdaID).child("sohbet_oda_mesaj")

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
        secilenSohbetOdaID = intent.getStringExtra("sohbetodasi_id")
        Log.e("IDdogTest",secilenSohbetOdaID)
        MesajListener()
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