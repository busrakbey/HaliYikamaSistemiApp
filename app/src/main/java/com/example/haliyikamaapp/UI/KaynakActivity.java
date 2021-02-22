package com.example.haliyikamaapp.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.HesapAdapter;
import com.example.haliyikamaapp.Adapter.KaynakAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Hesap;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KaynakActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    RecyclerView recyclerView;
    KaynakAdapter kaynakAdapter;
    RefrofitRestApi refrofitRestApi;
    ProgressDialog progressDoalog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.kaynak_activity);
        init_item();
        initToolBar();
        getKaynakListFromService();
        //senkronEdilmeyenKayitlariGonder();
        get_list();


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Kaynaklar");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void init_item() {
        db = HaliYikamaDatabase.getInstance(KaynakActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.kaynaklar_recyclerview);

        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(KaynakActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);



    }


    public void get_list() {
        final List<Kaynak> kaynakList = db.kaynakDao().getkaynakAll();
        kaynakAdapter = new KaynakAdapter(KaynakActivity.this, kaynakList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(KaynakActivity.this));
        recyclerView.setAdapter(kaynakAdapter);
        kaynakAdapter.notifyDataSetChanged();

    }


    List<Kaynak> gelenKaynakList;

    void getKaynakListFromService() {

        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        JSONObject object = new JSONObject();
        try {
            object.put("pageSize", 99999);
            object.put("pageNumber", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<List<Kaynak>> call = refrofitRestApi.getKaynakList(OrtakFunction.authorization, OrtakFunction.tenantId, "application/json");
        call.enqueue(new Callback<List<Kaynak>>() {
            @Override
            public void onResponse(Call<List<Kaynak>> call, Response<List<Kaynak>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(KaynakActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    db.kaynakDao().deletekaynakAll();
                    gelenKaynakList = response.body();

                    db.kaynakDao().setkaynakList(gelenKaynakList);

                }
            }


            @Override
            public void onFailure(Call<List<Kaynak>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(KaynakActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }

    void senkronEdilmeyenKayitlariGonder() {
        for (Hesap item : db.hesapDao().getSenkronEdilmeyenAll()) {
            try {
                postHesapListFromService(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    Hesap gelenHesap;

    public void postHesapListFromService(final Hesap hesap) {
        progressDoalog.show();
        Call<Hesap> call;
        final Long hesapMid = hesap.getMid();
        hesap.setMid(null);
        hesap.setSubeAdi(null);
        hesap.setSubeMid(null);
        hesap.setMustId(null);
        hesap.setSubeMid(null);
        hesap.setSenkronEdildi(null);
        hesap.setKaynakAdi(null);
        call = refrofitRestApi.postHesap(OrtakFunction.authorization, OrtakFunction.tenantId, hesap);

        call.enqueue(new Callback<Hesap>() {
            @Override
            public void onResponse(Call<Hesap> call, Response<Hesap> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(KaynakActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenHesap = response.body();
                    if (gelenHesap != null) {

                        db.hesapDao().updateHesapQuery(hesapMid, gelenHesap.getId(), true);
                        KaynakActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             /*   if (gelenHesapList.size() != kayitList.size())
                                    MessageBox.showAlert(HesapKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    get_list();*/

                            }
                        });


                    } else
                        MessageBox.showAlert(KaynakActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<Hesap> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(KaynakActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }

}