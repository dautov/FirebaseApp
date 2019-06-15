package com.example.bilal.firebaseapp.model

import java.util.*

class MetinMesaj {

    /*companion object {
        const val Text_Mes = 1
        const val IMG_MES = 2
        const val PDF_MES =3
    }*/

    var mesaj: String? = null
    var kullanici_id: String? = null
    var zaman: String? = null
    var profil_resmi: String? = null
    var adi: String? = null
    var type : Int?  = null
    var belge_adi : String? = null
    var mesaj_id : String? = null

    constructor() {}

    constructor(mesaj: String, kullanici_id: String, zaman: String, profil_resmi: String, adi: String ,type : Int,belge_adi : String,mesaj_id: String) {
        this.mesaj = mesaj
        this.kullanici_id = kullanici_id
        this.zaman = zaman
        this.profil_resmi = profil_resmi
        this.adi = adi
        this.type = type
        this.belge_adi = belge_adi
        this.mesaj_id=mesaj_id
    }
}
