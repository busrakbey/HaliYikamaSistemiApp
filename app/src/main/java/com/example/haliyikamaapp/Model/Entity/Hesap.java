package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "HESAP")
public class Hesap {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    Long mid;

    @ColumnInfo(name = "mustId")
    Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "createdDate")
    public Timestamp createdDate;

    @ColumnInfo(name = "modifiedDate")
    public Timestamp modifiedDate;

    @ColumnInfo(name = "subeId")
    public Long subeId;

    @ColumnInfo(name = "subeMid")
    public Long subeMid;

    @ColumnInfo(name = "kaynakId")
    public Long kaynakId;

    @ColumnInfo(name = "islemTuru")
    public String islemTuru;

    @ColumnInfo(name = "tarih")
    public String tarih;

    @ColumnInfo(name = "detayNeden")
    public String detayNeden;

    @ColumnInfo(name = "islemNedeni")
    public String islemNedeni;

    @ColumnInfo(name = "tutar")
    public Double tutar;

    @ColumnInfo(name = "aciklama")
    public String aciklama;

    @ColumnInfo(name = "tenantId")
    public String tenantId;

    @ColumnInfo(name = "senkronEdildi")
    public Boolean senkronEdildi;

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

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getSubeId() {
        return subeId;
    }

    public void setSubeId(Long subeId) {
        this.subeId = subeId;
    }

    public Long getKaynakId() {
        return kaynakId;
    }

    public void setKaynakId(Long kaynakId) {
        this.kaynakId = kaynakId;
    }

    public String getIslemTuru() {
        return islemTuru;
    }

    public void setIslemTuru(String islemTuru) {
        this.islemTuru = islemTuru;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getDetayNeden() {
        return detayNeden;
    }

    public void setDetayNeden(String detayNeden) {
        this.detayNeden = detayNeden;
    }

    public String getIslemNedeni() {
        return islemNedeni;
    }

    public void setIslemNedeni(String islemNedeni) {
        this.islemNedeni = islemNedeni;
    }

    public Double getTutar() {
        return tutar;
    }

    public void setTutar(Double tutar) {
        this.tutar = tutar;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getSubeMid() {
        return subeMid;
    }

    public void setSubeMid(Long subeMid) {
        this.subeMid = subeMid;
    }

    public Boolean getSenkronEdildi() {
        return senkronEdildi;
    }

    public void setSenkronEdildi(Boolean senkronEdildi) {
        this.senkronEdildi = senkronEdildi;
    }
}
