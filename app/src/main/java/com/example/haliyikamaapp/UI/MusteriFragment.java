package com.example.haliyikamaapp.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.MusteriAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.DefaultException;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RSOperator;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MusteriFragment extends Fragment {
    ConstraintLayout relativeLayout;
   public  MusteriAdapter adapter;
    RecyclerView recyclerView;
    HaliYikamaDatabase db;
    Snackbar snackbar;
    Activity mActivity;
    Context mContext;
    MenuItem searchMenuItem;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_musteri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_item(view);
        getMusteriListFromService();


    }

    void init_item(View view) {
        relativeLayout = (ConstraintLayout) view.findViewById(R.id.relativeLayout);
        db = HaliYikamaDatabase.getInstance(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.musteri_recyclerview);


    }

    void get_list() {
        List<Musteri> kisiler = db.musteriDao().getMusteriAll();
        adapter = new MusteriAdapter(getContext(), kisiler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(mContext) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final List<MusteriIletisim> silinecekDetayListe = db.musteriIletisimDao().getMusteriIletisimForMustId(adapter.getData().get(position).getMid());
                final int silinenMusteriDetayMid = db.musteriIletisimDao().deletedMusteriIletisimForMustId(adapter.getData().get(position).getMid());
                if (silinenMusteriDetayMid == silinecekDetayListe.size()) {
                    final int silinenMusteriMid = db.musteriDao().deletedMusteriForMid(adapter.getData().get(position).getMid());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (silinenMusteriMid == 1) {
                                adapter.getData().remove(position);
                                adapter.notifyDataSetChanged();
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

                /*snackbar.setAction("Geri Al", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });*/

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

    List<Musteri> gelenMusteriList;
    List<Musteri> updateMusteriList;

    void getMusteriListFromService() {

        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        progressDoalog.show();
        Call<List<Musteri>> call = refrofitRestApi.getMusteriList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Musteri>>() {
            @Override
            public void onResponse(Call<List<Musteri>> call, Response<List<Musteri>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenMusteriList = response.body();
                    if (gelenMusteriList != null && gelenMusteriList.size() > 0) {
                        updateMusteriList = new ArrayList<Musteri>();
                        final List<Musteri> musteriList = db.musteriDao().getMusteriAll();
                        for (Musteri item : musteriList) {
                            for (Musteri i : gelenMusteriList) {
                                if (i.getId() == item.getId())
                                    updateMusteriList.add(i);
                            }
                        }
                        if (gelenMusteriList != null && gelenMusteriList.size() > 0)
                            gelenMusteriList.removeAll(updateMusteriList);
                        final List<Long> kayitList = db.musteriDao().setMusteriList(gelenMusteriList);
                        db.musteriDao().updateMusteriList(updateMusteriList);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (gelenMusteriList.size() != kayitList.size())
                                    MessageBox.showAlert(getContext(), "Müşteri listesi alınırken hata oluştu.", false);
                                else
                                    get_list();

                            }
                        });


                    } else
                        MessageBox.showAlert(getContext(), "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<List<Musteri>> call, Throwable t) {
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