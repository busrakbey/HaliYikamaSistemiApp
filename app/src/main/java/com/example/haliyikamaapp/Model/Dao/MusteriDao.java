package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.haliyikamaapp.Model.Entity.Musteri;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface MusteriDao {
    @Query("SELECT * FROM MUSTERI")
    List<Musteri> getMusteriAll();

    @Insert
    long setMusteri(Musteri musteri);

    @Insert
    List<Long> setMusteriList(List<Musteri> musteri);

    @Update
    int updateMusteriList(List<Musteri> musteri);

    @Update
    int updateMusteri(Musteri musteri);

    @Query("UPDATE MUSTERI SET id = :id WHERE mid = :mid")
    int updateMusteriQuery(Long mid, Long id);

    @Query("UPDATE MUSTERI SET musteriAdi = :musteriAdi, musteriSoyadi = :musteriSoyadi, musteriTuru = :musteriTuru, telefonNumarasi = :telefonNumarasi, " +
            "vergiKimlikNo =:vergiNo, tcKimlikNo = :tcNo  WHERE id = :id")
    int updateMusteriAllColumnQuery(Long id, String musteriAdi, String musteriSoyadi, String musteriTuru , String telefonNumarasi, String tcNo, String vergiNo);

    @Query("DELETE FROM MUSTERI")
    void deleteMusteriAll();

    @Query("DELETE FROM MUSTERI where mid = :mid")
    int deletedMusteriForMid(Long mid );

    @Query("SELECT * FROM MUSTERI where mid = :mid")
    List<Musteri> getMusteriForMid(Long mid);

    @Query("SELECT * FROM MUSTERI where id = :id")
    List<Musteri> getMusteriForId(Long id);

}


