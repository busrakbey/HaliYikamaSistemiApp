package com.example.haliyikamaapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.haliyikamaapp.Adapter.AnaMenuAdapter;


public class HomeFragment extends Fragment {
    Toolbar toolbar;

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
        String[] prgmNameList = {"Müşteri", "Sipariş", "Müşteri ve Görevlerim", "Ayarlar  \n" };
        int[] prgmImages = {R.drawable.shadow_musteri, R.drawable.shadow_siparis, R.drawable.shadow_gorevler, R.drawable.shadow_ayarlar};
        GridView gridView = view.findViewById(R.id.main_fragment_grid_view);
        AnaMenuAdapter adapter = new AnaMenuAdapter(getContext(), prgmNameList, prgmImages);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (position == 0) {
                    Fragment selectedFragment = new MusteriFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    ((MainActivity)getActivity()).initToolBar("Müşteri");
                    ((MainActivity)getActivity()).ekleButon.setVisibility(View.VISIBLE);
                }

                if (position == 1) {
                    Fragment selectedFragment = new SiparisFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    ((MainActivity)getActivity()).initToolBar("Sipariş");
                    ((MainActivity)getActivity()).ekleButon.setVisibility(View.VISIBLE);


                }

                if (position == 2) {
                    Fragment selectedFragment = new MusteriGorevlerimFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    ((MainActivity)getActivity()).initToolBar("Müşrei ve Görevlerim");
                    ((MainActivity)getActivity()).ekleButon.setVisibility(View.VISIBLE);

                }

                if (position == 3) {
                    ((MainActivity)getActivity()).initToolBar("Ayarlar");

                }

            }
        });
    }

    private void initToolBar(View view) {
        try {

            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            // toolbar.setNavigationIcon(R.drawable.left);

            ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((MainActivity)getActivity()). getSupportActionBar().setHomeButtonEnabled(true);
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Ana Sayfa");

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


            ((MainActivity)getActivity()).setSupportActionBar(toolbar);

        } catch (Exception e) {
            e.printStackTrace();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}