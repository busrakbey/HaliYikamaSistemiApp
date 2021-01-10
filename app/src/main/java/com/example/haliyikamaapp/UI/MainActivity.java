package com.example.haliyikamaapp.UI;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    BottomNavigationView bottomNavigationView;
    String gelenPage, telephoneNumber;
    HaliYikamaDatabase db;
    boolean phoneDialed = false;


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
        permissonControl();

        if (savedInstanceState == null && gelenPage == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            initToolBar("Anasayfa");
            ekleButon.setVisibility(View.INVISIBLE);

        }
        getAuth();


    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
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
                            initToolBar("Müşteri ve Görevlerim");
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


    public void initToolBar(final String tittle) {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            toolbar.setNavigationIcon(R.drawable.left);
            //  setSupportActionBar(toolbar);

            /*getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
            final TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
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
                        builder.setMessage("Uygulamadan çıkış yapılacaktır. Devam etmek istiyor musunuz?");
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
        builder.setTitle("Uyarı");
        builder.setMessage("Uygulamadan çıkış yapılacaktır. Devam etmek istiyor musunuz?");
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
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
        if (OrtakFunction.tokenList == null || OrtakFunction.tokenList.size() == 0)
            OrtakFunction.getTtoken(MainActivity.this);
    }


}