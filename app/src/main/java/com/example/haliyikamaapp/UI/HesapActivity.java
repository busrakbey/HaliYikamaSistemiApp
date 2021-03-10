package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.HesapAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Hesap;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HesapActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    RecyclerView recyclerView;
    HesapAdapter hesapAdapter;
    RefrofitRestApi refrofitRestApi;
    ProgressDialog progressDoalog;
    Button harcama_girisi_button, para_girisi_button;
    BottomNavigationView bottomNavigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.hesap_activity);
        init_item();
        initToolBar();
        getHesapListFromService();
        senkronEdilmeyenKayitlariGonder();
        get_list();


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Hesaplar");
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
        db = HaliYikamaDatabase.getInstance(HesapActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.hesap_listview);

        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(HesapActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        harcama_girisi_button = (Button) findViewById(R.id.harcama_girisi_button);
        para_girisi_button = (Button) findViewById(R.id.para_girisi_button);


        harcama_girisi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HesapActivity.this, HesapKayitActivity.class);
                intent.putExtra("islemTuru" , "Çıkış");
                startActivity(intent);
            }
        });


        para_girisi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HesapActivity.this, HesapKayitActivity.class);
                intent.putExtra("islemTuru" , "Giriş" );

                startActivity(intent);

            }
        });



    }


    public void get_list() {
        final List<Hesap> hesapList = db.hesapDao().getHesapAll();
        hesapAdapter = new HesapAdapter(HesapActivity.this, hesapList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HesapActivity.this));
        recyclerView.setAdapter(hesapAdapter);
        hesapAdapter.notifyDataSetChanged();

    }


    String gelenHesapList;
    void getHesapListFromService() {

        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
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

        Call<String> call = refrofitRestApi.getHesapList(OrtakFunction.authorization, OrtakFunction.tenantId, "application/json", object.toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(HesapActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenHesapList = response.body();


                    JSONObject gelenObject = null;
                    try {
                        gelenObject = new JSONObject(gelenHesapList);
                        JSONArray rowArray = new JSONArray(gelenObject.getString("rows"));
                        Boolean yeniKayitMi = true;

                       // db.hesapDao().deleteHesapAll();
                        for (int i = 0; i < rowArray.length(); i++) {
                            JSONObject rowObject = new JSONObject(rowArray.getString(i));
                            List<Hesap> allHesapList = Arrays.asList(gson.fromJson(rowObject.toString(), Hesap.class));


                            if (db.hesapDao().getHesapAll().size() == 0) {
                                db.hesapDao().setHesap(allHesapList.get(0));

                            } else {
                                for (Hesap all : db.hesapDao().getHesapAll()) {
                                    for (Hesap item : allHesapList) {
                                        if (all.getId() != null && all.getId().toString().equalsIgnoreCase(rowObject.getString("id"))) {
                                            yeniKayitMi = false;
                                            item.setSubeMid(all.getSubeMid());
                                            item.setMid(all.getMid());
                                            db.hesapDao().updateHesap(item);
                                        }
                                    }

                                }

                                if (yeniKayitMi)
                                    db.hesapDao().setHesap(allHesapList.get(0));

                            }
                        }

                        get_list();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
              //  MessageBox.showAlert(HesapActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
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
                    MessageBox.showAlert(HesapActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenHesap = response.body();
                    if (gelenHesap != null) {

                        db.hesapDao().updateHesapQuery(hesapMid, gelenHesap.getId(), true);
                        HesapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             /*   if (gelenHesapList.size() != kayitList.size())
                                    MessageBox.showAlert(HesapKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    get_list();*/

                            }
                        });


                    } else
                        MessageBox.showAlert(HesapActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<Hesap> call, Throwable t) {
                progressDoalog.dismiss();
               // MessageBox.showAlert(HesapActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    Fragment selectedFragment = null;
                    Intent i = null;
                    switch (item.getItemId()) {
                     /*   case R.id.nav_home:
                            i = new Intent(HesapActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musteri:
                            i = new Intent(HesapActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                      /*  case R.id.nav_siparis:
                            i = new Intent(HesapActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musterigorevlerim:
                            i = new Intent(HesapActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri_görevlerim");
                            startActivity(i);
                            break;


                        case R.id.nav_ozet:
                            i = new Intent(HesapActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "ozet");
                            startActivity(i);
                            break;



                    }
                  /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/
                    return true;
                }
            };

}