package com.example.bilal.firebaseapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.P
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.activity.MainActivity
import com.example.bilal.firebaseapp.activity.SohbetOdaActivity
import com.example.bilal.firebaseapp.model.SohbetOdasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var  okunacakMesajSayisi = 0

    override fun onMessageReceived(p0: RemoteMessage?) {

        if (!checkActivity()) {
            var bildirimBaslik = p0?.notification?.title
            var bildirimBody = p0?.notification?.body
            var data = p0?.data

            var baslik = data?.get("baslik")
            var icerik = data?.get("icerik")
            var bildirim = data?.get("bildirim_turu")
            var sohbetOdaID = data?.get("sohbet_odasi_id")



            Log.e(
                "FCM",
                "Başlık = " + baslik + " İçerik = " + icerik + " Bildirim Turu" + bildirim + " OdaID" + sohbetOdaID
            )


            var ref = FirebaseDatabase.getInstance().reference
                .child("sohbet_odasi")
                .orderByKey()
                .equalTo(sohbetOdaID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var tekSohbetOdasi = p0.children.iterator().next()
                        var oAnkiSohbetOdasi = SohbetOdasi()

                        var nesneMap = (tekSohbetOdasi.getValue() as HashMap<String, Object>)

                        oAnkiSohbetOdasi.olusturan_id = nesneMap.get("olusturan_id").toString()
                        oAnkiSohbetOdasi.sohbetodasi_adi = nesneMap.get("sohbetodasi_adi").toString()
                        oAnkiSohbetOdasi.sohbetodasi_id = nesneMap.get("sohbetodasi_id").toString()
                        oAnkiSohbetOdasi.karsi_kisi_id = nesneMap.get("karsi_kisi_id").toString()
                        oAnkiSohbetOdasi.durum = nesneMap.get("durum").toString()

                        var gorulenMesajSayisi = tekSohbetOdasi.child("sohbet_odasindaki_kullanicilar")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("okunan_mesaj_sayisi").getValue().toString().toInt()

                        var toplamMesajSayisi = tekSohbetOdasi.child("sohbet_oda_mesaj").childrenCount.toInt()

                        okunacakMesajSayisi = toplamMesajSayisi - gorulenMesajSayisi

                        bildirimGonder(baslik, icerik, bildirim, oAnkiSohbetOdasi)
                    }

                })
        }

    }
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        registerToDatabase(p0)
    }


    private fun bildirimGonder(baslik: String?, icerik: String?, bildirim: String?, oAnkiSohbetOdasi: SohbetOdasi) {
        var bildirimID = notificationIDOlustur(oAnkiSohbetOdasi.sohbetodasi_id)
        Log.e("FCMBID",bildirimID.toString())


        /*var builder = NotificationCompat.Builder(this@MyFirebaseMessagingService,oAnkiSohbetOdasi.sohbetodasi_adi!!)
            .setSmallIcon(R.drawable.ic_action_user)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_action_user))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(baslik)
            .setContentText("İçerik")
            .setAutoCancel(true)
            .setSubText("sub text")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Yazi"))
            .setNumber(987)
            .setOnlyAlertOnce(true)
            var builder = Notification.Builder(this)
            .setContentTitle("baslik")

        var noticationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        noticationManager.notify(bildirimID,builder.build())

        var pendingIntent = Intent(this,MainActivity::class.java)
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("sohbet_odasi_id",oAnkiSohbetOdasi.sohbetodasi_id!!)

        var bildirimPendingIntent = PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_CANCEL_CURRENT)
            .setContentIntent(bildirimPendingIntent)
            */

        var ChannelID = "ABCDRF"

        var builder = NotificationCompat.Builder(this,oAnkiSohbetOdasi.sohbetodasi_adi!!)
            .setSmallIcon(R.drawable.ic_action_user)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_action_user))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(baslik)
            .setContentText("Text")
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(icerik))
            .setNumber(okunacakMesajSayisi)
            .setSubText("" + okunacakMesajSayisi + " yeni mesaj")
            .build()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel  = NotificationChannel(oAnkiSohbetOdasi.sohbetodasi_adi!!,"SohbetChannel",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(bildirimID,builder)





    }

    private fun notificationIDOlustur(sohbetOdasiID: String?): Int {


        var ref = FirebaseDatabase.getInstance().reference
            .child("sohbet_odasi")
        var odaid = ref.push().key

        var id = 0

        for (i in 4..8) {
            id = id + sohbetOdasiID!![i].toInt()
        }



        return id
    }

    private fun checkActivity() : Boolean{
        if (SohbetOdaActivity.open){
            return true
        }else{
            return false
        }
    }



    private fun registerToDatabase(refreshedToken: String?) {
        var ref = FirebaseDatabase.getInstance().reference
            .child("kullanici")
            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
            .child("mesaj_token")
            .setValue(refreshedToken)
    }


}