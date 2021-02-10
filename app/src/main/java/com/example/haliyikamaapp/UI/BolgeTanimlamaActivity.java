package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.BolgeAdapter;
import com.example.haliyikamaapp.Adapter.SubeAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Bolge;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BolgeTanimlamaActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText bolgeAdi;
    RecyclerView sube_listview;
    BolgeAdapter bolgeAdapter;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    Button subeKayitButton, subeGuncelleButton;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;

    List<Bolge> bolgeler;
    List<String> bolgeStringList;
    int selected_bolge_index = 0;
    Long bolgeMid = null;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.bolge_tanimlama_activity);
        init_item();
        initToolBar();
        get_list();

    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Bölge Tanımlama");
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
        db = HaliYikamaDatabase.getInstance(BolgeTanimlamaActivity.this);
        sube_listview = (RecyclerView) findViewById(R.id.bolge_listview);
        bolgeAdi = (EditText) findViewById(R.id.bolge_adi);

        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        subeKayitButton = (Button) findViewById(R.id.sube_kayit_button);
        subeGuncelleButton = (Button) findViewById(R.id.sube_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(BolgeTanimlamaActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        bolgeStringList = new ArrayList<String>();
        bolgeler = new ArrayList<Bolge>();


    }

    public void get_list() {
        final List<Bolge> bolgeAll = db.bolgeDao().getBolgeAll();
        bolgeAdapter = new BolgeAdapter(BolgeTanimlamaActivity.this, bolgeAll);
        sube_listview.setHasFixedSize(true);
        sube_listview.setLayoutManager(new LinearLayoutManager(BolgeTanimlamaActivity.this));
        sube_listview.setAdapter(bolgeAdapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(BolgeTanimlamaActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final int silinenMusteriDetayMid = db.subeDao().deletedSubeForMid(bolgeAdapter.getData().get(position).getMid());
                BolgeTanimlamaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (silinenMusteriDetayMid == 1) {
                            bolgeAdapter.getData().remove(position);
                            bolgeAdapter.notifyDataSetChanged();
                            snackbar = Snackbar
                                    .make(constraintLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();


                        } else
                            MessageBox.showAlert(BolgeTanimlamaActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(sube_listview);
    }

    public void subeKayitOnClick(View view) {
        siparis_kayit(null);
    }


    public void getEditMode(Long subeMid) {
        bolgeMid = subeMid;
        List<Bolge> updateKayitList = db.bolgeDao().getBolgeForMid(bolgeMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid().equals(subeMid)) {
            bolgeAdi.setText(updateKayitList.get(0).getBolge());

        }
    }


    void siparis_kayit(final Long gelenSubeMid) {
        if (bolgeAdi.getText().toString().trim().equalsIgnoreCase("")) {
            MessageBox.showAlert(BolgeTanimlamaActivity.this, "Lütfen bölge adı giriniz.", false);

        } else {
            final Bolge bolge = new Bolge();
            bolge.setBolge(bolgeAdi.getText().toString());
            bolge.setSenkronEdildi(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long musteriMid = 0;
                    musteriMid = db.bolgeDao().setBolge(bolge);


                    final long finalSiparisMid = musteriMid;
                    BolgeTanimlamaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (gelenSubeMid == null && Integer.valueOf(String.valueOf(finalSiparisMid)) > 0) {
                                MessageBox.showAlert(BolgeTanimlamaActivity.this, "Kayıt Başarılı..\n", false);
                                get_list();
                                bolgeAdapter.notifyDataSetChanged();
                                // Intent i = new Intent(SubeTanimlamaActivity.this, SiparisKayitActivity.class);
                            }



                            if (finalSiparisMid < 0)
                                MessageBox.showAlert(BolgeTanimlamaActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }

}


