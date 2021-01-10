package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.AutoCompleteAdapter.MusteriAutoCompleteAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class SiparisKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText sube_edittw, tarih_edittw, tutar_edittw, aciklama_edittw;
    AutoCompleteTextView musteri_edittw;
    Button kayit_ilerle_button, kayit_tamamla_button;
    String gelenSiparisMid, gelenMusteriMid;
    HaliYikamaDatabase db;
    private DatePickerDialog datePickerDialog;
    MusteriAutoCompleteAdapter autoCompleteAdapter;
    Long secilen_musteri_mid;
    String secilen_musteri_adi;
    CheckBox teslim_alinacak_checkbox;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.siparis_kayit_activity);
        init_item();
        initToolBar();


    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Sipariş Bilgileri");
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
        db = HaliYikamaDatabase.getInstance(SiparisKayitActivity.this);
        musteri_edittw = (AutoCompleteTextView) findViewById(R.id.musteri_autocomplete);
        sube_edittw = (EditText) findViewById(R.id.sube_adi);
        tarih_edittw = (EditText) findViewById(R.id.siparis_tarihi);
        tutar_edittw = (EditText) findViewById(R.id.siparis_tutar);
        aciklama_edittw = (EditText) findViewById(R.id.siparis_aciklama);
        kayit_ilerle_button = (Button) findViewById(R.id.musteri_kayit_button);
        kayit_tamamla_button = (Button) findViewById(R.id.musteri_tamamla_button);
        teslim_alinacak_checkbox = (CheckBox) findViewById(R.id.siparis_teslim_alinacakmi);


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        tarih_edittw.setText(dayOfMonth + "." + Integer.valueOf(month + 1) + "." + year);
        tarih_edittw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                datePickerDialog = new DatePickerDialog(SiparisKayitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                tarih_edittw.setText(day + "." + Integer.valueOf(month + 1) + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();

            }
        });

        gelenSiparisMid = getIntent().getStringExtra("siparisMid");
        if (gelenSiparisMid != null)
            getEditMode(Long.valueOf(gelenSiparisMid));

        gelenMusteriMid = getIntent().getStringExtra("musteriMid");
        if (gelenMusteriMid != null) {
            musteri_edittw.setEnabled(false);
            List<Musteri> allMusteri = db.musteriDao().getMusteriForMid(Long.valueOf(gelenMusteriMid));
            musteri_edittw.setText(allMusteri.get(0).getMusteriAdi() + " " + allMusteri.get(0).getMusteriSoyadi());
        }


        List<Musteri> allMusteri = db.musteriDao().getMusteriAll();
        autoCompleteAdapter = new MusteriAutoCompleteAdapter(this, R.layout.activity_main, android.R.layout.simple_dropdown_item_1line, allMusteri);
        musteri_edittw.setThreshold(2);
        musteri_edittw.setAdapter(autoCompleteAdapter);


        musteri_edittw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Musteri dty = (Musteri) parent.getAdapter().getItem(position);

                if (dty != null) {
                    secilen_musteri_mid = dty.getMid();
                    secilen_musteri_adi = dty.getMusteriAdi() + " " + dty.getMusteriSoyadi();
                }
            }
        });

    }

    public void musteriKayitOnclik(View v) {
        yeni_musteri_kayit(false);
    }

    public void musteriTamamlaOnclik(View v) {
        yeni_musteri_kayit(true);

    }

    void yeni_musteri_kayit(final Boolean tamamla) {
      /*  if(!tc_no_edittw.getText().toString().trim().equalsIgnoreCase("") || !adi_edittw.getText().toString().trim().equalsIgnoreCase("")
                || !soyadi_edittw.getText().toString().trim().equalsIgnoreCase("")|| !tel_no_edittw.getText().toString().trim().equalsIgnoreCase("")){
        }*/
        //  else{

        final Siparis siparis = new Siparis();
        siparis.setMusteriMid(gelenMusteriMid != null ? Long.valueOf(gelenMusteriMid) : secilen_musteri_mid);
        siparis.setSubeId(1L);
        siparis.setSiparisTarihi(tarih_edittw.getText().toString());
     /*   if (!tutar_edittw.getText().toString().equalsIgnoreCase(""))
            siparis.setSiparisTutar(Double.parseDouble(tutar_edittw.getText().toString()));*/
        siparis.setAciklama(aciklama_edittw.getText().toString());
        siparis.setTeslimAlinacak(teslim_alinacak_checkbox.isChecked() ? true : false);


        new Thread(new Runnable() {
            @Override
            public void run() {
                long yeniKayitSiparisMid = -1;
                if (gelenSiparisMid == null)
                    yeniKayitSiparisMid = db.siparisDao().setSiparis(siparis);
                if (gelenSiparisMid != null) {
                    siparis.setMid(Long.valueOf(gelenSiparisMid));
                    yeniKayitSiparisMid = db.siparisDao().updateSiparis(siparis);
                }

                final long finalYeniKayitSiparisMid = yeniKayitSiparisMid;
                SiparisKayitActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (gelenSiparisMid == null && Integer.valueOf(String.valueOf(finalYeniKayitSiparisMid)) > 0) {
                            if (tamamla) {
                                MessageBox.showAlert(SiparisKayitActivity.this, "Kayıt Başarılı..\n", false);
                                Intent i = new Intent(SiparisKayitActivity.this, MainActivity.class);
                                i.putExtra("gelenPage", "sipariş");
                                finish();
                            } else {
                                Intent i = new Intent(SiparisKayitActivity.this, SiparisDetayKayitActivity.class);
                                i.putExtra("siparisMid", String.valueOf(finalYeniKayitSiparisMid));
                                finish();
                                startActivity(i);
                            }

                        }
                        if (gelenSiparisMid != null && finalYeniKayitSiparisMid == 1) {
                            if (tamamla) {
                                MessageBox.showAlert(SiparisKayitActivity.this, "İşlem Başarılı..\n", false);
                                Intent i = new Intent(SiparisKayitActivity.this, MainActivity.class);
                                i.putExtra("gelenPage", "sipariş");
                                finish();
                            } else {
                                Intent i = new Intent(SiparisKayitActivity.this, SiparisDetayKayitActivity.class);
                                i.putExtra("siparisMid", String.valueOf(finalYeniKayitSiparisMid));
                                finish();
                                startActivity(i);
                            }

                        } else if (finalYeniKayitSiparisMid < 0)
                            MessageBox.showAlert(SiparisKayitActivity.this, "İşlem başarısız..\n", false);

                    }
                });

            }
        }).start();
        //}

    }


    void getEditMode(Long siparisMid) {
        List<Siparis> updateKayitList = db.siparisDao().getSiparisForMid(siparisMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid() == siparisMid) {
            if (updateKayitList.get(0).getMusteriMid() != null) {
                List<Musteri> allMusteri = db.musteriDao().getMusteriForMid(updateKayitList.get(0).getMusteriMid());
                musteri_edittw.setText(allMusteri.get(0).getMusteriAdi() + " " + allMusteri.get(0).getMusteriSoyadi());
                musteri_edittw.setEnabled(false);
            }
            if (updateKayitList.get(0).getTeslimAlinacak() != null && updateKayitList.get(0).getTeslimAlinacak()== true)
                teslim_alinacak_checkbox.setChecked(true);
            sube_edittw.setText("");
            tarih_edittw.setText(updateKayitList.get(0).getSiparisTarihi());
            //  tutar_edittw.setText(updateKayitList.get(0).getSiparisTutar() != null ? updateKayitList.get(0).getSiparisTutar().toString() : "");
            aciklama_edittw.setText(updateKayitList.get(0).getAciklama());


        }
    }

}

