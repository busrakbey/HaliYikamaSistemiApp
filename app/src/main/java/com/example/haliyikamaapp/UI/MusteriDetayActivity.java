package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.MusteriDetayAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MusteriDetayActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    ConstraintLayout relativeLayout;
    MusteriDetayAdapter musteri_detay_adapter;
    RecyclerView recyclerView;
    String musteriMid;
    HaliYikamaDatabase db;
    Snackbar snackbar;
    FloatingActionButton yeni_musteri_detay_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.musteri_detay_activity);
        init_item();
        initToolBar();
        get_list();

    }

    void init_item() {
        db = HaliYikamaDatabase.getInstance(MusteriDetayActivity.this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_musteri);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        yeni_musteri_detay_button = (FloatingActionButton) findViewById(R.id.btnAdd);
        relativeLayout = (ConstraintLayout) findViewById(R.id.container);
        recyclerView = (RecyclerView) findViewById(R.id.musteri_detay_recyclerview);
        Intent intent = getIntent();
        musteriMid = intent.getStringExtra("musteriMid");

        yeni_musteri_detay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MusteriDetayActivity.this, MusteriDetayKayitActivity.class);
                i.putExtra("musteriMid", musteriMid);
                finish();
                startActivity(i);
            }
        });
    }


    public void get_list() {
        final List<MusteriIletisim> kisiler = db.musteriIletisimDao().getMusteriIletisimForMustId(Long.valueOf(musteriMid));

        musteri_detay_adapter = new MusteriDetayAdapter(MusteriDetayActivity.this, kisiler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MusteriDetayActivity.this));
        recyclerView.setAdapter(musteri_detay_adapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(MusteriDetayActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.musteriIletisimDao().deletedMusteriIletisimForMid(musteri_detay_adapter.getData().get(position).getMid());
                MusteriDetayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (silinenMusteriDetayMid == 1) {
                            musteri_detay_adapter.getData().remove(position);
                            musteri_detay_adapter.notifyDataSetChanged();
                            snackbar = Snackbar
                                    .make(relativeLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();


                        } else
                            MessageBox.showAlert(MusteriDetayActivity.this, "İşlem başarısız..\n", false);

                    }
                });


               /* snackbar.setAction("Geri Al", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        musteri_detay_adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });*/


            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);


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
                            i = new Intent(MusteriDetayActivity.this, MainActivity2.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musteri:
                            i = new Intent(MusteriDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                       /* case R.id.nav_siparis:
                            i = new Intent(MusteriDetayActivity.this, MainActivity2.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;*/
                        case R.id.nav_musterigorevlerim:
                            i = new Intent(MusteriDetayActivity.this, MainActivity.class);
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
            toolbarTextView.setText("İletişim Bilgileri");
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

    @Override
    public void onResume() {
        super.onResume();
        get_list();
    }


}
