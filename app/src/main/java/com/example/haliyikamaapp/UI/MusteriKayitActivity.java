package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MusteriKayitActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton ekleButon;
    EditText tc_no_edittw, adi_edittw, soyadi_edittw, tel_no_edittw, vergi_no_edittw;
    Spinner musteri_turu_spinner;
    Button kayit_button;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.musteri_kayit_activity);
        init_item();


    }

    public void initToolBar(String tittle) {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        tc_no_edittw = (EditText) findViewById(R.id.tc_no);
        soyadi_edittw = (EditText) findViewById(R.id.musteri_soyadi);
        adi_edittw = (EditText) findViewById(R.id.musteri_adi);
        tel_no_edittw = (EditText) findViewById(R.id.telefon_no);
        musteri_turu_spinner = (Spinner) findViewById(R.id.musteri_turu);
        vergi_no_edittw = (EditText) findViewById(R.id.vergş_no);
        kayit_button = (Button) findViewById(R.id.musteri_kayit_button);


    }

    public void musteriKayitOnclik(View v) {
        yeni_musteri_kayit();
    }

    void yeni_musteri_kayit() {
      /*  if(!tc_no_edittw.getText().toString().trim().equalsIgnoreCase("") || !adi_edittw.getText().toString().trim().equalsIgnoreCase("")
                || !soyadi_edittw.getText().toString().trim().equalsIgnoreCase("")|| !tel_no_edittw.getText().toString().trim().equalsIgnoreCase("")){
        }*/
        //  else{
        final HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(MusteriKayitActivity.this);
        final Musteri musteri = new Musteri();
        musteri.setMusteriAdi(adi_edittw.getText().toString());
        musteri.setMusteriSoyadi(soyadi_edittw.getText().toString());
        musteri.setTelefonNumarasi(tel_no_edittw.getText().toString());
        musteri.setTcKimlikNo(tc_no_edittw.getText().toString());
        musteri.setMusteriTuru(String.valueOf(musteri_turu_spinner.getSelectedItemPosition()));
        musteri.setVergiKimlikNo(vergi_no_edittw.getText().toString());

        // db.musteriDao().setMusteri(musteri);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final long musteriMid = db.musteriDao().setMusteri(musteri);

                MusteriKayitActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Integer.valueOf(String.valueOf(musteriMid)) > 0) {
                            MessageBox.showAlert(MusteriKayitActivity.this, "Kayıt Başarılı..\n", false);
                            Intent i = new Intent(MusteriKayitActivity.this, MusteriDetayKayitActivity.class);
                            i.putExtra("musteriMid" , String.valueOf(musteriMid));
                            startActivity(i);

                        }

                        else
                            MessageBox.showAlert(MusteriKayitActivity.this, "Kayıt başarısız..\n", false);

                    }
                });

            }
        }).start();
        //}

    }
}
