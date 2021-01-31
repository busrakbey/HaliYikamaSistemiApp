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

import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Adapter.UruneSubeAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;

import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UruneSubeTanimlamaActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText aciklama;
    CheckBox aktifMi;
    Spinner sube_spinner, olcu_birim_spinner;
    RecyclerView urun_listview;
    UruneSubeAdapter urunAdapter;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    Button urunKayitButton, urunGuncelleButton;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    List<Sube> subeList;
    List<String> subeListString, olcuBirimListString;
    List<OlcuBirim> olcubirimList;
    Integer selected_sube_index, selected_olcu_index;
    Long secili_sube_id, secili_olcu_id;
    String gelenUrunId, gelenUrunMid, seciliSubeAdi;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.urune_sube_tanimlama_activity);
        init_item();
        initToolBar();
        get_list();
        subeOlcuSpinnerList();

    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Kayıtlı Olduğu Şubeler");
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
        db = HaliYikamaDatabase.getInstance(UruneSubeTanimlamaActivity.this);
        urun_listview = (RecyclerView) findViewById(R.id.urune_ait_sube_listview);
        sube_spinner = (Spinner) findViewById(R.id.urune_ait_sube_spinner);
        aktifMi = (CheckBox) findViewById(R.id.urune_ait_sube_aktif_mi);
        aciklama = (EditText) findViewById(R.id.urune_ait_aciklama);
        olcu_birim_spinner = (Spinner) findViewById(R.id.urune_ait_olcu_birimi);

        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        urunKayitButton = (Button) findViewById(R.id.sube_kayit_button);
        urunGuncelleButton = (Button) findViewById(R.id.sube_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(UruneSubeTanimlamaActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        gelenUrunId = getIntent().getStringExtra("urunId");
        gelenUrunMid = getIntent().getStringExtra("urunMid");


    }

    List<UrunSube> urunSubeList;

    public void get_list() {
        if (!gelenUrunId.equalsIgnoreCase("null"))
            urunSubeList = db.urunSubeDao().getUrunSubeForUrunId(Long.valueOf(gelenUrunId));
        else
            urunSubeList = db.urunSubeDao().getUrunSubeForUrunMid(Long.valueOf(gelenUrunMid));

        urunAdapter = new UruneSubeAdapter(UruneSubeTanimlamaActivity.this, urunSubeList);
        urun_listview.setHasFixedSize(true);
        urun_listview.setLayoutManager(new LinearLayoutManager(UruneSubeTanimlamaActivity.this));
        urun_listview.setAdapter(urunAdapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(UruneSubeTanimlamaActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.urunDao().deletedUrunForMid(urunAdapter.getData().get(position).getMid());
                UruneSubeTanimlamaActivity.this.runOnUiThread(new Runnable() {
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
                            MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(urun_listview);
    }

    public void uruneAitSubeKayitOnClick(View view) {
        siparis_kayit(null);
    }

    public void uruneAitSubeGuncelleOnClick(View view) {
        if (urunMid_ != null)
            siparis_kayit(urunMid_);
    }

    Long urunMid_ = null;

    public void getEditMode(Long urunMid) {
        urunMid_ = urunMid;
        List<UrunSube> updateKayitList = db.urunSubeDao().getUrunSubeForMid(urunMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid().equals(urunMid)) {
            if (updateKayitList.get(0).getAktif() != null && updateKayitList.get(0).getAktif() == true)
                aktifMi.setChecked(true);

            aciklama.setText(updateKayitList.get(0).getAciklama());
            for (Sube item : subeList) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getId() != null && item.getId() == updateKayitList.get(0).getId())
                    sube_spinner.setSelection(subeListString.indexOf(item.getSubeAdi()));
            }


            for (OlcuBirim item : olcubirimList) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getId() != null && item.getId() == updateKayitList.get(0).getId())
                    olcu_birim_spinner.setSelection(olcuBirimListString.indexOf(item.getOlcuBirimi()));
            }
        }
    }


    void siparis_kayit(final Long gelenurunMid) {
        if (sube_spinner.getSelectedItemPosition() == 0 || olcu_birim_spinner.getSelectedItemPosition() == 0) {
            MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "Lütfen şube ve ölçü birimini eksiksiz bir şekilde giriniz..", false);

        } else {
            final UrunSube urun = new UrunSube();
            urun.setAciklama(aciklama.getText().toString());
            urun.setAktif(aktifMi.isChecked() ? true : false);
            urun.setOlcuBirimId(secili_olcu_id);
            urun.setSubeId(secili_sube_id);
            urun.setUrunId(!gelenUrunId.equalsIgnoreCase("null")  ? Long.valueOf(gelenUrunId) : null);
            urun.setUrunMid(Long.valueOf(gelenUrunMid));
            urun.setSubeAdi(seciliSubeAdi);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long musteriMid = 0;

                    if (gelenurunMid == null)
                        musteriMid = db.urunSubeDao().setUrunSube(urun);
                    if (gelenurunMid != null) {
                        urun.setMid(Long.valueOf(gelenurunMid));
                        // musteriMid = db.urunSubeDao().u(urun);
                    }

                    final long finalSiparisMid = musteriMid;
                    UruneSubeTanimlamaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenurunMid == null && Integer.valueOf(String.valueOf(finalSiparisMid)) > 0) {
                                MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "Kayıt Başarılı..\n", false);
                                get_list();
                                urunAdapter.notifyDataSetChanged();
                                // Intent i = new Intent(urunTanimlamaActivity.this, SiparisKayitActivity.class);
                            }
                            if (gelenurunMid != null && finalSiparisMid == 1) {
                                MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "İşlem Başarılı..\n", false);
                                get_list();
                                urunAdapter.notifyDataSetChanged();


                            } else if (finalSiparisMid < 0)
                                MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    UrunSube gelenUrunSube;

    void subeOlcuSpinnerList() {
        subeList = new ArrayList<Sube>();
        subeList.add(null);
        subeList = db.subeDao().getSubeAll();
        subeListString = new ArrayList<String>();
        subeListString.add("Şube Seçiniz..");
        for (Sube item : subeList) {
            subeListString.add(item.getSubeAdi());
        }


        olcubirimList = new ArrayList<OlcuBirim>();
        olcubirimList.add(null);
        olcubirimList = db.olcuBirimDao().getOlcuBirimAll();
        olcuBirimListString = new ArrayList<String>();
        olcuBirimListString.add("Ölçü Birimi Seçiniz..");
        for (OlcuBirim item : olcubirimList) {
            olcuBirimListString.add(item.getOlcuBirimi());
        }

        ArrayAdapter<String> dataAdapter_sube = new ArrayAdapter<String>(UruneSubeTanimlamaActivity.this, android.R.layout.simple_spinner_item, subeListString);
        dataAdapter_sube.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sube_spinner.setAdapter(dataAdapter_sube);
        sube_spinner.setSelection(0);

        ArrayAdapter<String> dataAdapter_olcu = new ArrayAdapter<String>(UruneSubeTanimlamaActivity.this, android.R.layout.simple_spinner_item, olcuBirimListString);
        dataAdapter_olcu.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        olcu_birim_spinner.setAdapter(dataAdapter_olcu);
        olcu_birim_spinner.setSelection(0);

        sube_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = subeListString.get(position);
                    if (valInfo != null) {
                        selected_sube_index = position;
                        secili_sube_id = subeList.get(selected_sube_index - 1).getId();
                        seciliSubeAdi = subeList.get(selected_sube_index-1).getSubeAdi();

                    }
                } else {
                    secili_sube_id = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        olcu_birim_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = olcuBirimListString.get(position);
                    if (valInfo != null) {
                        selected_olcu_index = position;
                        secili_olcu_id = subeList.get(selected_olcu_index - 1).getId();

                    }
                } else {
                    secili_olcu_id = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void getUrunSubeService(final UrunSube urun) throws Exception {
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        final Long urunMid = urun.getMid();
        urun.setMid(null);
        urun.setMustId(null);
        urun.setId(null);
        String jsonStr = gson.toJson(urun);
        Call<UrunSube> call = refrofitRestApi.postUruneSubeEkle(OrtakFunction.authorization, OrtakFunction.tenantId, urun);
        call.enqueue(new Callback<UrunSube>() {
            @Override
            public void onResponse(Call<UrunSube> call, Response<UrunSube> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunSube = response.body();
                    if (gelenUrunSube != null) {
                        db.urunDao().updateUrunQuery(urunMid, gelenUrunSube.getId());
                        UruneSubeTanimlamaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });


                    } else
                        MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<UrunSube> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(UruneSubeTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


}


