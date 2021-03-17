package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Gorevler;

import java.util.List;

@Dao
public interface GorevlerDao {
    @Query("SELECT * FROM GOREVLER order by taskId desc")
    List<Gorevler> getGorevAll();

    @Query("SELECT * FROM GOREVLER where kaynakId = :kaynakId order by taskCreateTime desc")
    List<Gorevler> getGorevAllForKaynakId(Long kaynakId);

    @Query("SELECT * FROM GOREVLER where siparisDurumu in (:siparisDurumu)")
    List<Gorevler> getGorevForSiparisDurumu(List<String> siparisDurumu);

    @Query("SELECT g.* FROM GOREVLER g JOIN MUSTERI m ON g.musteriId = m.id  where  kaynakId = :kaynakId and " +
            "(m.musteriAdi LIKE :parameter || '%' or m.bolge LIKE  :parameter || '%' or m.telefonNumarasi LIKE '%' ||" +
            " :parameter || '%')  and (siparisDurumu in (:siparisDurumu)) order by taskCreateTime desc")
    List<Gorevler> getquery(String parameter,List<String> siparisDurumu, Long kaynakId);

    @Query("SELECT g.* FROM GOREVLER g JOIN MUSTERI m ON g.musteriId = m.id  where  kaynakId = :kaynakId and " +
            "(m.musteriAdi LIKE :parameter || '%' or m.bolge LIKE  :parameter || '%' or m.telefonNumarasi LIKE '%' ||" +
            " :parameter || '%')  and (siparisTarihi > :siparisTarihi) and (siparisDurumu in (:siparisDurumu)) order by taskCreateTime desc")
    List<Gorevler> getQueryIleriTarihTeslimAlmaTarihi(String parameter,List<String> siparisDurumu, String siparisTarihi,Long kaynakId);


    @Query("SELECT g.* FROM GOREVLER g JOIN MUSTERI m ON g.musteriId = m.id  where  kaynakId = :kaynakId and " +
            "(m.musteriAdi LIKE :parameter || '%' or m.bolge LIKE  :parameter || '%' or m.telefonNumarasi LIKE '%' ||" +
            " :parameter || '%') and ((siparisTarihi <= :siparisTarihi) ) and (siparisDurumu in (:siparisDurumu))  order by taskCreateTime desc ")
    List<Gorevler> getGorevQueryPrameterTeslimAlmaTarihi(String parameter,List<String> siparisDurumu, String siparisTarihi, Long kaynakId);


    @Query("SELECT q1.* from (SELECT * FROM GOREVLER g JOIN MUSTERI m ON g.musteriId = m.id   where  g.kaynakId = :kaynakId and" +
            " (m.musteriAdi LIKE :parameter || '%' or m.bolge LIKE  :parameter || '%' or m.telefonNumarasi LIKE '%' || :parameter || '%')  and " +
            "(siparisDurumu in (:siparisDurumu)) UNION all " +
            "SELECT *  FROM GOREVLER g JOIN MUSTERI m ON g.musteriId = m.id    where    (g.taskName ='Yikama' or g.taskName = 'Araca Yükle') " +
            "and g.kaynakId != :kaynakId and" +
            "(m.musteriAdi LIKE :parameter || '%' or m.bolge LIKE  :parameter || '%' or m.telefonNumarasi LIKE '%' || :parameter || '%')  and " +
            "(siparisDurumu in (:siparisDurumu)) ) q1 order by q1.taskCreateTime desc")
    List<Gorevler> getYikamaAllAndKaynakId (String parameter,List<String> siparisDurumu,Long kaynakId);

    @Query("SELECT q1.* from (SELECT * FROM GOREVLER g    where  g.kaynakId = :kaynakId  UNION all " +
            "SELECT *  FROM GOREVLER g    where    (g.taskName ='Yikama' or g.taskName = 'Araca Yükle') and g.kaynakId != :kaynakId) q1 order by q1.taskCreateTime desc")
    List<Gorevler> getYikamaAllForKaynakId (Long kaynakId);


    @Query("SELECT g.* FROM GOREVLER g JOIN MUSTERI m ON g.musteriId = m.id  where  " +
            "(m.musteriAdi LIKE :parameter || '%' or m.bolge LIKE  :parameter || '%' or m.telefonNumarasi LIKE '%' ||" +
            " :parameter || '%')  and (siparisDurumu in (:siparisDurumu)) order by taskCreateTime desc")
    List<Gorevler> getGorevAllForQuery(String parameter,List<String> siparisDurumu);


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

    @Query("SELECT * FROM GOREVLER where taskId = :taskId")
    List<Gorevler> getGorevForId(Long taskId);

    @Query("SELECT * FROM GOREVLER where siparisId = :siparisId")
    List<Gorevler> getGorevForSiparisId(Long siparisId);

    @Query("DELETE FROM GOREVLER where siparisId = :siparisId")
    int deletedGorevForSiparisId(Long siparisId);

    @Query("DELETE FROM GOREVLER where taskId = :taskId")
    int deletedGorevForTaskId(Long taskId);

}
