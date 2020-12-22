package com.example.haliyikamaapp.Model.Entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MUSTERI_ILETISIM")
public class MusteriIletisim {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "mustId")
    public Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "musteriId")
    public Long musteriId;

    @ColumnInfo(name = "musteriMid")
    public Long musteriMid;

    @ColumnInfo(name = "ilId")
    public Long ilId;

    @ColumnInfo(name = "ilceId")
    public Long ilceId;

    @ColumnInfo(name = "cadde")
    public String cadde;

    @ColumnInfo(name = "sokak")
    public String sokak;

    @ColumnInfo(name = "kapiNo")
    public String kapiNo;

    @ColumnInfo(name = "adres")
    public String adres;

    @ColumnInfo(name = "telefonNo")
    public String telefonNo;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;

    @ColumnInfo(name = "iletisimAdi")
    public String iletisimAdi;

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

    public Long getMusteriId() {
        return musteriId;
    }

    public void setMusteriId(Long musteriId) {
        this.musteriId = musteriId;
    }

    public Long getIlId() {
        return ilId;
    }

    public void setIlId(Long ilId) {
        this.ilId = ilId;
    }

    public Long getIlceId() {
        return ilceId;
    }

    public void setIlceId(Long ilceId) {
        this.ilceId = ilceId;
    }

    public String getCadde() {
        return cadde;
    }

    public void setCadde(String cadde) {
        this.cadde = cadde;
    }

    public String getSokak() {
        return sokak;
    }

    public void setSokak(String sokak) {
        this.sokak = sokak;
    }

    public String getKapiNo() {
        return kapiNo;
    }

    public void setKapiNo(String kapiNo) {
        this.kapiNo = kapiNo;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getTelefonNo() {
        return telefonNo;
    }

    public void setTelefonNo(String telefonNo) {
        this.telefonNo = telefonNo;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getIletisimAdi() {
        return iletisimAdi;
    }

    public void setIletisimAdi(String iletisimAdi) {
        this.iletisimAdi = iletisimAdi;
    }

    public Long getMusteriMid() {
        return musteriMid;
    }

    public void setMusteriMid(Long musteriMid) {
        this.musteriMid = musteriMid;
    }
}
