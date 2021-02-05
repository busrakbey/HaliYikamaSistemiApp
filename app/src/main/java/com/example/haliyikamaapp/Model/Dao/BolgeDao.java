package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Bolge;

import java.util.List;


@Dao
public interface BolgeDao {

    @Query("SELECT * FROM BOLGE")
    List<Bolge> getBolgeAll();

    @Insert
    long setBolge(Bolge bolge);

    @Insert
    List<Long> setBolgeList(List<Bolge> bolge);

    @Update
    int updateBolgeList(List<Bolge> bolge);

    @Update
    int updateBolge(Bolge Bolge);

    @Query("DELETE FROM BOLGE")
    void deleteBolgeAll();

    @Query("DELETE FROM BOLGE where mid = :mid")
    int deletedBolgeForMid(Long mid);

    @Query("SELECT * FROM BOLGE where mid = :mid")
    List<Bolge> getBolgeForMid(Long mid);

    @Query("SELECT * FROM BOLGE where id = :id")
    List<Bolge> getBolgeForId(Long id);

}
