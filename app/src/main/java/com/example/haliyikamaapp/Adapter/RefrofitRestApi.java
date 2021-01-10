package com.example.haliyikamaapp.Adapter;

import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.Model.Entity.Musteri;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RefrofitRestApi {

    @Headers({"Accept: application/json" , "Content-Type: application/x-www-form-urlencoded", "tenant-id: test"})
    @POST("haliBackend/oauth/token")
    Call<List<AuthToken>> getToken (@Header("Authorization") String auth, @Body Map<String, String> data);

}


