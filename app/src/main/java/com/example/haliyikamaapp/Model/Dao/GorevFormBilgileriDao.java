package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.GorevFomBilgileri;

import java.util.List;

@Dao
public interface GorevFormBilgileriDao {
    @Query("SELECT * FROM GOREV_FORM_BILGILERI")
    List<GorevFomBilgileri> getGorevAll();

    @Insert
    void setGorev(GorevFomBilgileri musteri);

    @Insert
    List<Long> setGorevList(List<GorevFomBilgileri> urun);

    @Update
    int updateGorevList(List<GorevFomBilgileri> urun);

    @Query("DELETE FROM GOREV_FORM_BILGILERI")
    void deleteGorevAll();

    @Query("DELETE FROM GOREV_FORM_BILGILERI where mid = :mid")
    void deletedGorevForMid(Long mid);

    @Query("DELETE FROM GOREV_FORM_BILGILERI where gorevId = :gorevId")
    void deletedGorevForGorevId(Long gorevId);

    @Query("SELECT * FROM GOREV_FORM_BILGILERI where mid = :mid")
    List<GorevFomBilgileri> getGorevForMid(Long mid);

    @Query("SELECT * FROM GOREV_FORM_BILGILERI where gorevId = :gorevId")
    List<GorevFomBilgileri> getGorevId(Long gorevId);

}
