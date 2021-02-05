package com.example.haliyikamaapp.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.haliyikamaapp.Model.Entity.AuthToken;

import java.util.List;

@Dao
public interface AuthTokenDao {
    @Query("SELECT * FROM AUTH_TOKEN")
    List<AuthToken> getAuthTokenAll();

    @Insert
    long setAuthToken(AuthToken authToken);



    @Query("DELETE FROM AUTH_TOKEN")
    void deleteTokenAll();



}


