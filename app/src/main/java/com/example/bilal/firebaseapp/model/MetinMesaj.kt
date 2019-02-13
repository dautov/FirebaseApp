package com.example.bilal.firebaseapp.model

class MetinMesaj {
    var mesaj: String? = null
    var kullanici_id: String? = null
    var zaman: String? = null
    var profil_resmi: String? = null
    var adi: String? = null

    constructor() {}

    constructor(mesaj: String, kullanici_id: String, zaman: String, profil_resmi: String, adi: String) {
        this.mesaj = mesaj
        this.kullanici_id = kullanici_id
        this.zaman = zaman
        this.profil_resmi = profil_resmi
        this.adi = adi
    }
}
