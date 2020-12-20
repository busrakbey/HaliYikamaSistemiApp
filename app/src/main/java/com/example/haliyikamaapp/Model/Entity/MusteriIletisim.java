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


}
