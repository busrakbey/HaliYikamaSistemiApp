package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Kaynak;

import java.util.List;

@Dao
public interface KaynakDao {

    @Query("SELECT * FROM KAYNAK ")
    List<Kaynak> getkaynakAll();

    @Query("SELECT * FROM KAYNAK where kaynakTuru = 'Araç' ")
    List<Kaynak> getkaynakAllForArac();

    @Query("SELECT * FROM KAYNAK where senkronEdildi = 0")
    List<Kaynak> getSenkronEdilmeyenAll();


    @Insert
    long setkaynak(Kaynak kaynak);

    @Update
    int updatekaynak(Kaynak kaynak);

    @Insert
    List<Long> setkaynakList(List<Kaynak> kaynak);

    @Update
    int updatekaynakList(List<Kaynak> kaynak);

    @Query("DELETE FROM KAYNAK")
    void deletekaynakAll();

    @Query("DELETE FROM KAYNAK where mid = :mid")
    int deletedkaynakForMid(Long mid);

    @Query("DELETE FROM KAYNAK where id = :id")
    int deletedkaynakForId(Long id);

    @Query("SELECT * FROM KAYNAK where mid = :mid")
    List<Kaynak> getkaynakForMid(Long mid);


    @Query("SELECT * FROM KAYNAK where id = :id")
    List<Kaynak> getkaynakForkaynakId(Long id);


    @Query("UPDATE KAYNAK SET id = :id , senkronEdildi = :senkronEdildiMi  WHERE mid = :mid")
    int updatekaynakQuery(Long mid, Long id, Boolean senkronEdildiMi);


    @Query("UPDATE KAYNAK set secilenKaynakMi = 0")
    int updateSecilenKaynakMiAll();

    @Query("UPDATE KAYNAK set secilenKaynakMi = :durum where mid = :mid")
    int updateSecilenKaynakMi(Long mid, Boolean durum);




}