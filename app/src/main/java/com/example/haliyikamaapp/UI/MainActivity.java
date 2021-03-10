package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.telephony.SmsManager;
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
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
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
import com.twilio.http.TwilioRestClient;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    BottomNavigationView bottomNavigationView;
    String gelenPage, telephoneNumber;
    HaliYikamaDatabase db;
    boolean phoneDialed = false;
    RefrofitRestApi refrofitRestApi;
    ProgressDialog progressDoalog, pd;
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
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        init_item();
        if (telephoneNumber != null)
            getCallLogs();
        permissonControl();


        if (savedInstanceState == null && gelenPage == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new GorevlerimFragment2()).commit();
            //  initToolBar("  Anasayfa");
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            toolbar.setVisibility(GONE);
            ekleButon.setVisibility(View.VISIBLE);

        }
        if (InternetKontrol()) {
            getAuth();

          /*  try {
                OrtakFunction.GetLocation(MainActivity.this, getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }*/

        }


    if (getIntent().getStringExtra("login") != null && getIntent().getStringExtra("login").equalsIgnoreCase("login")) {
            pd = ProgressDialog.show(MainActivity.this, "Lütfen bekleyiniz",
                    "Veriler güncelleniyor..", true);
            pd.setCancelable(false);
            pd.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
            pd.show();
            getServisler();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    pd.dismiss();

                }
            }, 11000);

        }

        //sms();
    }

    public void getServisler() {
       // getMusteriListFromService();
        getUrunListFromService();
      //  siparis_islemleri();
        getKaynakListFromService();

      /*  try {
            getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

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
                       /* case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            initToolBar("Anasayfa");
                            ekleButon.setVisibility(View.INVISIBLE);
                            break;*/
                        case R.id.nav_musteri:
                            selectedFragment = new MusteriFragment();
                            initToolBar("Müşteri");
                            ekleButon.setVisibility(View.VISIBLE);
                            click_ekle_button("Müşteri");
                            break;
                     /*   case R.id.nav_siparis:
                            selectedFragment = new SiparisFragment();
                            initToolBar("Sipariş");
                            ekleButon.setVisibility(View.INVISIBLE);
                            click_ekle_button("Sipariş");
                            musteriId = null;
                            musteriMid = null;
                            break;*/

                        case R.id.nav_ozet:
                            selectedFragment = new OzetFragment();
                            initToolBar("Özet");
                            ekleButon.setVisibility(View.GONE);
                            break;


                        case R.id.nav_diger:
                          //  selectedFragment = new TanimlamalarActivity();
                            initToolBar("Özet");
                            Intent i = new Intent(MainActivity.this, TanimlamalarActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(i);
                            break;

                        case R.id.nav_musterigorevlerim:
                            selectedFragment = new GorevlerimFragment2();
                            initToolBar("Görevlerim");
                            click_ekle_button("Görevlerim");

                            break;
                    }

                    if (selectedFragment != null) {
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        getFragmentManager().executePendingTransactions();
                        transaction.detach(selectedFragment);
                        transaction.attach(selectedFragment);
                        transaction.commit();
                    }
                   /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/
                    return true;
                }
            };


    TextView toolbarTextView;

    public void initToolBar(final String tittle) {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText(tittle);
            if (!tittle.equalsIgnoreCase("  Anasayfa")) {
                toolbar.setNavigationIcon(R.drawable.left);

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
                                    new GorevlerimFragment2()).commit();
                            toolbar.setVisibility(GONE);
                            bottomNavigationView.setSelectedItemId(R.id.nav_musterigorevlerim);

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
            }
            if (tittle.equalsIgnoreCase("Görevlerim") || tittle.equalsIgnoreCase("Özet"))
                toolbar.setVisibility(GONE);
            else
                toolbar.setVisibility(View.VISIBLE);


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
                builder.setTitle("Çıkış");
                builder.setMessage("Kullanıcı çıkışı yapılacaktır. Devam etmek istiyor musunuz?");
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
                        db.kaynakDao().deletekaynakAll();
                        db.hesapDao().deleteHesapAll();
                        db.userPermissionsDao().deleteUserAll();
                        db.smsDao().deleteSmsAll();
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

                    if (toolbarTextView.getText().toString().equalsIgnoreCase("Sipariş")) {
                        SiparisFragment fragment = (SiparisFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fragment.siparisAdapter.getFilter().filter(queryString);

                    }


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String queryString) {
                    if (toolbarTextView.getText().toString().equalsIgnoreCase("Müşteri")) {
                        MusteriFragment fragment = (MusteriFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fragment.adapter.getFilter().filter(queryString);

                    }

                    if (toolbarTextView.getText().toString().equalsIgnoreCase("Sipariş")) {
                        SiparisFragment fragment = (SiparisFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fragment.siparisAdapter.getFilter().filter(queryString);

                    }
                    return false;
                }
            });
        } else {
            myActionMenuItem.setVisible(false);
        }
        return true;

    }

    String musteriId, musteriMid;

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

            if (gelenPage.equalsIgnoreCase("ozet"))
                bottomNavigationView.setSelectedItemId(R.id.nav_ozet);

           /* if (gelenPage.equalsIgnoreCase("sipariş")) {
                musteriId = getIntent().getStringExtra("musteriId");
                musteriMid = getIntent().getStringExtra("musteriMid");
                bottomNavigationView.setSelectedItemId(R.id.nav_siparis);
                musteriId = null;
                musteriMid = null;
            }*/

            /*if (gelenPage.equalsIgnoreCase("anasayfa"))
                bottomNavigationView.setSelectedItemId(R.id.nav_home);*/

            if (gelenPage.equalsIgnoreCase("müşteri_görevlerim"))
                bottomNavigationView.setSelectedItemId(R.id.nav_musterigorevlerim);
        } else {
            musteriId = null;
            musteriMid = null;
        }


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
                } else if (page.equalsIgnoreCase("Görevlerim")) {
                  /*  selectedFragment = new MusteriFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    getFragmentManager().executePendingTransactions();
                    transaction.detach(selectedFragment);
                    transaction.attach(selectedFragment);
                    transaction.commit();*/
                    bottomNavigationView.setSelectedItemId(R.id.nav_musteri);

                    Intent musteri = new Intent(getApplication().getApplicationContext(), MusteriKayitActivity.class);
                    startActivity(musteri);

                   /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();*/

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


            if (telephoneNumber.length() > 0) {
                telephoneNumber = telephoneNumber.substring(3, 13);
                List<Musteri> allMusteriList = db.musteriDao().getMusteriAll();
                Boolean telefonNumarasiVarMi = false;
                for (final Musteri item : allMusteriList) {
                    if (item.getTelefonNumarasi().replace("(", "").replace(")", "")
                            .replace("-", "").replace(" ", "").equalsIgnoreCase(telephoneNumber)) {
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
                                intent.putExtra("musteriId", String.valueOf(item.getId()));
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
                                intent.putExtra("musteriId", item.getId().toString());

                                finish();
                                startActivity(intent);
                            }
                        });


                    }
                }
                if (!telefonNumarasiVarMi) {
                    call_button2.setVisibility(GONE);
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
                    //  MessageBox.showAlert(MainActivity.this, "Ürün bilgileri alınırken hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    //progressDoalog.dismiss();
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


                        ////////urune ait sube listesi geliyor...////
                        for (Urun item : gelenUrunList) {
                            //getUruneGoreSubeService(item.getId().toString());
                        }

                        /////şube listesi geliyor  //////
                        progressDoalog.show();
                        Call<List<Sube>> call2 = refrofitRestApi.getSubeList(OrtakFunction.authorization, OrtakFunction.tenantId);
                        call2.enqueue(new Callback<List<Sube>>() {
                            @Override
                            public void onResponse(Call<List<Sube>> call2, Response<List<Sube>> response) {
                                if (!response.isSuccessful()) {
                                    progressDoalog.dismiss();
                                  //  MessageBox.showAlert(MainActivity.this, "Şube verileri alınırken hata oluştu...", false);
                                    return;
                                }
                                if (response.isSuccessful()) {
                                    //  progressDoalog.dismiss();
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

                                                     /*   if (kayitList.size() != gelenSubeList.size())
                                                            MessageBox.showAlert(MainActivity.this, "Şube listesi alınırken hata oluştu.", false);
                                                        else {*/
                                        getUrunFiyatSubeAndOlcuBirimFromService(gelenSubeList);
                                        //////// müşteri türü listesi geliyor //////////

                                        Call<List<String>> call = refrofitRestApi.getMusteriTuruList(OrtakFunction.authorization, OrtakFunction.tenantId);
                                        call.enqueue(new Callback<List<String>>() {
                                            @Override
                                            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                                                if (!response.isSuccessful()) {
                                                    progressDoalog.dismiss();
                                                    MessageBox.showAlert(MainActivity.this, "Müşteri türü listesi alınırke hata oluştu...", false);
                                                    return;
                                                }
                                                if (response.isSuccessful()) {
                                                    // progressDoalog.dismiss();
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
                                              //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                            }
                                        });
                                        //  }


                                    } /*else
                                                MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Sube>> call2, Throwable t) {
                                progressDoalog.dismiss();
                              //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                            }
                        });

                        //    }


                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                    getIlAndIlceFromService();
                }
            }

            @Override
            public void onFailure(Call<List<Urun>> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    String gelenProcessId = null;
    public void postSiparisSureciBaslatService(final Siparis item) {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        JsonObject object = new JsonObject();
        object.addProperty("subeId", item.getSubeId());
        object.addProperty("musteriId", item.getMusteriId());
        if (item.getTeslimAlinacak() != null)
            object.addProperty("teslimAlinacak", item.getTeslimAlinacak() == true ? "Evet" : "Hayır");
        else
            object.addProperty("teslimAlinacak", "Hayır");
        object.addProperty("siparisId", item.getId());


        progressDoalog.show();
        Call<String> call = refrofitRestApi.startSiparisSureci(OrtakFunction.authorization, OrtakFunction.tenantId, object.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenProcessId = response.body();
                    if (!gelenProcessId.equalsIgnoreCase("")) {
                        JSONObject gelenObject = null;
                        try {
                            gelenObject = new JSONObject(gelenProcessId);
                            if (item.getTeslimAlinacak() != null && item.getTeslimAlinacak() == true)
                                db.siparisDao().updateSiparisProcessId(Long.valueOf(gelenObject.getString("processInstanceId")), item.getId(), "Teslim Alınacak");
                            if (item.getTeslimAlinacak() == null || item.getTeslimAlinacak() == false)
                                db.siparisDao().updateSiparisProcessId(Long.valueOf(gelenObject.getString("processInstanceId")), item.getId(), "Yıkamada");

                            //  MessageBox.showAlert(MainActivity.this, "Sipariş süreci başarılı bir şekilde başlatılmıştır.", false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }



            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
              //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
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
                        // MessageBox.showAlert(MainActivity.this, "Ürüne ait bilgiler alınırken hata oluştu...", false);
                        return;
                    }
                    if (response.isSuccessful()) {
                        // progressDoalog.dismiss();
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


                        } /*else
                            MessageBox.showAlert(MainActivity.this, "Ürün-fiyat listesi bulunamamıştır..", false);*/
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
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
                    //  MessageBox.showAlert(MainActivity.this, "İl verisi alınırken  hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    Long ilId;
                    //  progressDoalog.dismiss();
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
                                            //  MessageBox.showAlert(MainActivity.this, "İlçe verisi alınırken hata oluştu...", false);
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


                                            }/* else
                                                MessageBox.showAlert(MainActivity.this, "Ürün-fiyat listesi bulunamamıştır..", false);*/
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        progressDoalog.dismiss();
                                      //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Ürün-fiyat listesi bulunamamıştır..", false);*/
                    getBolgeList();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
               // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
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
                    //  progressDoalog.dismiss();
                    //  MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
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
               // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }

    List<String> gelenBolgeList;

    void getBolgeList() {
        progressDoalog.show();
        Call<List<String>> call = refrofitRestApi.getBolgeList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                  //  MessageBox.showAlert(MainActivity.this, "Bölge listesi alınırken hata oluştu...", false);
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


                        }
                    } /*else
                       MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                progressDoalog.dismiss();
              //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


    public void sendSMS() {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+905543283278", null, "selammm", null, null);
            getContentResolver().delete(Uri.parse("content://sms/outbox"), "address = ? and body = ?", new String[]{"+905543283278", "selammm"});

            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    void siparis_islemleri() {

        for (Siparis item : db.siparisDao().getSiparisAll()) {
            if (item.getMusteriId() == null && db.musteriDao().getMusteriForMid(item.getMusteriMid()).size() > 0) {
                Musteri musteri = db.musteriDao().getMusteriForMid(item.getMusteriMid()).get(0);
                db.siparisDao().updateSiparisMusteriId(item.getMid(), musteri.getId());
                item.setMusteriId(musteri.getId());
            }
            item.setMustId(null);
            item.setSenkronEdildi(null);
            item.setMusteriMid(null);
            postSiparisListFromService(item, item.getMid());
        }

        getSiparisListFromService();



    }

    Siparis gelenSiparis;
    public void postSiparisListFromService(final Siparis siparis, final Long siparisMid) {
        progressDoalog.show();
        siparis.setMid(null);
        siparis.setBarkod(null);
        siparis.setSubeMid(null);
        siparis.setSiparisTarihi(siparis.getSiparisTarihi() + " 00:00");
        siparis.setKaynakMid(null);
        Call<Siparis> call = refrofitRestApi.postSiparis(OrtakFunction.authorization, OrtakFunction.tenantId, siparis);
        call.enqueue(new Callback<Siparis>() {
            @Override
            public void onResponse(Call<Siparis> call, Response<Siparis> response) {
                if (!response.isSuccessful()) {
                    //progressDoalog.dismiss();
                    // MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
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
                                List<SiparisDetay> siparisdetayList = db.siparisDetayDao().getSiparisDetayForMustId(siparisMid);

                                List<SiparisDetay> senkronEdilecekler = new ArrayList<>();

                                for (SiparisDetay item : siparisdetayList) {
                                    if (item.getSenkronEdildi() == null || item.getSenkronEdildi() == false)
                                        senkronEdilecekler.add(item);
                                }

                                if (senkronEdilecekler != null && senkronEdilecekler.size() > 0) {
                                    List<Siparis> gidecekSiparis = db.siparisDao().getSiparisForSiparisId(gelenSiparis.getId());
                                    db.siparisDetayDao().updateSiparisId(siparisMid, gelenSiparis.getId());
                                    postSiparisDetayListFromService(senkronEdilecekler, gidecekSiparis);

                                } else {

                                    if (gelenSiparis.processInstanceId == null && (gelenSiparis.getTeslimAlinacak() == null || gelenSiparis.getTeslimAlinacak() == false)) {
                                        postSiparisSureciBaslatService(gelenSiparis);
                                    }

                                }
                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Siparis> call, Throwable t) {
                progressDoalog.dismiss();
                //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


    List<SiparisDetay> gelenSiparisDetayLists;
    List<SiparisDetay> updateSiparisDetayList;
    String gelenSiparisDetayList = null;
    public void postSiparisDetayListFromService(final List<SiparisDetay> siparisDetayList, final List<Siparis> gelenSiparis) {
        progressDoalog.show();
        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        final List<Long> midList = new ArrayList<>();
        JsonArray datas = new JsonArray();
        for (SiparisDetay item : siparisDetayList) {
            JsonObject object = new JsonObject();
            object.addProperty("id", item.getId());
            object.addProperty("siparisId", gelenSiparis.get(0).getId());
            object.addProperty("urunId", item.getUrunId());
            if (item.getUrunId() != null && db.urunDao().getUrunForId(item.getUrunId()).size() > 0)
                object.addProperty("urunAdi", db.urunDao().getUrunForId(item.getUrunId()).get(0).getUrunAdi());
            else
                object.addProperty("urunAdi", " ");

            if (item.getOlcuBirimId() != null && db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).size() > 0)
                object.addProperty("olcuBirimAdi", db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).get(0).getOlcuBirimi());
            else
                object.addProperty("olcuBirimAdi", " ");

            object.addProperty("olcuBirimId", item.getOlcuBirimId());
            object.addProperty("birimFiyat", item.getBirimFiyat());
            object.addProperty("miktar", item.getMiktar());
            if (item.getMiktar() != null && item.getBirimFiyat() != null)
                object.addProperty("toplamTutar", item.getMiktar() * item.getBirimFiyat());
            else
                object.addProperty("toplamTutar", 0);

            object.addProperty("musteriNotu", gelenSiparis.get(0).getAciklama());


            midList.add(item.getMid());
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
                    // MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisDetayList = response.body();
                    if (gelenSiparisDetayList != null) {


                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
                                Call<List<SiparisDetay>> call = refrofitRestApi.getSiparisDetayList("hy/siparis/siparisUrunler/" + gelenSiparis.get(0).getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                                call.enqueue(new Callback<List<SiparisDetay>>() {
                                    @Override
                                    public void onResponse(Call<List<SiparisDetay>> call, Response<List<SiparisDetay>> response) {
                                        if (!response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            //MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                                            return;
                                        }
                                        if (response.isSuccessful()) {
                                            progressDoalog.dismiss();

                                            db.siparisDetayDao().updateSiparisDetayQuery(gelenSiparis.get(0).getMid(), true);

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<SiparisDetay>> call, Throwable t) {
                                        progressDoalog.dismiss();
                                        //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                    }
                                });


                            }
                        });
                    }
                }

                if (gelenSiparis != null && gelenSiparis.size() > 0 && gelenSiparis.get(0).processInstanceId == null) {
                    postSiparisSureciBaslatService(gelenSiparis.get(0));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                //  MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    List<Siparis> gelenSiparisList;
    List<SiparisDetay> gelenSiparisDetayList2;
    void getSiparisListFromService() {
        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        Call<List<Siparis>> call = refrofitRestApi.getSiparisList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Siparis>>() {
            @Override
            public void onResponse(Call<List<Siparis>> call, Response<List<Siparis>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisList = response.body();
                    if (gelenSiparisList != null && gelenSiparisList.size() > 0) {


                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (final Siparis item : gelenSiparisList) {
                                    List<Siparis> urunVarMi = db.siparisDao().getSiparisForSiparisId(item.getId());
                                    item.setSenkronEdildi(true);
                                    if (urunVarMi.size() > 0) {
                                        item.setMid(urunVarMi.get(0).getMid());
                                        item.setMusteriMid(urunVarMi.get(0).getMusteriMid());
                                        item.setSubeMid(urunVarMi.get(0).getSubeMid());
                                        item.setKaynakMid(urunVarMi.get(0).getKaynakMid());
                                        db.siparisDao().updateSiparis(item);
                                    } else
                                        db.siparisDao().setSiparis(item);


                                    Call<List<SiparisDetay>> call = refrofitRestApi.getSiparisDetayList("hy/siparis/siparisUrunler/" + item.getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                                    call.enqueue(new Callback<List<SiparisDetay>>() {
                                        @Override
                                        public void onResponse(Call<List<SiparisDetay>> call, Response<List<SiparisDetay>> response) {
                                            if (!response.isSuccessful()) {
                                                progressDoalog.dismiss();
                                                //  MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                                                return;
                                            }
                                            if (response.isSuccessful()) {
                                                progressDoalog.dismiss();
                                                gelenSiparisDetayList2 = response.body();

                                                MainActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        for (SiparisDetay i : gelenSiparisDetayList2) {
                                                            i.setSenkronEdildi(true);
                                                            List<Siparis> siparis = db.siparisDao().getSiparisForSiparisId(item.getId());
                                                            List<SiparisDetay> urunVarMi = db.siparisDetayDao().getSiparisDetayForId(i.getId());
                                                            if (siparis != null && siparis.size() > 0) {
                                                                i.setMustId(siparis.get(0).getMid());
                                                                i.setSiparisMid(siparis.get(0).getMid());
                                                            }
                                                            if (urunVarMi.size() > 0) {
                                                                i.setMid(urunVarMi.get(0).getMid());
                                                                i.setUrunMid(urunVarMi.get(0).getUrunMid());
                                                                i.setOlcuBirimMid(urunVarMi.get(0).getOlcuBirimMid());
                                                                db.siparisDetayDao().updateSiparisDetay(i);
                                                            } else
                                                                db.siparisDetayDao().setSiparisDetay(i);
                                                        }


                                                    }
                                                });


                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<SiparisDetay>> call, Throwable t) {
                                            progressDoalog.dismiss();
                                            // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                                        }
                                    });


                                }


                            }
                        });


                    } /*else
                        MessageBox.showAlert(MainActivity.this, "Kayıt bulunamamıştır..", false);*/
                }

            }

            @Override
            public void onFailure(Call<List<Siparis>> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }


    List<Musteri> gelenMusteriList;

    void getMusteriListFromService() {

        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        Call<List<Musteri>> call = refrofitRestApi.getMusteriList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Musteri>>() {
            @Override
            public void onResponse(Call<List<Musteri>> call, Response<List<Musteri>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    //  //  MessageBox.showAlert(MainActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenMusteriList = response.body();
                    if (gelenMusteriList != null && gelenMusteriList.size() > 0) {
                        Boolean yeniKayitMi = true;
                        if (db.musteriDao().getMusteriAll().size() == 0)
                            db.musteriDao().setMusteriList(gelenMusteriList);
                        else {

                            for (Musteri i : gelenMusteriList) {

                                i.setSenkronEdildi(true);
                                for (Musteri all : db.musteriDao().getMusteriAll()) {

                                    if (all.getId() != null && all.getId().toString().equalsIgnoreCase(i.getId().toString())) {
                                        yeniKayitMi = false;
                                        i.setMid(all.getMid());
                                        db.musteriDao().updateMusteri(i);
                                    }
                                }

                                if (yeniKayitMi)
                                    db.musteriDao().setMusteri(i);
                            }
                        }


                    }


                }

            }

            @Override
            public void onFailure(Call<List<Musteri>> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


    List<Kaynak> gelenKaynakList;

    void getKaynakListFromService() {

        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        JSONObject object = new JSONObject();
        try {
            object.put("pageSize", 99999);
            object.put("pageNumber", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<List<Kaynak>> call = refrofitRestApi.getKaynakList(OrtakFunction.authorization, OrtakFunction.tenantId, "application/json");
        call.enqueue(new Callback<List<Kaynak>>() {
            @Override
            public void onResponse(Call<List<Kaynak>> call, Response<List<Kaynak>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(KaynakActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    Boolean yeniKayitMi = true;
                    progressDoalog.dismiss();
                    // db.kaynakDao().deletekaynakAll();
                    gelenKaynakList = response.body();

                    // db.kaynakDao().setkaynakList(gelenKaynakList);
                    for (Kaynak i : gelenKaynakList) {

                        i.setSenkronEdildi(true);

                        if (db.kaynakDao().getkaynakAll().size() == 0) {
                            db.kaynakDao().setkaynak(i);

                        } else {
                            for (Kaynak all : db.kaynakDao().getkaynakAll()) {

                                if (all.getId() != null && all.getId().toString().equalsIgnoreCase(i.getId().toString())) {
                                    yeniKayitMi = false;
                                    i.setMid(all.getMid());
                                    i.setSecilenKaynakMi(all.getSecilenKaynakMi());
                                    db.kaynakDao().updatekaynak(i);
                                }
                            }

                            if (yeniKayitMi)
                                db.kaynakDao().setkaynak(i);

                        }
                    }

                }
            }


            @Override
            public void onFailure(Call<List<Kaynak>> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(MainActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }



    String gelenGorevList = null;
    public void getGorevlerimFromService(Long kullaniciId) throws Exception {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        JSONObject object = new JSONObject();


        JSONObject object2 = new JSONObject();
        object2.put("musteriler", new JSONArray());
        object2.put("subeler", new JSONArray());
        object2.put("kaynaklar", new JSONArray());
        object2.put("durum", new JSONArray());
        object2.put("ilkTarih", null);
        object2.put("sonTarih", null);
        object.put("model", object2);

        JSONObject object1 = new JSONObject();
        object1.put("pageSize", 99999);
        object1.put("pageNumber", 0);
        object1.put("sortField", "siparis_tarihi");
        object1.put("sortOrder", -1);
        object.put("pageParams", object1);
        Call<String> call = refrofitRestApi.getKullaniciGorevList("hy/process/userTasks", OrtakFunction.authorization, OrtakFunction.tenantId, object.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    //  MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {

                    gelenGorevList = response.body();
                    if (!gelenGorevList.equalsIgnoreCase("")) {
                        db.gorevlerDao().deleteGorevAll();

                        List<Gorevler> allGorevList = db.gorevlerDao().getGorevAll();
                        JSONObject gelenObject = null;
                        //db.gorevlerDao().deleteGorevAll();
                        Boolean yeniKayitMi = true;
                        Long gorevMid;
                        try {
                            gelenObject = new JSONObject(gelenGorevList);
                            JSONArray rowArray = new JSONArray(gelenObject.getString("rows"));
                            for (int i = 0; i < rowArray.length(); i++) {
                                JSONObject rowObject = new JSONObject(rowArray.getString(i));
                            /*   JSONObject processVariablesObject = new JSONObject(rowObject.getString("processVariables"));
                                rowObject.put("siparisId", processVariablesObject.get("siparisId"));
                                rowObject.put("musteriId", processVariablesObject.get("musteriId"));*/
                                List<Gorevler> gorevlerList = Arrays.asList(gson.fromJson(rowObject.toString(), Gorevler.class));
                                for (Gorevler item : allGorevList) {
                                    if (item.getTaskId() != null && item.getTaskId().toString().equalsIgnoreCase(rowObject.getString("taskId"))) {
                                        yeniKayitMi = false;
                                    }

                                    if (yeniKayitMi)
                                        db.gorevlerDao().setGorev(item);

                                    else {
                                        item.setMid(item.getMid());
                                        db.gorevlerDao().updateGorev(item);

                                    }
                                }
                                if (allGorevList != null && allGorevList.size() == 0)
                                    db.gorevlerDao().setGorevList(gorevlerList);

                              /*  JSONObject musteriObject = new JSONObject(processVariablesObject.getString("musteri"));
                                List<Musteri> gelenMusteriList = Arrays.asList(gson.fromJson(musteriObject.toString(), Musteri.class));

                                JSONObject siparisObject = new JSONObject(processVariablesObject.getString("siparis"));
                                List<Siparis> gelenSiparisList = Arrays.asList(gson.fromJson(siparisObject.toString(), Siparis.class));*/


                                // get_list(null, null, null);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }





                    } /*else
                        MessageBox.showAlert(getContext(), "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }



}
