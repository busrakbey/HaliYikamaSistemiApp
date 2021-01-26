package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.S_ILCE;

import java.util.List;

@Dao
public interface SIlceDao {
    @Query("SELECT * FROM S_ILCE")
    List<S_ILCE> getIlceAll();

    @Insert
    long setIlce(S_ILCE il);

    @Update
    int updateIlce(S_ILCE il);

    @Insert
    List<Long> setIlceList(List<S_ILCE> il);

    @Update
    int updateIlceList(List<S_ILCE> il);

    @Query("DELETE FROM S_ILCE")
    void deleteIlceAll();

    @Query("UPDATE S_ILCE SET id = :id WHERE mid = :mid")
   int updateIlceQuery(Long mid, Long id);
    
    @Query("DELETE FROM S_ILCE where mid = :mid")
    int deletedIlceForMid(Long mid);

    @Query("SELECT * FROM S_ILCE where mid = :mid")
    List<S_ILCE> getIlceForMid(Long mid);
    

    @Query("SELECT * FROM S_ILCE where id = :id")
    List<S_ILCE> getIlceForIlceId(Long id);

}