package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM S_USER")
    List<User> getUserAll();

    @Query("SELECT * FROM S_USER where senkronEdildi = 0")
    List<User> getSenkronEdilmeyenUserAll();

    @Insert
    long setUser(User user);

    @Insert
    List<Long> setUserList(List<User> user);

    @Update
    int updateUserList(List<User> user);

    @Update
    int updateUser(User user);

    @Query("DELETE FROM S_USER")
    void deleteUserAll();

    @Query("DELETE FROM S_USER where mid = :mid")
    int deletedUserForMid(Long mid);

    @Query("SELECT * FROM S_USER where mid = :mid")
    List<User> getUserForMid(Long mid);

    @Query("UPDATE S_USER SET id = :id , senkronEdildi = :senkronEdildiMi  WHERE mid = :mid")
    int updateUserQuery(Long mid, Long id, Boolean senkronEdildiMi);

    @Query("SELECT * FROM S_USER where id = :id")
    List<User> getUserForId(Long id);


}
