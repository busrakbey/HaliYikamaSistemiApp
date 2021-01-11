package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Sube;

import java.util.List;

@Dao
public interface SubeDao {

    @Query("SELECT * FROM SUBE")
    List<Sube> getSiparisAll();

    @Insert
    long setSube(Sube sube);

    @Insert
    List<Long> setSubeList(List<Sube> sube);

    @Update
    int updateSubeList(List<Sube> sube);

    @Update
    int updateSube(Sube sube);

    @Query("DELETE FROM SUBE")
    void deleteSubeAll();

    @Query("DELETE FROM SUBE where mid = :mid")
    int deletedSubeForMid(Long mid);

    @Query("SELECT * FROM SUBE where mid = :mid")
    List<Sube> getSubeForMid(Long mid);


}
