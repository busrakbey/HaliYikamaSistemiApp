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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
            urunListesiVarMi = false, urunZorunluMu = false;
    Boolean teslimEdildiBilgisi = false, tesiimEdildiZorunluMu =false,
            tahsilEdilecekTutarBilgisi = false , tahsilEdilecekTutarZorunluMu = false;
    String gorevNotu = "";
    JSONObject jsonObjectGorevTamamla;
    LinearLayout not_linear,spinenr_linerar;
    GorevFomBilgileri not_item, urun_item, teslim_edildi_item, tahsil_edildi_item, tahasilatTutari_item,
            teslim_alinacak_item, sube_item, musteri_item;


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


        Intent intent = getIntent();
        gorevMid = intent.getStringExtra("gorevMid");
        gorevId = intent.getStringExtra("gorevId");
        siparisId = intent.getStringExtra("siparisId");

        List<GorevFomBilgileri> formBilgiList = db.gorevFomBilgileriDao().getGorevId(Long.valueOf(gorevId));
        for (GorevFomBilgileri item : formBilgiList) {
            if (item.getId().equalsIgnoreCase("notlar")) {
                notGirilecekMi = true;
                not_item = item;
            }
            if (item.getId().equalsIgnoreCase("urunListesi")){
                urunListesiVarMi = true;
                urun_item=item;
            }
            if (item.getId().equalsIgnoreCase("urunListesi")){
                urunListesiVarMi = true;
                urun_item=item;
            }
            if (item.getId().equalsIgnoreCase("teslimEdildi")){
                teslim_edildi_item=item;
            }
            if (item.getId().equalsIgnoreCase("tahsilEdilecekTutar")){
                tahsil_edildi_item=item;
            }
            if (item.getId().equalsIgnoreCase("tahasilatTutari")){
                tahasilatTutari_item =item;
            }
            if (item.getId().equalsIgnoreCase("teslimAlinacak")){
                teslim_alinacak_item =item;
            }



            if (notGirilecekMi == true && item.getRequired() == true)
                notZorunluMu = true;


            if (urunListesiVarMi == true && item.getRequired() == true)
                urunZorunluMu = true;


        }

        if (urunListesiVarMi)
            get_list();


        gorev_tamamla_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MusteriGorevlerimDetayActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.not_popup_window, null);
                final TextView call_message = (TextView) mView.findViewById(R.id.call_message);
                final Button vazgec_button = (Button) mView.findViewById(R.id.not_vazgec_button);
                final Button kaydet_button = (Button) mView.findViewById(R.id.not_kaydet_button);
                final TextView tittle = (TextView) mView.findViewById(R.id.call_tittle);
                not_linear = (LinearLayout) mView.findViewById(R.id.gorev_not);
                spinenr_linerar = (LinearLayout) mView.findViewById(R.id.gorev_spinner);
                final Spinner spinner= (Spinner) mView.findViewById(R.id.gorev_tamamla_spinner);

                if(teslim_edildi_item != null){
                    spinenr_linerar.setVisibility(View.VISIBLE);
                    if(teslim_edildi_item.getFormValues() != null){
                        try {
                            jsonObjectGorevTamamla = new JSONObject(teslim_edildi_item.getFormValues());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (urunListesiVarMi && urunZorunluMu && siparisDetayList.size() == 0) {
                    MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Ürün listesi olmadan görev tamamlanamaz..", false);
                    return;
                }
                if(!jsonObjectGorevTamamla.toString().equalsIgnoreCase("")){
                    spinenr_linerar.setVisibility(View.VISIBLE);
                    List<String> list2 = new ArrayList<String>();
                    list2.add("Seçiniz..");

                    for(int i = 0; i<jsonObjectGorevTamamla.names().length(); i++){
                        try {
                            list2.add((String) jsonObjectGorevTamamla.get(jsonObjectGorevTamamla.names().getString(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(MusteriGorevlerimDetayActivity.this,
                            android.R.layout.simple_spinner_item, list2);
                    dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter2);

                    if(spinner.getSelectedItemPosition() == 0){
                        MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Teslimat durumunu seçmeden görev tamamlanamaz..", false);
                        return;
                    }

                }

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
                                    gorevTamamlaPostService(Long.valueOf(gorevId));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                } else {

                    try {
                        gorevTamamlaPostService(Long.valueOf(gorevId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }


    List<Gorevler> gorevlerList;

    void get_list() {
        gorevlerList = db.gorevlerDao().getGorevForMid(Long.valueOf(gorevMid));
        if (gorevlerList.size() > 0) {
            List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
            if (siparisList.size() > 0) {
                siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
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
                        case R.id.nav_home:
                            i = new Intent(MusteriGorevlerimDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;
                        case R.id.nav_musteri:
                            i = new Intent(MusteriGorevlerimDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                        case R.id.nav_siparis:
                            i = new Intent(MusteriGorevlerimDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;
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
            toolbarTextView.setText("Görev Detayları");
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

    public void gorevTamamlaPostService(final Long gorevId) throws Exception {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();

        JSONObject disObje = new JSONObject();
        JSONArray urunArray = new JSONArray();
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
                icObje.put("toplamTutar", null);
                icObje.put("musteriNotu", null);
                urunArray.put(icObje);
            }
        disObje.put("urunListesi", urunArray);
        Call<String> call = refrofitRestApi.postGorevTamamla("fw/process/completeTask/" + gorevId, OrtakFunction.authorization, OrtakFunction.tenantId, disObje.toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MusteriGorevlerimDetayActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
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


}