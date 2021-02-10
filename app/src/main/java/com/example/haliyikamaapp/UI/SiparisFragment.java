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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;


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
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);


    }

    void senkronEdilmeyenKayitlariGonder() {
        for (Siparis item : db.siparisDao().getSenkronEdilmeyenAll()) {
            try {

                item.setMustId(null);
                item.setSenkronEdildi(null);
                item.setSubeMid(null);
                item.setMusteriMid(null);
                postSiparisListFromService(item, item.getMid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                            List<Siparis> urunVarMi = db.siparisDao().getSiparisForSiparisId(item.getId());
                            if (urunVarMi.size() > 0) {
                                item.setMid(urunVarMi.get(0).getMid());
                                item.setMusteriMid(urunVarMi.get(0).getMusteriMid());
                                item.setSubeMid(urunVarMi.get(0).getSubeMid());
                                db.siparisDao().updateSiparis(item);
                            } else
                                db.siparisDao().setSiparis(item);
                        }


                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                get_list();
                                senkronEdilmeyenKayitlariGonder();


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


    Siparis gelenSiparis;

    public void postSiparisListFromService(final Siparis siparis, final Long siparisMid) {
        progressDoalog.show();
        siparis.setMid(null);
        Call<Siparis> call = refrofitRestApi.postSiparis(OrtakFunction.authorization, OrtakFunction.tenantId, siparis);
        call.enqueue(new Callback<Siparis>() {
            @Override
            public void onResponse(Call<Siparis> call, Response<Siparis> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparis = response.body();
                    if (gelenSiparis != null) {

                        db.siparisDao().updateSiparisQuery(siparisMid, gelenSiparis.getId(), true);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // db.siparisDao().getSiparisAll()
                                List<SiparisDetay> siparisdetayList = db.siparisDetayDao().getSiparisDetayForMustId(siparisMid);
                                if (siparisdetayList != null && siparisdetayList.size() > 0) {
                                    List<Siparis> gidecekSiparis = db.siparisDao().getSiparisForSiparisId(gelenSiparis.getId());
                                    postSiparisDetayListFromService(siparisdetayList, gidecekSiparis);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Siparis> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


    List<SiparisDetay> gelenSiparisDetayLists;
    List<SiparisDetay> updateSiparisDetayList;
    String gelenSiparisDetayList = null;

    public void postSiparisDetayListFromService(List<SiparisDetay> siparisDetayList, final List<Siparis> gelenSiparis) {
        progressDoalog.show();
        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        final List<Long> midList = new ArrayList<>();
        JsonArray datas = new JsonArray();
        for (SiparisDetay item : siparisDetayList) {
            JsonObject object = new JsonObject();
            object.addProperty("id", item.getId());
            object.addProperty("siparisId", item.getSiparisId());
            object.addProperty("urunId", item.getUrunId());
            object.addProperty("olcuBirimId", item.getOlcuBirimId());
            object.addProperty("birimFiyat", item.getBirimFiyat());
            object.addProperty("miktar", item.getMiktar());
            midList.add(item.getMid());
            //  object.addProperty("musteriNotu", "");
            datas.add(object);
        }

        progressDoalog.show();
        Call<String> call = refrofitRestApi.postSiparisDetay(OrtakFunction.authorization, OrtakFunction.tenantId, datas.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisDetayList = response.body();
                    if (gelenSiparisDetayList != null) {


                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                Call<List<SiparisDetay>> call = refrofitRestApi.getSiparisDetayList("hy/siparis/siparisUrunler/" + gelenSiparis.get(0).getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                                call.enqueue(new Callback<List<SiparisDetay>>() {
                                    @Override
                                    public void onResponse(Call<List<SiparisDetay>> call, Response<List<SiparisDetay>> response) {
                                        if (!response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                                            return;
                                        }
                                        if (response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            gelenSiparisDetayLists = response.body();
                                            if (gelenSiparisDetayLists != null && gelenSiparisDetayLists.size() > 0) {
                                                updateSiparisDetayList = new ArrayList<SiparisDetay>();

                                                for (SiparisDetay item : gelenSiparisDetayLists) {
                                                    final List<Siparis> updateMustId = db.siparisDao().getSiparisForSiparisId(item.getSiparisId());
                                                    if (updateMustId != null && updateMustId.size() > 0) {
                                                        item.setSiparisMid(updateMustId.get(0).getMid());
                                                        item.setMustId(updateMustId.get(0).getMid());
                                                    }

                                                }

                                                for (SiparisDetay item : gelenSiparisDetayLists) {
                                                    List<SiparisDetay> urunVarMi = db.siparisDetayDao().getSiparisDetayForId(item.getId());
                                                    if (urunVarMi.size() > 0) {
                                                        item.setMid(urunVarMi.get(0).getMid());
                                                        item.setSiparisMid(urunVarMi.get(0).getSiparisMid());
                                                        item.setUrunMid(urunVarMi.get(0).getUrunMid());
                                                        item.setOlcuBirimMid(urunVarMi.get(0).getOlcuBirimMid());
                                                        // item.setSenkronEdildi(true);
                                                        db.siparisDetayDao().updateSiparisDetay(item);
                                                    } else
                                                        db.siparisDetayDao().setSiparisDetay(item);
                                                }
                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        get_list();

                                                    }
                                                });


                                            } else
                                                MessageBox.showAlert(getContext(), "Kayıt bulunamamıştır..", false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<SiparisDetay>> call, Throwable t) {
                                        progressDoalog.dismiss();
                                        MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
                                    }
                                });


                            }
                        });


                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }

}