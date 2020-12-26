package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class SiparisKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText musteri_edittw, sube_edittw, tarih_edittw, tutar_edittw, aciklama_edittw;
    Button kayit_button;
    String gelenSiparisMid;
    HaliYikamaDatabase db;
    private DatePickerDialog datePickerDialog;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.siparis_kayit_activity);
        init_item();
        initToolBar();


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Sipariş Bilgileri");
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
        db = HaliYikamaDatabase.getInstance(SiparisKayitActivity.this);
        musteri_edittw = (EditText) findViewById(R.id.musteri_autocomplete);
        sube_edittw = (EditText) findViewById(R.id.sube_adi);
        tarih_edittw = (EditText) findViewById(R.id.siparis_tarihi);
        tutar_edittw = (EditText) findViewById(R.id.siparis_tutar);
        aciklama_edittw = (EditText) findViewById(R.id.siparis_aciklama);
        kayit_button = (Button) findViewById(R.id.musteri_kayit_button);

        gelenSiparisMid = getIntent().getStringExtra("siparisMid");
        if (gelenSiparisMid != null)
            getEditMode(Long.valueOf(gelenSiparisMid));

        tarih_edittw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(SiparisKayitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                tarih_edittw.setText(day + "." + Integer.valueOf(month+1)+ "." + year);
                            }
                        },  year,   month,  dayOfMonth);
                datePickerDialog.show();

            }
        });

    }

    public void musteriKayitOnclik(View v) {
        yeni_musteri_kayit();
    }

    void yeni_musteri_kayit() {
      /*  if(!tc_no_edittw.getText().toString().trim().equalsIgnoreCase("") || !adi_edittw.getText().toString().trim().equalsIgnoreCase("")
                || !soyadi_edittw.getText().toString().trim().equalsIgnoreCase("")|| !tel_no_edittw.getText().toString().trim().equalsIgnoreCase("")){
        }*/
        //  else{

        final Siparis siparis = new Siparis();
        siparis.setMusteriId(1L);
        siparis.setSubeId(1L);
        siparis.setSiparisTarihi(tarih_edittw.getText().toString());
        siparis.setSiparisTutar(Double.parseDouble(tutar_edittw.getText().toString()));
        siparis.setAciklama(aciklama_edittw.getText().toString());


        new Thread(new Runnable() {
            @Override
            public void run() {
                long yeniKayitSiparisMid = -1;
                if (gelenSiparisMid == null)
                    yeniKayitSiparisMid = db.siparisDao().setSiparis(siparis);
                if (gelenSiparisMid != null) {
                    siparis.setMid(Long.valueOf(gelenSiparisMid));
                    yeniKayitSiparisMid = db.siparisDao().updateSiparis(siparis);
                }

                final long finalYeniKayitSiparisMid = yeniKayitSiparisMid;
                SiparisKayitActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (gelenSiparisMid == null && Integer.valueOf(String.valueOf(finalYeniKayitSiparisMid)) > 0) {
                            MessageBox.showAlert(SiparisKayitActivity.this, "Kayıt Başarılı..\n", false);
                            Intent i = new Intent(SiparisKayitActivity.this, SiparisDetayKayitActivity.class);
                            i.putExtra("siparisMid", String.valueOf(gelenSiparisMid));
                            finish();
                            startActivity(i);
                        }
                        if (gelenSiparisMid != null && finalYeniKayitSiparisMid == 1) {
                            MessageBox.showAlert(SiparisKayitActivity.this, "İşlem Başarılı..\n", false);

                        } else if(finalYeniKayitSiparisMid < 0)
                            MessageBox.showAlert(SiparisKayitActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        }).start();
        //}

    }


    void getEditMode(Long siparisMid) {
        List<Siparis> updateKayitList = db.siparisDao().getSiparisForMid(siparisMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid() == siparisMid) {
            musteri_edittw.setText(updateKayitList.get(0).getMusteriId().toString());
            sube_edittw.setText(updateKayitList.get(0).getSubeId().toString());
            tarih_edittw.setText(updateKayitList.get(0).getSiparisTarihi().toString());
            tutar_edittw.setText(updateKayitList.get(0).getSiparisTutar().toString());
            aciklama_edittw.setText(updateKayitList.get(0).getAciklama().toString());


        }
    }

}

