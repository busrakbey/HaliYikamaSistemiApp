package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MUSTERI")
public class Musteri {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    Long mid;

    @ColumnInfo(name = "mustId")
    Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "musteriAdi")
    public String musteriAdi;

    @ColumnInfo(name = "musteriSoyadi")
    public String musteriSoyadi;

    @ColumnInfo(name = "musteriTuru")
    public String musteriTuru;

    @ColumnInfo(name = "telefonNumarasi")
    public String telefonNumarasi;

    @ColumnInfo(name = "vergiKimlikNo")
    public String vergiKimlikNo;

    @ColumnInfo(name = "tcKimlikNo")
    public String tcKimlikNo;

    @ColumnInfo(name = "tenantId")
    public  String tenantId;




  /*  @NonNull
    @Override
    public String toString() {
        return ad + " " + soyad + " " + gsm + " " + kisiId;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMusteriAdi() {
        return musteriAdi;
    }

    public void setMusteriAdi(String musteriAdi) {
        this.musteriAdi = musteriAdi;
    }

    public String getMusteriSoyadi() {
        return musteriSoyadi;
    }

    public void setMusteriSoyadi(String musteriSoyadi) {
        this.musteriSoyadi = musteriSoyadi;
    }

    public String getMusteriTuru() {
        return musteriTuru;
    }

    public void setMusteriTuru(String musteriTuru) {
        this.musteriTuru = musteriTuru;
    }

    public String getTelefonNumarasi() {
        return telefonNumarasi;
    }

    public void setTelefonNumarasi(String telefonNumarasi) {
        this.telefonNumarasi = telefonNumarasi;
    }

    public String getVergiKimlikNo() {
        return vergiKimlikNo;
    }

    public void setVergiKimlikNo(String vergiKimlikNo) {
        this.vergiKimlikNo = vergiKimlikNo;
    }

    public String getTcKimlikNo() {
        return tcKimlikNo;
    }

    public void setTcKimlikNo(String tcKimlikNo) {
        this.tcKimlikNo = tcKimlikNo;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @NonNull
    public Long getMid() {
        return mid;
    }

    public void setMid(@NonNull Long mid) {
        this.mid = mid;
    }

    public Long getMustId() {
        return mustId;
    }

    public void setMustId(Long mustId) {
        this.mustId = mustId;
    }
}