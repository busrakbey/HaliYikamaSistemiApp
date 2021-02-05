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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.MusteriDetayAdapter;
import com.example.haliyikamaapp.Adapter.SubeAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubeTanimlamaActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText subeAdi, durum, aciklama;
    Spinner il_spinner, ilce_spinner;
    RecyclerView sube_listview;
    SubeAdapter subeAdapter;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    Button subeKayitButton, subeGuncelleButton;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;

    List<S_IL> iller;
    List<S_ILCE> ilceler, all_ilce;
    List<String> ilStringList;
    List<String> ilceStringList;
    int selected_il_index = 0, selected_ilce_index = 0;
    Long secili_il_id, secili_ilce_id;
    Long subeMid_ = null;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.sube_tanimlama_activity);
        init_item();
        initToolBar();
        ilIlceSpinnerList();
        get_list();
        senkronEdilmeyenKayitlariGonder();

    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Şube Tanımlama");
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
        db = HaliYikamaDatabase.getInstance(SubeTanimlamaActivity.this);
        sube_listview = (RecyclerView) findViewById(R.id.sube_listview);
        subeAdi = (EditText) findViewById(R.id.sube_adi);
        durum = (EditText) findViewById(R.id.sube_durum);
        aciklama = (EditText) findViewById(R.id.sube_aciklama);
        il_spinner = (Spinner) findViewById(R.id.sube_il);
        ilce_spinner = (Spinner) findViewById(R.id.sube_ilce);
        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        subeKayitButton = (Button) findViewById(R.id.sube_kayit_button);
        subeGuncelleButton = (Button) findViewById(R.id.sube_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(SubeTanimlamaActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        ilStringList = new ArrayList<String>();
        ilceStringList = new ArrayList<String>();
        iller = new ArrayList<S_IL>();
        all_ilce = new ArrayList<S_ILCE>();
        ilceler = new ArrayList<S_ILCE>();




    }

    public void get_list() {
        final List<Sube> subeler = db.subeDao().getSubeAll();
        subeAdapter = new SubeAdapter(SubeTanimlamaActivity.this, subeler);
        sube_listview.setHasFixedSize(true);
        sube_listview.setLayoutManager(new LinearLayoutManager(SubeTanimlamaActivity.this));
        sube_listview.setAdapter(subeAdapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(SubeTanimlamaActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.subeDao().deletedSubeForMid(subeAdapter.getData().get(position).getMid());
                SubeTanimlamaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (silinenMusteriDetayMid == 1) {
                            subeAdapter.getData().remove(position);
                            subeAdapter.notifyDataSetChanged();
                            snackbar = Snackbar
                                    .make(constraintLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();


                        } else
                            MessageBox.showAlert(SubeTanimlamaActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(sube_listview);
    }

    public void subeKayitOnClick(View view) {
        siparis_kayit(null);
    }

    public void subeGuncelleOnClick(View view) {
        if (subeMid_ != null)
            siparis_kayit(subeMid_);
    }


    public void getEditMode(Long subeMid) {
        subeMid_ = subeMid;
        ilIlceSpinnerList();
        List<Sube> updateKayitList = db.subeDao().getSubeForMid(subeMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid().equals(subeMid)) {
            subeAdi.setText(updateKayitList.get(0).getSubeAdi().toString());
            durum.setText(updateKayitList.get(0).getDurum());
            aciklama.setText(updateKayitList.get(0).getAciklama());

            for (S_IL item : iller) {
                if (item.getId() != null && updateKayitList.get(0).getIlId() != null && item.getId() == updateKayitList.get(0).getIlId())
                il_spinner.setSelection(ilStringList.indexOf(item.getAd()));
            }

            for (S_ILCE item : ilceler) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getIlceId() != null && item.getId() == updateKayitList.get(0).getIlceId())
                ilce_spinner.setSelection(ilceStringList.indexOf(item.getAd()));
            }

        }
    }


    void siparis_kayit(final Long gelenSubeMid) {
        if (subeAdi.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(SubeTanimlamaActivity.this, "Lütfen bilgileri eksiksiz bir şekilde giriniz.", false);

        } else {
            final Sube sube = new Sube();
            sube.setAciklama(aciklama.getText().toString());
            sube.setDurum(durum.getText().toString());
            sube.setIlId(secili_il_id);
            sube.setIlceId(secili_ilce_id);
            sube.setSubeAdi(subeAdi.getText().toString());
            sube.setSenkronEdildi(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long musteriMid = 0;

                    if (gelenSubeMid == null)
                        musteriMid = db.subeDao().setSube(sube);
                    if (gelenSubeMid != null) {
                        sube.setMid(Long.valueOf(gelenSubeMid));
                        musteriMid = db.subeDao().updateSube(sube);
                    }

                    final long finalSiparisMid = musteriMid;
                    SubeTanimlamaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenSubeMid == null && Integer.valueOf(String.valueOf(finalSiparisMid)) > 0) {
                                MessageBox.showAlert(SubeTanimlamaActivity.this, "Kayıt Başarılı..\n", false);
                                get_list();
                                subeAdapter.notifyDataSetChanged();
                                // Intent i = new Intent(SubeTanimlamaActivity.this, SiparisKayitActivity.class);
                            }
                            if (gelenSubeMid != null && finalSiparisMid == 1) {
                                MessageBox.showAlert(SubeTanimlamaActivity.this, "Güncelleme Başarılı..\n", false);
                                get_list();
                                subeAdapter.notifyDataSetChanged();


                            } else if (finalSiparisMid < 0)
                                MessageBox.showAlert(SubeTanimlamaActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    void senkronEdilmeyenKayitlariGonder() {
        for(Sube item :  db.subeDao().getSenkronEdilmeyenSubeAll()) {
            try {
                postSubeService(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Sube gelenSube;
    public void postSubeService(final Sube sube) throws Exception {
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        final Long subeMid = sube.getMid();
        sube.setMid(null);
        sube.setMustId(null);
       // sube.setId(null);
        sube.setSenkronEdildi(null);
        if(sube.getIlceId() != null && sube.getIlceId().toString().equalsIgnoreCase("-1"))
        sube.setIlceId(null);
        if(sube.getIlId() != null && sube.getIlId().toString().equalsIgnoreCase("-1"))
            sube.setIlId(null);
        String jsonStr = gson.toJson(sube);
        Call<Sube> call = refrofitRestApi.postSube(OrtakFunction.authorization, OrtakFunction.tenantId, sube);
        call.enqueue(new Callback<Sube>() {
            @Override
            public void onResponse(Call<Sube> call, Response<Sube> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(SubeTanimlamaActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSube = response.body();
                    if (gelenSube != null) {
                        db.subeDao().updateSubeQuery(subeMid, gelenSube.getId(), true);
                        SubeTanimlamaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });


                    } else
                        MessageBox.showAlert(SubeTanimlamaActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<Sube> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(SubeTanimlamaActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    void ilIlceSpinnerList() {
        all_ilce.add(null);
        iller = db.sIlDao().getIlAll();
        all_ilce = db.sIlceDao().getIlceAll();
        ilStringList.add("İl Seçiniz..");
        for (S_IL item : iller) {
            ilStringList.add(item.getAd());
        }

        ArrayAdapter<String> dataAdapter_il = new ArrayAdapter<String>(SubeTanimlamaActivity.this, android.R.layout.simple_spinner_item, ilStringList);
        dataAdapter_il.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        il_spinner.setAdapter(dataAdapter_il);
        il_spinner.setSelection(0);

        il_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = ilStringList.get(position);
                    if (valInfo != null) {
                        selected_il_index = position;

                        Long il_id = iller.get(selected_il_index - 1).getId();
                        secili_il_id = il_id;
                        secili_ilce_id = null;

                        ilceStringList.clear();
                        ilceler.clear();

                        ilceler.add(null);
                        ilceStringList.add("İlçe Seçiniz..");

                        for (S_ILCE item : all_ilce) {
                            if (item == null)
                                continue;
                            if (String.valueOf(item.getIlId()).equals(String.valueOf(il_id))) {
                                ilceStringList.add(item.getAdi());
                                ilceler.add(item);
                            }
                        }


                        ArrayAdapter<String> dataAdapter_ilce = new ArrayAdapter<String>(SubeTanimlamaActivity.this, android.R.layout.simple_spinner_item, ilceStringList);
                        dataAdapter_ilce.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        ilce_spinner.setAdapter(dataAdapter_ilce);
                        ilce_spinner.setSelection(0);

                    }
                } else {
                    secili_il_id = -1L;
                    secili_ilce_id = -1L;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> dataAdapter_ilce = new ArrayAdapter<String>(SubeTanimlamaActivity.this, android.R.layout.simple_spinner_item, ilceStringList);
        dataAdapter_ilce.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ilce_spinner.setAdapter(dataAdapter_ilce);
        ilce_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = ilceStringList.get(position);
                    if (valInfo != null) {
                        selected_ilce_index = position;
                        Long mud_id = ilceler.get(selected_ilce_index).getId();
                        secili_ilce_id = mud_id;
                    } else {

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

}


