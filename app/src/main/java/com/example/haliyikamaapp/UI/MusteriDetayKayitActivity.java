package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MusteriDetayKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText iletisim_baslik_edittw, cep_no_edittw, cadde_edittw, sokak_edittw, kapi_no_edittw, adres_edittw;
    Button kaydet_button;
    Spinner il_spinner, ilce_spinner;
    String musteriMid, musteriDetayMid, gelenCepNo;
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
        setContentView(R.layout.musteri_detay_kayit_activity);
        init_item();
        initToolBar();


    }


    void init_item() {
        db = HaliYikamaDatabase.getInstance(MusteriDetayKayitActivity.this);
        iletisim_baslik_edittw = (EditText) findViewById(R.id.iletisim_baslik);
        cep_no_edittw = (EditText) findViewById(R.id.cep_to_iletisim);
        cadde_edittw = (EditText) findViewById(R.id.cadde);
        sokak_edittw = (EditText) findViewById(R.id.sokak);
        kapi_no_edittw = (EditText) findViewById(R.id.kapi_no);
        adres_edittw = (EditText) findViewById(R.id.adres);
        il_spinner = (Spinner) findViewById(R.id.il);
        ilce_spinner = (Spinner) findViewById(R.id.ilce);
        kaydet_button = (Button) findViewById(R.id.iletisim_kaydet);
        Intent intent = getIntent();
        musteriMid = intent.getStringExtra("musteriMid");
        musteriDetayMid = intent.getStringExtra("musteriDetayMid");
        musteriMid = intent.getStringExtra("musteriMid");
        gelenCepNo = intent.getStringExtra("cepNo");

        if (musteriDetayMid == null ){
            cep_no_edittw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    cep_no_edittw.setHint("Lütfen ilk hanesini sıfır giriniz");
                    if(!hasFocus)
                        cep_no_edittw.setHint(cep_no_edittw.getText().toString());
                }
            });



        }

        if (musteriDetayMid != null)
            getEditMode(Long.valueOf(musteriDetayMid));

        if(gelenCepNo != null)
            cep_no_edittw.setText(gelenCepNo);

        List<String> list = new ArrayList<String>();
        list.add("İl Seçiniz..");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        il_spinner.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<String>();
        list2.add("İlçe Seçiniz..");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ilce_spinner.setAdapter(dataAdapter2);

        cep_no_edittw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://api.whatsapp.com/send?phone=+9" + cep_no_edittw.getText().toString() + "&text=Merhabalar! ...";
                try {
                    PackageManager pm = getApplicationContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(MusteriDetayKayitActivity.this, "Lütfen cihazınıza Whatsapp uygulamasını yükleyiniz..", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });


    }

    public void iletisimOnClick(View v) {
        yeni_ietisim_kayit();
    }

    void yeni_ietisim_kayit() {
        if (iletisim_baslik_edittw.getText().toString().trim().equalsIgnoreCase("") ||
                cep_no_edittw.getText().toString().trim().equalsIgnoreCase("")
                /* || (il_spinner.getSelectedItemPosition() == -1) || (ilce_spinner.getSelectedItemPosition() == -1)*/ ||
                adres_edittw.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(MusteriDetayKayitActivity.this, "Lütfen zorunlu alanları eksiksiz doldurunuz.", false);

        } else {
            final MusteriIletisim musteri = new MusteriIletisim();
            musteri.setIletisimAdi(iletisim_baslik_edittw.getText().toString());
            musteri.setTelefonNo(cep_no_edittw.getText().toString());
            musteri.setCadde(cadde_edittw.getText().toString());
            musteri.setKapiNo(kapi_no_edittw.getText().toString());
            musteri.setSokak(sokak_edittw.getText().toString());
            musteri.setIlId(Long.valueOf(il_spinner.getSelectedItemPosition()));
            musteri.setIlceId(Long.valueOf(ilce_spinner.getSelectedItemPosition()));
            musteri.setAdres(adres_edittw.getText().toString());
            musteri.setMustId(Long.valueOf(musteriMid));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long yeniKayitMusteriMid = -1;
                    if (musteriDetayMid == null)
                        yeniKayitMusteriMid = db.musteriIletisimDao().setMusteriIletisim(musteri);
                    if (musteriDetayMid != null) {
                        musteri.setMid(Long.valueOf(musteriDetayMid));
                        yeniKayitMusteriMid = db.musteriIletisimDao().updateMusteriIletisim(musteri);
                    }

                    final long finalYeniKayitMusteriMid = yeniKayitMusteriMid;
                    MusteriDetayKayitActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (musteriDetayMid == null && Integer.valueOf(String.valueOf(finalYeniKayitMusteriMid)) > 0) {
                                MessageBox.showAlert(MusteriDetayKayitActivity.this, "Kayıt Başarılı..\n", false);

                               // Intent i = new Intent(MusteriDetayKayitActivity.this, MusteriDetayActivity.class);
                                 Intent i = new Intent(MusteriDetayKayitActivity.this, SiparisKayitActivity.class);
                                i.putExtra("musteriMid", String.valueOf(musteriMid));
                                finish();
                                startActivity(i);
                            }
                            if (musteriDetayMid != null && finalYeniKayitMusteriMid == 1) {
                                MessageBox.showAlert(MusteriDetayKayitActivity.this, "İşlem Başarılı..\n", false);

                            } else if (finalYeniKayitMusteriMid < 0)
                                MessageBox.showAlert(MusteriDetayKayitActivity.this, "İşlem başarısız..\n", false);

                        }
                    });

                }
            }).start();
        }

    }

    void getEditMode(Long musteriDetayMid) {
        List<MusteriIletisim> updateKayitList = db.musteriIletisimDao().getMusteriIletisimForMid(musteriDetayMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid() == musteriDetayMid) {
            iletisim_baslik_edittw.setText(updateKayitList.get(0).getIletisimAdi());
            cep_no_edittw.setText(updateKayitList.get(0).getTelefonNo());
            cadde_edittw.setText(updateKayitList.get(0).getCadde());
            kapi_no_edittw.setText(updateKayitList.get(0).getKapiNo());
            sokak_edittw.setText(updateKayitList.get(0).getSokak());
            //  il_spinner.setSelection(updateKayitList.get(0).getId());
            // ilce_spinner.setSelection();
            adres_edittw.setText(updateKayitList.get(0).getAdres());

        }


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("İletişim Bilgileri");
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
}



