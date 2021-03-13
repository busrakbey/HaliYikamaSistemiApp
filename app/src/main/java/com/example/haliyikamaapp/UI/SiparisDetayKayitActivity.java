package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.EklenenUrunlerAdapter;
import com.example.haliyikamaapp.Adapter.SiparisDetayAdapter;
import com.example.haliyikamaapp.AutoCompleteAdapter.MusteriAutoCompleteAdapter;
import com.example.haliyikamaapp.AutoCompleteAdapter.OlcuBirimAutoCompleteAdapter;
import com.example.haliyikamaapp.AutoCompleteAdapter.UrunAutoCompleteAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.SwipeHelper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

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
    Spinner sip_urun_adi_spinner;
    List<String> urunStringList;
    Button urun_vazgec_button, urun_ekle_button;
    List<SiparisDetay> siparisDetayListTemp;
    RecyclerView eklenen_urunler_recyclerview;
    EklenenUrunlerAdapter siparis_detay_adapter;
    TextView toplam_tutar;
    Button btn_placeorder, urun_guncelle_button;
    LinearLayout siparisUrunEklemeLinear;



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
        sip_urun_adi_spinner = (Spinner) findViewById(R.id.sip_urun_adi_spinner);
        urun_vazgec_button = (Button) findViewById(R.id.urun_vazgec_button);
        urunStringList = new ArrayList<String>();
        urun_ekle_button = (Button) findViewById(R.id.urun_ekle_button);
        eklenen_urunler_recyclerview = (RecyclerView) findViewById(R.id.eklenen_urunler_recyclerview);
        siparisDetayListTemp = new ArrayList<SiparisDetay>();
        toplam_tutar = (TextView) findViewById(R.id.toplam_tutar_urun);
        btn_placeorder = (Button) findViewById(R.id.btn_placeorder);
        urun_guncelle_button = (Button) findViewById(R.id.urun_guncelle_button);
        siparisUrunEklemeLinear = (LinearLayout) findViewById(R.id.layout);

        Intent intent = getIntent();
        siparisMid = intent.getStringExtra("siparisMid");
        siparisDetayMid = intent.getStringExtra("siparisDetayMid");
        subeId = intent.getStringExtra("subeId");
        subeMid = intent.getStringExtra("subeMid");



        if (siparisMid != null && siparisMid.equalsIgnoreCase("null"))
            siparisMid = null;

        if (siparisDetayMid != null && siparisDetayMid.equalsIgnoreCase("null"))
            siparisDetayMid = null;

        if (subeMid != null && subeMid.equalsIgnoreCase("null"))
            subeMid = null;

        if (subeId != null && subeId.equalsIgnoreCase("null"))
            subeId = null;


        urun_vazgec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        kaydet_button();


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
        urunStringList.add("Ürün Seçiniz..");
        for (Urun item : allUrun) {
            urunStringList.add(item.getUrunAdi());
        }

        ArrayAdapter<String> dataAdapter_il = new ArrayAdapter<String>(SiparisDetayKayitActivity.this, android.R.layout.simple_spinner_item, urunStringList);
        dataAdapter_il.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sip_urun_adi_spinner.setAdapter(dataAdapter_il);
        sip_urun_adi_spinner.setSelection(0);

        if (siparisDetayMid != null)
            getEditMode(Long.valueOf(siparisDetayMid));



        final List<Urun> finalAllUrun = allUrun;
        sip_urun_adi_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = urunStringList.get(position);
                    if (valInfo != null) {
                        Urun dty = finalAllUrun.get(position - 1);
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
                } else {
                    secilenUrun = null;
                    olcu_birim_autocomplete.setText(null);
                    birim_fiyati_edittw.setText(null);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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


        urun_ekle_button();

    }

  /*  public void iletisimOnClick(View v) {
        yeni_ietisim_kayit(true);
    }*/

    public void iletisimIleriOnclik(View v) {
        yeni_ietisim_kayit(false);
    }

    public void urunGuncelleClick(View v) {
        yeni_ietisim_kayit(true);
    }

    void yeni_ietisim_kayit(final Boolean tamamla) {
        if (secilenUrun == null ||
                birim_fiyati_edittw.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(SiparisDetayKayitActivity.this, "Lütfen zorunlu alanları eksiksiz doldurunuz.", false);

        } else {
            final SiparisDetay siparisDetay = new SiparisDetay();
            if (!birim_fiyati_edittw.getText().toString().equalsIgnoreCase(""))
                siparisDetay.setBirimFiyat(Double.parseDouble(birim_fiyati_edittw.getText().toString()));

            siparisDetay.setOlcuBirimMid(secilenOlcuBirim.getMid());
            siparisDetay.setOlcuBirimId(secilenOlcuBirim.getId());
            if (!miktar_edittw.getText().toString().equalsIgnoreCase(""))
                siparisDetay.setMiktar(Double.parseDouble(miktar_edittw.getText().toString()));
            siparisDetay.setMustId(Long.valueOf(siparisMid));
            siparisDetay.setSiparisMid(Long.valueOf(siparisMid));
            siparisDetay.setUrunMid(secilenUrun.getMid());
            siparisDetay.setUrunId(secilenUrun.getId());
            siparisDetay.setSenkronEdildi(false);
            if(siparisDetayMid != null)
                siparisDetay.setMid(Long.valueOf(siparisDetayMid));

            db.siparisDao().updateSiparisSenkronDurum(Long.valueOf(siparisMid), false);

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
                                    //startActivityForResult(i,1);
                                } else {
                                    Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayKayitActivity.class);
                                    i.putExtra("siparisMid", siparisMid);
                                    i.putExtra("subeId", subeId);
                                    i.putExtra("subeMid", subeMid);
                                    finish();
                                   // startActivityForResult(i,1);

                                    //startActivity(i);
                                }

                            }
                            if (siparisDetayMid != null && finalYeniKayitSiparisMid == 1) {

                                if (tamamla) {
                                    MessageBox.showAlert(SiparisDetayKayitActivity.this, "Güncelleme işlemi başarılı..\n", false);
                                    Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayActivity.class);
                                    i.putExtra("siparisMid", String.valueOf(siparisMid));
                                    finish();
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayKayitActivity.class);
                                    i.putExtra("siparisMid", siparisMid);
                                    i.putExtra("siparisMid", siparisMid);
                                    i.putExtra("subeId", subeId);
                                    i.putExtra("subeMid", subeMid);
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

        urun_guncelle_button.setVisibility(View.VISIBLE);
        urun_ekle_button.setVisibility(GONE);
        eklenen_urunler_recyclerview.setVisibility(GONE);
        siparisUrunEklemeLinear.setVisibility(GONE);


        List<SiparisDetay> updateKayitList = db.siparisDetayDao().getSiparisDetayForMid(siparisDetayMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid().toString().equalsIgnoreCase(siparisDetayMid.toString())) {
            birim_fiyati_edittw.setText(updateKayitList.get(0).getBirimFiyat() != null ? updateKayitList.get(0).getBirimFiyat().toString() : "");
            //  olcu_birim_spinne.setText(updateKayitList.get(0).getKapiNo());


            for (Urun item : db.urunDao().getUrunAll()) {
                if (item != null && item.getId() != null && updateKayitList.get(0).getUrunId() != null &&
                        item.getId().toString().equalsIgnoreCase(updateKayitList.get(0).getUrunId().toString())) {
                    sip_urun_adi_spinner.setSelection(urunStringList.indexOf(item.getUrunAdi()));

                }

            }

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

            miktar_edittw.setText(updateKayitList.get(0).getMiktar() != null ? updateKayitList.get(0).getMiktar().toString() : "");



        }
    }

    Double toplamEklenenTurar = 0.0;

    void urun_ekle_button() {
        urun_ekle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (secilenUrun != null && !miktar_edittw.getText().toString().equalsIgnoreCase("")
                        && !olcu_birim_autocomplete.getText().toString().equalsIgnoreCase("") && !birim_fiyati_edittw.getText().toString().equalsIgnoreCase("")) {


                    final SiparisDetay siparisDetay = new SiparisDetay();
                    if (!birim_fiyati_edittw.getText().toString().equalsIgnoreCase(""))
                        siparisDetay.setBirimFiyat(Double.parseDouble(birim_fiyati_edittw.getText().toString()));

                    siparisDetay.setOlcuBirimMid(secilenOlcuBirim.getMid());
                    siparisDetay.setOlcuBirimId(secilenOlcuBirim.getId());
                    if (!miktar_edittw.getText().toString().equalsIgnoreCase(""))
                        siparisDetay.setMiktar(Double.parseDouble(miktar_edittw.getText().toString()));
                    siparisDetay.setMustId(Long.valueOf(siparisMid));
                    siparisDetay.setSiparisMid(Long.valueOf(siparisMid));
                    siparisDetay.setUrunMid(secilenUrun.getMid());
                    siparisDetay.setUrunId(secilenUrun.getId());
                    siparisDetay.setSenkronEdildi(false);

                    siparisDetayListTemp.add(siparisDetay);


                    toplamEklenenTurar = 0.0;

                    for (SiparisDetay item : siparisDetayListTemp) {
                        toplamEklenenTurar = toplamEklenenTurar + (item.getMiktar() * item.getBirimFiyat());

                    }
                    toplam_tutar.setText("   " + String.valueOf(toplamEklenenTurar) + " TL");


                    // MessageBox.showAlert(SiparisDetayKayitActivity.this, "Ürünler başarılı bir şekilde eklenmiştir.\n", false);

                    siparis_detay_adapter = new EklenenUrunlerAdapter(SiparisDetayKayitActivity.this, siparisDetayListTemp);
                    eklenen_urunler_recyclerview.setHasFixedSize(true);
                    eklenen_urunler_recyclerview.setLayoutManager(new LinearLayoutManager(SiparisDetayKayitActivity.this));
                    eklenen_urunler_recyclerview.setAdapter(siparis_detay_adapter);
                    siparis_detay_adapter.notifyDataSetChanged();
                } else
                    MessageBox.showAlert(SiparisDetayKayitActivity.this, "Lütfen tüm alanları eksiksiz doldurunuz.", false);
            }


        });
    }

    void kaydet_button() {
        btn_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.siparisDao().updateSiparisSenkronDurum(Long.valueOf(siparisMid), false);
                List<Long> yeniKayitSiparisMidList;
                if (siparisDetayMid == null)
                    yeniKayitSiparisMidList = db.siparisDetayDao().setSiparisDetayList(siparisDetayListTemp);
                Intent i = new Intent(SiparisDetayKayitActivity.this, SiparisDetayActivity.class);
                i.putExtra("siparisMid", String.valueOf(siparisMid));
             //   startActivityForResult(i,0);
                setResult(RESULT_OK,i);
                finish();

               // finish();
               // startActivity(i);
            }
        });


    }

    public void urun_sil_button(Integer position) {
        if (position != null) {
            toplamEklenenTurar = toplamEklenenTurar - (siparisDetayListTemp.get(position).getMiktar() * siparisDetayListTemp.get(position).getBirimFiyat());
            siparisDetayListTemp.remove(siparisDetayListTemp.get(position));
            MessageBox.showAlert(SiparisDetayKayitActivity.this, "Seçili ürün çıkarılmıştır.\n", false);
            toplam_tutar.setText("   " + toplamEklenenTurar + " TL");
        }
        siparis_detay_adapter = new EklenenUrunlerAdapter(SiparisDetayKayitActivity.this, siparisDetayListTemp);
        eklenen_urunler_recyclerview.setHasFixedSize(true);
        eklenen_urunler_recyclerview.setLayoutManager(new LinearLayoutManager(SiparisDetayKayitActivity.this));
        eklenen_urunler_recyclerview.setAdapter(siparis_detay_adapter);
        siparis_detay_adapter.notifyDataSetChanged();

    }

    public void urun_artir_azalt(Boolean artiyorMu, Integer position) {
        if (artiyorMu) {
            siparisDetayListTemp.get(position).setMiktar(siparisDetayListTemp.get(position).getMiktar() + 1);
            toplamEklenenTurar = toplamEklenenTurar + (siparisDetayListTemp.get(position).getBirimFiyat());
        } else {
            siparisDetayListTemp.get(position).setMiktar(siparisDetayListTemp.get(position).getMiktar() - 1);

            if (position != null) {
                toplamEklenenTurar = toplamEklenenTurar - (siparisDetayListTemp.get(position).getBirimFiyat());
                if (siparisDetayListTemp.get(position).getMiktar() < 1) {
                    siparisDetayListTemp.remove(siparisDetayListTemp.get(position));
                    MessageBox.showAlert(SiparisDetayKayitActivity.this, "Seçili ürün çıkarılmıştır.\n", false);
                }
            }

        }
        toplam_tutar.setText("   " + toplamEklenenTurar + " TL");
        siparis_detay_adapter = new EklenenUrunlerAdapter(SiparisDetayKayitActivity.this, siparisDetayListTemp);
        eklenen_urunler_recyclerview.setHasFixedSize(true);
        eklenen_urunler_recyclerview.setLayoutManager(new LinearLayoutManager(SiparisDetayKayitActivity.this));
        eklenen_urunler_recyclerview.setAdapter(siparis_detay_adapter);
        siparis_detay_adapter.notifyDataSetChanged();

    }
}



