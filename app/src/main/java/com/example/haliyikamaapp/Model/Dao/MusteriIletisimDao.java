package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;

import java.util.List;

@Dao
public interface MusteriIletisimDao {
    @Query("SELECT * FROM MUSTERI_ILETISIM")
    List<MusteriIletisim> getMusteriIletisimAll();

    @Insert
    long setMusteriIletisim(MusteriIletisim musteri);

    @Update
    int updateMusteriIletisim(MusteriIletisim musteri);

    @Query("DELETE FROM MUSTERI_ILETISIM")
    void deleteMusteriIletisimAll();

    @Query("DELETE FROM MUSTERI_ILETISIM where mid = :mid")
    void deletedMusteriIletisimForMid(Long mid);

    @Query("SELECT * FROM MUSTERI_ILETISIM where mid = :mid")
    List<MusteriIletisim> getMusteriIletisimForMid(Long mid);

}



