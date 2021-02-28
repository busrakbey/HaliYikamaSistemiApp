package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.R;

import java.util.List;


public class OzetFragment extends Fragment {
    Toolbar toolbar;
    CardView siparis_button, musteri_button, ayarlar_button, musteri_gorevlerim_button, hesap_cardview, kaynak_cardview;
    TextView toplam_musteri_tw, toplam_siparis_tw;
    HaliYikamaDatabase db;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ozet, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         //initToolBar(view);
        set_item(view);


    }

    void set_item(View view) {



    }

    private void initToolBar(View view) {
        try {

            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            // toolbar.setNavigationIcon(R.drawable.left);

            ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Ana Sayfa");

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


            ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        } catch (Exception e) {
            e.printStackTrace();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



}