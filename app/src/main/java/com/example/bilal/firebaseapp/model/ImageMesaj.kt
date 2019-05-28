package com.example.bilal.firebaseapp.model

class ImageMesaj {
    var mesaj: String? = null
    var kullanici_id: String? = null
    var zaman: String? = null
    var profil_resmi: String? = null
    var adi: String? = null
    var type : Int?  = null

    constructor() {}

    constructor(mesaj: String, kullanici_id: String, zaman: String, profil_resmi: String, adi: String ,type : Int) {
        this.mesaj = mesaj
        this.kullanici_id = kullanici_id
        this.zaman = zaman
        this.profil_resmi = profil_resmi
        this.adi = adi
        this.type = type
    }
}