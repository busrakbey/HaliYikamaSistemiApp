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

    @ColumnInfo(name = "urunId")
    public Long urunId;

    @ColumnInfo(name = "birimFiyat")
    public Double birimFiyat;

    @ColumnInfo(name = "miktar")
    public Double miktar;

    @ColumnInfo(name = "olcuBirimId")
    public Long olcuBirimId;

    @ColumnInfo(name = "musteriId")
    public String musteriId;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;


}
