package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.List;


@Entity(tableName = "URUN_SUBE")
public class UrunSube {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "mustId")
    public Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "urunId")
    public Long urunId;

    @ColumnInfo(name = "urunMid")
    public Long urunMid;

    @ColumnInfo(name = "subeId")
    public Long subeId;

    @ColumnInfo(name = "subeAdi")
    public String subeAdi;

    @ColumnInfo(name = "olcuBirimId")
    public Long olcuBirimId;

    @ColumnInfo(name = "aciklama")
    public String  aciklama;

    @ColumnInfo(name = "aktif")
    public Boolean  aktif;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;

    @ColumnInfo(name = "senkronEdildi")
    public Boolean senkronEdildi;

    public Boolean getSenkronEdildi() {
        return senkronEdildi;
    }

    public void setSenkronEdildi(Boolean senkronEdildi) {
        this.senkronEdildi = senkronEdildi;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrunId() {
        return urunId;
    }

    public void setUrunId(Long urunId) {
        this.urunId = urunId;
    }

    public Long getSubeId() {
        return subeId;
    }

    public void setSubeId(Long subeId) {
        this.subeId = subeId;
    }

    public Long getOlcuBirimId() {
        return olcuBirimId;
    }

    public void setOlcuBirimId(Long olcuBirimId) {
        this.olcuBirimId = olcuBirimId;
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

    public String getSubeAdi() {
        return subeAdi;
    }

    public void setSubeAdi(String subeAdi) {
        this.subeAdi = subeAdi;
    }

    public Long getUrunMid() {
        return urunMid;
    }

    public void setUrunMid(Long urunMid) {
        this.urunMid = urunMid;
    }
}
