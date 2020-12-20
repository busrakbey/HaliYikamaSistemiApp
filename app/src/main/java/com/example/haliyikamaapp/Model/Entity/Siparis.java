package com.example.haliyikamaapp.Model.Entity;

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

    @ColumnInfo(name = "subeId")
    public Long subeId;

    @ColumnInfo(name = "siparisTarihi")
    public String siparisTarihi;

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
}
