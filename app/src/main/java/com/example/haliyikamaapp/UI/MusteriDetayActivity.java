package com.example.haliyikamaapp.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.MusteriDetayAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MusteriDetayActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    MusteriDetayAdapter musteri_detay_adapter;
    RecyclerView recyclerView;
    String musteriMid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteCardColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.musteri_detay_activity);
        Intent intent = getIntent();
        musteriMid = intent.getStringExtra("musteriMid");
        get_list();

    }


    void get_list(){
        HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(MusteriDetayActivity.this);
        List<MusteriIletisim> kisiler = db.musteriIletisimDao().getMusteriIletisimForMid(Long.valueOf(musteriMid));

        musteri_detay_adapter = new MusteriDetayAdapter(MusteriDetayActivity.this, kisiler);
        recyclerView = (RecyclerView) findViewById(R.id.musteri_detay_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MusteriDetayActivity.this));
        recyclerView.setAdapter(musteri_detay_adapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(MusteriDetayActivity.this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final MusteriIletisim item = musteri_detay_adapter.getData().get(position);

                musteri_detay_adapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(relativeLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                snackbar.setAction("Geri Al", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        musteri_detay_adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);


    }


}
