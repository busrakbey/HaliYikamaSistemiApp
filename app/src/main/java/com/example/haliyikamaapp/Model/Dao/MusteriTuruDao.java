package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.MusteriTuru;

import java.util.List;


@Dao
public interface MusteriTuruDao {

    @Query("SELECT * FROM MUSTERI_TURU")
    List<MusteriTuru> getMusteriAll();

    @Insert
    long setMusteriTuru(MusteriTuru musteriTuru);

    @Insert
    List<Long> setMusteriTuruList(List<MusteriTuru> musteriTuru);

    @Update
    int updateMusteriTuruList(List<MusteriTuru> musteriTuru);

    @Update
    int updateMusteriTuru(MusteriTuru musteriTuru);

    @Query("DELETE FROM MUSTERI_TURU")
    void deleteMusteriTuruAll();

    @Query("DELETE FROM MUSTERI_TURU where mid = :mid")
    int deletedMusteriTuruForMid(Long mid);

    @Query("SELECT * FROM MUSTERI_TURU where mid = :mid")
    List<MusteriTuru> getMusteriTuruForMid(Long mid);

    @Query("SELECT * FROM MUSTERI_TURU where id = :id")
    List<MusteriTuru> getMusteriTuruForId(Long id);

}
