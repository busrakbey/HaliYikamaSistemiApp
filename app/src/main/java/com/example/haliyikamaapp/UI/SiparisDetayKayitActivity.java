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
import com.example.haliyikamaapp.AutoCompleteAdapter.UrunAutoCompleteAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SiparisDetayKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText birim_fiyati_edittw, miktar_edittw;
    Button kaydet_button, siparis_detay_ileri_button;
    AutoCompleteTextView olcu_birim_autocomplete, urun_adi_autocomplete;
    String siparisMid, siparisDetayMid, subeId, subeMid;
    HaliYikamaDatabase db;
    OlcuBirimAutoCompleteAdapter autoCompleteAdapter;
    UrunAutoCompleteAdapter urunAutoCompleteAdapter;
    Urun secilenUrun = null;
    OlcuBirim secilenOlcuBirim = null;


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
            toolbarTextView.setText("Ürünler");
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
        urun_adi_autocomplete = (AutoCompleteTextView) findViewById(R.id.sip_urun_adi);
        birim_fiyati_edittw = (EditText) findViewById(R.id.sip_birim_fiyat);
        miktar_edittw = (EditText) findViewById(R.id.sip_miktar);
        olcu_birim_autocomplete = (AutoCompleteTextView) findViewById(R.id.sip_olcu_birim);
        kaydet_button = (Button) findViewById(R.id.iletisim_kaydet);
        siparis_detay_ileri_button = (Button) findViewById(R.id.iletisim_ileri_button);
        Intent intent = getIntent();
        siparisMid = intent.getStringExtra("siparisMid");
        siparisDetayMid = intent.getStringExtra("siparisDetayMid");
        subeId = intent.getStringExtra("subeId");
        subeMid = intent.getStringExtra("subeMid");
        if (siparisDetayMid != null)
            getEditMode(Long.valueOf(siparisDetayMid));

        if (siparisMid != null && siparisMid.equalsIgnoreCase("null"))
            siparisMid = null;

        if (siparisDetayMid != null && siparisDetayMid.equalsIgnoreCase("null"))
            siparisDetayMid = null;

        if (subeMid != null && subeMid.equalsIgnoreCase("null"))
            subeMid = null;

        if (subeId != null && subeId.equalsIgnoreCase("null"))
            subeId = null;


        List<OlcuBirim> allOlcuBirim = db.olcuBirimDao().getOlcuBirimAll();
        autoCompleteAdapter = new OlcuBirimAutoCompleteAdapter(this, R.layout.activity_main, android.R.layout.simple_dropdown_item_1line, allOlcuBirim);
        olcu_birim_autocomplete.setThreshold(2);
        olcu_birim_autocomplete.setAdapter(autoCompleteAdapter);
        olcu_birim_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OlcuBirim dty = (OlcuBirim) parent.getAdapter().getItem(position);

                if (dty != null) {
                    secilenOlcuBirim = dty;
                }
            }
        });


        List<UrunSube> allUrunSube = new ArrayList<UrunSube>();
        List<Urun> allUrun = null;
        List<Long> urunIdList = new ArrayList<Long>();

        if (subeId != null)
            allUrunSube = db.urunSubeDao().getUrunSubeId(Long.valueOf(subeId));
        else if (subeMid != null)
            allUrunSube = db.urunSubeDao().getUrunSubeMid(Long.valueOf(subeMid));

        for (int i = 0; i < allUrunSube.size(); i++) {
            urunIdList.add(allUrunSube.get(i).getUrunId());
        }
        allUrun = db.urunDao().getUrunForIdList(urunIdList);

        urunAutoCompleteAdapter = new UrunAutoCompleteAdapter(this, R.layout.activity_main, android.R.layout.simple_dropdown_item_1line, allUrun);
        urun_adi_autocomplete.setThreshold(2);
        urun_adi_autocomplete.setAdapter(urunAutoCompleteAdapter);
        urun_adi_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Urun dty = (Urun) parent.getAdapter().getItem(position);

                if (dty != null) {
                    secilenUrun = dty;
                    List<UrunSube> listSubeId = db.urunSubeDao().getUrunSubeAndUrunId(secilenUrun.getId(), Long.valueOf(subeId));
                    List<UrunSube> listSubeMid = db.urunSubeDao().getUrunSubeForUrunMid(secilenUrun.getId());
                    if (listSubeId.size() > 0) {
                        secilenOlcuBirim = db.olcuBirimDao().getOlcuBirimForId(listSubeId.get(0).getOlcuBirimId()).get(0);
                        olcu_birim_autocomplete.setText(secilenOlcuBirim.getOlcuBirimi());
                    }

                    List<UrunFiyat> listFiyatId = db.urunFiyatDao().getForUrunSubeId(listSubeId.get(0).getId());
                    List<UrunFiyat> listFiyatMid = db.urunFiyatDao().getForUrunSubeMid(secilenUrun.getMid());
                    if (listFiyatId.size() > 0) {
                        birim_fiyati_edittw.setText(listFiyatId.get(0).getBirimFiyat().toString());
                    }


                }

            }
        });


    }

    public void iletisimOnClick(View v) {
        yeni_ietisim_kayit(true);
    }

    public void iletisimIleriOnclik(View v) {
        yeni_ietisim_kayit(false);
    }

    void yeni_ietisim_kayit(final Boolean tamamla) {
        if (urun_adi_autocomplete.getText().toString().trim().equalsIgnoreCase("") ||
                birim_fiyati_edittw.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(SiparisDetayKayitActivity.this, "Lütfen zorunlu alanları eksiksiz doldurunuz.", false);

        } else {
            final SiparisDetay siparisDetay = new SiparisDetay();
            if (!birim_fiyati_edittw.getText().toString().equalsIgnoreCase(""))
                siparisDetay.setBirimFiyat(Double.parseDouble(birim_fiyati_edittw.getText().toString()));

            siparisDetay.setOlcuBirimMid(secilenOlcuBirim.getMid());
            siparisDetay.setOlcuBirimId(secilenUrun.getId());
            if (!miktar_edittw.getText().toString().equalsIgnoreCase(""))
                siparisDetay.setMiktar(Double.parseDouble(miktar_edittw.getText().toString()));
            siparisDetay.setMustId(Long.valueOf(siparisMid));
            siparisDetay.setSiparisMid(Long.valueOf(siparisMid));
            siparisDetay.setUrunMid(secilenUrun.getMid());
            siparisDetay.setUrunId(secilenUrun.getId());

            db.siparisDao().updateSiparisSenkronDurum(Long.valueOf(siparisMid),false);

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
                                if (tamamla) {
                                    MessageBox.showAlert(SiparisDetayKayitActivity.this, "Kayıt Başarılı..\n", false);
                                    Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayActivity.class);
                                    i.putExtra("siparisMid", String.valueOf(siparisMid));
                                    finish();
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayKayitActivity.class);
                                    i.putExtra("siparisMid", siparisMid);
                                    i.putExtra("subeId", subeId );
                                    i.putExtra("subeMid", subeMid );
                                    finish();
                                    startActivity(i);
                                }

                            }
                            if (siparisDetayMid != null && finalYeniKayitSiparisMid == 1) {

                                if (tamamla) {
                                    MessageBox.showAlert(SiparisDetayKayitActivity.this, "Kayıt Başarılı..\n", false);
                                    Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayActivity.class);
                                    i.putExtra("siparisMid", String.valueOf(siparisMid));
                                    finish();
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayKayitActivity.class);
                                    i.putExtra("siparisMid", siparisMid);
                                    i.putExtra("siparisMid", siparisMid);
                                    i.putExtra("subeId", subeId );
                                    i.putExtra("subeMid", subeMid );
                                    finish();
                                    startActivity(i);
                                }

                            } else if (finalYeniKayitSiparisMid < 0)
                                MessageBox.showAlert(SiparisDetayKayitActivity.this, "İşlem başarısız..\n", false);

                        }
                    });

                }
            }).start();
            //  }

        }
    }

    void getEditMode(Long siparisDetayMid) {
        List<SiparisDetay> updateKayitList = db.siparisDetayDao().getSiparisDetayForMid(siparisDetayMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid() == siparisDetayMid) {
            birim_fiyati_edittw.setText(updateKayitList.get(0).getBirimFiyat() != null ? updateKayitList.get(0).getBirimFiyat().toString() : "");
            miktar_edittw.setText(updateKayitList.get(0).getMiktar() != null ? updateKayitList.get(0).getMiktar().toString() : "");
            //  olcu_birim_spinne.setText(updateKayitList.get(0).getKapiNo());

            List<Urun> allUrun = db.urunDao().getUrunForMid(updateKayitList.get(0).getUrunMid());
            if (allUrun.size() == 0)
                allUrun = db.urunDao().getUrunForId(updateKayitList.get(0).getUrunId());
            if (allUrun != null && allUrun.size() > 0)
                urun_adi_autocomplete.setText(allUrun.get(0).getUrunAdi());


            List<OlcuBirim> olcuBirim = db.olcuBirimDao().getOlcuBirimForMid(updateKayitList.get(0).getOlcuBirimMid());
            if (olcuBirim.size() == 0)
                olcuBirim = db.olcuBirimDao().getOlcuBirimForId(updateKayitList.get(0).getOlcuBirimId());
            if (olcuBirim != null && olcuBirim.size() > 0)
                olcu_birim_autocomplete.setText(olcuBirim.get(0).getOlcuBirimi());

        }
    }
}



