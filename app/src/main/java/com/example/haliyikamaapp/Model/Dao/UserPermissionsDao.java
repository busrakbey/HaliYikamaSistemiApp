package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.UserPermissions;

import java.util.List;

@Dao
public interface UserPermissionsDao {

    @Query("SELECT * FROM USER_PERMISSIONS")
    List<UserPermissions> getUserAll();

    @Insert
    long setUser(UserPermissions user);

    @Insert
    List<Long> setUserList(List<UserPermissions> user);

    @Update
    int updateUserList(List<UserPermissions> user);

    @Update
    int updateUser(UserPermissions user);

    @Query("DELETE FROM USER_PERMISSIONS")
    void deleteUserAll();

    @Query("DELETE FROM USER_PERMISSIONS where mid = :mid")
    int deletedUserForMid(Long mid);

    @Query("SELECT * FROM USER_PERMISSIONS where mid = :mid")
    List<UserPermissions> getUserForMid(Long mid);

    @Query("SELECT * FROM USER_PERMISSIONS where id = :id")
    List<UserPermissions> getUserForId(Long id);

    @Query("SELECT * FROM USER_PERMISSIONS where userId = :userId")
    List<UserPermissions> getUserId(Long userId);


}
