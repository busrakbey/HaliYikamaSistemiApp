package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "S_ILCE")

public class S_ILCE {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public Long mid;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "adi")
    public String adi;

    @ColumnInfo(name = "ilId")
    public Long ilId;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return adi;
    }

    public void setAd(String ad) {
        this.adi = ad;
    }

    @NonNull
    public Long getMid() {
        return mid;
    }

    public void setMid(@NonNull Long mid) {
        this.mid = mid;
    }

    public String getAdi() {
        return adi;
    }

    public void setAdi(String adi) {
        this.adi = adi;
    }

    public Long getIlId() {
        return ilId;
    }

    public void setIlId(Long ilId) {
        this.ilId = ilId;
    }
}
