package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.KaynakAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Hesap;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KaynakKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    RecyclerView recyclerView;
    KaynakAdapter kaynakAdapter;
    RefrofitRestApi refrofitRestApi;
    ProgressDialog progressDoalog;
    BottomNavigationView bottomNavigationView;
    EditText kaynak_adi_edittext, kaynak_plaka_edittext, kaynak_model_edittext, kaynak_aciklama_adittext, kaynak_marka_editetxt,
            kaynak_seri_no_editetxt, kaynak_edinim_tarihi_edittext, kaynak_terk_tarihi_edittext;
    Spinner kaynak_turu_spinner;
    Button kaydet_button;
    List<String> kaynakTuruStringList;
    String seciliKaynakTuru, gelenKaynakMid, gelenKaynakId;
    private DatePickerDialog datePickerDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.kaynak_kayit_activity);
        init_item();
        initToolBar();
        //senkronEdilmeyenKayitlariGonder();
        spinner_items();
        if (gelenKaynakMid != null || gelenKaynakId != null)
            getEditMode(Long.valueOf(gelenKaynakMid));

    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Kaynak Tanımı");
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
        db = HaliYikamaDatabase.getInstance(KaynakKayitActivity.this);

        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(KaynakKayitActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        kaynakTuruStringList = new ArrayList<String>();
        kaynak_adi_edittext = (EditText) findViewById(R.id.kaynak_adi_edittext);
        kaynak_aciklama_adittext = (EditText) findViewById(R.id.kaynak_aciklama);
        kaynak_edinim_tarihi_edittext = (EditText) findViewById(R.id.kaynak_edinim_tarihi);
        kaynak_terk_tarihi_edittext = (EditText) findViewById(R.id.kaynak_terk_tarihi);
        kaynak_marka_editetxt = (EditText) findViewById(R.id.kaynak_marka_edittext);
        kaynak_model_edittext = (EditText) findViewById(R.id.kaynak_model_Edittext);
        kaynak_plaka_edittext = (EditText) findViewById(R.id.kaynak_plaka_edittext);
        kaynak_seri_no_editetxt = (EditText) findViewById(R.id.kaynak_seri_no_edittext);
        kaynak_turu_spinner = (Spinner) findViewById(R.id.kaynak_turu_spinner);
        kaydet_button = (Button) findViewById(R.id.kaynak_kayit_button);
        kaydet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kaynak_kaydet();
            }
        });

        gelenKaynakId = getIntent().getStringExtra("kaynakId");
        gelenKaynakMid = getIntent().getStringExtra("kaynakMid");

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        kaynak_edinim_tarihi_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                datePickerDialog = new DatePickerDialog(KaynakKayitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                kaynak_edinim_tarihi_edittext.setText(day + "." + Integer.valueOf(month + 1) + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();

            }
        });


        kaynak_terk_tarihi_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                datePickerDialog = new DatePickerDialog(KaynakKayitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                kaynak_terk_tarihi_edittext.setText(day + "." + Integer.valueOf(month + 1) + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();

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
                            i = new Intent(KaynakKayitActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musteri:
                            i = new Intent(KaynakKayitActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                      /*  case R.id.nav_siparis:
                            i = new Intent(KaynakKayitActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musterigorevlerim:
                            i = new Intent(KaynakKayitActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri_görevlerim");
                            startActivity(i);
                            break;
                    }
                  /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/
                    return true;
                }
            };





    void kaynak_kaydet() {
        Kaynak kaynak = new Kaynak();
        kaynak.setAciklama(kaynak_aciklama_adittext.getText().toString() != null ? kaynak_aciklama_adittext.getText().toString() : null);
        kaynak.setSenkronEdildi(false);
        kaynak.setSeriNo(kaynak_seri_no_editetxt.getText().toString() != null ? kaynak_seri_no_editetxt.getText().toString() : null);
        kaynak.setEdinimTarihi(kaynak_edinim_tarihi_edittext.getText().toString() != null ? kaynak_edinim_tarihi_edittext.getText().toString() : null);
        kaynak.setKaynakTuru(seciliKaynakTuru);
        kaynak.setKaynakAdi(kaynak_adi_edittext.getText().toString() != null ? kaynak_adi_edittext.getText().toString() : null);
        kaynak.setPlakaNo(kaynak_plaka_edittext.getText().toString() != null ? kaynak_plaka_edittext.getText().toString() : null);
        kaynak.setMarka(kaynak_marka_editetxt.getText().toString() != null ? kaynak_marka_editetxt.getText().toString() : null);
        kaynak.setModel(kaynak_model_edittext.getText().toString() != null ? kaynak_model_edittext.getText().toString() : null);
        kaynak.setTerkTarihi(kaynak_terk_tarihi_edittext.getText().toString() != null ? kaynak_terk_tarihi_edittext.getText().toString() :null);
        kaynak.setAktif(null);


        if (gelenKaynakMid != null || gelenKaynakId != null) {
            List<Kaynak> updateKaynakList = null;
            if (gelenKaynakId != null)
                updateKaynakList = db.kaynakDao().getkaynakForkaynakId(Long.valueOf(gelenKaynakId));
            else
                updateKaynakList = db.kaynakDao().getkaynakForMid(Long.valueOf(gelenKaynakMid));

            kaynak.setMid(updateKaynakList.get(0).getMid());
            kaynak.setId(updateKaynakList.get(0).getId());
           int updateMid = db.kaynakDao().updatekaynak(kaynak);
           if(updateMid > 0) {
               MessageBox.showAlert(KaynakKayitActivity.this, "Güncelleme işlemi başarılı.", false);
               finish();
           }


        } else {
            long kayitMid = db.kaynakDao().setkaynak(kaynak);
            if(kayitMid > 0) {
                MessageBox.showAlert(KaynakKayitActivity.this, "Kayıt işlemi başarılı.", false);
                finish();
            }

        }

    }


    Hesap gelenHesap;

    public void postKaynakListFromService(final Hesap hesap) {
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
                    MessageBox.showAlert(KaynakKayitActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenHesap = response.body();
                    if (gelenHesap != null) {

                        db.hesapDao().updateHesapQuery(hesapMid, gelenHesap.getId(), true);
                        KaynakKayitActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             /*   if (gelenHesapList.size() != kayitList.size())
                                    MessageBox.showAlert(KaynakKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    get_list();*/

                            }
                        });


                    } else
                        MessageBox.showAlert(KaynakKayitActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<Hesap> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(KaynakKayitActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


    void spinner_items() {
        kaynakTuruStringList.add("Araç");
        kaynakTuruStringList.add("Personel");
        kaynakTuruStringList.add("Makine");
        kaynakTuruStringList.add("Hammadde");

        ArrayAdapter<String> dataAdapter_sube = new ArrayAdapter<String>(KaynakKayitActivity.this, android.R.layout.simple_spinner_item, kaynakTuruStringList);
        dataAdapter_sube.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        kaynak_turu_spinner.setAdapter(dataAdapter_sube);
        kaynak_turu_spinner.setSelection(0);

        kaynak_turu_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                seciliKaynakTuru = kaynak_turu_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }



    void getEditMode(Long kaynakMid) {
        kaydet_button.setText("GÜNCELLE");
        List<Kaynak> updateKayitList = db.kaynakDao().getkaynakForMid(kaynakMid);
        if (updateKayitList != null && updateKayitList.size() > 0) {
            kaynak_adi_edittext.setText(updateKayitList.get(0).getKaynakAdi());
            kaynak_edinim_tarihi_edittext.setText(updateKayitList.get(0).getEdinimTarihi());
            kaynak_terk_tarihi_edittext.setText(updateKayitList.get(0).getTerkTarihi());
            kaynak_marka_editetxt.setText(updateKayitList.get(0).getMarka());
            kaynak_model_edittext.setText(updateKayitList.get(0).getModel());
            kaynak_seri_no_editetxt.setText(updateKayitList.get(0).getSeriNo());
            kaynak_plaka_edittext.setText(updateKayitList.get(0).getPlakaNo());
            kaynak_aciklama_adittext.setText(updateKayitList.get(0).getAciklama());
            kaynak_turu_spinner.setSelection(kaynakTuruStringList.indexOf(updateKayitList.get(0).getKaynakTuru()));



        }

    }

}