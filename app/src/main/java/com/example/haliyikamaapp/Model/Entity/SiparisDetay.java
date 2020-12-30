package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "SIPARIS_DETAY")
public class SiparisDetay {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "mustId")
    public Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "siparisId")
    public Long siparisId;

    @ColumnInfo(name = "siparisMid")
    public Long siparisMid;

    @ColumnInfo(name = "urunId")
    public Long urunId;

    @ColumnInfo(name = "urunMid")
    public Long urunMid;

    @ColumnInfo(name = "birimFiyat")
    public Double birimFiyat;

    @ColumnInfo(name = "miktar")
    public Double miktar;

    @ColumnInfo(name = "olcuBirimId")
    public Long olcuBirimId;

    @ColumnInfo(name = "olcuBirimMid")
    public Long olcuBirimMid;

    @ColumnInfo(name = "musteriId")
    public String musteriId;

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

    public Long getSiparisId() {
        return siparisId;
    }

    public void setSiparisId(Long siparisId) {
        this.siparisId = siparisId;
    }

    public Long getSiparisMid() {
        return siparisMid;
    }

    public void setSiparisMid(Long siparisMid) {
        this.siparisMid = siparisMid;
    }

    public Long getUrunId() {
        return urunId;
    }

    public void setUrunId(Long urunId) {
        this.urunId = urunId;
    }

    public Double getBirimFiyat() {
        return birimFiyat;
    }

    public void setBirimFiyat(Double birimFiyat) {
        this.birimFiyat = birimFiyat;
    }

    public Double getMiktar() {
        return miktar;
    }

    public void setMiktar(Double miktar) {
        this.miktar = miktar;
    }

    public Long getOlcuBirimId() {
        return olcuBirimId;
    }

    public void setOlcuBirimId(Long olcuBirimId) {
        this.olcuBirimId = olcuBirimId;
    }

    public String getMusteriId() {
        return musteriId;
    }

    public void setMusteriId(String musteriId) {
        this.musteriId = musteriId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getOlcuBirimMid() {
        return olcuBirimMid;
    }

    public void setOlcuBirimMid(Long olcuBirimMid) {
        this.olcuBirimMid = olcuBirimMid;
    }

    public Long getUrunMid() {
        return urunMid;
    }

    public void setUrunMid(Long urunMid) {
        this.urunMid = urunMid;
    }
}
