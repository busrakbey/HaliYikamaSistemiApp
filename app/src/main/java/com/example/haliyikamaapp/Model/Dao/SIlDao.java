package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.S_IL;

import java.util.List;

@Dao
public interface SIlDao {
    @Query("SELECT * FROM S_IL")
    List<S_IL> getIlAll();

    @Insert
    long setIl(S_IL il);

    @Update
    int updateIl(S_IL il);

    @Insert
    List<Long> setIlList(List<S_IL> il);

    @Update
    int updateIlList(List<S_IL> il);

    @Query("DELETE FROM S_IL")
    void deleteIlAll();

    @Query("UPDATE S_IL SET id = :id WHERE mid = :mid")
   int updateIlQuery(Long mid, Long id);
    
    @Query("DELETE FROM S_IL where mid = :mid")
    int deletedIlForMid(Long mid);

    @Query("SELECT * FROM S_IL where mid = :mid")
    List<S_IL> getIlForMid(Long mid);
    

    @Query("SELECT * FROM S_IL where id = :id")
    List<S_IL> getIlForIlId(Long id);

}