package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Adapter.UrunAdapter;
import com.example.haliyikamaapp.Adapter.UruneSubeAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class UrunTanimlamaKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText urunAdi, aciklama;
    CheckBox aktifMi;
    Spinner il_spinner, ilce_spinner;
    RecyclerView urun_listview;
    UruneSubeAdapter urunSubeAdapter;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    Button urunKayitButton, urunGuncelleButton, urune_sube_ekle_button;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    String gelenUrunId, gelenUrunMid;
    LinearLayout sube_detay_linear;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.urun_tanimlama_kayit_activity);
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
        db = HaliYikamaDatabase.getInstance(UrunTanimlamaKayitActivity.this);
        urun_listview = (RecyclerView) findViewById(R.id.urun_listview);
        urunAdi = (EditText) findViewById(R.id.urun_adi);
        aktifMi = (CheckBox) findViewById(R.id.urun_aktifmi);
        aciklama = (EditText) findViewById(R.id.urun_aciklama);
        urune_sube_ekle_button = (Button) findViewById(R.id.urune_sube_ekle_button);
        sube_detay_linear = (LinearLayout) findViewById(R.id.sube_detay_linear);

        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        urunKayitButton = (Button) findViewById(R.id.urun_kayit_button);
        urunGuncelleButton = (Button) findViewById(R.id.urun_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(UrunTanimlamaKayitActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        gelenUrunId = getIntent().getStringExtra("urunId");
        gelenUrunMid = getIntent().getStringExtra("urunMid");

        urune_sube_ekle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gelenUrunMid == null)
                    MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Lütfen öncelikle ürün tanımlayınız.\n", false);
                else {
                    Intent i = new Intent(UrunTanimlamaKayitActivity.this, UruneSubeTanimlamaActivity.class);
                    i.putExtra("urunId", gelenUrunId);
                    i.putExtra("urunMid", gelenUrunMid);
                    startActivity(i);
                }
            }
        });

        gelenUrunId = getIntent().getStringExtra("urunId");
        gelenUrunMid = getIntent().getStringExtra("urunMid");

        if (gelenUrunMid != null) {
            getEditMode(Long.valueOf(gelenUrunMid));
            urunKayitButton.setVisibility(GONE);
        } else {
            urunGuncelleButton.setVisibility(GONE);
            sube_detay_linear.setVisibility(GONE);
            urune_sube_ekle_button.setVisibility(GONE);
        }


    }

    public void get_list() {
        List<UrunSube> urunler;
        if (gelenUrunId != null && !gelenUrunId.equalsIgnoreCase("null"))
            urunler = db.urunSubeDao().getUrunSubeForUrunId(Long.valueOf(gelenUrunId));
        else
            urunler = db.urunSubeDao().getUrunSubeForUrunMid(Long.valueOf(gelenUrunMid));
        urunSubeAdapter = new UruneSubeAdapter(UrunTanimlamaKayitActivity.this, urunler);
        urun_listview.setHasFixedSize(true);
        urun_listview.setLayoutManager(new LinearLayoutManager(UrunTanimlamaKayitActivity.this));
        urun_listview.setAdapter(urunSubeAdapter);
        urunSubeAdapter.notifyDataSetChanged();
       /* SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(UrunTanimlamaKayitActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.urunDao().deletedUrunForMid(urunAdapter.getData().get(position).getMid());
                UrunTanimlamaKayitActivity.this.runOnUiThread(new Runnable() {
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
                            MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(urun_listview);*/
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


    public long finalSiparisMid = 0L;

    void siparis_kayit(final Long gelenurunMid) {
        if (urunAdi.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Lütfen bilgileri eksiksiz bir şekilde giriniz.", false);

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

                    finalSiparisMid = musteriMid;
                    UrunTanimlamaKayitActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenurunMid == null && Integer.valueOf(String.valueOf(finalSiparisMid)) > 0) {
                                MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Kayıt başarılı.\n", false);

                               /* get_list();
                                urunSubeAdapter.notifyDataSetChanged();*/
                                if (OrtakFunction.internetKontrol(getApplicationContext()))
                                    postUrunService(urun);
                                else {
                                    Intent i = new Intent(UrunTanimlamaKayitActivity.this, UrunTanimlamaKayitActivity.class);
                                    i.putExtra("urunMid", String.valueOf(finalSiparisMid));
                                    startActivity(i);
                                }

                            }
                            if (gelenurunMid != null && finalSiparisMid == 1) {
                                MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Güncelleme başarılı.\n", false);
                                get_list();
                                urunSubeAdapter.notifyDataSetChanged();
                                if (OrtakFunction.internetKontrol(getApplicationContext()))
                                    putUrunService(urun);

                            } else if (finalSiparisMid < 0)
                                MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "İşlem başarısız.\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    void senkronEdilmeyenKayitlariGonder() {


      /*  for (Urun item : db.urunDao().getUrunAll()) {
            if (item.getId() == null)
                postUrunService(item);
            else
                putUrunService(item);

        }*/


        if (gelenUrunId != null && !gelenUrunId.equalsIgnoreCase("null"))
            getUruneGoreSubeService(Long.valueOf(gelenUrunId));
    }

    Urun gelenUrun;
    public void postUrunService(final Urun urun) {
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
                    MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrun = response.body();
                    if (gelenUrun != null) {
                        db.urunDao().updateUrunQuery(urunMid, gelenUrun.getId(), true);
                        db.urunSubeDao().updateUrunSubeQueryForUrunMid(urunMid, gelenUrun.getId());

                        if (finalSiparisMid != 0L) {
                            Intent i = new Intent(UrunTanimlamaKayitActivity.this, UrunTanimlamaKayitActivity.class);
                            i.putExtra("urunMid", String.valueOf(finalSiparisMid));
                            i.putExtra("urunId" ,  gelenUrun.getId().toString());
                            startActivity(i);
                        }
                       /* try {
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

                        }*/
                    }
                }
            }

            @Override
            public void onFailure(Call<Urun> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
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
                    MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
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
                        UrunTanimlamaKayitActivity.this.runOnUiThread(new Runnable() {
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
                MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
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
                    MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunSube = response.body();
                    if (gelenUrunSube != null) {
                        db.urunSubeDao().updateUrunSubeQuery(urunSubeMid, gelenUrunSube.getId(), true);


                        UrunTanimlamaKayitActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });


                    } else
                        MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<UrunSube> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(UrunTanimlamaKayitActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }


    List<UrunSube> gelenUrunSubeList = null;

    public void getUruneGoreSubeService(Long urunId) {
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        Call<List<UrunSube>> call = refrofitRestApi.getUrunSube("hy/urun/urunSubeler/" + urunId, OrtakFunction.authorization, OrtakFunction.tenantId);
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
                        get_list();


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


    @Override
    public void onResume() {
        super.onResume();
      /*  if (OrtakFunction.internetKontrol(getApplicationContext()))
            senkronEdilmeyenKayitlariGonder();
        else*/
      if(gelenUrunMid != null)
            get_list();

    }


}


