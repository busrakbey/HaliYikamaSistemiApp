package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MusteriKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText tc_no_edittw, adi_edittw, soyadi_edittw, tel_no_edittw, vergi_no_edittw;
    Spinner musteri_turu_spinner;
    Button kayit_button;
    String gelenMusteriMid, gelenTelefonNumarasi;
    HaliYikamaDatabase db;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.musteri_kayit_activity);
        init_item();
        initToolBar();


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Müşteri");
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
        db = HaliYikamaDatabase.getInstance(MusteriKayitActivity.this);
        tc_no_edittw = (EditText) findViewById(R.id.tc_no);
        soyadi_edittw = (EditText) findViewById(R.id.musteri_soyadi);
        adi_edittw = (EditText) findViewById(R.id.musteri_adi);
        tel_no_edittw = (EditText) findViewById(R.id.telefon_no);
        musteri_turu_spinner = (Spinner) findViewById(R.id.musteri_turu);
        vergi_no_edittw = (EditText) findViewById(R.id.vergş_no);
        kayit_button = (Button) findViewById(R.id.musteri_kayit_button);
        if (gelenMusteriMid == null) {
            tel_no_edittw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    tel_no_edittw.setHint("Lütfen ilk hanesini sıfır giriniz");
                    if (!hasFocus)
                        tel_no_edittw.setHint(tel_no_edittw.getText().toString());
                }
            });


        }


        gelenMusteriMid = getIntent().getStringExtra("musteriMid");
        if (gelenMusteriMid != null)
            getEditMode(Long.valueOf(gelenMusteriMid));
        gelenTelefonNumarasi = getIntent().getStringExtra("number");
        if (gelenTelefonNumarasi != null)
            tel_no_edittw.setText(gelenTelefonNumarasi);

        List<String> list = new ArrayList<String>();
        list.add("Müşteri türü seçiniz..");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musteri_turu_spinner.setAdapter(dataAdapter);

    }

    public void musteriKayitOnclik(View v) {
        yeni_musteri_kayit();
    }

    void yeni_musteri_kayit() {
        if (tc_no_edittw.getText().toString().length() != 11 || adi_edittw.getText().toString().trim().equalsIgnoreCase("")
                || soyadi_edittw.getText().toString().trim().equalsIgnoreCase("") || tel_no_edittw.getText().toString().length() != 11) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Lütfen bilgileri eksizksiz bir şekilde giriniz.", false);

        } else {
            final Musteri musteri = new Musteri();
            musteri.setMusteriAdi(adi_edittw.getText().toString());
            musteri.setMusteriSoyadi(soyadi_edittw.getText().toString());
            musteri.setTelefonNumarasi(tel_no_edittw.getText().toString());
            musteri.setTcKimlikNo(tc_no_edittw.getText().toString());
            musteri.setMusteriTuru(String.valueOf(musteri_turu_spinner.getSelectedItemPosition()));
            musteri.setVergiKimlikNo(vergi_no_edittw.getText().toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long musteriMid = 0;

                    if (gelenMusteriMid == null)
                        musteriMid = db.musteriDao().setMusteri(musteri);
                    if (gelenMusteriMid != null) {
                        musteri.setMid(Long.valueOf(gelenMusteriMid));
                        musteriMid = db.musteriDao().updateMusteri(musteri);
                    }

                    final long finalMusteriMid = musteriMid;
                    MusteriKayitActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenMusteriMid == null && Integer.valueOf(String.valueOf(finalMusteriMid)) > 0) {
                                MessageBox.showAlert(MusteriKayitActivity.this, "Kayıt Başarılı..\n", false);
                                Intent i = new Intent(MusteriKayitActivity.this, SiparisKayitActivity.class);
                                i.putExtra("musteriMid", String.valueOf(finalMusteriMid));
                                finish();
                                startActivity(i);
                            }
                            if (gelenMusteriMid != null && finalMusteriMid == 1) {
                                MessageBox.showAlert(MusteriKayitActivity.this, "İşlem Başarılı..\n", false);

                            } else if (finalMusteriMid < 0)
                                MessageBox.showAlert(MusteriKayitActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    void getEditMode(Long musteriMid) {
        List<Musteri> updateKayitList = db.musteriDao().getMusteriForMid(musteriMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid() == musteriMid) {
            adi_edittw.setText(updateKayitList.get(0).getMusteriAdi().toString());
            soyadi_edittw.setText(updateKayitList.get(0).getMusteriSoyadi().toString());
            tel_no_edittw.setText(updateKayitList.get(0).getTelefonNumarasi().toString());
            tc_no_edittw.setText(updateKayitList.get(0).getTcKimlikNo().toString());
            // musteri_turu_spinner.setText(updateKayitList.get(0).getAciklama().toString());
            vergi_no_edittw.setText(updateKayitList.get(0).getVergiKimlikNo());


        }
    }
}
