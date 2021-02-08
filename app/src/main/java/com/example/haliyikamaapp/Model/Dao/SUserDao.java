package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.S_User;

import java.util.List;

@Dao
public interface SUserDao {

    @Query("SELECT * FROM S_USER")
    List<S_User> getUserAll();

    @Query("SELECT * FROM S_USER where senkronEdildi = 0")
    List<S_User> getSenkronEdilmeyenUserAll();

    @Insert
    long setUser(S_User user);

    @Insert
    List<Long> setUserList(List<S_User> user);

    @Update
    int updateUserList(List<S_User> user);

    @Update
    int updateUser(S_User user);

    @Query("DELETE FROM S_USER")
    void deleteUserAll();

    @Query("DELETE FROM S_USER where mid = :mid")
    int deletedUserForMid(Long mid);

    @Query("SELECT * FROM S_USER where mid = :mid")
    List<S_User> getUserForMid(Long mid);

    @Query("UPDATE S_USER SET id = :id , senkronEdildi = :senkronEdildiMi  WHERE mid = :mid")
    int updateUserQuery(Long mid, Long id, Boolean senkronEdildiMi);

    @Query("SELECT * FROM S_USER where id = :id")
    List<S_User> getUserForId(Long id);


}
