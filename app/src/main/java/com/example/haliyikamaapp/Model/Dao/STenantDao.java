package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.haliyikamaapp.Model.Entity.STenant;

import java.util.List;

@Dao
public interface STenantDao {
   @Query("SELECT * FROM S_TENANT")
    List<STenant> getSTenantAll();

    @Insert
    void setSTenant(STenant musteri);

    @Query("DELETE FROM S_TENANT")
    void deleteSTenantAll();

    @Query("DELETE FROM S_TENANT where mid = :mid")
    void deletedSTenantForMid(Long mid);

    @Query("SELECT * FROM S_TENANT where mid = :mid")
    List<STenant> getSTenantForMid(Long mid);

}
