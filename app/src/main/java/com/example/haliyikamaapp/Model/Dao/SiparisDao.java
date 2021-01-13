package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Siparis;

import java.util.List;

@Dao
public interface SiparisDao {
    @Query("SELECT * FROM SIPARIS")
    List<Siparis> getSiparisAll();

    @Insert
    long setSiparis(Siparis siparis);

    @Update
    int updateSiparis(Siparis siparis);

    @Insert
    List<Long> setSiparisList(List<Siparis> siparis);

    @Update
    int updateSiparisList(List<Siparis> siparis);

    @Query("DELETE FROM SIPARIS")
    void deleteSiparisAll();

    @Query("UPDATE SIPARIS SET id = :id WHERE mid = :mid")
   int updateSiparisQuery(Long mid, Long id);

    @Query("DELETE FROM SIPARIS where mid = :mid")
    int deletedSiparisForMid(Long mid );

    @Query("SELECT * FROM SIPARIS where mid = :mid")
    List<Siparis> getSiparisForMid(Long mid);

    @Query("SELECT * FROM SIPARIS where musteriMid = :musteriMid")
    List<Siparis> getSiparisForMusteriMid(Long musteriMid);


    @Query("SELECT * FROM SIPARIS where id = :id")
    List<Siparis> getSiparisForSiparisId(Long id);

}