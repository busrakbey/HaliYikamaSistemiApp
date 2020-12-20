package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


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

    @ColumnInfo(name = "subeId")
    public Long subeId;

    @ColumnInfo(name = "olcuBirimId")
    public Long olcuBirimId;

    @ColumnInfo(name = "aciklama")
    public String  aciklama;

    @ColumnInfo(name = "aktif")
    public Boolean  aktif;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;

}
