package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.haliyikamaapp.Model.Entity.UrunSube;

import java.util.List;

@Dao
public interface UrunSubeDao {
    @Query("SELECT * FROM URUN_SUBE")
    List<UrunSube> getUrunSubeAll();

    @Insert
    void setUrunSube(UrunSube musteri);

    @Insert
    void setUrunSubeList(List<UrunSube> subeList);

    @Query("DELETE FROM URUN_SUBE")
    void deleteUrunSubeAll();

    @Query("DELETE FROM URUN_SUBE where mid = :mid")
    void deletedUrunSubeForMid(Long mid );

    @Query("SELECT * FROM URUN_SUBE where mid = :mid")
    List<UrunSube> getUrunSubeForMid(Long mid);

}




