package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Hesap;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;

import java.util.ArrayList;
import java.util.List;

public class HesapKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    Spinner sube_spinner, kaynak_spinner, detay_neden_spinner;
    EditText tutar_edittext, aciklama_edittext, tarih_edittext, yeni_detay_edittext;
    String islemTuru, seciliDetayNeden;
    Sube secili_sube;
    List<Sube> subeList;
    List<String> subeListString;
    Button hesap_kayit_button;
    List<String> detayNedenList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.hesap_kayit_activity);
        init_item();
        initToolBar();


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            if (islemTuru.equalsIgnoreCase("Çıkış"))
                toolbarTextView.setText("Harcama Girişi");
            if (islemTuru.equalsIgnoreCase("Giriş"))
                toolbarTextView.setText("Para Girişi");
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
        db = HaliYikamaDatabase.getInstance(HesapKayitActivity.this);
        sube_spinner = (Spinner) findViewById(R.id.harcama_sube);
        kaynak_spinner = (Spinner) findViewById(R.id.harcama_kaynak);
        detay_neden_spinner = (Spinner) findViewById(R.id.harcama_detay_neden);
        tarih_edittext = (EditText) findViewById(R.id.harcama_tarih);
        tarih_edittext = (EditText) findViewById(R.id.harcama_tarih);
        aciklama_edittext = (EditText) findViewById(R.id.harcama_aciklama);
        tutar_edittext = (EditText) findViewById(R.id.harcama_tutar);
        hesap_kayit_button = (Button) findViewById(R.id.hesap_kayit_button);
        subeList = new ArrayList<Sube>();
        subeListString = new ArrayList<String>();
        detayNedenList = new ArrayList<String>();
        yeni_detay_edittext = (EditText) findViewById(R.id.harcama_detay_ekle);
        islemTuru = getIntent().getStringExtra("islemTuru");
        spinner_items();


        hesap_kayit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hesap_kayit();
            }
        });


    }

    void hesap_kayit() {
        if (tarih_edittext.getText().toString().equalsIgnoreCase("") || tutar_edittext.getText().toString().equalsIgnoreCase(""))
            MessageBox.showAlert(HesapKayitActivity.this, "Lütfen zorunlu alanları eksiksiz doldurunuz.", false);
        else {
            Hesap hesap = new Hesap();
            hesap.setAciklama(aciklama_edittext.getText().toString());
            hesap.setDetayNeden(seciliDetayNeden);
            hesap.setIslemTuru(islemTuru);
            hesap.setTarih(tarih_edittext.getText().toString());
            hesap.setSenkronEdildi(false);
            hesap.setTutar(tutar_edittext.getText().toString() != null ? Double.valueOf(tutar_edittext.getText().toString()) : null);
            if (islemTuru.equalsIgnoreCase("Çıkış"))
                hesap.setIslemNedeni("Harcama Girişi");
            if (islemTuru.equalsIgnoreCase("Giriş"))
                hesap.setIslemNedeni("Para Girişi");
            // hesap.setKaynakId();
            if (secili_sube != null) {
                hesap.setSubeId(secili_sube.getId());
                hesap.setSubeMid(secili_sube.getMid());
            }
        }

    }

    void spinner_items() {
        subeList.add(null);
        subeList = db.subeDao().getSubeAll();
        subeListString.add("Şube");
        for (Sube item : subeList) {
            subeListString.add(item.getSubeAdi());
        }

        ArrayAdapter<String> dataAdapter_sube = new ArrayAdapter<String>(HesapKayitActivity.this, android.R.layout.simple_spinner_item, subeListString);
        dataAdapter_sube.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sube_spinner.setAdapter(dataAdapter_sube);
        sube_spinner.setSelection(0);

        sube_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = subeListString.get(position);
                    if (valInfo != null) {
                        secili_sube = subeList.get(position - 1);
                    }
                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List<Hesap> detayNeden = db.hesapDao().getHesapDetayNedenAll();
        subeList.add(null);
        subeList = db.subeDao().getSubeAll();
        detayNedenList.add("Detay Neden");
        detayNedenList.add("--Yeni Ekle--");
        for (Hesap item : detayNeden) {
            detayNedenList.add(item.getDetayNeden());
        }

        ArrayAdapter<String> dataAdapter_neden = new ArrayAdapter<String>(HesapKayitActivity.this, android.R.layout.simple_spinner_item, detayNedenList);
        dataAdapter_neden.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        detay_neden_spinner.setAdapter(dataAdapter_neden);
        detay_neden_spinner.setSelection(0);

        detay_neden_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    yeni_detay_edittext.setVisibility(View.VISIBLE);
                    detayNedenList.add(yeni_detay_edittext.getText().toString());
                    detay_neden_spinner.setSelection((detayNedenList.indexOf(yeni_detay_edittext.getText().toString())));
                    seciliDetayNeden = detay_neden_spinner.getSelectedItem().toString();

                } else if (position > 0) {
                    seciliDetayNeden = detay_neden_spinner.getSelectedItem().toString();
                    yeni_detay_edittext.setVisibility(View.GONE);

                } else {
                    seciliDetayNeden = null;
                    yeni_detay_edittext.setVisibility(View.GONE);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}