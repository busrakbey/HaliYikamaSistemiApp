package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.Permissions;

import java.util.List;

@Dao
public interface PermissionsDao {

    @Query("SELECT * FROM PERMISSIONS")
    List<Permissions> getPermissionsAll();

    @Query("SELECT * FROM PERMISSIONS where senkronEdildi = 0")
    List<Permissions> getSenkronEdilmeyenPermissionsAll();

    @Insert
    long setPermissions(Permissions permissions);

    @Insert
    List<Long> setPermissionsList(List<Permissions> permissions);

    @Update
    int updatePermissionsList(List<Permissions> permissions);

    @Update
    int updatePermissions(Permissions permissions);

    @Query("DELETE FROM PERMISSIONS")
    void deletePermissionsAll();

    @Query("DELETE FROM PERMISSIONS where mid = :mid")
    int deletedPermissionsForMid(Long mid);

    @Query("SELECT * FROM PERMISSIONS where mid = :mid")
    List<Permissions> getPermissionsForMid(Long mid);

    @Query("UPDATE PERMISSIONS SET id = :id , senkronEdildi = :senkronEdildiMi  WHERE mid = :mid")
    int updatePermissionsQuery(Long mid, Long id, Boolean senkronEdildiMi);

    @Query("SELECT * FROM PERMISSIONS where id = :id")
    List<Permissions> getPermissionsForId(Long id);


}
