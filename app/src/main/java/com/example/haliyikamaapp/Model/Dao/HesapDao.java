package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Hesap;

import java.util.List;

@Dao
public interface HesapDao {

    @Query("SELECT * FROM HESAP order by tarih desc")
    List<Hesap> getHesapAll();

    @Insert
    long setHesap(Hesap hesap);

    @Update
    int updateHesap(Hesap hesap);

    @Insert
    List<Long> setHesapList(List<Hesap> hesap);

    @Update
    int updateHesapList(List<Hesap> hesap);

    @Query("DELETE FROM HESAP")
    void deleteHesapAll();

    @Query("DELETE FROM HESAP where mid = :mid")
    int deletedHesapForMid(Long mid);

    @Query("SELECT * FROM HESAP where mid = :mid")
    List<Hesap> getHesapForMid(Long mid);


    @Query("SELECT * FROM HESAP where id = :id")
    List<Hesap> getHesapForHesapId(Long id);

    @Query("SELECT detayNeden FROM HESAP order by detayNeden asc")
    List<Hesap> getHesapDetayNedenAll();

    List<Hesap> getSenkronEdilmeyenAll();




}