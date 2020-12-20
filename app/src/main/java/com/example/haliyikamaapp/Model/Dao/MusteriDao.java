package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.haliyikamaapp.Model.Entity.Musteri;

import java.util.List;

@Dao
public interface MusteriDao {
    @Query("SELECT * FROM MUSTERI")
    List<Musteri> getMusteriAll();

    @Insert
    void setMusteri(Musteri musteri);

    @Query("DELETE FROM MUSTERI")
     void deleteMusteriAll();

    @Query("DELETE FROM MUSTERI where mid = :mid")
    void deletedMusteriForMid(Long mid );

    @Query("SELECT * FROM MUSTERI where mid = :mid")
    List<Musteri> getMusteriForMid(Long mid);

}


