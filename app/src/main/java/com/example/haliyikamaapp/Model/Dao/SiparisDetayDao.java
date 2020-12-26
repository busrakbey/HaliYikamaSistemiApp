package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.SiparisDetay;

import java.util.List;

@Dao
public interface SiparisDetayDao {
    @Query("SELECT * FROM SIPARIS_DETAY")
    List<SiparisDetay> getSiparisDetayAll();

    @Insert
    long setSiparisDetay(SiparisDetay siparisDetay);

    @Update
    int updateSiparisDetay(SiparisDetay siparisDetay);

    @Query("DELETE FROM SIPARIS_DETAY")
    void deleteSiparisDetayAll();

    @Query("DELETE FROM SIPARIS_DETAY where mid = :mid")
    int deletedSiparisDetayForMid(Long mid);

    @Query("DELETE FROM SIPARIS_DETAY where mustId = :siparisMid")
    int deletedSiparisDetayForMustId(Long siparisMid);

    @Query("SELECT * FROM SIPARIS_DETAY where mid = :mid")
    List<SiparisDetay> getSiparisDetayForMid(Long mid);

    @Query("SELECT * FROM SIPARIS_DETAY where mustId = :siparisMid")
    List<SiparisDetay> getSiparisDetayForMustId(Long siparisMid);
}
