package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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

import com.example.haliyikamaapp.Adapter.SmsAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Adapter.UrunAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Sms;
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

public class SmsTanimlamaActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText smsBaslik, smsAciklama;
    Spinner il_spinner, ilce_spinner;
    RecyclerView sms_listview;
    SmsAdapter smsAdapter;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    Button urunKayitButton, urunGuncelleButton;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    Spinner siparisDurumuspinner;
    ArrayList<String> spinnerArray;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.sms_tanimlama_activity);
        init_item();
        initToolBar();
        get_list();
        senkronEdilmeyenKayitlariGonder();

    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Sms Tanımlama");
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
        db = HaliYikamaDatabase.getInstance(SmsTanimlamaActivity.this);
        sms_listview = (RecyclerView) findViewById(R.id.sms_listview);
        smsAciklama = (EditText) findViewById(R.id.sms_aciklama);
        smsBaslik = (EditText) findViewById(R.id.sms_baslik);
        siparisDurumuspinner = (Spinner) findViewById(R.id.siparis_durumu) ;

        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        urunKayitButton = (Button) findViewById(R.id.sms_kayit_button);
        urunGuncelleButton = (Button) findViewById(R.id.sms_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(SmsTanimlamaActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        spinnerArray = new ArrayList<String>();
        spinnerArray.add("Sipariş Durumu Seçiniz");
        spinnerArray.add("Teslim Alınacak");
        spinnerArray.add("Teslim Edildi");
        spinnerArray.add("Teslime Hazır");
        spinnerArray.add("Yıkamada");
        spinnerArray.add("Yıkanacak");

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);
        siparisDurumuspinner.setAdapter(spinnerArrayAdapter);


    }

    public void get_list() {
        final List<Sms> urunler = db.smsDao().getSmsAll();
        smsAdapter = new SmsAdapter(SmsTanimlamaActivity.this, urunler);
        sms_listview.setHasFixedSize(true);
        sms_listview.setLayoutManager(new LinearLayoutManager(SmsTanimlamaActivity.this));
        sms_listview.setAdapter(smsAdapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(SmsTanimlamaActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.urunDao().deletedUrunForMid(smsAdapter.getData().get(position).getMid());
                SmsTanimlamaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (silinenMusteriDetayMid == 1) {
                            smsAdapter.getData().remove(position);
                            smsAdapter.notifyDataSetChanged();
                            snackbar = Snackbar
                                    .make(constraintLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();


                        } else
                            MessageBox.showAlert(SmsTanimlamaActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(sms_listview);
    }

    public void smsKayitOnClick(View view) {
        siparis_kayit(null);
    }

    public void smsGuncelleOnClick(View view) {
        if (urunMid_ != null)
            siparis_kayit(urunMid_);
    }

    Long urunMid_ = null;

    public void getEditMode(Long urunMid, Long urunId) {
        urunMid_ = urunMid;
        List<Sms> updateKayitList = db.smsDao().getSmsForId(urunId);
        if(updateKayitList.size() ==0)
            updateKayitList = db.smsDao().getSmsForMid(urunMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid().equals(urunMid)) {
            smsBaslik.setText(updateKayitList.get(0).getBaslik());
            smsAciklama.setText(updateKayitList.get(0).getAciklama());
            siparisDurumuspinner.setSelection(spinnerArray.indexOf(updateKayitList.get(0).getSiparisDurumu()));

        }
    }


    void siparis_kayit(final Long gelenurunMid) {
        if (smsAciklama.getText().toString().trim().equalsIgnoreCase("") ||smsBaslik.getText().toString().trim().equalsIgnoreCase("") ||
                siparisDurumuspinner.getSelectedItem().toString().equalsIgnoreCase("Sipariş Durumu Seçiniz") ) {
            MessageBox.showAlert(SmsTanimlamaActivity.this, "Lütfen bilgileri eksiksiz bir şekilde giriniz.", false);

        } else {
            final Sms sms = new Sms();
            sms.setAciklama(smsAciklama.getText().toString());
            sms.setBaslik(smsBaslik.getText().toString());
            sms.setSiparisDurumu(siparisDurumuspinner.getSelectedItem().toString());



            new Thread(new Runnable() {
                @Override
                public void run() {
                    long musteriMid = 0;

                    if (gelenurunMid == null)
                        musteriMid = db.smsDao().setSms(sms);
                    if (gelenurunMid != null) {
                        sms.setMid(Long.valueOf(gelenurunMid));
                        musteriMid = db.smsDao().updateSms(sms);
                    }

                    final long finalSiparisMid = musteriMid;
                    SmsTanimlamaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenurunMid == null && Integer.valueOf(String.valueOf(finalSiparisMid)) > 0) {
                                MessageBox.showAlert(SmsTanimlamaActivity.this, "Kayıt Başarılı..\n", false);
                                get_list();
                                smsAdapter.notifyDataSetChanged();
                                // Intent i = new Intent(urunTanimlamaActivity.this, SiparisKayitActivity.class);
                            }
                            if (gelenurunMid != null && finalSiparisMid == 1) {
                                MessageBox.showAlert(SmsTanimlamaActivity.this, "İşlem Başarılı..\n", false);
                                get_list();
                                smsAdapter.notifyDataSetChanged();


                            } else if (finalSiparisMid < 0)
                                MessageBox.showAlert(SmsTanimlamaActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    void senkronEdilmeyenKayitlariGonder() {
        for (Urun item : db.urunDao().getSenkronEdilmeyenUrunAll()) {
            try {
                //postUrunService(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Urun gelenUrun;

  /*  public void postUrunService(final Urun urun) throws Exception {
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
                    MessageBox.showAlert(SmsTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
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
                        SmsTanimlamaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });


                    } /*else
                        MessageBox.showAlert(UrunTanimlamaActivity.this, "Kayıt bulunamamıştır..", false);*/
              /*  }
            }

            @Override
            public void onFailure(Call<Urun> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(SmsTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }*/


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
                    MessageBox.showAlert(SmsTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunSube = response.body();
                    if (gelenUrunSube != null) {
                        db.urunSubeDao().updateUrunSubeQuery(urunSubeMid, gelenUrunSube.getId(), true);
                        db.urunFiyatDao().updateUrunFiyatQueryForUrunMid(urunSubeMid, gelenUrunSube.getId());

                        try {
                            if (gelenUrunSube.getId() != null) {
                                for (UrunFiyat item : db.urunFiyatDao().getForUrunSubeId(gelenUrunSube.getId()))
                                    if (item.getSenkronEdildi() == false)
                                        postUrunFiyatService(item);
                            } else {
                                for (UrunFiyat item : db.urunFiyatDao().getForUrunSubeMid(urunSubeMid))
                                    if (item.getSenkronEdildi() == false)
                                        postUrunFiyatService(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        SmsTanimlamaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });


                    } else
                        MessageBox.showAlert(SmsTanimlamaActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<UrunSube> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(SmsTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
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
        item.setSenkronEdildi(null);

        // item.setId(null);
        String jsonStr = gson.toJson(item);
        Call<UrunFiyat> call = refrofitRestApi.postUruneFiyatEkle(OrtakFunction.authorization, OrtakFunction.tenantId, item);
        call.enqueue(new Callback<UrunFiyat>() {
            @Override
            public void onResponse(Call<UrunFiyat> call, Response<UrunFiyat> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(SmsTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunFiyat = response.body();
                    if (gelenUrunSube != null) {
                        db.urunFiyatDao().updateUrunFiyatQuery(urunFiyatMid, gelenUrunFiyat.getId(), true);
                        SmsTanimlamaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });


                    } else
                        MessageBox.showAlert(SmsTanimlamaActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<UrunFiyat> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(SmsTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }


}


