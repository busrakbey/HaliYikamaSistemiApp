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


}
