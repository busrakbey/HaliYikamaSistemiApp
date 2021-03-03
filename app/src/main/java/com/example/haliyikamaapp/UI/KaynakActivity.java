package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
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
    BottomNavigationView bottomNavigationView;
    FloatingActionButton yeni_kaynak_button;


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
        senkronEdilmeyenKayitlariGonder();
        getKaynakListFromService();
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
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //bottomNav.setSelectedItemId(R.id.nav_siparis);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        yeni_kaynak_button = (FloatingActionButton) findViewById(R.id.btnAdd);


        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(KaynakActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        yeni_kaynak_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(KaynakActivity.this, KaynakKayitActivity.class);
                startActivity(i);
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
                       /* case R.id.nav_home:
                            i = new Intent(KaynakActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musteri:
                            i = new Intent(KaynakActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                        /*case R.id.nav_siparis:
                            i = new Intent(KaynakActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musterigorevlerim:
                            i = new Intent(KaynakActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri_görevlerim");
                            startActivity(i);
                            break;
                    }
                  /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/
                    return true;
                }
            };


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
                    // MessageBox.showAlert(KaynakActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    Boolean yeniKayitMi = true;
                    progressDoalog.dismiss();
                    //db.kaynakDao().deletekaynakAll();
                    gelenKaynakList = response.body();

                   // db.kaynakDao().setkaynakList(gelenKaynakList);
                    for (Kaynak i : gelenKaynakList) {

                        i.setSenkronEdildi(true);

                        if (db.kaynakDao().getkaynakAll().size() == 0) {
                            db.kaynakDao().setkaynak(i);

                        } else {
                            for (Kaynak all : db.kaynakDao().getkaynakAll()) {

                                if (all.getId() != null && all.getId().toString().equalsIgnoreCase(i.getId().toString())) {
                                    yeniKayitMi = false;
                                    i.setMid(all.getMid());
                                    i.setSecilenKaynakMi(all.getSecilenKaynakMi());
                                    db.kaynakDao().updatekaynak(i);
                                }
                            }

                            if (yeniKayitMi)
                                db.kaynakDao().setkaynak(i);

                        }
                    }

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
        for (Kaynak item : db.kaynakDao().getSenkronEdilmeyenAll()) {
            try {
                postKaynakListFromService(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    Kaynak gelenKaynak;

    public void postKaynakListFromService(final Kaynak kaynak) {
        progressDoalog.show();
        Call<Kaynak> call;
        final Long kaynakMid = kaynak.getMid();
        kaynak.setMid(null);
        kaynak.setMustId(null);
        kaynak.setSenkronEdildi(null);
        kaynak.setSecilenKaynakMi(null);

        call = refrofitRestApi.postKaynak(OrtakFunction.authorization, OrtakFunction.tenantId, "application/json", kaynak);

        call.enqueue(new Callback<Kaynak>() {
            @Override
            public void onResponse(Call<Kaynak> call, Response<Kaynak> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(KaynakActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenKaynak = response.body();
                    if (gelenKaynak != null) {

                        db.kaynakDao().updatekaynakQuery(kaynakMid, gelenKaynak.getId(), true);
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
            public void onFailure(Call<Kaynak> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(KaynakActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }


        });
    }


    @Override
    public void onResume() {
        super.onResume();
        get_list();
    }


}

