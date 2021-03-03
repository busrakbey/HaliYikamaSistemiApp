package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "KAYNAK")
public class Kaynak {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    Long mid;

    @ColumnInfo(name = "mustId")
    Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "kaynakAdi")
    public String kaynakAdi;

    @ColumnInfo(name = "edinimTarihi")
    public String edinimTarihi;

    @ColumnInfo(name = "terkTarihi")
    public String terkTarihi;

    @ColumnInfo(name = "aktif")
    public Boolean aktif;

    @ColumnInfo(name = "aciklama")
    public String aciklama;

    @ColumnInfo(name = "kaynakTuru")
    public String kaynakTuru;

    @ColumnInfo(name = "marka")
    public String marka;

    @ColumnInfo(name = "model")
    public String model;

    @ColumnInfo(name = "seriNo")
    public String seriNo;

    @ColumnInfo(name = "plakaNo")
    public String plakaNo;

    @ColumnInfo(name = "senkronEdildi")
    public Boolean senkronEdildi;

    @ColumnInfo(name = "secilenKaynakMi")
    public Boolean secilenKaynakMi;

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

    public String getKaynakAdi() {
        return kaynakAdi;
    }

    public void setKaynakAdi(String kaynakAdi) {
        this.kaynakAdi = kaynakAdi;
    }

    public String getEdinimTarihi() {
        return edinimTarihi;
    }

    public void setEdinimTarihi(String edinimTarihi) {
        this.edinimTarihi = edinimTarihi;
    }

    public String getTerkTarihi() {
        return terkTarihi;
    }

    public void setTerkTarihi(String terkTarihi) {
        this.terkTarihi = terkTarihi;
    }

    public Boolean getAktif() {
        return aktif;
    }

    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getKaynakTuru() {
        return kaynakTuru;
    }

    public void setKaynakTuru(String kaynakTuru) {
        this.kaynakTuru = kaynakTuru;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSeriNo() {
        return seriNo;
    }

    public void setSeriNo(String seriNo) {
        this.seriNo = seriNo;
    }

    public String getPlakaNo() {
        return plakaNo;
    }

    public void setPlakaNo(String plakaNo) {
        this.plakaNo = plakaNo;
    }

    public Boolean getSenkronEdildi() {
        return senkronEdildi;
    }

    public void setSenkronEdildi(Boolean senkronEdildi) {
        this.senkronEdildi = senkronEdildi;
    }

    public Boolean getSecilenKaynakMi() {
        return secilenKaynakMi;
    }

    public void setSecilenKaynakMi(Boolean secilenKaynakMi) {
        this.secilenKaynakMi = secilenKaynakMi;
    }
}
