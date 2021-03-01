package com.example.haliyikamaapp.Model.Entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SIPARIS")
public class Siparis {

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

    @ColumnInfo(name = "subeId")
    public Long subeId;

    @ColumnInfo(name = "subeMid")
    public Long subeMid;

    @ColumnInfo(name = "siparisTarihi")
    public String siparisTarihi;

    @ColumnInfo(name = "teslimEtmeTarihi")
    public String teslimEtmeTarihi;

    @ColumnInfo(name = "teslimAlmaTarihi")
    public String teslimAlmaTarihi;

    @ColumnInfo(name = "siparisTutar")
    public Double siparisTutar;

    @ColumnInfo(name = "aciklama")
    public String aciklama;

    @ColumnInfo(name = "aktif")
    public Boolean aktif;

    @ColumnInfo(name = "uygulananIskontoOrani")
    public  Double uygulananIskontoOrani;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;

    @ColumnInfo(name = "siparisDurumu")
    public String siparisDurumu;

    @ColumnInfo(name = "teslimAlinacak")
    public Boolean teslimAlinacak;

    @ColumnInfo(name = "processInstanceId")
    public Long processInstanceId;

    @ColumnInfo(name = "kaynakId")
    public Long kaynakId;

    @ColumnInfo(name = "kaynakMid")
    public Long kaynakMid;

    @ColumnInfo(name = "barkod")
    public String barkod;

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

    public Long getMusteriId() {
        return musteriId;
    }

    public void setMusteriId(Long musteriId) {
        this.musteriId = musteriId;
    }

    public Long getMusteriMid() {
        return musteriMid;
    }

    public void setMusteriMid(Long musteriMid) {
        this.musteriMid = musteriMid;
    }

    public Long getSubeId() {
        return subeId;
    }

    public void setSubeId(Long subeId) {
        this.subeId = subeId;
    }

    public Long getSubeMid() {
        return subeMid;
    }

    public void setSubeMid(Long subeMid) {
        this.subeMid = subeMid;
    }

    public String getSiparisTarihi() {
        return siparisTarihi;
    }

    public void setSiparisTarihi(String siparisTarihi) {
        this.siparisTarihi = siparisTarihi;
    }

    public Double getSiparisTutar() {
        return siparisTutar;
    }

    public void setSiparisTutar(Double siparisTutar) {
        this.siparisTutar = siparisTutar;
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

    public Double getUygulananIskontoOrani() {
        return uygulananIskontoOrani;
    }

    public void setUygulananIskontoOrani(Double uygulananIskontoOrani) {
        this.uygulananIskontoOrani = uygulananIskontoOrani;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getSiparisDurumu() {
        return siparisDurumu;
    }

    public void setSiparisDurumu(String siparisDurumu) {
        this.siparisDurumu = siparisDurumu;
    }

    public Boolean getTeslimAlinacak() {
        return teslimAlinacak;
    }

    public void setTeslimAlinacak(Boolean teslimAlinacak) {
        this.teslimAlinacak = teslimAlinacak;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getBarkod() {
        return barkod;
    }

    public void setBarkod(String barkod) {
        this.barkod = barkod;
    }

    public Long getKaynakId() {
        return kaynakId;
    }

    public void setKaynakId(Long kaynakId) {
        this.kaynakId = kaynakId;
    }

    public Long getKaynakMid() {
        return kaynakMid;
    }

    public void setKaynakMid(Long kaynakMid) {
        this.kaynakMid = kaynakMid;
    }

    public String getTeslimEtmeTarihi() {
        return teslimEtmeTarihi;
    }

    public void setTeslimEtmeTarihi(String teslimEtmeTarihi) {
        this.teslimEtmeTarihi = teslimEtmeTarihi;
    }

    public String getTeslimAlmaTarihi() {
        return teslimAlmaTarihi;
    }

    public void setTeslimAlmaTarihi(String teslimAlmaTarihi) {
        this.teslimAlmaTarihi = teslimAlmaTarihi;
    }
}
