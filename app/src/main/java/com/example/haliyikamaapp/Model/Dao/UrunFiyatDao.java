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
    long setUrunFiyat(UrunFiyat musteri);

    @Insert
    void setUrunFiyatList(List<UrunFiyat> urunFiyatList);


    @Query("DELETE FROM URUN_FIYAT")
    void deleteUrunFiyatAll();

    @Query("DELETE FROM URUN_FIYAT where mid = :mid")
    void deletedUrunFiyatForMid(Long mid);

    @Query("SELECT * FROM URUN_FIYAT where mid = :mid")
    List<UrunFiyat> getUrunFiyatForMid(Long mid);

    @Query("SELECT * FROM URUN_FIYAT where urunSubeMid = :urunSubeMid")
    List<UrunFiyat> getForUrunSubeMid(Long urunSubeMid);

    @Query("SELECT * FROM URUN_FIYAT where urunSubeId = :urunSubeId")
    List<UrunFiyat> getForUrunSubeId(Long urunSubeId);

}
