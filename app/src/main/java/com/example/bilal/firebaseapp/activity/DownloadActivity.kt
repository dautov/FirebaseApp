package com.example.bilal.firebaseapp.activity

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.adapters.SohbetMesajRecyclerViewAdapter
import com.example.bilal.firebaseapp.model.MetinMesaj

class DownloadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        downloadFile()

        onBackPressed()

    }
    private fun downloadFile(mesaj : MetinMesaj){

        //val url = etURL.text.toString()

        val request = DownloadManager.Request(Uri.parse())

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or  DownloadManager.Request.NETWORK_WIFI)
        request.setTitle("Download")
        request.setDescription("The File is Downloading ...")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"${System.currentTimeMillis()}")

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)



    }

}
