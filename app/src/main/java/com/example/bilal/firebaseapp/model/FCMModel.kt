package com.example.bilal.firebaseapp.model


import com.google.gson.annotations.SerializedName

 class FCMModel {

    @SerializedName("to")
    var to: String? = null

    @SerializedName("data")
    var data: Data? = null

    constructor(to :String,data : Data){
        this.to = to
        this.data = data
    }





    class Data {

        @SerializedName("bildirim_turu")
        var bildirim_turu: String? = null

        @SerializedName("icerik")
        var icerik: String? = null

        @SerializedName("baslik")
        var baslik: String? = null

        @SerializedName("sohbet_odasi_id")
        var sohbet_odasi_id :String? = null

        constructor(baslik : String, icerik : String,bildirim_turu : String,sohbet_odasi_id : String){
            this.baslik = baslik
            this.icerik = icerik
            this.bildirim_turu = bildirim_turu
            this.sohbet_odasi_id = sohbet_odasi_id
        }
    }
}