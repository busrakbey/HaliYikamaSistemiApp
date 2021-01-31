package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
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

    List<S_IL> iller;
    List<S_ILCE> ilceler, all_ilce;
    List<String> ilStringList;
    List<String> ilceStringList;
    int selected_il_index = 0, selected_ilce_index = 0;
    Long secili_il_id, secili_ilce_id;


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

        get_list();

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

        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        urunKayitButton = (Button) findViewById(R.id.urun_kayit_button);
        urunGuncelleButton = (Button) findViewById(R.id.urun_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(UrunTanimlamaActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);


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
                                MessageBox.showAlert(UrunTanimlamaActivity.this, "Kayıt Başarılı..\n", false);
                                get_list();
                                urunAdapter.notifyDataSetChanged();
                                // Intent i = new Intent(urunTanimlamaActivity.this, SiparisKayitActivity.class);
                            }
                            if (gelenurunMid != null && finalSiparisMid == 1) {
                                MessageBox.showAlert(UrunTanimlamaActivity.this, "İşlem Başarılı..\n", false);
                                get_list();
                                urunAdapter.notifyDataSetChanged();


                            } else if (finalSiparisMid < 0)
                                MessageBox.showAlert(UrunTanimlamaActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    Urun gelenUrun;

    public void postUrunService(final Urun urun) throws Exception {
        progressDoalog.show();
        final Long urunMid = urun.getMid();
        final Long urunId = urun.getId();
        urun.setMid(null);
        urun.setMustId(null);
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
                        db.urunDao().updateUrunQuery(urunMid, gelenUrun.getId());
                        db.urunSubeDao().updateUrunSubeQueryForUrunMid(urunMid, gelenUrun.getId());
                        try {
                            if (gelenUrun.getId() != null) {
                                for (UrunSube item : db.urunSubeDao().getUrunSubeForUrunId(gelenUrun.getId()))
                                    postUrunSubeService(item);
                            } else {
                                for (UrunSube item : db.urunSubeDao().getUrunSubeForUrunMid(urunMid))
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


                    } else
                        MessageBox.showAlert(UrunTanimlamaActivity.this, "Kayıt bulunamamıştır..", false);
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
                        db.urunSubeDao().updateUrunSubeQuery(urunSubeMid, gelenUrunSube.getId());
                        db.urunFiyatDao().updateUrunFiyatQueryForUrunMid(urunSubeMid, gelenUrunSube.getId());

                        try {
                            if (gelenUrunSube.getId() != null) {
                                for (UrunFiyat item : db.urunFiyatDao().getForUrunSubeId(gelenUrunSube.getId()))
                                    postUrunFiyatService(item);
                            } else {
                                for (UrunFiyat item : db.urunFiyatDao().getForUrunSubeMid(urunSubeMid))
                                    postUrunFiyatService(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }




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


    UrunFiyat gelenUrunFiyat;
    public void postUrunFiyatService(final UrunFiyat item) throws Exception {
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
       // refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();

        final Long urunFiyatMid = item.getMid();
        item.setMid(null);
        item.setMustId(null);
        item.setUrunSubeMid(null);
        // item.setId(null);
        String jsonStr = gson.toJson(item);
        Call<UrunFiyat> call = refrofitRestApi.postUruneFiyatEkle(OrtakFunction.authorization, OrtakFunction.tenantId, item);
        call.enqueue(new Callback<UrunFiyat>() {
            @Override
            public void onResponse(Call<UrunFiyat> call, Response<UrunFiyat> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(UrunTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunFiyat = response.body();
                    if (gelenUrunSube != null) {
                        db.urunFiyatDao().updateUrunFiyatQuery(urunFiyatMid, gelenUrunFiyat.getId());
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
            public void onFailure(Call<UrunFiyat> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(UrunTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }


}


