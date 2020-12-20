package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "URUN")
public class Urun {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "mustId")
    public Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "urunAdi")
    public String urunAdi;

    @ColumnInfo(name = "aktif")
    public Boolean aktif;

    @ColumnInfo(name = "aciklama")
    public String aciklama;


    @ColumnInfo(name = "tenantId")
    public Long tenantId;
}
