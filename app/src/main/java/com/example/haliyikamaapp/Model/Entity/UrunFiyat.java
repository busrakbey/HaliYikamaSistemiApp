package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "URUN_FIYAT")
public class UrunFiyat {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "mustId")
    public Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "urunSubeId")
    public Long urunSubeId;


    @ColumnInfo(name = "urunSubeMid")
    public Long urunSubeMid;

    @ColumnInfo(name = "baslangicTarihi")
    public String baslangicTarihi;

    @ColumnInfo(name = "bitisTarihi")
    public String bitisTarihi;

    @ColumnInfo(name = "birimFiyat")
    public Double birimFiyat;

    @ColumnInfo(name = "aciklama")
    public String  aciklama;

    @ColumnInfo(name = "aktif")
    public Boolean  aktif;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrunSubeId() {
        return urunSubeId;
    }

    public void setUrunSubeId(Long urunSubeId) {
        this.urunSubeId = urunSubeId;
    }

    public String getBaslangicTarihi() {
        return baslangicTarihi;
    }

    public void setBaslangicTarihi(String baslangicTarihi) {
        this.baslangicTarihi = baslangicTarihi;
    }

    public String getBitisTarihi() {
        return bitisTarihi;
    }

    public void setBitisTarihi(String bitisTarihi) {
        this.bitisTarihi = bitisTarihi;
    }

    public Double getBirimFiyat() {
        return birimFiyat;
    }

    public void setBirimFiyat(Double birimFiyat) {
        this.birimFiyat = birimFiyat;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public Boolean getAktif() {
        return aktif;
    }

    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUrunSubeMid() {
        return urunSubeMid;
    }

    public void setUrunSubeMid(Long urunSubeMid) {
        this.urunSubeMid = urunSubeMid;
    }
}
