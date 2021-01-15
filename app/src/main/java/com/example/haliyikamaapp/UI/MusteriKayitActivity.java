package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusteriKayitActivity extends AppCompatActivity implements ExpandableLayout.OnExpansionUpdateListener {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText tc_no_edittw, adi_edittw, soyadi_edittw, tel_no_edittw, vergi_no_edittw;
    Spinner musteri_turu_spinner;
    Button kayit_button;
    String gelenMusteriMid, gelenTelefonNumarasi;
    HaliYikamaDatabase db;

    private ExpandableLayout expandableLayout;
    private ImageView expandButton;
    TextView deneme, deneme2, deneme7;
    LinearLayout toplamalan;
    Button adresGirButton, siparisOlusturButton, siparislerimButton,adresListeleButton;
    Long yeniKayitMusteriMid;

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
        adresGirButton = findViewById(R.id.adres_gir_button);
        siparisOlusturButton = findViewById(R.id.siparis_olustur_button);
        siparislerimButton = findViewById(R.id.siparis_listele_button);
        adresListeleButton= findViewById(R.id.adres_listele_button);


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


        expandableLayout = findViewById(R.id.expandable_layout);

        expandButton = findViewById(R.id.expand_button);
        // deneme = findViewById(R.id.deneme);
        deneme2 = findViewById(R.id.deneme2);
        toplamalan = findViewById(R.id.toplam_alan1);

        expandableLayout.setOnExpansionUpdateListener(this);

        tel_no_edittw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=+9" + tel_no_edittw.getText().toString() + "&text=Merhabalar! ...";
                try {
                    PackageManager pm = getApplicationContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(MusteriKayitActivity.this, "Lütfen cihazınıza Whatsapp uygulamasını yükleyiniz..", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });




    }

    public void musteriKayitOnclik(View v) {
        yeni_musteri_kayit();
    }

    void yeni_musteri_kayit() {
        if (adi_edittw.getText().toString().trim().equalsIgnoreCase("")
                || soyadi_edittw.getText().toString().trim().equalsIgnoreCase("") /*|| tel_no_edittw.getText().toString().length() != 11*/) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Lütfen bilgileri eksizksiz bir şekilde giriniz.", false);

        } else {
            final Musteri musteri = new Musteri();
            musteri.setMusteriAdi(adi_edittw.getText().toString());
            musteri.setMusteriSoyadi(soyadi_edittw.getText().toString());
            musteri.setTelefonNumarasi(PhoneNumberUtils.formatNumber(tel_no_edittw.getText().toString()));
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
                                // Intent i = new Intent(MusteriKayitActivity.this, SiparisKayitActivity.class);

                                Intent i = new Intent(MusteriKayitActivity.this, MusteriDetayKayitActivity.class);
                                i.putExtra("musteriMid", String.valueOf(finalMusteriMid));
                                i.putExtra("cepNo" , String.valueOf(tel_no_edittw.getText().toString()));
                                yeniKayitMusteriMid = finalMusteriMid;
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

    @Override
    public void onExpansionUpdate(float expansionFraction, int state) {
        Log.d("ExpandableLayout", "State: " + state);
        expandButton.setRotation(expansionFraction * 90);
    }

    public void detayOnClick(View view) {
        expandableLayout.toggle();

        if (expandableLayout.getState() == 2) {
            // deneme.setVisibility(View.VISIBLE);
            deneme2.setVisibility(View.GONE);
            toplamalan.setVisibility(View.VISIBLE);
        } else {
            expandableLayout.collapse();
            deneme2.setVisibility(View.VISIBLE);


        }
    }

    public void siparisOlusturOnClick(View v) {
        if (yeniKayitMusteriMid == null && gelenMusteriMid == null) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Mevcut müşteri olmadan sipariş verilemez..\n", false);
            return;
        }
        Intent i = new Intent(MusteriKayitActivity.this, SiparisKayitActivity.class);
        i.putExtra("musteriMid", gelenMusteriMid != null ? gelenMusteriMid : String.valueOf(yeniKayitMusteriMid));
        startActivity(i);
    }


    public void siparisListeleOnClick(View v) {
        if (yeniKayitMusteriMid == null && gelenMusteriMid == null) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Mevcut müşteri olmadan sipariş listelenemez..\n", false);
            return;
        }

        List<Siparis> siparisList = db.siparisDao().getSiparisForMusteriMid(gelenMusteriMid != null ? Long.valueOf(gelenMusteriMid) : yeniKayitMusteriMid);
        if (siparisList != null && siparisList.size() > 0) {
            List<SiparisDetay> siparisDetayList = db.siparisDetayDao().getSiparisDetayForMid(siparisList.get(0).getMid());  ///bir müşterinin birden fazla siparişi olabılır. o yuzden burasıdegısecek
            Intent i = new Intent(MusteriKayitActivity.this, SiparisDetayActivity.class);
            i.putExtra("siparisMid", siparisList.get(0).getMid().toString());
            startActivity(i);
        }
        else{
            MessageBox.showAlert(MusteriKayitActivity.this, "Mevcut sipariş bulunamamıştır..\n", false);
        }
    }


    public void adresGirOnClick(View v) {
        if (yeniKayitMusteriMid == null && gelenMusteriMid == null) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Mevcut müşteri olmadan adres girilemez..\n", false);
            return;
        }
        Intent i = new Intent(MusteriKayitActivity.this, MusteriDetayKayitActivity.class);
        i.putExtra("musteriMid", gelenMusteriMid != null ? gelenMusteriMid : String.valueOf(yeniKayitMusteriMid));
        startActivity(i);
    }


    public void adresListeleOnClick(View v) {
        if (yeniKayitMusteriMid == null && gelenMusteriMid == null) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Mevcut müşteri olmadan adres listelenemez..\n", false);
            return;
        }
        Intent i = new Intent(MusteriKayitActivity.this, MusteriDetayActivity.class);
        i.putExtra("musteriMid", gelenMusteriMid != null ? gelenMusteriMid : String.valueOf(yeniKayitMusteriMid));
        startActivity(i);
    }





}
