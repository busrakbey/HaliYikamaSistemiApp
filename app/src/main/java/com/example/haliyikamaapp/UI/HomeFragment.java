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


public class HomeFragment extends Fragment {
    Toolbar toolbar;
    CardView siparis_button, musteri_button, ayarlar_button, musteri_gorevlerim_button, hesap_cardview, kaynak_cardview;
    TextView toplam_musteri_tw, toplam_siparis_tw;
    HaliYikamaDatabase db;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         //initToolBar(view);
        set_item(view);


    }

    void set_item(View view) {
        musteri_button = (CardView) view.findViewById(R.id.musteri_cardview);
        siparis_button = (CardView) view.findViewById(R.id.siparis_cardview);
        musteri_gorevlerim_button = (CardView) view.findViewById(R.id.mus_gorevlerim_cardview);
        ayarlar_button = (CardView) view.findViewById(R.id.ayarlar_cardview);
        hesap_cardview = (CardView) view.findViewById(R.id.hesap_cardview);
        kaynak_cardview = (CardView) view.findViewById(R.id.kaynaklar_cardview);


        musteri_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
             /*   Fragment selectedFragment = new MusteriFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                ((MainActivity2) getActivity()).initToolBar("Müşteri");
                ((MainActivity2) getActivity()).ekleButon.setVisibility(View.VISIBLE);
                ((MainActivity2) getActivity()).click_ekle_button("Müşteri");*/
                ((MainActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_musteri);

            }
        });

        siparis_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                /*Fragment selectedFragment = new SiparisFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                ((MainActivity2) getActivity()).initToolBar("Sipariş");
                ((MainActivity2) getActivity()).ekleButon.setVisibility(View.VISIBLE);
                ((MainActivity2) getActivity()).click_ekle_button("Sipariş");*/
             //   ((MainActivity2) getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_siparis);


            }
        });

        musteri_gorevlerim_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")

            @Override
            public void onClick(View view) {
               /* Fragment selectedFragment = new MusteriGorevlerimFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                ((MainActivity2) getActivity()).initToolBar("Görevlerim");
                ((MainActivity2) getActivity()).ekleButon.setVisibility(View.VISIBLE);*/
                ((MainActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_musterigorevlerim);

            }
        });

        ayarlar_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                Fragment selectedFragment = new AyarlarFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                ((MainActivity) getActivity()).initToolBar("Ayarlar");
                ((MainActivity) getActivity()).ekleButon.setVisibility(View.GONE);


            }
        });

        hesap_cardview.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HesapActivity.class);
                startActivity(intent);

            }
        });

        kaynak_cardview.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), KaynakActivity.class);
                startActivity(intent);


            }
        });


        toplam_musteri_tw = (TextView) view.findViewById(R.id.toplam_kayit_musteri);
        toplam_siparis_tw = (TextView) view.findViewById(R.id.toplam_kayit_siparis);
        db = HaliYikamaDatabase.getInstance(getContext());
        List<Siparis> siparisler = db.siparisDao().getSiparisAll();
        List<Musteri> musteriler = db.musteriDao().getMusteriAll();
        toplam_siparis_tw.setText("Toplam Sipariş: " + siparisler.size());
        toplam_musteri_tw.setText("Toplam Müşteri: " + musteriler.size());




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