package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.SiparisDetayAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SiparisDetayActivity extends AppCompatActivity {
    ConstraintLayout relativeLayout;
    SiparisDetayAdapter siparis_detay_adapter;
    RecyclerView recyclerView;
    String siparisMid;
    HaliYikamaDatabase db;
    Snackbar snackbar;
    FloatingActionButton yeni_siparis_detay_button;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.siparis_detay_activity);
        initToolBar();
        init_item();
        get_list();

    }

    void init_item() {
        db = HaliYikamaDatabase.getInstance(SiparisDetayActivity.this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_siparis);
        bottomNav.setOnNavigationItemSelectedListener(navListener);



        yeni_siparis_detay_button = (FloatingActionButton) findViewById(R.id.btnAdd);
        relativeLayout = (ConstraintLayout) findViewById(R.id.container);
        recyclerView = (RecyclerView) findViewById(R.id.siparis_detay_recyclerview);
        Intent intent = getIntent();
        siparisMid = intent.getStringExtra("siparisMid");

        yeni_siparis_detay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SiparisDetayActivity.this, MusteriDetayKayitActivity.class);
                i.putExtra("siparisMid", siparisMid);
                finish();
                startActivity(i);
            }
        });
    }

    public void get_list() {
        final List<SiparisDetay> kisiler = db.siparisDetayDao().getSiparisDetayForMustId(Long.valueOf(siparisMid));

        siparis_detay_adapter = new SiparisDetayAdapter(SiparisDetayActivity.this, kisiler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SiparisDetayActivity.this));
        recyclerView.setAdapter(siparis_detay_adapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(SiparisDetayActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.musteriIletisimDao().deletedMusteriIletisimForMid(siparis_detay_adapter.getData().get(position).getMid());
                SiparisDetayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (silinenMusteriDetayMid == 1) {
                            siparis_detay_adapter.getData().remove(position);
                            siparis_detay_adapter.notifyDataSetChanged();
                            snackbar = Snackbar
                                    .make(relativeLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();


                        } else
                            MessageBox.showAlert(SiparisDetayActivity.this, "İşlem başarısız..\n", false);

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
                        case R.id.nav_home:
                            i = new Intent(SiparisDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "anasayfa");
                            startActivity(i);
                            break;
                        case R.id.nav_musteri:
                            i = new Intent(SiparisDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "müşteri");
                            startActivity(i);
                            break;
                        case R.id.nav_siparis:
                            i = new Intent(SiparisDetayActivity.this, MainActivity.class);
                            i.putExtra("gelenPage", "sipariş");
                            startActivity(i);
                            break;
                        case R.id.nav_musterigorevlerim:
                            i = new Intent(SiparisDetayActivity.this, MainActivity.class);
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
            toolbarTextView.setText("Sipariş Detay Bilgileri");
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
