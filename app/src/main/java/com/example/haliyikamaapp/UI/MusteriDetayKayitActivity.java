package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
    String musteriMid, musteriDetayMid;
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

       /* S_IL il = new S_IL();
       il.setId(6L);
       il.setAd("Ankara");

        List<Long> il_id = new ArrayList<Long>();//add ids in this list
        List<String> il_adi = new ArrayList<String>();//add names in this list*/


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
                                Intent i = new Intent(MusteriDetayKayitActivity.this, MusteriDetayActivity.class);
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
            adres_edittw.setText(adres_edittw.getText().toString());

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



