package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.core.app.ActivityCompat;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Bolge;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.MusteriTuru;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.GPSTracker;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.github.reinaldoarrosi.maskededittext.MaskedEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.IOException;
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
    String gelenMusteriMid, gelenTelefonNumarasi, secili_bolge_adi = null, secili_musteri_turu = null, gelenMusteriId;
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
    Long secili_sube_id,secili_sube_mid,musteriId = null;
    List<S_IL> iller;
    List<S_ILCE> ilceler, all_ilce;
    List<String> ilStringList;
    List<String> ilceStringList, musteriTuruStringList;
    int selected_il_index = 0, selected_ilce_index = 0;
    Long secili_il_id=null, secili_ilce_id=null;
    String editModeGelenIlceAdi = null;
    ImageView konum_button;
    GPSTracker mGPS;
    double latitude  ,longitude; // latitude

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
        //ilIlceSpinnerList();
        subeAndBolgeSpinner();






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
        konum_button = (ImageView) findViewById(R.id.musteri_konum_button);
        mGPS = new GPSTracker(this);

        konum_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mGPS.canGetLocation ){
                    mGPS.getLocation();
                    if(mGPS.location != null) {
                        mGPS.getAdress();
                        adres_edittw.setText(mGPS.getAdress().get(0).getAddressLine(0));
                        longitude = mGPS.getLongitude();
                        latitude = mGPS.getLatitude();

                    }
                    //  text.setText("Lat"+mGPS.getLatitude()+"Lon"+mGPS.getLongitude());
                }else{
                    // text.setText("Unabletofind");
                    System.out.println("Konum bulunamadı.");
                }

            }
        });
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
        musteriTuruStringList = new ArrayList<String>();
        iller = new ArrayList<S_IL>();
        all_ilce = new ArrayList<S_ILCE>();
        ilceler = new ArrayList<S_ILCE>();





        expandableLayout = findViewById(R.id.expandable_layout);

        expandButton = findViewById(R.id.expand_button);
        // deneme = findViewById(R.id.deneme);
        deneme2 = findViewById(R.id.deneme2);
        toplamalan = findViewById(R.id.toplam_alan1);

        expandableLayout.setOnExpansionUpdateListener(this);


    }

    public void musteriKayitOnclik(View v) {
        yeni_musteri_kayit();
    }

    public void barkodYazdirOnClick(View v) {
        barkodYazdir();
    }

    void yeni_musteri_kayit() {
        if (adi_edittw.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Lütfen bilgileri eksizksiz bir şekilde giriniz.", false);

        } else {
            final Musteri musteri = new Musteri();
            musteri.setMusteriAdi(adi_edittw.getText().toString());
            musteri.setMusteriSoyadi(soyadi_edittw.getText().toString());
            musteri.setTelefonNumarasi(PhoneNumberUtils.formatNumber(tel_no_edittw.getText().toString()));
            //musteri.setTcKimlikNo(tc_no_edittw.getText().toString());
            musteri.setMusteriTuru(secili_musteri_turu);
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
            musteri.setSubeMid(secili_sube_mid);
            musteri.setxKoor( String.valueOf(longitude) );
            musteri.setyKoor(String.valueOf(latitude) );


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
                    yeniKayitMusteriMid = musteriMid;
                    MusteriKayitActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenMusteriMid == null && Integer.valueOf(String.valueOf(finalMusteriMid)) > 0) {
                                MessageBox.showAlert(MusteriKayitActivity.this, "Kayıt Başarılı.\n", false);
                                finish();
                            }
                            if (gelenMusteriMid != null && finalMusteriMid == 1) {
                                MessageBox.showAlert(MusteriKayitActivity.this, "Güncelleme Başarılı.\n", false);
                                finish();

                            } else if (finalMusteriMid < 0)
                                MessageBox.showAlert(MusteriKayitActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }

    void getEditMode(Long musteriMid) {
       // ilIlceSpinnerList();
        List<Musteri> updateKayitList = db.musteriDao().getMusteriForMid(musteriMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid() == musteriMid) {
            ilStringList.add("İl");
            ilceStringList.add("İlçe");

            for (S_IL item : db.sIlDao().getIlAll()) {
                ilStringList.add(item.getAd());
            }

            for (S_IL item : db.sIlDao().getIlAll()) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getIlId()!= null &&
                        item.getId().toString().equalsIgnoreCase(updateKayitList.get(0).getIlId().toString())) {
                    il_spinner.setSelection((ilStringList.indexOf(item.getAd())));
                    secili_il_id = item.getId();
                    secili_ilce_id = updateKayitList.get(0).getIlceId();
                    for (S_ILCE i : db.sIlceDao().getIlceAll()) {
                        if (i == null)
                            continue;
                        if (String.valueOf(i.getIlId()).equals(String.valueOf(secili_il_id))) {
                            ilceStringList.add(i.getAdi());
                            ilceler.add(i);
                        }
                    }
                }

            }

            adi_edittw.setText(updateKayitList.get(0).getMusteriAdi());
            soyadi_edittw.setText(updateKayitList.get(0).getMusteriSoyadi());
            tel_no_edittw.setText(updateKayitList.get(0).getTelefonNumarasi());
            //tc_no_edittw.setText(updateKayitList.get(0).getTcKimlikNo().toString());
            musteri_turu_spinner.setSelection(musteriTuruStringList.indexOf(updateKayitList.get(0).getMusteriTuru()));
            vergi_no_edittw.setText(updateKayitList.get(0).getVergiKimlikNo());
            adres_edittw.setText(updateKayitList.get(0).getAdres());
            musteriId = updateKayitList.get(0).getId();
            cadde_edittw.setText(updateKayitList.get(0).getCadde());
            kapi_no_edittw.setText(updateKayitList.get(0).getKapiNo());
            sokak_edittw.setText(updateKayitList.get(0).getSokak());


            for (Sube item : db.subeDao().getSubeAll()) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getSubeId()!= null &&
                        item.getId().toString().equalsIgnoreCase(updateKayitList.get(0).getSubeId().toString())) {
                    sube_spinner.setSelection(subeListString.indexOf(item.getSubeAdi()));
                    secili_sube_id = updateKayitList.get(0).getSubeId();
                    secili_sube_mid = updateKayitList.get(0).getSubeMid();

                }
            }

            for (Bolge item : db.bolgeDao().getBolgeAll()) {
                if (item != null && item.getBolge() != null && updateKayitList.get(0).getBolge()!= null && item.getBolge().equalsIgnoreCase(updateKayitList.get(0).getBolge()))
                    bolge_spinner.setSelection(bolgeListString.indexOf(item.getBolge()));
            }

            for (S_ILCE item : ilceler) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getIlceId()!= null &&
                        item.getId().toString().equalsIgnoreCase(updateKayitList.get(0).getIlceId().toString())) {
                    ilce_spinner.setSelection(ilceStringList.indexOf(item.getAdi()));
                    editModeGelenIlceAdi = item.getAdi();
                    secili_ilce_id = updateKayitList.get(0).getIlceId();
                }

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
        i.putExtra("musteriId", gelenMusteriId != null ? String.valueOf(gelenMusteriId) : null);
        i.putExtra("subeId" , secili_sube_id != null ? String.valueOf(secili_sube_id) : null);
        i.putExtra("subeMid" , secili_sube_mid != null ? String.valueOf(secili_sube_mid) : null);
        startActivity(i);
    }


    public void siparisListeleOnClick(View v) {
        if (yeniKayitMusteriMid == null && gelenMusteriMid == null) {
            MessageBox.showAlert(MusteriKayitActivity.this, "Mevcut müşteri olmadan sipariş listelenemez..\n", false);
            return;
        }


        List<Siparis> siparisList2 = db.siparisDao().getSiparisForMusterIid(gelenMusteriId != null ? Long.valueOf(gelenMusteriId) : 0L);
        List<Siparis> siparisList = db.siparisDao().getSiparisForMusteriMid(gelenMusteriMid != null ? Long.valueOf(gelenMusteriMid) : yeniKayitMusteriMid);
        if (siparisList2 != null && siparisList2.size() > 0) {
            Intent i = new Intent(MusteriKayitActivity.this, SiparisActivity.class);
            i.putExtra("gelenPage", "sipariş");
            i.putExtra("musteriId" , gelenMusteriId);
            i.putExtra("musteriMid" , gelenMusteriMid != null ? Long.valueOf(gelenMusteriMid) : yeniKayitMusteriMid);
            startActivity(i);
        }

         else if  (siparisList != null && siparisList.size() > 0) {
            Intent i = new Intent(MusteriKayitActivity.this, SiparisActivity.class);
            i.putExtra("gelenPage", "sipariş");
            i.putExtra("musteriId" , gelenMusteriId);
            i.putExtra("musteriMid" , gelenMusteriMid != null ? Long.valueOf(gelenMusteriMid) : yeniKayitMusteriMid);
            startActivity(i);
        }
        else {
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
        subeListString.add("Şube");
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
                        secili_sube_mid = subeList.get(position-1).getMid();
                    }
                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        musteriTuruStringList.add("Müşteri Türü");
        for (MusteriTuru item : db.musteriTuruDao().getMusteriAll()) {
            musteriTuruStringList.add(item.getMusteriTuru());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, musteriTuruStringList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musteri_turu_spinner.setAdapter(dataAdapter);
        musteri_turu_spinner.setSelection(musteriTuruStringList.indexOf("Şahıs"));
        musteri_turu_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    String valInfo = musteriTuruStringList.get(position);
                    if (valInfo != null) {
                        secili_musteri_turu = musteriTuruStringList.get(position);
                    } else {

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        bolgeList.add(null);
        bolgeList = db.bolgeDao().getBolgeAll();
        bolgeListString.add("Bölge");
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
        gelenMusteriId = getIntent().getStringExtra("musteriId");
        if (gelenMusteriMid != null)
            getEditMode(Long.valueOf(gelenMusteriMid));
        if (gelenMusteriMid != null && gelenMusteriMid.equalsIgnoreCase("null"))
            gelenMusteriMid = null;

        if (gelenMusteriId != null && gelenMusteriId.equalsIgnoreCase("null"))
            gelenMusteriId = null;

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
                        ilceStringList.add("İlçe");

                        for (S_ILCE item : all_ilce) {
                            if (item == null)
                                continue;
                            if (String.valueOf(item.getIlId()).equals(String.valueOf(secili_il_id))) {
                                ilceStringList.add(item.getAdi());
                                ilceler.add(item);
                            }
                        }


                        ArrayAdapter<String> dataAdapter_ilce = new ArrayAdapter<String>(MusteriKayitActivity.this, android.R.layout.simple_spinner_item, ilceStringList);
                        dataAdapter_ilce.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        ilce_spinner.setAdapter(dataAdapter_ilce);
                        if(editModeGelenIlceAdi == null)
                        ilce_spinner.setSelection(0);
                        else {
                            ilce_spinner.setSelection(ilceStringList.indexOf(editModeGelenIlceAdi));

                        }

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
