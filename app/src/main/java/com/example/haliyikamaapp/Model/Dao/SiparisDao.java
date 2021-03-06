package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Siparis;

import java.util.List;

@Dao
public interface SiparisDao {
    @Query("SELECT * FROM SIPARIS order by siparisTarihi desc")
    List<Siparis> getSiparisAll();

    @Query("SELECT s.* FROM SIPARIS s, MUSTERI m where s.musteriId = m.id and s.siparisDurumu = 'Teslim Edildi' and s.kaynakId = :kaynakId and" +
            " (m.musteriAdi LIKE :parameter || '%' or m.bolge LIKE  :parameter || '%' or m.telefonNumarasi LIKE '%' ||" + ":parameter || '%') ")
    List<Siparis> getSiparisTelsimEdildiList(Long kaynakId, String parameter);

    @Insert
    long setSiparis(Siparis siparis);

    @Update
    int updateSiparis(Siparis siparis);

    @Insert
    List<Long> setSiparisList(List<Siparis> siparis);

    @Update
    int updateSiparisList(List<Siparis> siparis);

    @Query("DELETE FROM SIPARIS")
    void deleteSiparisAll();

    @Query("UPDATE SIPARIS SET id = :id, senkronEdildi = :senkronEdildi WHERE mid = :mid")
   int updateSiparisQuery(Long mid, Long id, Boolean senkronEdildi);

    @Query("UPDATE SIPARIS SET processInstanceId = :processInstanceId WHERE id = :id")
    int updateSiparisProcessId(Long processInstanceId, Long id);

    @Query("DELETE FROM SIPARIS where mid = :mid")
    int deletedSiparisForMid(Long mid );

    @Query("SELECT * FROM SIPARIS where mid = :mid")
    List<Siparis> getSiparisForMid(Long mid);

    @Query("SELECT * FROM SIPARIS where musteriMid = :musteriMid order by siparisTarihi desc")
    List<Siparis> getSiparisForMusteriMid(Long musteriMid);

    @Query("SELECT * FROM SIPARIS where id = :id order by siparisTarihi desc")
    List<Siparis> getSiparisForSiparisId(Long id);

    @Query("UPDATE SIPARIS SET barkod = :barkod WHERE mid = :mid")
    int updateSiparisBarkod(String barkod, Long mid);

    @Query("SELECT * FROM SIPARIS where senkronEdildi = 0")
    List<Siparis> getSenkronEdilmeyenAll();

    @Query("SELECT * FROM SIPARIS where musteriId = :musteriId order by siparisTarihi desc")
    List<Siparis> getSiparisForMusterIid(Long musteriId);

    @Query("UPDATE SIPARIS SET siparisDurumu = :siparisDurumu WHERE id = :id")
    int updateSiparisDurumu(Long id, String siparisDurumu);

    @Query("UPDATE SIPARIS SET musteriId = :musteriId WHERE mid = :mid")
    int updateSiparisMusteriId(Long mid, Long musteriId);

    @Query("UPDATE SIPARIS SET  senkronEdildi = :senkronEdildi WHERE mid = :mid")
    int updateSiparisSenkronDurum(Long mid ,Boolean senkronEdildi);



}