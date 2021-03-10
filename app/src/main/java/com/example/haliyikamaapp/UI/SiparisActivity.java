package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.SiparisAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.example.haliyikamaapp.ToolLayer.SwipeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SiparisActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    RecyclerView recyclerView;
    HaliYikamaDatabase db;
    SiparisAdapter siparisAdapter;
    Snackbar snackbar;
    Toolbar toolbar;

    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    String gelenMusteriId, gelenMusteriMid, gelenSiparisId, allSiparis;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.siparis_activity);
        initToolBar();
        init_item();
        get_list();
        swipe_item();
        //ilIlceSpinnerList();






    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Sipariş");
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
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        db = HaliYikamaDatabase.getInstance(SiparisActivity.this);
        recyclerView = (RecyclerView)findViewById(R.id.siparis_recyclerview);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(SiparisActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_musteri);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        gelenMusteriId = getIntent().getStringExtra("musteriId");
        gelenMusteriMid = getIntent().getStringExtra("musteriMid");
        gelenSiparisId = getIntent().getStringExtra("siparisId");

    }

    void senkronEdilmeyenKayitlariGonder() {
        for (Siparis item : db.siparisDao().getSenkronEdilmeyenAll()) {
            try {
                if (item.getMusteriId() == null) {
                    Musteri musteri = db.musteriDao().getMusteriForMid(item.getMusteriMid()).get(0);
                    db.siparisDao().updateSiparisMusteriId(item.getMid(), musteri.getId());
                    item.setMusteriId(musteri.getId());
                }
                item.setMustId(null);
                item.setSenkronEdildi(null);
                //item.setSubeMid(null);
                item.setMusteriMid(null);
                // item.setId(null);
                postSiparisListFromService(item, item.getMid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getSiparisListFromService();


    }


    void get_list() {
        List<Siparis> siparisler = null;
        if (gelenMusteriId != null)
            siparisler = db.siparisDao().getSiparisForMusterIid(Long.valueOf(gelenMusteriId));
        else if (gelenMusteriMid != null)
            siparisler = db.siparisDao().getSiparisForMusteriMid(Long.valueOf(gelenMusteriMid));
        else if (gelenSiparisId != null) {
            siparisler = db.siparisDao().getSiparisForSiparisId(Long.valueOf(gelenSiparisId));
        } else
            siparisler = db.siparisDao().getSiparisAll();
        siparisAdapter = new SiparisAdapter(SiparisActivity.this, siparisler,false);
        siparisAdapter.notifyDataSetChanged();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SiparisActivity.this));
        recyclerView.setAdapter(siparisAdapter);
        siparisAdapter.notifyDataSetChanged();

        gelenSiparisId = null;
        gelenMusteriId = null;
        gelenMusteriMid = null;


    }

    /*@Override
    public void onResume() {
        super.onResume();
        get_list();

    }*/

    @Override
    public void onPause() {
        super.onPause();


    }


    List<Siparis> gelenSiparisList;
    List<SiparisDetay> gelenSiparisDetayList2;
    List<SiparisDetay> updateSiparisDetayList2;

    void getSiparisListFromService() {

        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        Call<List<Siparis>> call = refrofitRestApi.getSiparisList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Siparis>>() {
            @Override
            public void onResponse(Call<List<Siparis>> call, Response<List<Siparis>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(SiparisActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisList = response.body();
                    if (gelenSiparisList != null && gelenSiparisList.size() > 0) {


                        SiparisActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (final Siparis item : gelenSiparisList) {
                                    List<Siparis> urunVarMi = db.siparisDao().getSiparisForSiparisId(item.getId());
                                    if (urunVarMi.size() > 0) {
                                        item.setMid(urunVarMi.get(0).getMid());
                                        item.setMusteriMid(urunVarMi.get(0).getMusteriMid());
                                        item.setSubeMid(urunVarMi.get(0).getSubeMid());
                                        item.setKaynakMid(urunVarMi.get(0).getKaynakMid());
                                        db.siparisDao().updateSiparis(item);
                                    } else
                                        db.siparisDao().setSiparis(item);


                                    Call<List<SiparisDetay>> call = refrofitRestApi.getSiparisDetayList("hy/siparis/siparisUrunler/" + item.getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                                    call.enqueue(new Callback<List<SiparisDetay>>() {
                                        @Override
                                        public void onResponse(Call<List<SiparisDetay>> call, Response<List<SiparisDetay>> response) {
                                            if (!response.isSuccessful()) {
                                                progressDoalog.dismiss();
                                                //  MessageBox.showAlert(SiparisActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                                                return;
                                            }
                                            if (response.isSuccessful()) {
                                                progressDoalog.dismiss();
                                                gelenSiparisDetayList2 = response.body();

                                                SiparisActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        for (SiparisDetay i : gelenSiparisDetayList2) {
                                                            List<Siparis> siparis = db.siparisDao().getSiparisForSiparisId(item.getId());
                                                            List<SiparisDetay> urunVarMi = db.siparisDetayDao().getSiparisDetayForId(i.getId());
                                                            i.setMustId(siparis.get(0).getMid());
                                                            i.setSiparisMid(siparis.get(0).getMid());
                                                            if (urunVarMi.size() > 0) {
                                                                i.setMid(urunVarMi.get(0).getMid());
                                                                i.setUrunMid(urunVarMi.get(0).getUrunMid());
                                                                i.setOlcuBirimMid(urunVarMi.get(0).getOlcuBirimMid());
                                                                db.siparisDetayDao().updateSiparisDetay(i);
                                                            } else
                                                                db.siparisDetayDao().setSiparisDetay(i);
                                                        }



                                                    }
                                                });


                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<SiparisDetay>> call, Throwable t) {
                                            progressDoalog.dismiss();
                                            MessageBox.showAlert(SiparisActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                        }
                                    });


                                }


                            }
                        });


                    } /*else
                        MessageBox.showAlert(SiparisActivity.this, "Kayıt bulunamamıştır..", false);*/
                }
                get_list();
            }

            @Override
            public void onFailure(Call<List<Siparis>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(SiparisActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }



    Siparis gelenSiparis;
    public void postSiparisListFromService(final Siparis siparis, final Long siparisMid) {
        progressDoalog.show();
        siparis.setMid(null);
        siparis.setBarkod(null);
        siparis.setSubeMid(null);
        siparis.setSiparisTarihi(siparis.getSiparisTarihi() + " 00:00");
        Call<Siparis> call = refrofitRestApi.postSiparis(OrtakFunction.authorization, OrtakFunction.tenantId, siparis);
        call.enqueue(new Callback<Siparis>() {
            @Override
            public void onResponse(Call<Siparis> call, Response<Siparis> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(SiparisActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparis = response.body();
                    if (gelenSiparis != null) {

                        db.siparisDao().updateSiparisQuery(siparisMid, gelenSiparis.getId(), true);
                        SiparisActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // db.siparisDao().getSiparisAll()
                                List<SiparisDetay> siparisdetayList = db.siparisDetayDao().getSiparisDetayForMustId(siparisMid);

                                List<SiparisDetay> siparisdetayList2 = db.siparisDetayDao().getSiparisDetayForSiparisId(siparis.getId());

                                if (siparisdetayList != null && siparisdetayList.size() > 0) {
                                    List<Siparis> gidecekSiparis = db.siparisDao().getSiparisForSiparisId(gelenSiparis.getId());
                                    postSiparisDetayListFromService(siparisdetayList, gidecekSiparis);
                                    for (SiparisDetay item : siparisdetayList) {
                                        db.siparisDetayDao().updateSiparisId(siparisMid, siparis.getId());
                                    }
                                }

                                /*if (siparisdetayList != null && siparisdetayList.size() > 0) {
                                    List<Siparis> gidecekSiparis = db.siparisDao().getSiparisForMid(gelenSiparis.getMid());
                                    postSiparisDetayListFromService(siparisdetayList, gidecekSiparis);
                                }*/

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Siparis> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(SiparisActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


    List<SiparisDetay> gelenSiparisDetayLists;
    List<SiparisDetay> updateSiparisDetayList;
    String gelenSiparisDetayList = null;


    public void postSiparisDetayListFromService(List<SiparisDetay> siparisDetayList, final List<Siparis> gelenSiparis) {
        progressDoalog.show();
        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        final List<Long> midList = new ArrayList<>();
        JsonArray datas = new JsonArray();
        for (SiparisDetay item : siparisDetayList) {
            JsonObject object = new JsonObject();
            object.addProperty("id", item.getId());
            object.addProperty("siparisId", item.getSiparisId());
            object.addProperty("urunId", item.getUrunId());
            object.addProperty("olcuBirimId", item.getOlcuBirimId());
            object.addProperty("birimFiyat", item.getBirimFiyat());
            object.addProperty("miktar", item.getMiktar());
            midList.add(item.getMid());
            //  object.addProperty("musteriNotu", "");
            datas.add(object);
        }

        progressDoalog.show();
        Call<String> call = refrofitRestApi.postSiparisDetay(OrtakFunction.authorization, OrtakFunction.tenantId, datas.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(SiparisActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisDetayList = response.body();
                    if (gelenSiparisDetayList != null) {


                        SiparisActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                Call<List<SiparisDetay>> call = refrofitRestApi.getSiparisDetayList("hy/siparis/siparisUrunler/" + gelenSiparis.get(0).getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                                call.enqueue(new Callback<List<SiparisDetay>>() {
                                    @Override
                                    public void onResponse(Call<List<SiparisDetay>> call, Response<List<SiparisDetay>> response) {
                                        if (!response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            //MessageBox.showAlert(SiparisActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                                            return;
                                        }
                                        if (response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            gelenSiparisDetayLists = response.body();
                                            if (gelenSiparisDetayLists != null && gelenSiparisDetayLists.size() > 0) {
                                                updateSiparisDetayList = new ArrayList<SiparisDetay>();


                                                for (SiparisDetay item : gelenSiparisDetayLists) {
                                                    List<SiparisDetay> urunVarMi = db.siparisDetayDao().getSiparisDetayForId(item.getId());
                                                    final List<Siparis> updateMustId = db.siparisDao().getSiparisForSiparisId(item.getSiparisId());

                                                    if (updateMustId != null && updateMustId.size() > 0) {
                                                        item.setSiparisMid(updateMustId.get(0).getMid());
                                                        item.setMustId(updateMustId.get(0).getMid());
                                                    }
                                                    item.setSenkronEdildi(true);

                                                    if (urunVarMi.size() > 0) {
                                                        item.setMid(urunVarMi.get(0).getMid());
                                                        item.setSiparisMid(urunVarMi.get(0).getSiparisMid());
                                                        item.setUrunMid(urunVarMi.get(0).getUrunMid());
                                                        item.setOlcuBirimMid(urunVarMi.get(0).getOlcuBirimMid());
                                                        db.siparisDetayDao().updateSiparisDetay(item);
                                                    } else
                                                        db.siparisDetayDao().setSiparisDetay(item);
                                                }
                                                SiparisActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {


                                                    }
                                                });


                                            } else
                                                MessageBox.showAlert(SiparisActivity.this, "Kayıt bulunamamıştır..", false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<SiparisDetay>> call, Throwable t) {
                                        progressDoalog.dismiss();
                                       // MessageBox.showAlert(SiparisActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                    }
                                });


                            }
                        });


                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
             //   MessageBox.showAlert(SiparisActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }

    void swipe_item(){
        SwipeHelper swipeHelper = new SwipeHelper(SiparisActivity.this, recyclerView, false) {
            @Override
            public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Düzenle",null,

                       /* AppCompatResources.getDrawable(
                                SiparisActivity.this,
                                android.R.drawable.ic_menu_edit
                        ),*/
                        Color.parseColor("#FF9800"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                Intent musteri = new Intent(SiparisActivity.this, SiparisKayitActivity.class);
                                musteri.putExtra("siparisMid", String.valueOf(siparisAdapter.getData().get(position).getMid()));
                                musteri.putExtra("siparisId", String.valueOf(siparisAdapter.getData().get(position).getId()));
                                musteri.putExtra("musteriMid", String.valueOf(siparisAdapter.getData().get(position).getMusteriMid()));
                                musteri.putExtra("siparisId", String.valueOf(siparisAdapter.getData().get(position).getId()));
                                musteri.putExtra("musteriId", String.valueOf(siparisAdapter.getData().get(position).getMusteriId()));

                                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                SiparisActivity.this.getApplicationContext().startActivity(musteri);
                            }
                        }
                ));






            }
        };


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
                            i = new Intent(SiparisActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musteri:
                            i = new Intent(SiparisActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                        /*case R.id.nav_siparis:
                            i = new Intent(SiparisActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musterigorevlerim:
                            i = new Intent(SiparisActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri_görevlerim");
                            startActivity(i);
                            break;
                    }
                  /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/
                    return true;
                }
            };


}