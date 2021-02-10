package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.haliyikamaapp.Adapter.MusteriAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Bolge;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.MusteriTuru;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.DefaultException;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RSOperator;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    BottomNavigationView bottomNavigationView;
    String gelenPage, telephoneNumber;
    HaliYikamaDatabase db;
    boolean phoneDialed = false;
    RefrofitRestApi refrofitRestApi;
    ProgressDialog progressDoalog;
    MusteriAdapter adapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);

        init_item();
        if (telephoneNumber != null)
            getCallLogs();
//        permissonControl();

        if (savedInstanceState == null && gelenPage == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            initToolBar("Anasayfa");
            ekleButon.setVisibility(View.INVISIBLE);

        }
        if (InternetKontrol()) {
            getAuth();
            getUrunListFromService();
            getIlAndIlceFromService();
            getBolgeList();
            try {
                OrtakFunction.GetLocation(MainActivity.this, getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public boolean InternetKontrol() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null
                && manager.getActiveNetworkInfo().isAvailable()
                && manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    Fragment selectedFragment = null;
    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            initToolBar("Anasayfa");
                            ekleButon.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.nav_musteri:
                            selectedFragment = new MusteriFragment();
                            initToolBar("Müşteri");
                            ekleButon.setVisibility(View.VISIBLE);
                            click_ekle_button("Müşteri");
                            break;
                        case R.id.nav_siparis:
                            selectedFragment = new SiparisFragment();
                            initToolBar("Sipariş");
                            ekleButon.setVisibility(View.VISIBLE);
                            click_ekle_button("Sipariş");
                            break;
                        case R.id.nav_musterigorevlerim:
                            selectedFragment = new MusteriGorevlerimFragment();
                            initToolBar("Görevlerim");
                            ekleButon.setVisibility(View.VISIBLE);
                            break;
                    }

                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.commit();
                   /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/
                    return true;
                }
            };


    TextView toolbarTextView;

    public void initToolBar(final String tittle) {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            toolbar.setNavigationIcon(R.drawable.left);
            setSupportActionBar(toolbar);

            toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText(tittle);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tittle.equalsIgnoreCase("Anasayfa")) {
                      /*  Fragment selectedFragment = new HomeFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        transaction.commit();*/

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomeFragment()).commit();
                        toolbarTextView.setText("Anasayfa");
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Uyarı");
                        builder.setMessage("Devam etmek istiyor musunuz?");
                        builder.setNegativeButton("Hayır", null);
                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        builder.show();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem myActionMenuItem = menu.findItem(R.id.search);
        MenuItem userProfile = menu.findItem(R.id.setting);
        MenuItem exit_user = menu.findItem(R.id.action_exit_user);

        exit_user.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sistem");
                builder.setMessage("Çıkış yapılacaktır. Devam etmek istiyor musunuz?");
                builder.setNegativeButton("Hayır", null);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.userDao().deleteUserAll();
                        db.sIlceDao().deleteIlceAll();
                        db.sIlDao().deleteIlAll();
                        db.musteriDao().deleteMusteriAll();
                        db.siparisDao().deleteSiparisAll();
                        db.gorevlerDao().deleteGorevAll();
                        db.gorevFomBilgileriDao().deleteGorevAll();
                        db.subeDao().deleteSubeAll();
                        db.urunDao().deleteUrunAll();
                        db.urunFiyatDao().deleteUrunFiyatAll();
                        db.urunSubeDao().deleteUrunSubeAll();
                        db.olcuBirimDao().deleteOlcuBirimAll();
                        db.sTenantDao().deleteSTenantAll();
                        db.permissionsDao().deletePermissionsAll();
                        db.siparisDetayDao().deleteSiparisDetayAll();
                        db.bolgeDao().deleteBolgeAll();
                        db.musteriTuruDao().deleteMusteriTuruAll();
                        db.authToken().deleteTokenAll();
                        finish();
                        Intent a = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(a);
                        // System.exit(0);


                    }
                });
                builder.show();
                return false;
            }
        });


        if (toolbarTextView.getText().toString().equalsIgnoreCase("Müşteri") || toolbarTextView.getText().toString().equalsIgnoreCase("Sipariş")) {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(myActionMenuItem);
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setOnQueryTextListener(this);
            myActionMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    // mImDbAdapter.setSearchResult(mListImDb);
                    return true; // Return true to collapse action view

                }


            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryString) {

                    if (toolbarTextView.getText().toString().equalsIgnoreCase("Müşteri")) {
                        MusteriFragment fragment = (MusteriFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fragment.adapter.getFilter().filter(queryString);

                    }


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String queryString) {
                    if (toolbarTextView.getText().toString().equalsIgnoreCase("Müşteri")) {
                        MusteriFragment fragment = (MusteriFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fragment.adapter.getFilter().filter(queryString);

                    }
                    return false;
                }
            });
        } else {
            myActionMenuItem.setVisible(false);
        }
        return true;

    }

    void init_item() {
        ekleButon = (FloatingActionButton) findViewById(R.id.btnAdd);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        db = HaliYikamaDatabase.getInstance(MainActivity.this);

        telephoneNumber = getIntent().getStringExtra("number");
        gelenPage = getIntent().getStringExtra("gelenPage");
        if (gelenPage != null) {
            if (gelenPage.equalsIgnoreCase("müşteri"))
                bottomNavigationView.setSelectedItemId(R.id.nav_musteri);

            if (gelenPage.equalsIgnoreCase("sipariş"))
                bottomNavigationView.setSelectedItemId(R.id.nav_siparis);

            if (gelenPage.equalsIgnoreCase("anasayfa"))
                bottomNavigationView.setSelectedItemId(R.id.nav_home);

            if (gelenPage.equalsIgnoreCase("müşteri_görevlerim"))
                bottomNavigationView.setSelectedItemId(R.id.nav_musterigorevlerim);
        }


        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
    }


    void click_ekle_button(final String page) {
        //  ekleButon.setBackgroundTintList(new ColorStateList(states, colors));
        ekleButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (page.equalsIgnoreCase("Müşteri")) {
                    Intent musteri = new Intent(getApplication().getApplicationContext(), MusteriKayitActivity.class);
                    startActivity(musteri);
                }
                if (page.equalsIgnoreCase("Sipariş")) {
                    Intent siparis = new Intent(getApplication().getApplicationContext(), SiparisKayitActivity.class);
                    startActivity(siparis);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sistem");
        builder.setMessage("Uygulama kapatılacaktır. Devam etmek istiyor musunuz?");
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                System.exit(0);

            }
        });
        builder.show();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    private void getCallLogs() {

        ContentResolver cr = getBaseContext().getContentResolver();

        if (telephoneNumber != null) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.popup_window, null);
            final TextView call_tittle = (TextView) mView.findViewById(R.id.call_tittle);
            final TextView call_message = (TextView) mView.findViewById(R.id.call_message);
            final Button call_button = (Button) mView.findViewById(R.id.call_button);
            final Button call_button2 = (Button) mView.findViewById(R.id.call_button2);
            final ImageView cancel_button = (ImageView) mView.findViewById(R.id.call_close);
            call_tittle.setText("ÇAĞRI BİLGİ EKRANI");


            if (telephoneNumber.length() == 13) {
                telephoneNumber = telephoneNumber.substring(2, 13);
                List<Musteri> allMusteriList = db.musteriDao().getMusteriAll();
                Boolean telefonNumarasiVarMi = false;
                for (final Musteri item : allMusteriList) {
                    if (item.getTelefonNumarasi().equalsIgnoreCase(telephoneNumber)) {
                        telefonNumarasiVarMi = true;
                        call_button2.setVisibility(View.VISIBLE);
                        call_button2.setText("Müşteri İşlemleri");
                        call_button.setText("Sipariş Oluştur");
                        call_message.setText("Müşteri Adı : " + item.getMusteriAdi() + " " + item.getMusteriSoyadi());
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        call_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, SiparisKayitActivity.class);
                                intent.putExtra("musteriMid", String.valueOf(item.getMid()));
                                finish();
                                startActivity(intent);
                            }
                        });
                        cancel_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        call_button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, MusteriKayitActivity.class);
                                intent.putExtra("musteriMid", item.getMid().toString());
                                finish();
                                startActivity(intent);
                            }
                        });


                    }
                }
                if (!telefonNumarasiVarMi) {
                    call_button2.setVisibility(View.GONE);
                    call_button.setText("Müşteri Oluştur");
                    call_message.setText("Telefon No : " + telephoneNumber);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    final String finalPhNumber = telephoneNumber;
                    call_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, MusteriKayitActivity.class);
                            intent.putExtra("number", finalPhNumber);
                            finish();
                            startActivity(intent);
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }


            }
        }

    }


    void permissonControl() {
        OrtakFunction.permission_control(MainActivity.this, MainActivity.this);
    }


    void getAuth() {
        OrtakFunction.tokenControl(MainActivity.this);
        // if (OrtakFunction.tokenList == null || OrtakFunction.tokenList.size() == 0)
        // OrtakFunction.getTtoken(MainActivity.this);
    }

    List<Urun> gelenUrunList = null;
    List<Sube> gelenSubeList = null;
    List<String> gelenMusteriTuruList = null;

    void getUrunListFromService() {
        progressDoalog.show();

        /////////ürün listesi alınıyor /////
        Call<List<Urun>> call = refrofitRestApi.getUrunList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Urun>>() {
            @Override
            public void onResponse(Call<List<Urun>> call, Response<List<Urun>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Ürün bilgileri alınırken hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunList = response.body();
                    if (gelenUrunList != null && gelenUrunList.size() > 0) {
                        //  db.urunDao().deleteUrunAll();
                        for (Urun item : gelenUrunList) {
                            List<Urun> urunVarMi = db.urunDao().getUrunForId(item.getId());
                            if (urunVarMi.size() > 0) {
                                item.setMid(urunVarMi.get(0).getMid());
                                db.urunDao().updateUrun(item);
                            } else
                                db.urunDao().setUrun(item);
                        }
                        //final List<Long> kayitList = db.urunDao().setUrunList(gelenUrunList);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*if (kayitList.size() != gelenUrunList.size())
                                    MessageBox.showAlert(MainActivity.this, "Ürün listesi alınırken hata oluştu.", false);
                                else {*/


                                ////////urune ait sube listesi geliyor...////
                                for (Urun item : gelenUrunList) {
                                    //getUruneGoreSubeService(item.getId().toString());
                                }

                                /////şube listesi geliyor  //////
                                progressDoalog.show();
                                Call<List<Sube>> call = refrofitRestApi.getSubeList(OrtakFunction.authorization, OrtakFunction.tenantId);
                                call.enqueue(new Callback<List<Sube>>() {
                                    @Override
                                    public void onResponse(Call<List<Sube>> call, Response<List<Sube>> response) {
                                        if (!response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            MessageBox.showAlert(MainActivity.this, "Şube verileri alınırken hata oluştu...", false);
                                            return;
                                        }
                                        if (response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            gelenSubeList = response.body();
                                            if (gelenSubeList != null && gelenSubeList.size() > 0) {
                                                // db.subeDao().deleteSubeAll();

                                                for (Sube item : gelenSubeList) {
                                                    List<Sube> subeVarMi = db.subeDao().getSubeForId(item.getId());
                                                    if (subeVarMi.size() > 0) {
                                                        item.setMid(subeVarMi.get(0).getMid());
                                                        db.subeDao().updateSube(item);
                                                    } else
                                                        db.subeDao().setSube(item);
                                                }

                                                //  final List<Long> kayitList = db.subeDao().setSubeList(gelenSubeList);
                                                MainActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                     /*   if (kayitList.size() != gelenSubeList.size())
                                                            MessageBox.showAlert(MainActivity.this, "Şube listesi alınırken hata oluştu.", false);
                                                        else {*/
                                                        getUrunFiyatSubeAndOlcuBirimFromService(gelenSubeList);
                                                        //////// müşteri türü listesi geliyor //////////

                                                        progressDoalog.show();
                                                        Call<List<String>> call = refrofitRestApi.getMusteriTuruList(OrtakFunction.authorization, OrtakFunction.tenantId);
                                                        call.enqueue(new Callback<List<String>>() {
                                                            @Override
                                                            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                                                                if (!response.isSuccessful()) {
                                                                    progressDoalog.dismiss();
                                                                    MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                                                                    return;
                                                                }
                                                                if (response.isSuccessful()) {
                                                                    progressDoalog.dismiss();
                                                                    List<MusteriTuru> kayitOlunacakMusteriList = null;
                                                                    gelenMusteriTuruList = response.body();
                                                                    if (gelenMusteriTuruList != null && gelenMusteriTuruList.size() > 0) {
                                                                        db.musteriTuruDao().deleteMusteriTuruAll();
                                                                        for (String item : gelenMusteriTuruList) {
                                                                            MusteriTuru musteriTuru = new MusteriTuru();
                                                                            musteriTuru.setMusteriTuru(item);
                                                                            final long kayit = db.musteriTuruDao().setMusteriTuru(musteriTuru);


                                                                            MainActivity.this.runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (kayit < 0)
                                                                                        MessageBox.showAlert(MainActivity.this, "Müşteri türü listesi alınırken hata oluştu.", false);
                                                                                    else {
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    } /*else
                                                                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<List<String>> call, Throwable t) {
                                                                progressDoalog.dismiss();
                                                                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                                            }
                                                        });
                                                        //  }
                                                    }
                                                });
                                            } /*else
                                                MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Sube>> call, Throwable t) {
                                        progressDoalog.dismiss();
                                        MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                    }
                                });

                                //    }
                            }
                        });
                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<List<Urun>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    Siparis gelenSiparis;

    public void postSiparisListFromService(final Siparis siparis, final Long siparisMid) {
        progressDoalog.show();

        Call<Siparis> call = refrofitRestApi.postSiparis(OrtakFunction.authorization, OrtakFunction.tenantId, siparis);
        call.enqueue(new Callback<Siparis>() {
            @Override
            public void onResponse(Call<Siparis> call, Response<Siparis> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparis = response.body();
                    if (gelenSiparis != null) {

                        db.siparisDao().updateSiparisQuery(siparisMid, gelenSiparis.getId(), true);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // db.siparisDao().getSiparisAll()
                                List<SiparisDetay> siparisdetayList = db.siparisDetayDao().getSiparisDetayForMustId(siparisMid);
                                if (siparisdetayList != null && siparisdetayList.size() > 0) {
                                    List<Siparis> gidecekSiparis = db.siparisDao().getSiparisForSiparisId(gelenSiparis.getId());
                                    postSiparisDetayListFromService(siparisdetayList, gidecekSiparis);
                                }
                               /* if (gelenMusteriList.size() != kayitList.size())
                                    MessageBox.showAlert(MusteriKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    get_list();*/

                            }
                        });


                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<Siparis> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


    String gelenSiparisDetayList = null;

    public void postSiparisDetayListFromService(List<SiparisDetay> siparisDetayList, final List<Siparis> gelenSiparis) {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        JsonArray datas = new JsonArray();
        for (SiparisDetay item : siparisDetayList) {
            JsonObject object = new JsonObject();
            object.addProperty("id", item.getId());
            object.addProperty("siparisId", item.getSiparisId());
            object.addProperty("urunId", item.getUrunId());
            object.addProperty("olcuBirimId", item.getOlcuBirimId());
            object.addProperty("birimFiyat", item.getBirimFiyat());
            object.addProperty("miktar", item.getMiktar());
            //  object.addProperty("musteriNotu", "");
            datas.add(object);
        }

        progressDoalog.show();
        Call<String> call = refrofitRestApi.postSiparisDetay(OrtakFunction.authorization, OrtakFunction.tenantId, datas.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisDetayList = response.body();
                    if (gelenSiparisDetayList != null) {

                        //  db.siparisDao().updateSiparis(gelenSiparisDetayList);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             /*   if (gelenMusteriList.size() != kayitList.size())
                                    MessageBox.showAlert(MusteriKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    get_list();*/
                                postSiparisSureciBaslatService(gelenSiparis);

                            }
                        });


                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    String gelenProcessId = null;

    public void postSiparisSureciBaslatService(final List<Siparis> item) {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        JsonObject object = new JsonObject();
        object.addProperty("subeId", item.get(0).getSubeId());
        object.addProperty("musteriId", item.get(0).getMusteriId());
        if (item.get(0).getTeslimAlinacak() != null)
            object.addProperty("teslimAlinacak", item.get(0).getTeslimAlinacak() == true ? "Evet" : "Hayır");
        else
            object.addProperty("teslimAlinacak", "Hayır");
        object.addProperty("siparisId", item.get(0).getId());


        progressDoalog.show();
        Call<String> call = refrofitRestApi.startSiparisSureci(OrtakFunction.authorization, OrtakFunction.tenantId, object.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenProcessId = response.body();
                    if (!gelenProcessId.equalsIgnoreCase("")) {
                        JSONObject gelenObject = null;
                        try {
                            gelenObject = new JSONObject(gelenProcessId);
                            db.siparisDao().updateSiparisProcessId(Long.valueOf(gelenObject.getString("processInstanceId")), item.get(0).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             /*   if (gelenMusteriList.size() != kayitList.size())
                                    MessageBox.showAlert(MusteriKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    get_list();*/

                            }
                        });


                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    String gelenUrunFiyatJson = null;

    public void getUrunFiyatSubeAndOlcuBirimFromService(final List<Sube> subeList) {
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        //   db.urunSubeDao().deleteUrunSubeAll();
        // db.olcuBirimDao().deleteOlcuBirimAll();
        //  db.urunFiyatDao().deleteUrunFiyatAll();
        for (final Sube item : subeList) {
            Call<String> call = refrofitRestApi.getSubeyeGoreUrunFiyatListesi("hy/urun/subeyeGoreUrunAra/" + item.getId() + "/___", OrtakFunction.authorization, OrtakFunction.tenantId);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (!response.isSuccessful()) {
                        progressDoalog.dismiss();
                        MessageBox.showAlert(MainActivity.this, "Ürüne ait bilgiler alınırken hata oluştu...", false);
                        return;
                    }
                    if (response.isSuccessful()) {
                        progressDoalog.dismiss();
                        gelenUrunFiyatJson = response.body();
                        if (!gelenUrunFiyatJson.equalsIgnoreCase("")) {

                            JSONArray datas = null;
                            try {
                                datas = new JSONArray(gelenUrunFiyatJson);

                                for (int i = 0; i < datas.length(); i++) {
                                    JSONObject object = new JSONObject(datas.get(i).toString());
                                    List<UrunSube> urunSubeList = Arrays.asList(gson.fromJson(object.toString(), UrunSube.class));
                                    for (UrunSube item2 : urunSubeList) {
                                        item2.setSubeId(item.getId());
                                        List<UrunSube> urunSubeVarMi = db.urunSubeDao().getUrunSubeForId(item2.getId());
                                        if (urunSubeVarMi.size() > 0) {
                                            item.setMid(urunSubeVarMi.get(0).getMid());
                                            db.urunSubeDao().updateUrunSube(item2);
                                        } else
                                            db.urunSubeDao().setUrunSube(item2);
                                    }
                                    //  db.urunSubeDao().setUrunSubeList(urunSubeList);
                                    if (db.olcuBirimDao().getOlcuBirimForId(object.getLong("olcuBirimId")).size() == 0) {
                                        OlcuBirim olcuBirim = new OlcuBirim();
                                        olcuBirim.setOlcuBirimi(object.getString("olcuBirimi"));
                                        olcuBirim.setId(object.getLong("olcuBirimId"));
                                        olcuBirim.setAktif(object.getBoolean("aktif"));

                                        List<OlcuBirim> olcuBirimVarMi = db.olcuBirimDao().getOlcuBirimForId(olcuBirim.getId());
                                        if (olcuBirimVarMi.size() > 0) {
                                            olcuBirim.setMid(olcuBirimVarMi.get(0).getMid());
                                            db.olcuBirimDao().updateOlcuBirim(olcuBirim);
                                        } else
                                            db.olcuBirimDao().setOlcuBirim(olcuBirim);
                                    }
                                    JSONArray arrayFiyat = new JSONArray(object.getString("fiyatlar"));
                                    for (int j = 0; j < arrayFiyat.length(); j++) {
                                        JSONObject objectFiyat = new JSONObject(arrayFiyat.get(j).toString());
                                        List<UrunFiyat> data = Arrays.asList(gson.fromJson(objectFiyat.toString(), UrunFiyat.class));
                                        // db.urunFiyatDao().setUrunFiyatList(data);

                                        for (UrunFiyat item2 : data) {
                                            List<UrunFiyat> urunFiyatVarMi = db.urunFiyatDao().getForId(item2.getId());
                                            if (urunFiyatVarMi.size() > 0) {
                                                item.setMid(urunFiyatVarMi.get(0).getMid());
                                                db.urunFiyatDao().updateUrunFiyat(item2);
                                            } else
                                                db.urunFiyatDao().setUrunFiyat(item2);
                                        }
                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else
                            MessageBox.showAlert(MainActivity.this, "Ürün-fiyat listesi bulunamamıştır..", false);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                }
            });
        }
    }

    void getIlAndIlceFromService() {

        final String all_il_query = "{\"tableName\": \"S_IL\", \"valueField\": \"ID\", \"textField\": \"AD\", \"whereSql\": \" where 1=1 order by AD\"}";
        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        progressDoalog.show();
        Call<String> call = refrofitRestApi.getSelectService(OrtakFunction.authorization, OrtakFunction.tenantId, all_il_query);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    Long ilId;
                    progressDoalog.dismiss();
                    gelenUrunFiyatJson = response.body();
                    if (!gelenUrunFiyatJson.equalsIgnoreCase("")) {
                        db.sIlDao().deleteIlAll();
                        db.sIlceDao().deleteIlceAll();

                        JSONArray datas = null;
                        try {
                            datas = new JSONArray(gelenUrunFiyatJson);

                            for (int i = 0; i < datas.length(); i++) {
                                JSONArray object = new JSONArray(datas.get(i).toString());
                                S_IL il = new S_IL();
                                il.setId(Long.valueOf(object.get(0).toString()));
                                il.setAd(object.get(1).toString());
                                db.sIlDao().setIl(il);
                                ilId = Long.valueOf(object.get(0).toString());
                                String all_ilce_query = "{\"tableName\": \"S_ILCE\", \"valueField\": \"ID\", \"textField\": \"ADI\", \"whereSql\": \" where il_id= '" + object.get(0).toString() + "' order by ADI\"}";

                                Call<String> call2 = refrofitRestApi.getSelectService(OrtakFunction.authorization, OrtakFunction.tenantId, all_ilce_query);
                                final Long finalIlId = ilId;
                                call2.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                                            return;
                                        }
                                        if (response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            gelenUrunFiyatJson = response.body();
                                            if (!gelenUrunFiyatJson.equalsIgnoreCase("")) {

                                                JSONArray datas = null;
                                                try {
                                                    datas = new JSONArray(gelenUrunFiyatJson);

                                                    for (int i = 0; i < datas.length(); i++) {
                                                        JSONArray object = new JSONArray(datas.get(i).toString());
                                                        S_ILCE ilce = new S_ILCE();
                                                        ilce.setId(Long.valueOf(object.get(0).toString()));
                                                        ilce.setAd(object.get(1).toString());
                                                        ilce.setIlId(finalIlId);
                                                        db.sIlceDao().setIlce(ilce);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            } else
                                                MessageBox.showAlert(MainActivity.this, "Ürün-fiyat listesi bulunamamıştır..", false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        progressDoalog.dismiss();
                                        MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else
                        MessageBox.showAlert(MainActivity.this, "Ürün-fiyat listesi bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    List<UrunSube> gelenUrunSubeList = null;

    public void getUruneGoreSubeService(final String urunId) {
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        Call<List<UrunSube>> call = refrofitRestApi.getUrunSube("hy/urun/urunSubeler/" + urunId, OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<UrunSube>>() {
            @Override
            public void onResponse(Call<List<UrunSube>> call, Response<List<UrunSube>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUrunSubeList = response.body();
                    if (gelenUrunSubeList != null) {
                        final List<Long> kayitList = db.urunSubeDao().setUrunSubeList(gelenUrunSubeList);

                    } else
                        MessageBox.showAlert(MainActivity.this, "Ürün-fiyat listesi bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<List<UrunSube>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }

    List<String> gelenBolgeList;
    void getBolgeList() {
        Call<List<String>> call = refrofitRestApi.getBolgeList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenBolgeList = response.body();
                    if (gelenBolgeList != null && gelenBolgeList.size() > 0) {
                        db.bolgeDao().deleteBolgeAll();
                        for (String item : gelenBolgeList) {
                            Bolge bolge = new Bolge();
                            bolge.setBolge(item);
                            final long kayit = db.bolgeDao().setBolge(bolge);


                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (kayit < 0)
                                        MessageBox.showAlert(MainActivity.this, "Bölge listesi alınırken hata oluştu.", false);
                                    else {
                                    }
                                }
                            });
                        }
                    } /*else
                       MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


}
