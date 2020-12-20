package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.haliyikamaapp.Model.Entity.SiparisDetay;

import java.util.List;

@Dao
public interface SiparisDetayDao {
    @Query("SELECT * FROM SIPARIS_DETAY")
    List<SiparisDetay> getSiparisDetayAll();

    @Insert
    void setSiparisDetay(SiparisDetay musteri);

    @Query("DELETE FROM SIPARIS_DETAY")
    void deleteSiparisDetayAll();

    @Query("DELETE FROM SIPARIS_DETAY where mid = :mid")
    void deletedSiparisDetayForMid(Long mid);

    @Query("SELECT * FROM SIPARIS_DETAY where mid = :mid")
    List<SiparisDetay> getSiparisDetayForMid(Long mid);

}
