package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.haliyikamaapp.R;


public class HomeFragment extends Fragment {
    Toolbar toolbar;
    CardView siparis_button, musteri_button, ayarlar_button, musteri_gorevlerim_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initToolBar(view);
        set_item(view);


    }

    void set_item(View view) {
        String[] prgmNameList = {"Müşteri", "Sipariş", "Müşteri ve Görevlerim", "Ayarlar  \n"};
        int[] prgmImages = {R.drawable.shadow_musteri, R.drawable.shadow_siparis, R.drawable.shadow_gorevler, R.drawable.shadow_ayarlar};

        musteri_button = (CardView) view.findViewById(R.id.musteri_cardview);
        siparis_button = (CardView) view.findViewById(R.id.siparis_cardview);
        musteri_gorevlerim_button = (CardView) view.findViewById(R.id.mus_gorevlerim_cardview);
        ayarlar_button = (CardView) view.findViewById(R.id.ayarlar_cardview);

        musteri_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new MusteriFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                ((MainActivity) getActivity()).initToolBar("Müşteri");
                ((MainActivity) getActivity()).ekleButon.setVisibility(View.VISIBLE);
            }
        });

        siparis_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new MusteriGorevlerimFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                ((MainActivity) getActivity()).initToolBar("Müşrei ve Görevlerim");
                ((MainActivity) getActivity()).ekleButon.setVisibility(View.VISIBLE);
            }
        });

        musteri_gorevlerim_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")

            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new MusteriGorevlerimFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                ((MainActivity) getActivity()).initToolBar("Müşrei ve Görevlerim");
                ((MainActivity) getActivity()).ekleButon.setVisibility(View.VISIBLE);
            }
        });

        ayarlar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).initToolBar("Ayarlar");

            }
        });


       /* GridView gridView = view.findViewById(R.id.main_fragment_grid_view);
        AnaMenuAdapter adapter = new AnaMenuAdapter(getContext(), prgmNameList, prgmImages);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (position == 0) {

                }

                if (position == 1) {
                    Fragment selectedFragment = new SiparisFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    ((MainActivity) getActivity()).initToolBar("Sipariş");
                    ((MainActivity) getActivity()).ekleButon.setVisibility(View.VISIBLE);


                }

                if (position == 2) {
                    Fragment selectedFragment = new MusteriGorevlerimFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    ((MainActivity) getActivity()).initToolBar("Müşrei ve Görevlerim");
                    ((MainActivity) getActivity()).ekleButon.setVisibility(View.VISIBLE);

                }

                if (position == 3) {
                    ((MainActivity) getActivity()).initToolBar("Ayarlar");

                }

            }
        });*/
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