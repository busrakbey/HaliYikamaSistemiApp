package com.example.haliyikamaapp.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.SiparisAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SiparisFragment extends Fragment {
    RelativeLayout relativeLayout;
    RecyclerView recyclerView;
    HaliYikamaDatabase db;
    SiparisAdapter siparisAdapter;
    Snackbar snackbar;
    Activity mActivity;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_siparis, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_item(view);
        getSiparisListFromService();
        //get_list();


    }

    void init_item(View view) {
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        db = HaliYikamaDatabase.getInstance(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.siparis_recyclerview);


    }

    void get_list() {
        List<Siparis> siparisler = db.siparisDao().getSiparisAll();
        siparisAdapter = new SiparisAdapter(getContext(), siparisler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(siparisAdapter);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(mContext) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final List<SiparisDetay> silinecekDetayListe = db.siparisDetayDao().getSiparisDetayForMustId(siparisAdapter.getData().get(position).getMid());
                final int silinenSiparisDetayMidSayisi = db.siparisDetayDao().deletedSiparisDetayForMustId(siparisAdapter.getData().get(position).getMid());
                if (silinenSiparisDetayMidSayisi == silinecekDetayListe.size()) {
                    final int silinenSiparisMid = db.siparisDao().deletedSiparisForMid(siparisAdapter.getData().get(position).getMid());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (silinenSiparisMid == 1) {
                                siparisAdapter.getData().remove(position);
                                siparisAdapter.notifyDataSetChanged();
                                snackbar = Snackbar
                                        .make(relativeLayout, "Kayıt silinmiştir.", Snackbar.LENGTH_LONG);
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();

                            } else
                                MessageBox.showAlert(getContext(), "İşlem başarısız..\n", false);

                        }
                    });


                } else
                    MessageBox.showAlert(getContext(), "İşlem başarısız..\n", false);

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);


    }

    @Override
    public void onResume() {
        super.onResume();
        get_list();
    }


    List<Siparis> gelenSiparisList;
    List<Siparis> updateSiparisList;

    void getSiparisListFromService() {

        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        progressDoalog.show();
        Call<List<Siparis>> call = refrofitRestApi.getSiparisList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Siparis>>() {
            @Override
            public void onResponse(Call<List<Siparis>> call, Response<List<Siparis>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisList = response.body();
                    if (gelenSiparisList != null && gelenSiparisList.size() > 0) {
                        for (Siparis item : gelenSiparisList) {
                            final List<Musteri> updateMustId = db.musteriDao().getMusteriForId(item.getMusteriId());
                            if (updateMustId != null && updateMustId.size() > 0)
                                item.setMusteriMid(updateMustId.get(0).getMid());
                        }

                        updateSiparisList = new ArrayList<Siparis>();
                        final List<Siparis> musteriList = db.siparisDao().getSiparisAll();
                        for (Siparis item : musteriList) {
                            for (Siparis i : gelenSiparisList) {
                                if (i.getId() == item.getId())
                                    updateSiparisList.add(i);
                            }
                        }
                        if (gelenSiparisList != null && gelenSiparisList.size() > 0)
                            gelenSiparisList.removeAll(updateSiparisList);
                        final List<Long> kayitList = db.siparisDao().setSiparisList(gelenSiparisList);
                        db.siparisDao().updateSiparisList(updateSiparisList);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (gelenSiparisList.size() != kayitList.size())
                                    MessageBox.showAlert(getContext(), "Sipariş listesi alınırken hata oluştu.", false);
                                else
                                    get_list();

                            }
                        });


                    } else
                        MessageBox.showAlert(getContext(), "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<List<Siparis>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
            mContext = (Context) context;
        }
    }

}