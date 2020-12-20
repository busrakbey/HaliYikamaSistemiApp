package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.haliyikamaapp.Model.Entity.Urun;

import java.util.List;

@Dao
public interface UrunDao {
   @Query("SELECT * FROM URUN")
    List<Urun> getUrunAll();

    @Insert
    void setUrun(Urun musteri);

    @Query("DELETE FROM URUN")
    void deleteUrunAll();

    @Query("DELETE FROM URUN where mid = :mid")
    void deletedUrunForMid(Long mid);

    @Query("SELECT * FROM URUN where mid = :mid")
    List<Urun> getUrunForMid(Long mid);

}
