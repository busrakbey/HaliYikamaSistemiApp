package com.example.haliyikamaapp.UI;
import android.annotation.SuppressLint;

import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import com.example.haliyikamaapp.Adapter.CustomBottomNavigationView;
import com.example.haliyikamaapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    CustomBottomNavigationView customBottomNavigationView;

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
        setContentView(R.layout.deneme);

        init_item();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            initToolBar("Anasayfa");
            ekleButon.setVisibility(View.INVISIBLE);

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
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
                            break;
                        case R.id.nav_siparis:
                            selectedFragment = new SiparisFragment();
                            initToolBar("Sipariş");
                            ekleButon.setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_musterigorevlerim:
                            selectedFragment = new MusteriGorevlerimFragment();
                            initToolBar("Ayarlar");
                            ekleButon.setVisibility(View.VISIBLE);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };


    public void initToolBar(String tittle) {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            // toolbar.setNavigationIcon(R.drawable.left);
            //  setSupportActionBar(toolbar);

            /*getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText(tittle);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void init_item() {
       /* final int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed}

        };

        final int[] colors = new int[]{
                ContextCompat.getColor(MainActivity.this, R.color.green)
        };*/
        ekleButon = (FloatingActionButton) findViewById(R.id.btnAdd);
        //  ekleButon.setBackgroundTintList(new ColorStateList(states, colors));
        ekleButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }


}