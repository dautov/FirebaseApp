package com.example.bilal.firebaseapp.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.adapters.SohbetMesajRecyclerViewAdapter
import com.example.bilal.firebaseapp.model.FCMModel
import com.example.bilal.firebaseapp.model.Kullanici
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sohbet_oda.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class SohbetOdaActivity : AppCompatActivity() {

companion object {
    var open : Boolean = false
}

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
    var SERVER_KEY : String? = null
    var BASE_URL = "https://fcm.googleapis.com/fcm/"

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

        getServerKey()

        init()
        initBelge()
        rvMesaj.smoothScrollToPosition(mAdapter!!.itemCount-1)




    }

    private fun getServerKey(){
        var ref = FirebaseDatabase.getInstance().reference
            .child("server")
            .orderByValue()

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var singleSnapshot = p0?.children.iterator().next()
                SERVER_KEY = singleSnapshot.getValue().toString()
                Log.e("SERVERKEY",SERVER_KEY)
            }

        })
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

            dialog.setNegativeButton("Iptal",object : DialogInterface.OnClickListener{
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
            var asd = data.dataString

            var parsedilen = Uri.parse(asd)
            Log.e("ParsTestIMG","Adı : " +parsedilen.lastPathSegment + "Urı" + parsedilen.toString())

            resimYolu = galeriResimYolu
            Log.e("URIYoluTestResim"," Adı " +galeriResimYolu.lastPathSegment+" Urı"+ galeriResimYolu.toString())
            var dosyAdi = galeriResimYolu.lastPathSegment
            //tvDosyaAdi.text = dosyAdi
            uploadResim(galeriResimYolu)

        }else{
            var PdfYolu = data!!.data
            Log.e("URIYoluTestPDF",PdfYolu.toString())
            belgeYolu = PdfYolu
            var belgeAdi = PdfYolu.lastPathSegment


            uploadPdf(belgeAdi)
        }
    }

    private fun uploadResim(dosyaAdi: Uri?) {
        var storageReference = FirebaseStorage.getInstance().getReference()

        Log.e("UploadTestUR",resimYolu.toString())

        var ref = FirebaseDatabase.getInstance().reference
        var key = ref.push().key

        var keyIMG= getKey(key)
        var yol = storageReference.child("messages/users"+ FirebaseAuth.getInstance().currentUser!!.uid + "/images/"+ keyIMG +".jpeg")

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
                //Toast.makeText(this@SohbetOdaActivity,"Mesaj GİDECEKKK",Toast.LENGTH_SHORT).show()
                Log.e("DownloadURLRES",task.result.toString())
                Log.e("IMGNAME",dosyaAdi?.lastPathSegment?.takeLast(10))


                var ref = FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi")
                    .child(secilenSohbetOdaID)
                    .child("sohbet_oda_mesaj")


                var yenimesajID = ref.push().key

                var downloadLink = task.result
                var yazilanMesaj = downloadLink.toString()
                var kaydedilecekMesaj = MetinMesaj()
                kaydedilecekMesaj.mesaj = yazilanMesaj
                kaydedilecekMesaj.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                kaydedilecekMesaj.zaman = getMesajTarih()
                kaydedilecekMesaj.type = 2
                kaydedilecekMesaj.belge_adi = keyIMG
                kaydedilecekMesaj.mesaj_id = yenimesajID


                ref.child(yenimesajID!!)
                    .setValue(kaydedilecekMesaj)
            }
        }
    }

    private fun getKey(key: String?) : String{
        var id = 0

        for (i in 4..8) {
            id = id + key!![i].toInt()
        }



        return id.toString()
    }

    private fun uploadPdf(belgeAdi: String?) {
        var storageReference = FirebaseStorage.getInstance().getReference()

        Log.e("UploadTestUR",belgeYolu.toString())

        var ref = FirebaseDatabase.getInstance().reference
        var key = ref.key
        var yol = storageReference.child("messages/users"+ FirebaseAuth.getInstance().currentUser!!.uid + "/pdf/"+ belgeAdi +".pdf")

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

                var ref = FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi")
                    .child(secilenSohbetOdaID)
                    .child("sohbet_oda_mesaj")


                var yenimesajID = ref.push().key

                var downloadLink = task.result
                var yazilanMesaj = downloadLink.toString()
                var kaydedilecekMesaj = MetinMesaj()
                kaydedilecekMesaj.mesaj = yazilanMesaj
                kaydedilecekMesaj.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                kaydedilecekMesaj.zaman = getMesajTarih()
                kaydedilecekMesaj.type = 3
                kaydedilecekMesaj.belge_adi = belgeAdi
                kaydedilecekMesaj.mesaj_id = yenimesajID


                ref.child(yenimesajID!!)
                    .setValue(kaydedilecekMesaj)

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



                var retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myInterface = retrofit.create(FCMINterface::class.java)

                var headers = HashMap<String,String>()
                headers.put("Content-Type","application/json")
                headers.put("Authorization","key="+SERVER_KEY)


                var reference = FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi")
                    .child(secilenSohbetOdaID)
                    .child("sohbet_odasindaki_kullanicilar")
                    .orderByKey()
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            for (kullanici in p0.children){

                                var kullaniciID = kullanici.key
                                if (!kullaniciID.equals(FirebaseAuth.getInstance().currentUser?.uid)){
                                    var newRef = FirebaseDatabase.getInstance().reference
                                        .child("kullanici")
                                        .orderByKey()
                                        .equalTo(kullaniciID)
                                        .addListenerForSingleValueEvent(object : ValueEventListener{
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                var istenilenSatir = p0.children.iterator().next()
                                                var token = istenilenSatir.getValue(Kullanici::class.java)?.mesaj_token

                                                var data = FCMModel.Data("Yeni Mesaj Var",etMesaj.text.toString(),"sohbet",secilenSohbetOdaID)
                                                var to = token

                                                var bildirim : FCMModel = FCMModel(to!!,data)

                                                var istek = myInterface.bildirimGonder(headers,bildirim)
                                                istek.enqueue(object : Callback<Response<FCMModel>>{
                                                    override fun onFailure(call: Call<Response<FCMModel>>, t: Throwable) {
                                                        Log.e("RETROFIT","HATA: "+ t.message)
                                                    }

                                                    override fun onResponse(call: Call<Response<FCMModel>>, response: Response<Response<FCMModel>>) {
                                                        Log.e("RETROFIT","Başarılı: "+ response.toString())
                                                    }

                                                })
                                                etMesaj.setText("")


                                            }

                                        })
                                }
                            }
                        }//etMesaj.setText("")

                    })


                //etMesaj.setText("")
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



    //değişiklikleri dinleyen kısım
    var mValueEventListener : ValueEventListener = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            sohbetOdaMesajlarGetir()
            rvMesaj.smoothScrollToPosition(mAdapter!!.itemCount-1)
            if (open) {
                gorunenMesajSayisi(p0.childrenCount.toInt())
            }
        }

    }

    private fun gorunenMesajSayisi(mesajSayisi: Int) {
        var ref = FirebaseDatabase.getInstance().reference
            .child("sohbet_odasi").child(secilenSohbetOdaID).child("sohbet_odasindaki_kullanicilar")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("okunan_mesaj_sayisi")
            .setValue(mesajSayisi)

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
                                yeniMesaj.mesaj_id = mesaj.getValue(MetinMesaj::class.java)!!.mesaj_id

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
                                var ilk = tumMesajlar?.get(0)
                                Log.e("ILKMEsaj",ilk?.mesaj_id.toString())

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
        open = true
        FirebaseAuth.getInstance().addAuthStateListener { mAuthListener!! }
    }

    override fun onStop() {
        super.onStop()
        open = false
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