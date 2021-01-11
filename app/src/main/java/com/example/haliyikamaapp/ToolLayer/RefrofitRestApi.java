package com.example.haliyikamaapp.ToolLayer;

import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunSube;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RefrofitRestApi {

    @GET("hy/musteri")
    Call<List<Musteri>> getMusteriList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @POST("hy/musteri")
    Call<List<Musteri>> postMusteriList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body HashMap<String,String> bodyList);

    @GET("hy/urun")
    Call<List<Urun>> getUrunList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @GET("hy/sube")
    Call<List<Sube>> getSubeList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @GET("hy/siparis")
    Call<List<Siparis>> getSiparisList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @GET() // "hy/siparis/siparisUrunler"
    Call<List<SiparisDetay>> getSiparisDetayList (@Url String url, @Header("Authorization") String auth, @Header("tenant-id") String tenantId);
}


