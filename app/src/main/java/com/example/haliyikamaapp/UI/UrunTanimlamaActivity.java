package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.UrunAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.MusteriTuru;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UrunTanimlamaActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText urunAdi, aciklama;
    CheckBox aktifMi;
    Spinner il_spinner, ilce_spinner;
    RecyclerView urun_listview;
    UrunAdapter urunAdapter;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    Button urunKayitButton, urunGuncelleButton;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    FloatingActionButton yeniUrunButton;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.urun_tanimlama_activity);
        init_item();
        initToolBar();

        // senkronEdilmeyenKayitlariGonder();

    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Ürün Tanımlama");
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
        db = HaliYikamaDatabase.getInstance(UrunTanimlamaActivity.this);
        urun_listview = (RecyclerView) findViewById(R.id.urun_listview);
        urunAdi = (EditText) findViewById(R.id.urun_adi);
        aktifMi = (CheckBox) findViewById(R.id.urun_aktifmi);
        aciklama = (EditText) findViewById(R.id.urun_aciklama);
        yeniUrunButton = (FloatingActionButton) findViewById(R.id.btnAdd);


        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        urunKayitButton = (Button) findViewById(R.id.urun_kayit_button);
        urunGuncelleButton = (Button) findViewById(R.id.urun_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(UrunTanimlamaActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        yeniUrunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UrunTanimlamaActivity.this, UrunTanimlamaKayitActivity.class);
                startActivity(i);
            }
        });


    }

    public void get_list() {
        final List<Urun> urunler = db.urunDao().getUrunAll();
        urunAdapter = new UrunAdapter(UrunTanimlamaActivity.this, urunler);
        urun_listview.setHasFixedSize(true);
        urun_listview.setLayoutManager(new LinearLayoutManager(UrunTanimlamaActivity.this));
        urun_listview.setAdapter(urunAdapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(UrunTanimlamaActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.urunDao().deletedUrunForMid(urunAdapter.getData().get(position).getMid());
                UrunTanimlamaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (silinenMusteriDetayMid == 1) {
                            urunAdapter.getData().remove(position);
                            urunAdapter.notifyDataSetChanged();
                            snackbar = Snackbar
                                    .make(constraintLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();


                        } else
                            MessageBox.showAlert(UrunTanimlamaActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(urun_listview);
    }

    public void urunKayitOnClick(View view) {
        siparis_kayit(null);
    }

    public void urunGuncelleOnClick(View view) {
        if (urunMid_ != null)
            siparis_kayit(urunMid_);
    }

    Long urunMid_ = null;

    public void getEditMode(Long urunMid) {
        urunMid_ = urunMid;
        List<Urun> updateKayitList = db.urunDao().getUrunForMid(urunMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid().equals(urunMid)) {
            urunAdi.setText(updateKayitList.get(0).getUrunAdi());
            if (updateKayitList.get(0).getAktif() != null && updateKayitList.get(0).getAktif() == true)
                aktifMi.setChecked(true);
            aciklama.setText(updateKayitList.get(0).getAciklama());
        }
    }


    void siparis_kayit(final Long gelenurunMid) {
        if (urunAdi.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(UrunTanimlamaActivity.this, "Lütfen bilgileri eksiksiz bir şekilde giriniz.", false);

        } else {
            final Urun urun = new Urun();
            urun.setAciklama(aciklama.getText().toString());
            urun.setUrunAdi(urunAdi.getText().toString());
            urun.setAktif(aktifMi.isChecked() ? true : false);
            urun.setSenkronEdildi(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long musteriMid = 0;

                    if (gelenurunMid == null)
                        musteriMid = db.urunDao().setUrun(urun);
                    if (gelenurunMid != null) {
                        urun.setMid(Long.valueOf(gelenurunMid));
                        musteriMid = db.urunDao().updateUrun(urun);
                    }

                    final long finalSiparisMid = musteriMid;
                    UrunTanimlamaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenurunMid == null && Integer.valueOf(String.valueOf(finalSiparisMid)) > 0) {
                                MessageBox.showAlert(UrunTanimlamaActivity.this, "Kayıt başarılı.\n", false);
                                get_list();
                                urunAdapter.notifyDataSetChanged();
                                if(OrtakFunction.internetKontrol(getApplicationContext()))
                                    postUrunService(urun);

                            }
                            if (gelenurunMid != null && finalSiparisMid == 1) {
                                MessageBox.showAlert(UrunTanimlamaActivity.this, "Güncelleme başarılı.\n", false);
                                get_list();
                                urunAdapter.notifyDataSetChanged();
                                if(OrtakFunction.internetKontrol(getApplicationContext()))
                                    putUrunService(urun);

                            } else if (finalSiparisMid < 0)
                                MessageBox.showAlert(UrunTanimlamaActivity.this, "İşlem başarısız.\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    void senkronEdilmeyenKayitlariGonder() {


            for (Urun item : db.urunDao().getUrunAll()) {
                if (item.getId() == null)
                    postUrunService(item);
                else
                    putUrunService(item);

            }


        if (db.urunDao().getSenkronEdilmeyenAll().size() == 0)
            getUrunListFromService();

    }

    Urun gelenUrun;

    public void postUrunService(final Urun urun)  {
        progressDoalog.show();
        final Long urunMid = urun.getMid();
        final Long urunId = urun.getId();
        urun.setMid(null);
        urun.setMustId(null);
        urun.setSenkronEdildi(null);

        // urun.setId(null);
        Call<Urun> call = refrofitRestApi.postUrun(OrtakFunction.authorization, OrtakFunction.tenantId, urun);
        call.enqueue(new Callback<Urun>() {
            @Override
            public void onResponse(Call<Urun> call, Response<Urun> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(UrunTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrun = response.body();
                    if (gelenUrun != null) {
                        db.urunDao().updateUrunQuery(urunMid, gelenUrun.getId(), true);
                        db.urunSubeDao().updateUrunSubeQueryForUrunMid(urunMid, gelenUrun.getId());
                        try {
                            if (gelenUrun.getId() != null) {
                                for (UrunSube item : db.urunSubeDao().getUrunSubeForUrunId(gelenUrun.getId()))
                                    if (item.getSenkronEdildi() == false)
                                        postUrunSubeService(item);
                            } else {
                                for (UrunSube item : db.urunSubeDao().getUrunSubeForUrunMid(urunMid))
                                    if (item.getSenkronEdildi() == false)
                                        postUrunSubeService(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Urun> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(UrunTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    public void putUrunService(final Urun urun) {
        progressDoalog.show();
        final Long urunMid = urun.getMid();
        final Long urunId = urun.getId();
        urun.setMid(null);
        urun.setMustId(null);
        urun.setSenkronEdildi(null);

        // urun.setId(null);
        Call<Urun> call = refrofitRestApi.putUrun("hy/urun/" + urun.getId(), OrtakFunction.authorization, OrtakFunction.tenantId, urun);
        call.enqueue(new Callback<Urun>() {
            @Override
            public void onResponse(Call<Urun> call, Response<Urun> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(UrunTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrun = response.body();
                    if (gelenUrun != null) {
                        db.urunDao().updateUrunQuery(urunMid, gelenUrun.getId(), true);
                        db.urunSubeDao().updateUrunSubeQueryForUrunMid(urunMid, gelenUrun.getId());
                        try {
                            if (gelenUrun.getId() != null) {
                                for (UrunSube item : db.urunSubeDao().getUrunSubeForUrunId(gelenUrun.getId()))
                                    if (item.getSenkronEdildi() == false)
                                        postUrunSubeService(item);
                            } else {
                                for (UrunSube item : db.urunSubeDao().getUrunSubeForUrunMid(urunMid))
                                    if (item.getSenkronEdildi() == false)
                                        postUrunSubeService(item);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        UrunTanimlamaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Urun> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(UrunTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    UrunSube gelenUrunSube;
    public void postUrunSubeService(final UrunSube item) throws Exception {
        progressDoalog.show();
        final Long urunSubeMid = item.getMid();
        item.setMid(null);
        item.setMustId(null);
        item.setUrunMid(null);
        item.setSubeAdi(null);
        item.setSenkronEdildi(null);

        //item.setId(null);
        Call<UrunSube> call = refrofitRestApi.postUruneSubeEkle(OrtakFunction.authorization, OrtakFunction.tenantId, item);
        call.enqueue(new Callback<UrunSube>() {
            @Override
            public void onResponse(Call<UrunSube> call, Response<UrunSube> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(UrunTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunSube = response.body();
                    if (gelenUrunSube != null) {
                        db.urunSubeDao().updateUrunSubeQuery(urunSubeMid, gelenUrunSube.getId(), true);


                        UrunTanimlamaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });


                    } else
                        MessageBox.showAlert(UrunTanimlamaActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<UrunSube> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(UrunTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }



    List<Urun> gelenUrunList = null;
    List<Sube> gelenSubeList = null;
    List<String> gelenMusteriTuruList = null;

    void getUrunListFromService() {
        progressDoalog.show();
        /////////ürün listesi alınıyor /////
        Call<List<Urun>> call = refrofitRestApi.getUrunList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Urun>>() {
            @Override
            public void onResponse(Call<List<Urun>> call, Response<List<Urun>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    //  MessageBox.showAlert(MainActivity2.this, "Ürün bilgileri alınırken hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunList = response.body();
                    if (gelenUrunList != null && gelenUrunList.size() > 0) {
                        for (Urun item : gelenUrunList) {
                            List<Urun> urunVarMi = db.urunDao().getUrunForId(item.getId());
                            if (urunVarMi.size() > 0) {
                                item.setSenkronEdildi(true);
                                item.setMid(urunVarMi.get(0).getMid());
                                db.urunDao().updateUrun(item);
                            } else
                                db.urunDao().setUrun(item);
                        }


                        //////////////////// ürüne ait şubeler geliyor...
                        getUruneGoreSubeService(db.urunDao().getUrunAll());

                    }

                }
                get_list();
            }

            @Override
            public void onFailure(Call<List<Urun>> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(MainActivity2.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    List<UrunSube> gelenUrunSubeList = null;
    public void getUruneGoreSubeService(List<Urun> urunList) {
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        for (Urun urun : urunList) {
            if (urun.getId() != null) {
                progressDoalog.show();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                final Gson gson = gsonBuilder.create();
                Call<List<UrunSube>> call = refrofitRestApi.getUrunSube("hy/urun/urunSubeler/" + urun.getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                call.enqueue(new Callback<List<UrunSube>>() {
                    @Override
                    public void onResponse(Call<List<UrunSube>> call, Response<List<UrunSube>> response) {
                        if (!response.isSuccessful()) {
                            progressDoalog.dismiss();
                            //  MessageBox.showAlert(MainActivity2.this, "Servisle bağlantı sırasında hata oluştu...", false);
                            return;
                        }
                        if (response.isSuccessful()) {
                            gelenUrunSubeList = response.body();
                            if (gelenUrunSubeList != null) {
                                // final List<Long> kayitList = db.urunSubeDao().setUrunSubeList(gelenUrunSubeList)
                                for (UrunSube item : gelenUrunSubeList) {
                                    List<UrunSube> urunSubeVarMi = db.urunSubeDao().getUrunSubeForId(item.getId());
                                    if (urunSubeVarMi.size() > 0) {
                                        item.setSenkronEdildi(true);
                                        item.setMustId(urunSubeVarMi.get(0).getMustId());
                                        item.setSubeMid(urunSubeVarMi.get(0).getSubeMid());
                                        item.setUrunMid(urunSubeVarMi.get(0).getUrunMid());
                                        item.setMid(urunSubeVarMi.get(0).getMid());
                                        db.urunSubeDao().updateUrunSube(item);
                                    } else
                                        db.urunSubeDao().setUrunSube(item);
                                }
                                progressDoalog.dismiss();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UrunSube>> call, Throwable t) {
                        progressDoalog.dismiss();
                        // MessageBox.showAlert(MainActivity2.this, "Hata Oluştu.. " + t.getMessage(), false);
                    }
                });
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
       /* if (OrtakFunction.internetKontrol(getApplicationContext()))
            senkronEdilmeyenKayitlariGonder();
        else*/
            get_list();

    }


}


