package com.example.haliyikamaapp.UI;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.CustomDialog;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;


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
       /* Cursor c = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int totalCall = 1;
        if (c != null) {
            if (c.moveToFirst()) { //starts pulling logs from last - you can use moveToFirst() for first logs
                for (int j = 0; j < totalCall; j++) {
                    String phNumber = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                    String callDate = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DATE));
                    String callDuration = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    Date dateFormat = new Date(Long.valueOf(callDate));
                    String callDayTimes = String.valueOf(dateFormat);

                    String direction = null;
                    switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(CallLog.Calls.TYPE)))) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            direction = "OUTGOING";
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            direction = "INCOMING";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            direction = "MISSED";
                            break;
                        default:
                            break;
                    }
                    c.moveToPrevious(); // if you used moveToFirst() for first logs, you should this line to moveToNext

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.popup_window, null);
                    final TextView call_tittle = (TextView) mView.findViewById(R.id.call_tittle);
                    final TextView call_message = (TextView) mView.findViewById(R.id.call_message);
                    final Button call_button = (Button) mView.findViewById(R.id.call_button);
                    call_tittle.setText("ÇAĞRI UYARI");


                    if (phNumber.length() == 13) {
                        phNumber = phNumber.substring(2, 13);
                        List<Musteri> allMusteriList = db.musteriDao().getMusteriAll();
                        for (final Musteri item : allMusteriList) {
                            if (item.getTelefonNumarasi().equalsIgnoreCase(phNumber)) {

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

                            }
                        }
                        if (allMusteriList.size() == 0) {
                            call_button.setText("Müşteri Oluştur");
                            call_message.setVisibility(View.GONE);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            final String finalPhNumber = phNumber;
                            call_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this, MusteriKayitActivity.class);
                                    intent.putExtra("number", finalPhNumber);
                                    finish();
                                    startActivity(intent);
                                }
                            });

                        }


                    }


                }
            }
            c.close();
        }*/
    }


    void permissonControl() {
        Boolean hasCallPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED;
        if (!hasCallPermission)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);


    }
}