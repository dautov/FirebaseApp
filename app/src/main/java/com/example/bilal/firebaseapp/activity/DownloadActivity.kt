package com.example.bilal.firebaseapp.activity

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.example.bilal.firebaseapp.R
import kotlinx.android.synthetic.main.activity_download.*

class DownloadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        progressBar2.visibility = View.VISIBLE
        var url = intent.getStringExtra("linkDownload")
        downloadFile(url)


        //onBackPressed()

    }
    private fun downloadFile(url: String) {

        val url = url

        val request = DownloadManager.Request(Uri.parse(url))

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or  DownloadManager.Request.NETWORK_WIFI)
        request.setTitle("Download")
        request.setDescription("The File is Downloading ...")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"${System.currentTimeMillis()}")

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        progressBar2.visibility = View.INVISIBLE

    }

}
