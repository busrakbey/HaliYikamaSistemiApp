package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.haliyikamaapp.Model.Entity.Siparis;

import java.util.List;

@Dao
public interface SiparisDao {
    @Query("SELECT * FROM SIPARIS")
    List<Siparis> getMusteriAll();

    @Insert
    void setMusteri(Siparis siparis);

    @Query("DELETE FROM SIPARIS")
    void deleteMusteriAll();

    @Query("DELETE FROM SIPARIS where mid = :mid")
    void deletedMusteriForMid(Long mid );

    @Query("SELECT * FROM SIPARIS where mid = :mid")
    List<Siparis> getMusteriForMid(Long mid);

}