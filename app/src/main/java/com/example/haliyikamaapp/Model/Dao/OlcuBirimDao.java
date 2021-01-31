package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.OlcuBirim;

import java.util.List;


@Dao
public interface OlcuBirimDao {
    @Query("SELECT * FROM OLCU_BIRIM")
    List<OlcuBirim> getOlcuBirimAll();

    @Insert
    void setOlcuBirim(OlcuBirim musteri);

    @Update
    int updateOlcuBirim(OlcuBirim musteri);

    @Query("DELETE FROM OLCU_BIRIM")
    void deleteOlcuBirimAll();

    @Query("DELETE FROM OLCU_BIRIM where mid = :mid")
    void deletedOlcuBirimForMid(Long mid);

    @Query("SELECT * FROM OLCU_BIRIM where mid = :mid")
    List<OlcuBirim> getOlcuBirimForMid(Long mid);

    @Query("SELECT * FROM OLCU_BIRIM where id = :id")
    List<OlcuBirim> getOlcuBirimForId(Long id);

}



