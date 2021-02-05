package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Update
    int updateUrunFiyat(UrunFiyat musteri);

    @Query("SELECT * FROM URUN_FIYAT where id = :id")
    List<UrunFiyat> getForId(Long id);

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

    @Query("UPDATE URUN_FIYAT SET id = :id , senkronEdildi = :senkronEdildiMi WHERE mid = :mid")
    int updateUrunFiyatQuery(Long mid, Long id, Boolean senkronEdildiMi);

    @Query("UPDATE URUN_FIYAT SET urunSubeId = :urunSubeId WHERE urunSubeMid = :urunSubeMid")
    int updateUrunFiyatQueryForUrunMid(Long urunSubeMid, Long urunSubeId);

}
