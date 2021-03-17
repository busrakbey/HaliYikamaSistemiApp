package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.GorevlerAdapter;
import com.example.haliyikamaapp.Adapter.GorevlerDetayAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.GorevFomBilgileri;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusteriGorevlerimDetayActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    RecyclerView recyclerView;
    String gorevMid, gorevId, siparisId;
    GorevlerDetayAdapter gorevlerAdapter;
    List<SiparisDetay> siparisDetayList;
    EditText not_edittext;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton gorev_tamamla_button;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    Boolean notGirilecekMi = false, notZorunluMu = false,
            urunListesiVarMi = false, urunZorunluMu = false, kaynakGirilecekMi = false, hesapKapatilacakMi = false;
    Boolean teslimEdildiBilgisi = false, tesiimEdildiZorunluMu = false,
            tahsilEdilecekTutarBilgisi = false, tahsilEdilecekTutarZorunluMu = false;
    String gorevNotu = "", secilenTeslimEtDurumu = "", girilenTahsilatTutari = "", gorevTamamlamaNotu = "";
    JSONObject jsonObjectGorevTamamla;
    LinearLayout not_linear, spinenr_linerar;
    GorevFomBilgileri not_item, urun_item, teslim_edildi_item, tahsil_edildi_item, tahsilatTutari_item,
            teslim_alinacak_item, sube_item, musteri_item, hesabi_kapat_item, kaynak_item;
    Button urunEkleButton;
    List<Gorevler> gorevList;
    Boolean hesapKapatCheckbox;
    Kaynak secili_kaynak;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.musteri_gorevlerim_detay_activity);
        initToolBar();
        init_item();


    }

    void init_item() {
        recyclerView = (RecyclerView) findViewById(R.id.gorevlerim_rcyc);


        db = HaliYikamaDatabase.getInstance(MusteriGorevlerimDetayActivity.this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_musterigorevlerim);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        gorev_tamamla_button = (FloatingActionButton) findViewById(R.id.btnAdd);
        progressDoalog = new ProgressDialog(MusteriGorevlerimDetayActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        db = HaliYikamaDatabase.getInstance(MusteriGorevlerimDetayActivity.this);
        urunEkleButton = (Button) findViewById(R.id.gorevde_urun_ekle_button);
        urunEkleButton.setVisibility(View.GONE);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MusteriGorevlerimDetayActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.not_popup_window, null);
        final EditText call_message = (EditText) mView.findViewById(R.id.call_message);
        final Button vazgec_button = (Button) mView.findViewById(R.id.not_vazgec_button);
        final Button kaydet_button = (Button) mView.findViewById(R.id.not_kaydet_button);
        final TextView tittle = (TextView) mView.findViewById(R.id.call_tittle);
        final EditText tahsilatTutari = (EditText) mView.findViewById(R.id.gorev_tahsilat_tutari);
        not_linear = (LinearLayout) mView.findViewById(R.id.gorev_not);
        spinenr_linerar = (LinearLayout) mView.findViewById(R.id.gorev_teslim_et_linear);
        final Spinner spinner_teslim_edildi_mi = (Spinner) mView.findViewById(R.id.gorev_tamamla_spinner);
        final CheckBox gorev_hesap_kapat = (CheckBox) mView.findViewById(R.id.gorev_hesap_kapat);
        final EditText gorev_teslim_et_notlar = (EditText) mView.findViewById(R.id.gorev_teslim_edilecek_not);
        final LinearLayout gorev_araca_yukle_linear = (LinearLayout) mView.findViewById(R.id.gorev_araca_yukle_linear);
        final Spinner gorev_kaynak_spinner = (Spinner) mView.findViewById(R.id.gorev_kaynak_spinner);


        Intent intent = getIntent();
        gorevMid = intent.getStringExtra("gorevMid");
        gorevId = intent.getStringExtra("gorevId");
        siparisId = intent.getStringExtra("siparisId");

        if (gorevId != null) {
            gorevList = db.gorevlerDao().getGorevForId(Long.valueOf(gorevId));
        }


        if ((gorevList != null && gorevList.size() > 0) && (!gorevList.get(0).getTaskName().equalsIgnoreCase("Araca Yükle")
                || !gorevList.get(0).getTaskName().equalsIgnoreCase("Yikama"))) {
            List<GorevFomBilgileri> formBilgiList = db.gorevFomBilgileriDao().getGorevId(Long.valueOf(gorevId));
            for (GorevFomBilgileri item : formBilgiList) {
                if (item.getId().equalsIgnoreCase("notlar")) {
                    notGirilecekMi = true;
                    not_item = item;
                    if (notGirilecekMi == true && item.getRequired() == true)
                        notZorunluMu = true;
                }

                if (item.getId().equalsIgnoreCase("urunListesi")) {
                    urunEkleButton.setVisibility(View.VISIBLE);
                    urunListesiVarMi = true;
                    urun_item = item;
                }
                if (item.getId().equalsIgnoreCase("teslimEdildi")) {
                    teslim_edildi_item = item;
                    spinenr_linerar.setVisibility(View.VISIBLE);

                }
                if (item.getId().equalsIgnoreCase("tahsilEdilecekTutar")) {
                    tahsil_edildi_item = item;
                }
                if (item.getId().equalsIgnoreCase("tahsilatTutari")) {
                    tahsilatTutari_item = item;
                }
                if (item.getId().equalsIgnoreCase("teslimAlinacak")) {
                    teslim_alinacak_item = item;
                }

                if (item.getId().equalsIgnoreCase("hesabiKapat")) {
                    hesabi_kapat_item = item;
                    hesapKapatilacakMi = true;
                }

                if (item.getId().equalsIgnoreCase("kaynak")) {
                    kaynak_item = item;
                    kaynakGirilecekMi = true;
                }

                if (urunListesiVarMi == true && item.getRequired() == true)
                    urunZorunluMu = true;


            }
        }

        if (urunListesiVarMi)
            get_list();


        gorev_tamamla_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (teslim_edildi_item != null) {
                    spinenr_linerar.setVisibility(View.VISIBLE);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    vazgec_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    kaydet_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gorevNotu = call_message.getText().toString();
                            if (spinner_teslim_edildi_mi.getSelectedItemPosition() == 0) {
                                MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Teslimat durumunu seçmeden görev tamamlanamaz..", false);
                                return;
                            }
                            if (spinner_teslim_edildi_mi.getSelectedItemPosition() == 1)
                                secilenTeslimEtDurumu = "Evet";
                            if (spinner_teslim_edildi_mi.getSelectedItemPosition() == 2)
                                secilenTeslimEtDurumu = "Hayır";

                            if (tahsilatTutari.getText().toString().equalsIgnoreCase("")) {
                                MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Tahsilat tutarını girmeden görev tamamlanamaz..", false);
                                return;
                            } else
                                girilenTahsilatTutari = tahsilatTutari.getText().toString();

                            hesapKapatCheckbox = gorev_hesap_kapat.isChecked() == true ? true : false;
                            gorevTamamlamaNotu = gorev_teslim_et_notlar.getText().toString();
                            dialog.dismiss();
                            try {
                                gorevTamamlaPostService(Long.valueOf(gorevId),null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    });

                    if (kaynak_item.getFormValues() != null) {

                        secili_kaynak = null;
                        List<Kaynak> kaynakList = new ArrayList<Kaynak>();
                        final List<String> kaynakListString = new ArrayList<String>();
                        gorev_araca_yukle_linear.setVisibility(View.VISIBLE);
                        kaynakList.add(null);
                        kaynakList = db.kaynakDao().getkaynakAll();
                        kaynakListString.add("Kaynak");
                        for (Kaynak item : kaynakList) {
                            kaynakListString.add(item.getKaynakAdi());
                        }

                        ArrayAdapter<String> dataAdapter_kaynak = new ArrayAdapter<String>(MusteriGorevlerimDetayActivity.this, android.R.layout.simple_spinner_item, kaynakListString);
                        dataAdapter_kaynak.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        gorev_kaynak_spinner.setAdapter(dataAdapter_kaynak);
                        gorev_kaynak_spinner.setSelection(0);

                        final List<Kaynak> finalKaynakList = kaynakList;
                        gorev_kaynak_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    String valInfo = kaynakListString.get(position);
                                    if (valInfo != null) {
                                        secili_kaynak = finalKaynakList.get(position - 1);
                                    }
                                } else {
                                    secili_kaynak = null;
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                        spinenr_linerar.setVisibility(View.VISIBLE);
                        mBuilder.setView(mView);
                        final AlertDialog dialog2 = mBuilder.create();
                        dialog2.show();

                        vazgec_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog2.dismiss();
                            }
                        });

                        kaydet_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                secili_kaynak.getId();
                                dialog.dismiss();
                                try {
                                    gorevTamamlaPostService(Long.valueOf(gorevId),null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        });
                    }

                    if (teslim_edildi_item.getFormValues() != null) {
                        try {
                            jsonObjectGorevTamamla = new JSONObject(teslim_edildi_item.getFormValues());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!jsonObjectGorevTamamla.toString().equalsIgnoreCase("")) {
                        spinenr_linerar.setVisibility(View.VISIBLE);
                        List<String> list2 = new ArrayList<String>();
                        list2.add("Seçiniz..");

                        for (int i = 0; i < jsonObjectGorevTamamla.names().length(); i++) {
                            try {
                                list2.add((String) jsonObjectGorevTamamla.get(jsonObjectGorevTamamla.names().getString(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(MusteriGorevlerimDetayActivity.this,
                                android.R.layout.simple_spinner_item, list2);
                        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_teslim_edildi_mi.setAdapter(dataAdapter2);


                    }
                }

                if (urunListesiVarMi && urunZorunluMu && siparisDetayList.size() == 0) {
                    MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Ürün listesi olmadan görev tamamlanamaz..", false);
                    return;
                }
                if (urunListesiVarMi && urunZorunluMu && siparisDetayList.size() > 0) {
                    if (not_item != null) {
                        not_linear.setVisibility(View.VISIBLE);
                        tittle.setText("Görevi Tamamla");
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        vazgec_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        kaydet_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gorevNotu = call_message.getText().toString();
                                if (notZorunluMu && gorevNotu.equalsIgnoreCase("")) {
                                    MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Not doldurulmadan görev tamamlanamaz", false);
                                    return;
                                } else {
                                    dialog.dismiss();
                                    try {
                                        gorevTamamlaPostService(Long.valueOf(gorevId),null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                    }

                   /* try {
                        gorevTamamlaPostService(Long.valueOf(gorevId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }


           /*else {

                    try {
                        gorevTamamlaPostService(Long.valueOf(gorevId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/


            }
        });
    }


    List<Gorevler> gorevlerList;

    void get_list() {
        gorevlerList = db.gorevlerDao().getGorevForId(Long.valueOf(gorevId));
        if (gorevlerList.size() > 0) {
            List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
            if (siparisList.size() > 0) {
                siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
                if (siparisDetayList.size() == 0)
                    siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());
            }
        }

        gorevlerAdapter = new GorevlerDetayAdapter(MusteriGorevlerimDetayActivity.this, siparisDetayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MusteriGorevlerimDetayActivity.this));
        recyclerView.setAdapter(gorevlerAdapter);


    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    Fragment selectedFragment = null;
                    Intent i = null;
                    switch (item.getItemId()) {
                       /* case R.id.nav_home:
                            i = new Intent(MusteriGorevlerimDetayActivity.this, MainActivity2.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musteri:
                            i = new Intent(MusteriGorevlerimDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                       /* case R.id.nav_siparis:
                            i = new Intent(MusteriGorevlerimDetayActivity.this, MainActivity2.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musterigorevlerim:
                            i = new Intent(MusteriGorevlerimDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri_görevlerim");
                            startActivity(i);
                            break;
                    }
                  /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/
                    return true;
                }
            };

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Ürün Listesi");
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


    String gelenGorevList = null;
    public void gorevTamamlaPostService(final Long gorevId, final Long kaynakId) throws Exception {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        JSONObject disObje = new JSONObject();
        final JSONArray urunArray = new JSONArray();

        if (gorevlerList != null && gorevlerList.size() != 0 && gorevlerList.get(0).getTaskName().equalsIgnoreCase("TeslimAl")) {
            disObje.put("notlar", gorevNotu);
            if (siparisDetayList != null && siparisDetayList.size() > 0)
                for (SiparisDetay item : siparisDetayList) {
                    JSONObject icObje = new JSONObject();
                    icObje.put("id", item.getId());
                    icObje.put("siparisId", item.getSiparisId());
                    icObje.put("urunId", item.getUrunId());
                    icObje.put("urunAdi", db.urunDao().getUrunForId(item.getUrunId()).get(0).getUrunAdi());
                    icObje.put("olcuBirimId", item.getOlcuBirimId());
                    icObje.put("olcuBirimAdi", db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).get(0).getOlcuBirimi());
                    icObje.put("birimFiyat", item.getBirimFiyat());
                    icObje.put("miktar", item.getMiktar());
                    icObje.put("toplamTutar", item.getBirimFiyat() * item.getMiktar());
                    icObje.put("musteriNotu", null);
                    urunArray.put(icObje);
                }
            disObje.put("urunListesi", urunArray);
        }

        if (!girilenTahsilatTutari.equalsIgnoreCase("")) {
            disObje.put("tahsilatTutari", !girilenTahsilatTutari.equalsIgnoreCase("") ? girilenTahsilatTutari : 0);
            disObje.put("teslimEdildi", secilenTeslimEtDurumu);
            disObje.put("tahsilEdilecekTutar", null);
            disObje.put("hesabiKapat", hesapKapatCheckbox == false ? "" : true);
            disObje.put("teslimatNotu", gorevTamamlamaNotu);

        }

        if (secili_kaynak != null || kaynakId != null) {
            disObje.put("kaynak", secili_kaynak.getId() != null ? secili_kaynak.getId() : null);


        }
        Call<String> call = refrofitRestApi.postGorevTamamla("fw/process/completeTask/" + gorevId, OrtakFunction.authorization, OrtakFunction.tenantId, disObje.toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                secili_kaynak = null;
                if (!response.isSuccessful()) {

                    progressDoalog.dismiss();
                    //  MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    /*if (urunArray != null && urunArray.length() > 0) {
                        db.siparisDao().updateSiparisDurumu(gorevId, "Yıkanacak");
                    }

                    if (gorevlerList.get(0).getName().equalsIgnoreCase("TeslimEt")){
                        db.siparisDao().updateSiparisDurumu(gorevId, )
                    }*/
                    MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Görev başarıyla tamamlanmıştır..", false);
                    finish();

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }

        });

    }

    public void gorevUrunEkleOnClick(View v) {
        List<Siparis> siparisList = null;
        gorevlerList = db.gorevlerDao().getGorevForId(Long.valueOf(gorevId));
        if (gorevlerList.size() > 0) {
             siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
            if (siparisList.size() > 0)
                siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
            if (siparisDetayList.size() == 0)
                siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());

        }
        Intent i = new Intent(MusteriGorevlerimDetayActivity.this, SiparisDetayActivity.class);
        // i.putExtra("gelenPage", "sipariş");
        i.putExtra("siparisId", siparisId);
        i.putExtra("subeMid", siparisList.get(0).getSubeMid()  != null ? siparisList.get(0).getSubeMid().toString() : null);
        i.putExtra("subeId" , siparisList.get(0).getSubeId() != null ? siparisList.get(0).getSubeId().toString() : null);
        i.putExtra("siparisMid " ,siparisList != null ? siparisList.get(0).getMid().toString(): null);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (urunListesiVarMi)
            get_list();
    }


}