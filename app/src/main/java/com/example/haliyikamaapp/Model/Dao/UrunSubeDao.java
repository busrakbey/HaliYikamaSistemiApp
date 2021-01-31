package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.UrunSube;

import java.util.List;

@Dao
public interface UrunSubeDao {
    @Query("SELECT * FROM URUN_SUBE")
    List<UrunSube> getUrunSubeAll();

    @Insert
    long setUrunSube(UrunSube musteri);

    @Insert
    List<Long> setUrunSubeList(List<UrunSube> subeList);

    @Update
    int updateUrunSube(UrunSube sube);

    @Query("DELETE FROM URUN_SUBE")
    void deleteUrunSubeAll();

    @Query("DELETE FROM URUN_SUBE where mid = :mid")
    void deletedUrunSubeForMid(Long mid );

    @Query("SELECT * FROM URUN_SUBE where mid = :mid")
    List<UrunSube> getUrunSubeForMid(Long mid);

    @Query("SELECT * FROM URUN_SUBE where id = :id")
    List<UrunSube> getUrunSubeForId(Long id);

    @Query("SELECT * FROM URUN_SUBE where urunMid = :urunMid")
    List<UrunSube> getUrunSubeForUrunMid(Long urunMid);

    @Query("SELECT * FROM URUN_SUBE where urunId = :urunId")
    List<UrunSube> getUrunSubeForUrunId(Long urunId);

    @Query("UPDATE URUN_SUBE SET id = :id WHERE mid = :mid")
    int updateUrunSubeQuery(Long mid, Long id);

    @Query("UPDATE URUN_SUBE SET urunId = :urunId WHERE urunMid = :urunMid")
    int updateUrunSubeQueryForUrunMid(Long urunMid, Long urunId);

}




