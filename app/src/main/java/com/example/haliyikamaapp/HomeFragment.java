package com.example.haliyikamaapp;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.haliyikamaapp.Adapter.AnaMenuAdapter;


public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        set_item(view);


    }

    void set_item(View view) {
        String[] prgmNameList = {"Müşteri", "Sipariş", "Müşteri ve Görevlerim", "Ayarlar  \n"};
        int[] prgmImages = {R.drawable.shadow_musteri, R.drawable.shadow_siparis, R.drawable.shadow_gorevler, R.drawable.shadow_ayarlar};
        GridView gridView = view.findViewById(R.id.main_fragment_grid_view);
        AnaMenuAdapter adapter = new AnaMenuAdapter(getContext(), prgmNameList, prgmImages);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (position == 0) {
                    Fragment selectedFragment = new MusteriFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                if (position == 1) {
                    Fragment selectedFragment = new SiparisFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                }

                if (position == 2) {
                    Fragment selectedFragment = new MusteriGorevlerimFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                if (position == 3) {

                }

            }
        });
    }
}