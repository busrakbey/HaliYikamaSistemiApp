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
import android.widget.CheckBox;
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

import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Adapter.UruneSubeAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
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

public class UruneFiyatTanimlamaActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText aciklama, baslangicTarihi, bitisTarihi, birimFiyat;
    CheckBox aktifMi;
    UruneSubeAdapter urunAdapter;
    ConstraintLayout constraintLayout;
    Button fiyatKayitButton, fiyatGuncelleButton;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    List<UrunFiyat> urunFiyatList;
    String gelenUrunSubeId, gelenUrunSubeMid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.urune_fiyat_tanimlama_activity);
        init_item();
        initToolBar();


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Fiyat Bilgisi");
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
        db = HaliYikamaDatabase.getInstance(UruneFiyatTanimlamaActivity.this);
        aktifMi = (CheckBox) findViewById(R.id.urune_ait_sube_aktif_mi);
        aciklama = (EditText) findViewById(R.id.urune_ait_aciklama);
        baslangicTarihi = (EditText) findViewById(R.id.urun_fiyat_bas_tarih);
        bitisTarihi = (EditText) findViewById(R.id.urun_fiyat_bit_tarih);
        birimFiyat = (EditText) findViewById(R.id.urune_ait_fiyat);

        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
        fiyatKayitButton = (Button) findViewById(R.id.fiyat_kayit_button);
        fiyatGuncelleButton = (Button) findViewById(R.id.fiyat_guncelle_button);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(UruneFiyatTanimlamaActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);

        gelenUrunSubeId = getIntent().getStringExtra("urunSubeId");
        gelenUrunSubeMid = getIntent().getStringExtra("urunSubeMid");

        if(gelenUrunSubeMid != null)
            getEditMode();


    }

    public void uruneAitFiyatKayitOnClick(View view) {
        fiyat_kayit(null);
    }

    public void uruneAitFiyatGuncelleOnClick(View view) {
        if (gelenUrunSubeMid != null)
            fiyat_kayit(Long.valueOf(gelenUrunSubeMid));
    }


    List<UrunFiyat> updateKayitList;
    public void getEditMode() {
        if(!gelenUrunSubeId.equalsIgnoreCase("null"))
            updateKayitList = db.urunFiyatDao().getForUrunSubeId(Long.valueOf(gelenUrunSubeId));
        else
            updateKayitList = db.urunFiyatDao().getForUrunSubeMid(Long.valueOf(gelenUrunSubeMid));


        if (updateKayitList != null && updateKayitList.size() > 0) {
            if (updateKayitList.get(0).getAktif() != null && updateKayitList.get(0).getAktif() == true)
                aktifMi.setChecked(true);

            aciklama.setText(updateKayitList.get(0).getAciklama());
            baslangicTarihi.setText(updateKayitList.get(0).getBaslangicTarihi());
            bitisTarihi.setText(updateKayitList.get(0).getBitisTarihi());
            birimFiyat.setText(updateKayitList.get(0).getBirimFiyat() != null ? updateKayitList.get(0).getBirimFiyat().toString() : "");
        }
    }


    void fiyat_kayit(final Long gelenurunMid) {
        if (baslangicTarihi.getText().toString().equalsIgnoreCase("") || birimFiyat.getText().toString().equalsIgnoreCase("")) {
            MessageBox.showAlert(UruneFiyatTanimlamaActivity.this, "Lütfen başlangıç tarihi ve birim fiyatını giriniz..", false);

        } else {
            final UrunFiyat urun = new UrunFiyat();
            urun.setAciklama(aciklama.getText().toString());
            urun.setAktif(aktifMi.isChecked() ? true : false);
            urun.setBaslangicTarihi(baslangicTarihi.getText().toString());
            urun.setBitisTarihi(bitisTarihi.getText().toString());
            urun.setBirimFiyat(birimFiyat.getText() != null ? Double.valueOf(birimFiyat.getText().toString()) : null);
            urun.setUrunSubeMid(Long.valueOf(gelenUrunSubeMid));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long musteriMid = 0;

                    if (gelenurunMid == null)
                        musteriMid = db.urunFiyatDao().setUrunFiyat(urun);
                    if (gelenurunMid != null) {
                        urun.setMid(Long.valueOf(gelenurunMid));
                    }

                    final long finalSiparisMid = musteriMid;
                    UruneFiyatTanimlamaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (gelenurunMid == null && Integer.valueOf(String.valueOf(finalSiparisMid)) > 0) {
                                MessageBox.showAlert(UruneFiyatTanimlamaActivity.this, "Kayıt Başarılı..\n", false);
                            }
                            if (gelenurunMid != null && finalSiparisMid == 1) {
                                MessageBox.showAlert(UruneFiyatTanimlamaActivity.this, "İşlem Başarılı..\n", false);
                                urunAdapter.notifyDataSetChanged();


                            } else if (finalSiparisMid < 0)
                                MessageBox.showAlert(UruneFiyatTanimlamaActivity.this, "İşlem başarısız..\n", false);


                        }
                    });

                }
            }).start();
        }

    }
}


