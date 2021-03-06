package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.AutoCompleteAdapter.MusteriAutoCompleteAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sms;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.example.haliyikamaapp.ToolLayer.SharedPreferencesSettings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiparisKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    Spinner sube_spinner, kaynak_spinner;
    EditText tarih_edittw, tutar_edittw, aciklama_edittw;
    AutoCompleteTextView musteri_edittw;
    Button kayit_ilerle_button, kayit_tamamla_button;
    String gelenSiparisMid, gelenMusteriMid, gelenSiparisId;
    HaliYikamaDatabase db;
    private DatePickerDialog datePickerDialog;
    MusteriAutoCompleteAdapter autoCompleteAdapter;
    CheckBox teslim_alinacak_checkbox;
    Musteri secilenMusteri;
    public List<String> subeListString;
    Sube secilenSube;
    String gelenSubeId, gelenSubeMid, gelenMusteriId, seciliSubeAdi;
    Kaynak secili_kaynak;
    List<Kaynak> kaynakList;
    List<String> kaynakListString;
    ImageView siparisBarkodYazdirButton;
    EditText teslimEdilmeTarihi, teslimAlinmaTarihi;
    SharedPreferencesSettings sharedPreferencesSettings;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;




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
        sube_spinner = (Spinner) findViewById(R.id.sube_adi);
        tarih_edittw = (EditText) findViewById(R.id.siparis_tarihi);
        tutar_edittw = (EditText) findViewById(R.id.siparis_tutar);
        aciklama_edittw = (EditText) findViewById(R.id.siparis_aciklama);
        kayit_ilerle_button = (Button) findViewById(R.id.musteri_kayit_button);
        kayit_tamamla_button = (Button) findViewById(R.id.musteri_tamamla_button);
        teslim_alinacak_checkbox = (CheckBox) findViewById(R.id.siparis_teslim_alinacakmi);
        kaynak_spinner = (Spinner) findViewById(R.id.siparis_kaynak_spinner);
        kaynakList = new ArrayList<Kaynak>();
        kaynakListString = new ArrayList<String>();
        siparisBarkodYazdirButton = (ImageView) findViewById(R.id.siparis_barkod_yazdir);
        teslimAlinmaTarihi = (EditText) findViewById(R.id.siparis_teslim_alinma_tarihi);
        teslimEdilmeTarihi = (EditText) findViewById(R.id.siparis_teslim_tarihi);
        sharedPreferencesSettings = new SharedPreferencesSettings();
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();


        progressDoalog = new ProgressDialog(SiparisKayitActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);



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

        teslimAlinmaTarihi.setText(dayOfMonth + "." + Integer.valueOf(month + 1) + "." + year);
        teslimAlinmaTarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                datePickerDialog = new DatePickerDialog(SiparisKayitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                teslimAlinmaTarihi.setText(day + "." + Integer.valueOf(month + 1) + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();

            }
        });


        teslimEdilmeTarihi.setText(dayOfMonth + "." + Integer.valueOf(month + 1) + "." + year);
        teslimEdilmeTarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                datePickerDialog = new DatePickerDialog(SiparisKayitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                teslimEdilmeTarihi.setText(day + "." + Integer.valueOf(month + 1) + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();

            }
        });

        if (sharedPreferencesSettings.getValues(getApplicationContext(), "siparis_gun") != null &&
                !sharedPreferencesSettings.getValues(getApplicationContext(), "siparis_gun").equals("")) {
            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DATE, sharedPreferencesSettings.getValues(getApplicationContext(), "siparis_gun"));
            final int year2 = calendar2.get(Calendar.YEAR);
            final int month2 = calendar2.get(Calendar.MONTH);
            final int dayOfMonth2 = calendar2.get(Calendar.DAY_OF_MONTH);
            teslimEdilmeTarihi.setText(dayOfMonth2 + "." + Integer.valueOf(month2 + 1) + "." + year2);
        }

        siparisBarkodYazdirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bluetooth = new Intent(SiparisKayitActivity.this, BluetoothActivity.class);
                bluetooth.putExtra("siparisId", gelenSiparisId != null ? gelenSiparisId : null);
                bluetooth.putExtra("siparisMid", gelenSiparisMid != null ? gelenSiparisMid : null);
                bluetooth.putExtra("subeAdi", seciliSubeAdi);
                startActivity(bluetooth);
            }
        });

        gelenSiparisMid = getIntent().getStringExtra("siparisMid");
        gelenSubeId = getIntent().getStringExtra("subeId");
        gelenSubeMid = getIntent().getStringExtra("subeMid");
        gelenMusteriMid = getIntent().getStringExtra("musteriMid");
        gelenMusteriId = getIntent().getStringExtra("musteriId");
        gelenSiparisId = getIntent().getStringExtra("siparisId");

        if (gelenSiparisMid != null && gelenSiparisMid.equalsIgnoreCase("null"))
            gelenSiparisMid = null;
        if (gelenSubeId != null && gelenSubeId.equalsIgnoreCase("null"))
            gelenSubeId = null;
        if (gelenSubeMid != null && gelenSubeMid.equalsIgnoreCase("null"))
            gelenSubeMid = null;
        if (gelenMusteriMid != null && gelenMusteriMid.equalsIgnoreCase("null"))
            gelenMusteriMid = null;
        if (gelenMusteriId != null && gelenMusteriId.equalsIgnoreCase("null"))
            gelenMusteriId = null;
        if (gelenSiparisId != null && gelenSiparisId.equalsIgnoreCase("null"))
            gelenSiparisId = null;

        List<Musteri> musterimList = null;
        if (gelenSubeId == null) {
            musterimList = db.musteriDao().getMusteriForId(Long.valueOf(gelenMusteriId));
            if (musterimList.size() == 0)
                musterimList = db.musteriDao().getMusteriForMid(Long.valueOf(gelenMusteriMid));

            if (musterimList.get(0).getSubeId() != null)
                gelenSubeId = String.valueOf(musterimList.get(0).getSubeId());
            else
                sube_spinner.setEnabled(false);

        }


        final List<Sube> subeList = db.subeDao().getSubeAll();
        subeListString = new ArrayList<String>();
        subeListString.add("Şube");
        for (Sube item : subeList)
            subeListString.add(item.getSubeAdi());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, subeListString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sube_spinner.setEnabled(false);
        sube_spinner.setAdapter(adapter);
        sube_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Sube selected = subeList.get(position - 1);
                    secilenSube = selected;

                } else
                    secilenSube = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sube_spinner.setSelection(0);

        List<Musteri> allMusteri = db.musteriDao().getMusteriAll();
        autoCompleteAdapter = new MusteriAutoCompleteAdapter(this, R.layout.activity_main, android.R.layout.simple_dropdown_item_1line, allMusteri);
        musteri_edittw.setThreshold(2);
        musteri_edittw.setAdapter(autoCompleteAdapter);
        musteri_edittw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Musteri dty = (Musteri) parent.getAdapter().getItem(position);

                if (dty != null) {
                    secilenMusteri = dty;
                    List<Sube> allSube = null;

                    if (secilenMusteri.getSubeMid() != null)
                        allSube = db.subeDao().getSubeForId(Long.valueOf(secilenMusteri.getSubeMid()));
                    if (secilenMusteri.getSubeId() != null)
                        allSube = db.subeDao().getSubeForId(Long.valueOf(secilenMusteri.getSubeId()));


                    if (allSube != null && allSube.size() > 0) {
                        sube_spinner.setEnabled(false);
                        sube_spinner.setSelection(subeListString.indexOf(allSube.get(0).getSubeAdi()));
                    }


                }
            }
        });


        kaynakList.add(null);
        kaynakList = db.kaynakDao().getkaynakAll();
        kaynakListString.add("Kaynak");
        for (Kaynak item : kaynakList) {
            kaynakListString.add(item.getKaynakAdi());
        }

        try {
            secili_kaynak = null;
            for (Kaynak item : kaynakList)
                if (item.getSecilenKaynakMi() != null && item.getSecilenKaynakMi() == true)
                    secili_kaynak = item;


        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> dataAdapter_kaynak = new ArrayAdapter<String>(SiparisKayitActivity.this, android.R.layout.simple_spinner_item, kaynakListString);
        dataAdapter_kaynak.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        kaynak_spinner.setAdapter(dataAdapter_kaynak);
        // kaynak_spinner.setSelection(0);


        if (kaynakList.size() > 0) {
            for (Kaynak item : kaynakList) {
                kaynak_spinner.setSelection(kaynakListString.indexOf(secili_kaynak.getKaynakAdi()));
            }
        }


        kaynak_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = kaynakListString.get(position);
                    if (valInfo != null) {
                        secili_kaynak = kaynakList.get(position - 1);
                    }
                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (gelenMusteriMid != null) {
            musteri_edittw.setEnabled(false);
            List<Musteri> musteri = db.musteriDao().getMusteriForMid(Long.valueOf(gelenMusteriMid));
            musteri_edittw.setText(musteri.get(0).getMusteriAdi() + " " + musteri.get(0).getMusteriSoyadi());
        }

        List<Sube> allSube = null;
        if (gelenSubeMid != null) {
            sube_spinner.setEnabled(false);
            allSube = db.subeDao().getSubeForMid(Long.valueOf(gelenSubeMid));
            seciliSubeAdi = allSube.get(0).getSubeAdi();
        }
        if (gelenSubeId != null) {
            sube_spinner.setEnabled(false);
            allSube = db.subeDao().getSubeForId(Long.valueOf(gelenSubeId));
            seciliSubeAdi = allSube.get(0).getSubeAdi();

        }


        if (allSube != null && allSube.size() > 0)
            sube_spinner.setSelection(subeListString.indexOf(allSube.get(0).getSubeAdi()));

        if (gelenSiparisMid != null)
            getEditMode(Long.valueOf(gelenSiparisMid));
        else
            teslim_alinacak_checkbox.setChecked(true);

    }

    public void musteriKayitOnclik(View v) {
        yeni_musteri_kayit(false);
    }

    public void musteriTamamlaOnclik(View v) {
        yeni_musteri_kayit(true);

    }

    void yeni_musteri_kayit(final Boolean tamamla) {
        if (secili_kaynak == null) {
            MessageBox.showAlert(SiparisKayitActivity.this, "Lütfen kaynak seçimi yapınız.", false);
        } else {
            final Siparis siparis = new Siparis();

            siparis.setMusteriMid(gelenMusteriMid != null ? Long.valueOf(gelenMusteriMid) : null);
            siparis.setMusteriId(gelenMusteriId != null ? Long.valueOf(gelenMusteriId) : null);
            siparis.setSubeId(gelenSubeId != null ? Long.valueOf(gelenSubeId) : secilenSube.getId());
            siparis.setSubeMid(gelenSubeMid != null ? Long.valueOf(gelenSubeMid) : secilenSube.getMid());
            siparis.setSiparisTarihi(tarih_edittw.getText().toString());
            siparis.setAciklama(aciklama_edittw.getText().toString());
            siparis.setTeslimAlinacak(teslim_alinacak_checkbox.isChecked() ? true : false);
            siparis.setSiparisDurumu(teslim_alinacak_checkbox.isChecked() ? "Teslim Alınacak" : "");
            siparis.setId(gelenSiparisId != null ? Long.valueOf(gelenSiparisId) : null);
            siparis.setSenkronEdildi(false);
            siparis.setTeslimAlmaTarihi(teslimAlinmaTarihi.getText().toString());
            siparis.setTeslimEtmeTarihi(teslimAlinmaTarihi.getText().toString());
            siparis.setKaynakId(secili_kaynak != null ? secili_kaynak.getId() : null);
            siparis.setKaynakMid(secili_kaynak != null ? secili_kaynak.getMid() : null);


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
                                    MessageBox.showAlert(SiparisKayitActivity.this, "Sipariş oluşturuldu. \n", false);
                                    Intent i = new Intent(SiparisKayitActivity.this, MainActivity.class);
                                    i.putExtra("gelenPage", "müşteri_görevlerim");
                                    i.putExtra("siparisMid", String.valueOf(finalYeniKayitSiparisMid));
                                    i.putExtra("siparisId", siparis.getId() != null ? String.valueOf(siparis.getId()) : null);
                                    finish();
                                    startActivity(i);

                                } else {
                                    Intent i = new Intent(SiparisKayitActivity.this, SiparisDetayKayitActivity.class);
                                    i.putExtra("siparisMid", String.valueOf(finalYeniKayitSiparisMid));
                                    i.putExtra("subeId", gelenSubeId != null ? gelenSubeId : secilenSube.getId());
                                    i.putExtra("subeMid", gelenSubeMid != null ? gelenSubeMid : secilenSube.getMid());
                                    finish();
                                    startActivity(i);
                                }
                            }
                            if (gelenSiparisMid != null && finalYeniKayitSiparisMid == 1) {
                                if (tamamla) {
                                    MessageBox.showAlert(SiparisKayitActivity.this, "Sipariş güncellendi.\n", false);
                                    Intent i = new Intent(SiparisKayitActivity.this, MainActivity.class);
                                    i.putExtra("gelenPage", "müşteri_görevlerim");
                                    finish();
                                    startActivity(i);

                                } else {
                                    Intent i = new Intent(SiparisKayitActivity.this, SiparisDetayKayitActivity.class);
                                    i.putExtra("siparisMid", String.valueOf(finalYeniKayitSiparisMid));
                                    i.putExtra("subeId", gelenSubeId != null ? gelenSubeId : secilenSube.getId());
                                    i.putExtra("subeMid", gelenSubeMid != null ? gelenSubeMid : secilenSube.getMid());
                                    finish();
                                    startActivity(i);
                                }
                            } else if (finalYeniKayitSiparisMid < 0)
                                MessageBox.showAlert(SiparisKayitActivity.this, "İşlem başarısız..\n", false);

                        }
                    });
                }
            }).start();
        }
    }

    void getEditMode(Long siparisMid) {
        List<Siparis> updateKayitList = db.siparisDao().getSiparisForMid(siparisMid);
        if (updateKayitList != null && updateKayitList.size() > 0 && updateKayitList.get(0).getMid().toString().equalsIgnoreCase(siparisMid.toString())) {
            if (updateKayitList.get(0).getMusteriMid() != null) {
                List<Musteri> allMusteri = db.musteriDao().getMusteriForMid(updateKayitList.get(0).getMusteriMid());
                musteri_edittw.setText(allMusteri.get(0).getMusteriAdi() + " " + allMusteri.get(0).getMusteriSoyadi());
                musteri_edittw.setEnabled(false);
            }

            if (updateKayitList.get(0).getMusteriId() != null) {
                List<Musteri> allMusteri = db.musteriDao().getMusteriForId(updateKayitList.get(0).getMusteriId());
                musteri_edittw.setText(allMusteri.get(0).getMusteriAdi() + " " + allMusteri.get(0).getMusteriSoyadi());
                musteri_edittw.setEnabled(false);
            }
            if (updateKayitList.get(0).getTeslimAlinacak() != null && updateKayitList.get(0).getTeslimAlinacak() == true)
                teslim_alinacak_checkbox.setChecked(true);
            tarih_edittw.setText(updateKayitList.get(0).getSiparisTarihi());
            aciklama_edittw.setText(updateKayitList.get(0).getAciklama());
            //siparisId = updateKayitList.get(0).getId();

            List<Sube> subeList = db.subeDao().getSubeForId(updateKayitList.get(0).getSubeId());
            if (subeList.size() > 0) {
                for (Sube item : subeList) {
                    sube_spinner.setSelection(subeListString.indexOf(item.getSubeAdi()));
                }
            }


            List<Kaynak> kaynakList = db.kaynakDao().getkaynakForMid(updateKayitList.get(0).getKaynakMid());
            if (kaynakList.size() > 0) {
                for (Kaynak item : kaynakList) {
                    kaynak_spinner.setSelection(kaynakListString.indexOf(item.getKaynakAdi()));
                }
            }
            aciklama_edittw.setText(updateKayitList.get(0).getAciklama());

            if (updateKayitList.get(0).getTeslimAlinacak() != null && updateKayitList.get(0).getTeslimAlinacak() == true)
                teslim_alinacak_checkbox.setChecked(true);


        }

    }

    public String createBarkocImage(String siparisNo) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(siparisNo, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            // imageView.setImageBitmap(bitmap);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String barkodDegerString = Base64.encodeToString(b, Base64.DEFAULT);
            return barkodDegerString;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }







}

