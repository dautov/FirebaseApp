package com.example.bilal.firebaseapp.model

class SohbetOdasi {

    var sohbetodasi_adi: String? = null
    var olusturan_id: String? = null
    var sohbetodasi_id: String? = null
    var sohbet_oda_mesaj:List<MetinMesaj>? = null
    var karsi_kisi_id:String? = null
    var durum : String? = null

    constructor() {}

    constructor(sohbetodasi_adi: String, olusturan_id: String, sohbetodasi_id: String,sohbet_oda_mesaj:List<MetinMesaj>,karsi_kisi_id: String,durum : String) {
        this.sohbetodasi_adi = sohbetodasi_adi
        this.olusturan_id = olusturan_id
        this.sohbetodasi_id = sohbetodasi_id
        this.sohbet_oda_mesaj = sohbet_oda_mesaj
        this.karsi_kisi_id = karsi_kisi_id
        this.durum = durum
    }
}
