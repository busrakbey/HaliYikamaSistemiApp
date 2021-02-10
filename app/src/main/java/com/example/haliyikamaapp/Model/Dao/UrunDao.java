package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Urun;

import java.util.List;

@Dao
public interface UrunDao {
    @Query("SELECT * FROM URUN")
    List<Urun> getUrunAll();

    @Query("SELECT * FROM URUN where senkronEdildi = 0")
    List<Urun> getSenkronEdilmeyenUrunAll();

    @Insert
    long setUrun(Urun musteri);


    @Update
    int updateUrun(Urun urun);

    @Insert
    List<Long> setUrunList(List<Urun> urun);

    @Update
    int updateUrunList(List<Urun> urun);

    @Query("DELETE FROM URUN")
    void deleteUrunAll();

    @Query("DELETE FROM URUN where mid = :mid")
    int deletedUrunForMid(Long mid);

    @Query("SELECT * FROM URUN where mid = :mid")
    List<Urun> getUrunForMid(Long mid);

    @Query("SELECT * FROM URUN where id = :id")
    List<Urun> getUrunForId(Long id);

    @Query("SELECT * FROM URUN where id in (:id)")
    List<Urun> getUrunForIdList(List<Long> id);

    @Query("UPDATE URUN SET id = :id , senkronEdildi = :senkronEdildiMi WHERE mid = :mid")
    int updateUrunQuery(Long mid, Long id, Boolean senkronEdildiMi);

}
