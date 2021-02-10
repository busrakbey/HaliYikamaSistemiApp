package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Gorevler;

import java.util.List;

@Dao
public interface GorevlerDao {
    @Query("SELECT * FROM GOREVLER")
    List<Gorevler> getGorevAll();

    @Insert
    void setGorev(Gorevler musteri);

    @Update
    void updateGorev(Gorevler musteri);

    @Insert
    List<Long> setGorevList(List<Gorevler> urun);

    @Update
    int updateGorevList(List<Gorevler> urun);

    @Query("DELETE FROM GOREVLER")
    void deleteGorevAll();

    @Query("DELETE FROM GOREVLER where mid = :mid")
    void deletedGorevForMid(Long mid);

    @Query("SELECT * FROM GOREVLER where mid = :mid")
    List<Gorevler> getGorevForMid(Long mid);

    @Query("SELECT * FROM GOREVLER where siparisId = :siparisId")
    List<Gorevler> getGorevForSiparisId(Long siparisId);

}
