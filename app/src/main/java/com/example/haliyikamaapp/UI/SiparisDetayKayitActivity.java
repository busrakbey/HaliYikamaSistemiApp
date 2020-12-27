package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.AutoCompleteAdapter.MusteriAutoCompleteAdapter;
import com.example.haliyikamaapp.AutoCompleteAdapter.OlcuBirimAutoCompleteAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SiparisDetayKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText urun_adi_edittw, birim_fiyati_edittw, miktar_edittw;
    Button kaydet_button;
    AutoCompleteTextView olcu_birim_autocomplete;
    String siparisMid, siparisDetayMid;
    HaliYikamaDatabase db;
    OlcuBirimAutoCompleteAdapter autoCompleteAdapter;
    Long olcuBirimMid;
    String olcuBirimAdi;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.siparis_detay_kayit_activity);
        init_item();
        initToolBar();


    }


    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Sipariş Detay Bilgileri");
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
        db = HaliYikamaDatabase.getInstance(SiparisDetayKayitActivity.this);
        urun_adi_edittw = (EditText) findViewById(R.id.sip_urun_adi);
        birim_fiyati_edittw = (EditText) findViewById(R.id.sip_birim_fiyat);
        miktar_edittw = (EditText) findViewById(R.id.sip_miktar);
        olcu_birim_autocomplete = (AutoCompleteTextView) findViewById(R.id.sip_olcu_birim);
        kaydet_button = (Button) findViewById(R.id.iletisim_kaydet);
        Intent intent = getIntent();
        siparisMid = intent.getStringExtra("siparisMid");
        siparisDetayMid = intent.getStringExtra("siparisDetayMid");
        if (siparisDetayMid != null)
            getEditMode(Long.valueOf(siparisDetayMid));




        List<OlcuBirim> allOlcuBirim = db.olcuBirimDao().getOlcuBirimAll();
        autoCompleteAdapter = new OlcuBirimAutoCompleteAdapter(this, R.layout.activity_main, android.R.layout.simple_dropdown_item_1line, allOlcuBirim);
        olcu_birim_autocomplete.setThreshold(2);
        olcu_birim_autocomplete.setAdapter(autoCompleteAdapter);


        olcu_birim_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OlcuBirim dty = (OlcuBirim) parent.getAdapter().getItem(position);

                if (dty != null) {
                    olcuBirimMid = dty.getMid();
                    olcuBirimAdi = dty.getOlcuBirimi();
                }
            }
        });


    }

    public void iletisimOnClick(View v) {
        yeni_ietisim_kayit();
    }

    void yeni_ietisim_kayit() {
     /*   if (iletisim_baslik_edittw.getText().toString().trim().equalsIgnoreCase("") ||
                cep_no_edittw.getText().toString().trim().equalsIgnoreCase("")
                /* || (il_spinner.getSelectedItemPosition() == -1) || (ilce_spinner.getSelectedItemPosition() == -1) ||
                adres_edittw.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(SiparisDetayKayitActivity.this, "Lütfen zorunlu alanları eksiksiz doldurunuz.", false);

        } else {*/
        final SiparisDetay siparisDetay = new SiparisDetay();
        siparisDetay.setUrunId(1L);
        if(!birim_fiyati_edittw.getText().toString().equalsIgnoreCase(""))
        siparisDetay.setBirimFiyat(Double.parseDouble(birim_fiyati_edittw.getText().toString()));
        siparisDetay.setOlcuBirimMid(olcuBirimMid);
        if(!miktar_edittw.getText().toString().equalsIgnoreCase(""))
        siparisDetay.setMiktar(Double.parseDouble(miktar_edittw.getText().toString()));
        siparisDetay.setMustId(Long.valueOf(siparisMid));

        new Thread(new Runnable() {
            @Override
            public void run() {
                long yeniKayitSiparisMid = -1;
                if (siparisDetayMid == null)
                    yeniKayitSiparisMid = db.siparisDetayDao().setSiparisDetay(siparisDetay);
                if (siparisDetayMid != null)
                    yeniKayitSiparisMid = db.siparisDetayDao().updateSiparisDetay(siparisDetay);

                final long finalYeniKayitSiparisMid = yeniKayitSiparisMid;
                SiparisDetayKayitActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (siparisDetayMid == null && Integer.valueOf(String.valueOf(finalYeniKayitSiparisMid)) > 0) {
                            MessageBox.showAlert(SiparisDetayKayitActivity.this, "Kayıt Başarılı..\n", false);
                            Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayActivity.class);
                            i.putExtra("siparisMid", String.valueOf(siparisMid));
                            finish();
                            startActivity(i);
                        }
                        if (siparisDetayMid != null && finalYeniKayitSiparisMid == 1) {
                            MessageBox.showAlert(SiparisDetayKayitActivity.this, "İşlem Başarılı..\n", false);

                        } else if(finalYeniKayitSiparisMid < 0)
                            MessageBox.showAlert(SiparisDetayKayitActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        }).start();
        //  }

    }

    void getEditMode(Long siparisDetayMid) {
        List<SiparisDetay> updateKayitList = db.siparisDetayDao().getSiparisDetayForMid(siparisDetayMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid() == siparisDetayMid) {
            urun_adi_edittw.setText(""/*updateKayitList.get(0).getUrunId()*/);
            birim_fiyati_edittw.setText(updateKayitList.get(0).getBirimFiyat().toString());
            miktar_edittw.setText(updateKayitList.get(0).getMiktar().toString());
            //  olcu_birim_spinne.setText(updateKayitList.get(0).getKapiNo());

        }
    }
}



