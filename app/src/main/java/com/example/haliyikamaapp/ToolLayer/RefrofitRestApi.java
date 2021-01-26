package com.example.haliyikamaapp.ToolLayer;

import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.MusteriTuru;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface RefrofitRestApi {

    @GET("hy/musteri")
    Call<List<Musteri>> getMusteriList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @POST("hy/musteri")
    Call<Musteri> postMusteriList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body Musteri musteri);

    @GET("hy/urun")
    Call<List<Urun>> getUrunList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @GET("hy/sube")
    Call<List<Sube>> getSubeList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @GET("hy/siparis")
    Call<List<Siparis>> getSiparisList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @GET() // "hy/siparis/siparisUrunler"
    Call<List<SiparisDetay>> getSiparisDetayList (@Url String url, @Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @GET("hy/musteri/musteriTurleri")
    Call<List<String>> getMusteriTuruList (@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @POST("hy/siparis")
    Call<Siparis> postSiparis (@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body Siparis siparis);

    @POST("hy/siparis/siparisDetaylariKaydet")
    Call<String>  postSiparisDetay (@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body String siparisDetay);

    @POST("hy/siparis/startProcess")
    Call<String>  startSiparisSureci (@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body String siparisBilgileri);

    @GET()  ////"hy/urun/subeyeGoreUrunAra/1/___"
    Call<String>  getSubeyeGoreUrunFiyatListesi (@Url String url,@Header("Authorization") String auth, @Header("tenant-id") String tenantId);

    @POST()  ////hy/process/userTasks/999"
    Call<String>  getKullaniciGorevList (@Url String url,@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body String gorevPageBilgileri);

    @GET()  ////fw/process/taskFormData/15008"
    Call<String>  getGorevTamamlamakIcinGerekliFormList (@Url String url,@Header("Authorization") String auth, @Header("tenant-id") String tenantId,
                                                         @Header("Content-Type") String contentType, @Header("Accept") String accept  );

    @POST()  ////fw/process/completeTask/10013
    Call<String>  postGorevTamamla (@Url String url,@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body String gorevBilgileri);

    @POST("fw/select")
    Call<String>  getSelectService (@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body String selectSorgu);

    @POST("hy/sube")
    Call<Sube> postSube (@Header("Authorization") String auth, @Header("tenant-id") String tenantId, @Body String sube);
}


