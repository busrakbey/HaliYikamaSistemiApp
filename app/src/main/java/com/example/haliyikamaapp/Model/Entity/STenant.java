package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "S_TENANT")
public class STenant {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "mustId")
    public Long mustId;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "hesapTuru")
    public String hesapTuru;

    @ColumnInfo(name = "isletmeAdi")
    public String isletmeAdi;

    @ColumnInfo(name = "tcKimlikNo")
    public String tcKimlikNo;

    @ColumnInfo(name = "vergiKimlikNo")
    public String vergiKimlikNo;

    @ColumnInfo(name = "ePosta")
    public String ePosta;

    @ColumnInfo(name = "durum")
    public String durum;

    @ColumnInfo(name = "gecerlilikTarihi")
    public String gecerlilikTarihi;

    @ColumnInfo(name = "baslangicTarihi")
    public String baslangicTarihi;

    @ColumnInfo(name = "sektorId")
    public Long sektorId;

}
