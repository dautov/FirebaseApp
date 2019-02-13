package com.example.bilal.firebaseapp.model

class SohbetOdasi {

    var sohbetodasi_adi: String? = null
    var olusturan_id: String? = null
    var sohbetodasi_id: String? = null
    var sohbet_oda_mesaj:List<MetinMesaj>? = null

    constructor() {}

    constructor(sohbetodasi_adi: String, olusturan_id: String, sohbetodasi_id: String,sohbet_oda_mesaj:List<MetinMesaj>) {
        this.sohbetodasi_adi = sohbetodasi_adi
        this.olusturan_id = olusturan_id
        this.sohbetodasi_id = sohbetodasi_id
        this.sohbet_oda_mesaj = sohbet_oda_mesaj
    }
}
