package com.example.haliyikamaapp.Model;

public class Musteri {

    private String musteriAdiSoyadi, tarih, telefonNo;
    public Musteri(String musteriAdiSoyadi, String tarih, String telefonNo ) {
        this.musteriAdiSoyadi = musteriAdiSoyadi;
        this.tarih = tarih;
        this.telefonNo = telefonNo;
    }

    public String getMusteriAdiSoyadi() {
        return musteriAdiSoyadi;
    }

    public void setMusteriAdiSoyadi(String musteriAdiSoyadi) {
        this.musteriAdiSoyadi = musteriAdiSoyadi;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getTelefonNo() {
        return telefonNo;
    }

    public void setTelefonNo(String telefonNo) {
        this.telefonNo = telefonNo;
    }
}
