package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Sms;

import java.util.List;

@Dao
public interface SmsDao {
    @Query("SELECT * FROM SMS")
    List<Sms> getSmsAll();

    @Insert
    long setSms(Sms musteri);

    @Insert
    List<Long> setSmsList(List<Sms> subeList);

    @Update
    int updateSms(Sms sube);

    @Query("DELETE FROM SMS")
    void deleteSmsAll();

    @Query("DELETE FROM SMS where mid = :mid")
    void deletedSmsForMid(Long mid);

    @Query("SELECT * FROM SMS where mid = :mid")
    List<Sms> getSmsForMid(Long mid);

    @Query("SELECT * FROM SMS where id = :id")
    List<Sms> getSmsForId(Long id);

    @Query("SELECT * FROM SMS where siparisDurumu = :siparisDurumu")
    List<Sms> getSmsForSiparisDurumu(String siparisDurumu);





}




