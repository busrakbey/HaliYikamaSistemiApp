package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
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
import android.widget.AdapterView;
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
import com.example.haliyikamaapp.Model.Entity.Bolge;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.github.reinaldoarrosi.maskededittext.MaskedEditText;
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
    EditText tc_no_edittw, adi_edittw, soyadi_edittw, vergi_no_edittw, adres_edittw,cadde_edittw, sokak_edittw, kapi_no_edittw;
    MaskedEditText tel_no_edittw;
    Spinner musteri_turu_spinner, sube_spinner, bolge_spinner, il_spinner, ilce_spinner;
    Button kayit_button;
    String gelenMusteriMid, gelenTelefonNumarasi, secili_bolge_adi;
    HaliYikamaDatabase db;
    private ExpandableLayout expandableLayout;
    private ImageView expandButton;
    TextView deneme, deneme2, deneme7;
    LinearLayout toplamalan;
    Button adresGirButton, siparisOlusturButton, siparislerimButton, adresListeleButton, barkodYazdirButton;
    Long yeniKayitMusteriMid;
    List<Sube> subeList;
    List<Bolge> bolgeList;
    List<String> subeListString;
    List<String> bolgeListString;
    Long secili_sube_id,musteriId = null;
    List<S_IL> iller;
    List<S_ILCE> ilceler, all_ilce;
    List<String> ilStringList;
    List<String> ilceStringList;
    int selected_il_index = 0, selected_ilce_index = 0;
    Long secili_il_id=null, secili_ilce_id=null;

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
        subeAndBolgeSpinner();
        ilIlceSpinnerList();


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
        tel_no_edittw = (MaskedEditText) findViewById(R.id.telefon_no);
        musteri_turu_spinner = (Spinner) findViewById(R.id.musteri_turu);
        bolge_spinner = (Spinner) findViewById(R.id.musteri_bolge);
        sube_spinner = (Spinner) findViewById(R.id.musteri_sube);
        vergi_no_edittw = (EditText) findViewById(R.id.vergş_no);
        adres_edittw = (EditText) findViewById(R.id.adres);
        kayit_button = (Button) findViewById(R.id.musteri_kayit_button);
        barkodYazdirButton = (Button) findViewById(R.id.siparis_yazdir_button);
        cadde_edittw = (EditText) findViewById(R.id.cadde);
        sokak_edittw = (EditText) findViewById(R.id.sokak);
        kapi_no_edittw = (EditText) findViewById(R.id.kapi_no);
        il_spinner = (Spinner) findViewById(R.id.il);
        ilce_spinner = (Spinner) findViewById(R.id.ilce);
        if (gelenMusteriMid == null) {
            tel_no_edittw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    //tel_no_edittw.setHint("Lütfen ilk hanesini sıfır giriniz");
                    if (!hasFocus)
                        tel_no_edittw.setHint(tel_no_edittw.getText().toString());
                }
            });
        }


        adresGirButton = findViewById(R.id.adres_gir_button);
        siparisOlusturButton = findViewById(R.id.siparis_olustur_button);
        siparislerimButton = findViewById(R.id.siparis_listele_button);
        adresListeleButton = findViewById(R.id.adres_listele_button);




        bolgeListString = new ArrayList<String>();
        subeListString = new ArrayList<String>();
        subeList = new ArrayList<Sube>();
        bolgeList = new ArrayList<Bolge>();
        ilStringList = new ArrayList<String>();
        ilceStringList = new ArrayList<String>();
        iller = new ArrayList<S_IL>();
        all_ilce = new ArrayList<S_ILCE>();
        ilceler = new ArrayList<S_ILCE>();

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
                String url = "https://api.whatsapp.com/send?phone=+90" + tel_no_edittw.getText().toString() + "&text=Merhabalar! ...";
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

    public void barkodYazdirOnClick(View v) {
        barkodYazdir();
    }

    void yeni_musteri_kayit() {
        if (adi_edittw.getText().toString().trim().equalsIgnoreCase("")
                || soyadi_edittw.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Lütfen bilgileri eksizksiz bir şekilde giriniz.", false);

        } else {
            final Musteri musteri = new Musteri();
            musteri.setMusteriAdi(adi_edittw.getText().toString());
            musteri.setMusteriSoyadi(soyadi_edittw.getText().toString());
            musteri.setTelefonNumarasi(PhoneNumberUtils.formatNumber(tel_no_edittw.getText().toString()));
            //musteri.setTcKimlikNo(tc_no_edittw.getText().toString());
            musteri.setMusteriTuru(String.valueOf(musteri_turu_spinner.getSelectedItemPosition()));
            musteri.setVergiKimlikNo(vergi_no_edittw.getText().toString());
            musteri.setAdres(adres_edittw.getText().toString());
            musteri.setSubeId(secili_sube_id);
            musteri.setBolge(secili_bolge_adi);
            musteri.setSenkronEdildi(false);
            musteri.setIlId(secili_il_id);
            musteri.setIlceId(secili_ilce_id);
            musteri.setCadde(cadde_edittw.getText().toString());
            musteri.setSokak(sokak_edittw.getText().toString());
            musteri.setKapiNo(kapi_no_edittw.getText().toString());
            musteri.setId(musteriId);


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

                             /*   Intent i = new Intent(MusteriKayitActivity.this, MusteriDetayKayitActivity.class);
                                i.putExtra("musteriMid", String.valueOf(finalMusteriMid));
                                i.putExtra("cepNo", String.valueOf(tel_no_edittw.getText().toString()));
                                yeniKayitMusteriMid = finalMusteriMid;
                                finish();
                                startActivity(i);*/
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
            adi_edittw.setText(updateKayitList.get(0).getMusteriAdi());
            soyadi_edittw.setText(updateKayitList.get(0).getMusteriSoyadi());
            tel_no_edittw.setText(updateKayitList.get(0).getTelefonNumarasi());
            //tc_no_edittw.setText(updateKayitList.get(0).getTcKimlikNo().toString());
            // musteri_turu_spinner.setText(updateKayitList.get(0).getAciklama().toString());
            vergi_no_edittw.setText(updateKayitList.get(0).getVergiKimlikNo());
            adres_edittw.setText(updateKayitList.get(0).getAdres());
            musteriId = updateKayitList.get(0).getId();
            cadde_edittw.setText(updateKayitList.get(0).getCadde());
            kapi_no_edittw.setText(updateKayitList.get(0).getKapiNo());
            sokak_edittw.setText(updateKayitList.get(0).getSokak());


            for (Sube item : db.subeDao().getSubeAll()) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getSubeId()!= null && item.getId().toString().equalsIgnoreCase(updateKayitList.get(0).getSubeId().toString()))
                    sube_spinner.setSelection(subeListString.indexOf(item.getSubeAdi()));
            }

            for (Bolge item : db.bolgeDao().getBolgeAll()) {
                if (item != null && item.getBolge() != null && updateKayitList.get(0).getBolge()!= null && item.getBolge().equalsIgnoreCase(updateKayitList.get(0).getBolge()))
                    bolge_spinner.setSelection(bolgeListString.indexOf(item.getBolge()));
            }
            for (S_IL item : db.sIlDao().getIlAll()) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getIlId()!= null && item.getId().toString().equalsIgnoreCase(updateKayitList.get(0).getIlId().toString()))
                    il_spinner.setSelection(ilStringList.indexOf(item.getId()));
            }

            for (S_ILCE item : db.sIlceDao().getIlceAll()) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getIlceId()!= null && item.getId().toString().equalsIgnoreCase(updateKayitList.get(0).getIlceId().toString()))
                    ilce_spinner.setSelection(ilceStringList.indexOf(item.getId()));
            }


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
        } else {
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


    void subeAndBolgeSpinner() {
        subeList.add(null);
        subeList = db.subeDao().getSubeAll();
        subeListString.add("Şube Seçiniz..");
        for (Sube item : subeList) {
            subeListString.add(item.getSubeAdi());
        }

        ArrayAdapter<String> dataAdapter_il = new ArrayAdapter<String>(MusteriKayitActivity.this, android.R.layout.simple_spinner_item, subeListString);
        dataAdapter_il.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sube_spinner.setAdapter(dataAdapter_il);
        sube_spinner.setSelection(0);

        sube_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = subeListString.get(position);
                    if (valInfo != null) {
                        secili_sube_id = subeList.get(position - 1).getId();
                    }
                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        bolgeList.add(null);
        bolgeList = db.bolgeDao().getBolgeAll();
        bolgeListString.add("Bölge Seçiniz..");
        for (Bolge item : bolgeList) {
            bolgeListString.add(item.getBolge());
        }

        ArrayAdapter<String> dataAdapter_ilce = new ArrayAdapter<String>(MusteriKayitActivity.this, android.R.layout.simple_spinner_item, bolgeListString);
        dataAdapter_ilce.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        bolge_spinner.setAdapter(dataAdapter_ilce);
        bolge_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = bolgeListString.get(position);
                    if (valInfo != null) {
                        secili_bolge_adi = bolgeList.get(position - 1).getBolge();
                    } else {

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gelenMusteriMid = getIntent().getStringExtra("musteriMid");
        if (gelenMusteriMid != null)
            getEditMode(Long.valueOf(gelenMusteriMid));
        gelenTelefonNumarasi = getIntent().getStringExtra("number");
        if (gelenTelefonNumarasi != null)
            tel_no_edittw.setText(gelenTelefonNumarasi);


    }

    void barkodYazdir(){
        Intent bluetooth = new Intent(MusteriKayitActivity.this,BluetoothActivity.class);
       // bluetooth.putExtra()
        startActivity(bluetooth);
    }

    void ilIlceSpinnerList() {
        all_ilce.add(null);
        iller = db.sIlDao().getIlAll();
        all_ilce = db.sIlceDao().getIlceAll();
        ilStringList.add("İl");
        ilceStringList.add("İlçe");

        for (S_IL item : iller) {
            ilStringList.add(item.getAd());
        }

        ArrayAdapter<String> dataAdapter_il = new ArrayAdapter<String>(MusteriKayitActivity.this, android.R.layout.simple_spinner_item, ilStringList);
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


                        ArrayAdapter<String> dataAdapter_ilce = new ArrayAdapter<String>(MusteriKayitActivity.this, android.R.layout.simple_spinner_item, ilceStringList);
                        dataAdapter_ilce.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        ilce_spinner.setAdapter(dataAdapter_ilce);
                        ilce_spinner.setSelection(0);

                    }
                } else {
                    secili_il_id = null;
                    secili_ilce_id = null;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> dataAdapter_ilce = new ArrayAdapter<String>(MusteriKayitActivity.this, android.R.layout.simple_spinner_item, ilceStringList);
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
