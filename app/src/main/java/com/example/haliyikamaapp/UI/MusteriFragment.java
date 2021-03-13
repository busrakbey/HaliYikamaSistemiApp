package com.example.haliyikamaapp.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.haliyikamaapp.ToolLayer.SwipeHelper;
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
    SwipeRefreshLayout swipeRefreshLayout;
    public MusteriAdapter adapter;
    RecyclerView recyclerView;
    HaliYikamaDatabase db;
    Snackbar snackbar;
    Activity mActivity;
    Context mContext;
    MenuItem searchMenuItem;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_musteri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_item(view);
        senkronEdilmeyenKayitlariGonder();
        getMusteriListFromService();


    }

    void init_item(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.relativeLayout);
        db = HaliYikamaDatabase.getInstance(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.musteri_recyclerview);

        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        swipe_item();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    senkronEdilmeyenKayitlariGonder();
                    getMusteriListFromService();
                    (( MainActivity) mActivity).getServisler();
                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.colorAccent, R.color.colorPrimaryDark);


    }

    void senkronEdilmeyenKayitlariGonder() {
        for (Musteri item : db.musteriDao().getSenkronEdilmeyenMusteriAll()) {
            try {

                item.setMustId(null);
               /* item.setxKoor(Double.NaN);
                item.setyKoor(Double.NaN);*/
                item.setSenkronEdildi(null);
                // item.setSubeId(null);
                postMusteriListFromService(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void get_list() {
        List<Musteri> kisiler = db.musteriDao().getMusteriAll();
        adapter = new MusteriAdapter(getContext(), kisiler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
       /* SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(mContext) {
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

           /* }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);*/


    }

    @Override
    public void onResume() {
        super.onResume();
        senkronEdilmeyenKayitlariGonder();
        getMusteriListFromService();
        get_list();
    }

    List<Musteri> gelenMusteriList;

    void getMusteriListFromService() {

        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        Call<List<Musteri>> call = refrofitRestApi.getMusteriList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Musteri>>() {
            @Override
            public void onResponse(Call<List<Musteri>> call, Response<List<Musteri>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    //  //  MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenMusteriList = response.body();
                    if (gelenMusteriList != null && gelenMusteriList.size() > 0) {
                        if (db.musteriDao().getMusteriAll().size() == 0)
                            db.musteriDao().setMusteriList(gelenMusteriList);
                        else {

                            for (Musteri i : gelenMusteriList) {
                                Boolean yeniKayitMi = true;

                                i.setSenkronEdildi(true);
                                for (Musteri all : db.musteriDao().getMusteriAll()) {

                                    if (all.getId() != null && all.getId().toString().equalsIgnoreCase(i.getId().toString())) {
                                        yeniKayitMi = false;
                                        i.setMid(all.getMid());
                                        db.musteriDao().updateMusteri(i);
                                    }
                                }

                                if (yeniKayitMi)
                                    db.musteriDao().setMusteri(i);
                            }
                        }


                        get_list();


                    }


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

    Musteri gelenMusteri;

    public void postMusteriListFromService(final Musteri musteri) {
        progressDoalog.show();
        Call<Musteri> call;
        final Long musteriMid = musteri.getMid();
        musteri.setMid(null);
        musteri.setMusteriSoyadi("  ");
        musteri.setxKoor(null);
        musteri.setyKoor(null);
        musteri.setSubeMid(null);
        if (musteri.getId() != null)
            call = refrofitRestApi.putMusteriList("hy/musteri/" + musteri.getId().toString(), OrtakFunction.authorization, OrtakFunction.tenantId, musteri);
        else
            call = refrofitRestApi.postMusteriList(OrtakFunction.authorization, OrtakFunction.tenantId, musteri);

        call.enqueue(new Callback<Musteri>() {
            @Override
            public void onResponse(Call<Musteri> call, Response<Musteri> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    //  MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenMusteri = response.body();
                    if (gelenMusteri != null) {

                        db.musteriDao().updateMusteriQuery(musteriMid, gelenMusteri.getId(), true);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {



                             /*   if (gelenMusteriList.size() != kayitList.size())
                                    MessageBox.showAlert(MusteriKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    get_list();*/

                            }
                        });


                    } else
                        MessageBox.showAlert(getContext(), "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<Musteri> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }

    void swipe_item() {
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView, false) {
            @Override
            public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Ara", null

                       /* AppCompatResources.getDrawable(
                                getContext(),
                                android.R.drawable.ic_menu_call
                        )*/,
                        Color.parseColor("#FF0000"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                if (!adapter.getData().get(position).getTelefonNumarasi().equalsIgnoreCase("")) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel: " + adapter.getData().get(position).getTelefonNumarasi()));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.getApplicationContext().startActivity(intent);
                                }
                            }
                        }
                ));


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Sms", null

                      /*  AppCompatResources.getDrawable(
                                getContext(),
                                android.R.drawable.ic_menu_edit
                        )*/,
                        Color.parseColor("#FF9800"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                Intent musteri = new Intent(mContext, MusteriKayitActivity.class);
                                musteri.putExtra("musteriMid", String.valueOf(adapter.getData().get(position).getMid()));
                                musteri.putExtra("musteriId", String.valueOf(adapter.getData().get(position).getId()));
                                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.getApplicationContext().startActivity(musteri);
                            }
                        }
                ));


            }
        };


    }


}