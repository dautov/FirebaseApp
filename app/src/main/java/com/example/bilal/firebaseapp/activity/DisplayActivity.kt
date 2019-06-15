package com.example.bilal.firebaseapp.activity

import android.content.ComponentCallbacks2
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.bilal.firebaseapp.R
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_display.*
import java.io.File

class DisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        ///data/user/0/com.example.bilal.app/files/PDF/-209689187
        // https://firebasestorage.googleapis.com/v0/b/fir-app-9e274.appspot.com/o/messages%2FusersIxxpoaOV1Oh6r3BfdBXal9GlYpm1%2Fpdf%2F203?alt=media&token=bfb0cff2-8524-47a5-9943-4ab6aad650aa

        var link = intent.getStringExtra("link")

        if (link != null){
            prgrsBarDisplay.visibility = View.VISIBLE

            FileLoader.with(this)
                .load(link,false)
                .fromDirectory("PDF",FileLoader.DIR_INTERNAL)
                .asFile(object : FileRequestListener<File>{
                    override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                        var pdf = p1?.body

                        pdfDisplay.fromFile(pdf)
                            .defaultPage(0)
                            .enableAnnotationRendering(true)
                            .swipeHorizontal(false)
                            .load()

                        prgrsBarDisplay.visibility = View.INVISIBLE
                    }

                    override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                        Toast.makeText(this@DisplayActivity,"Error" + p1?.message, Toast.LENGTH_SHORT).show()
                        prgrsBarDisplay.visibility = View.INVISIBLE
                    }

                })
        }


    }
}
