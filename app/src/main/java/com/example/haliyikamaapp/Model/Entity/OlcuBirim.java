package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "OLCU_BIRIM")
public class OlcuBirim {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "mustId")
    public Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "olcuBirimi")
    public String olcuBirimi;

    @ColumnInfo(name = "aktif")
    public Boolean aktif;

    @ColumnInfo(name = "aciklama")
    public String aciklama;

}
