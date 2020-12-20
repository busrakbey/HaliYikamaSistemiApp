package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.haliyikamaapp.Model.Entity.UrunFiyat;

import java.util.List;

@Dao
public interface UrunFiyatDao {
   @Query("SELECT * FROM URUN_FIYAT")
    List<UrunFiyat> getUrunFiyatAll();

    @Insert
    void setUrunFiyat(UrunFiyat musteri);

    @Query("DELETE FROM URUN_FIYAT")
    void deleteUrunFiyatAll();

    @Query("DELETE FROM URUN_FIYAT where mid = :mid")
    void deletedUrunFiyatForMid(Long mid);

    @Query("SELECT * FROM URUN_FIYAT where mid = :mid")
    List<UrunFiyat> getUrunFiyatForMid(Long mid);

}
