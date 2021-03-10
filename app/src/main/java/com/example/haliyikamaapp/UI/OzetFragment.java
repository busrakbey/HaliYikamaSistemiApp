package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.haliyikamaapp.Adapter.GorevlerAdapter;
import com.example.haliyikamaapp.Adapter.SiparisAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.MultiSelectionSpinner;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OzetFragment extends Fragment {
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    HaliYikamaDatabase db;
    Activity mActivity;
    Context mContext;
    RecyclerView recyclerView;
    SiparisAdapter siparisAdapter;
    MultiSelectionSpinner spinner;
    SearchView search_view;
    ImageView userButton;
    public List<String> durumList;
    RadioButton filtre_tarih_bugun, filtre_tarih_yarin;
    String dateFiltre, searchviewText = "";
    RadioGroup tarihRadioGrup;
    Boolean tarihBugundeMi = true;
    Kaynak secili_kaynak;
    String gorevNotu = "", secilenTeslimEtDurumu = "", girilenTahsilatTutari = "", gorevTamamlamaNotu = "";
    Boolean hesapKapatCheckbox = false, teslimEdilmeTarihiMi = false;
    String tahsilEdilecekTuar = "";
    List<SiparisDetay> siparisDetayList = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    int position;
    Long seciliKaynakId = null;


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

        try {
            List<Kaynak> kaynakList = db.kaynakDao().getkaynakAll();
            seciliKaynakId = null;
            for (Kaynak item : kaynakList)
                if (item.getSecilenKaynakMi() != null && item.getSecilenKaynakMi() == true)
                    seciliKaynakId = item.getId();


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (seciliKaynakId == null)
            MessageBox.showAlert(getContext(), "Lütfen öncelikle kaynak seçimi yapınız.", false);
        else {
            get_list( "", seciliKaynakId);
            getSiparisListFromService();
        }


    }

    void set_item(View view) {

        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        db = HaliYikamaDatabase.getInstance(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.musterigorevlerim_recyclerview);
        userButton = (ImageView) view.findViewById(R.id.toolbar_right_subtitle2);
        filtre_tarih_bugun = (RadioButton) view.findViewById(R.id.filtre_tarih_bugun);
        filtre_tarih_yarin = (RadioButton) view.findViewById(R.id.filtre_tarih_yarin);
        durumList = new ArrayList<String>();
        tarihRadioGrup = (RadioGroup) view.findViewById(R.id.tarih_radio_group);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh_layout);

        search_view = (SearchView) view.findViewById(R.id.search_view);
        search_view.setFocusable(true);
        search_view.setIconified(false);
        search_view.clearFocus();
        setupSearchView();



        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Could hide open views here if you wanted. //
            }
        };

        recyclerView.setOnScrollListener(onScrollListener);


    }


    List<Siparis> gelenSiparisList;
    List<SiparisDetay> gelenSiparisDetayList2;
    List<SiparisDetay> updateSiparisDetayList2;

    void getSiparisListFromService() {

        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        Call<List<Siparis>> call = refrofitRestApi.getSiparisList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Siparis>>() {
            @Override
            public void onResponse(Call<List<Siparis>> call, Response<List<Siparis>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenSiparisList = response.body();
                    if (gelenSiparisList != null && gelenSiparisList.size() > 0) {


                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (final Siparis item : gelenSiparisList) {
                                    List<Siparis> urunVarMi = db.siparisDao().getSiparisForSiparisId(item.getId());
                                    if (urunVarMi.size() > 0) {
                                        item.setMid(urunVarMi.get(0).getMid());
                                        item.setMusteriMid(urunVarMi.get(0).getMusteriMid());
                                        item.setSubeMid(urunVarMi.get(0).getSubeMid());
                                        item.setKaynakMid(urunVarMi.get(0).getKaynakMid());
                                        db.siparisDao().updateSiparis(item);
                                    } else
                                        db.siparisDao().setSiparis(item);


                                    Call<List<SiparisDetay>> call = refrofitRestApi.getSiparisDetayList("hy/siparis/siparisUrunler/" + item.getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                                    call.enqueue(new Callback<List<SiparisDetay>>() {
                                        @Override
                                        public void onResponse(Call<List<SiparisDetay>> call, Response<List<SiparisDetay>> response) {
                                            if (!response.isSuccessful()) {
                                                progressDoalog.dismiss();
                                                //  MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                                                return;
                                            }
                                            if (response.isSuccessful()) {
                                                progressDoalog.dismiss();
                                                gelenSiparisDetayList2 = response.body();

                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        for (SiparisDetay i : gelenSiparisDetayList2) {
                                                            List<Siparis> siparis = db.siparisDao().getSiparisForSiparisId(item.getId());
                                                            List<SiparisDetay> urunVarMi = db.siparisDetayDao().getSiparisDetayForId(i.getId());
                                                            i.setMustId(siparis.get(0).getMid());
                                                            i.setSiparisMid(siparis.get(0).getMid());
                                                            if (urunVarMi.size() > 0) {
                                                                i.setMid(urunVarMi.get(0).getMid());
                                                                i.setUrunMid(urunVarMi.get(0).getUrunMid());
                                                                i.setOlcuBirimMid(urunVarMi.get(0).getOlcuBirimMid());
                                                                db.siparisDetayDao().updateSiparisDetay(i);
                                                            } else
                                                                db.siparisDetayDao().setSiparisDetay(i);
                                                        }


                                                    }
                                                });


                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<SiparisDetay>> call, Throwable t) {
                                            progressDoalog.dismiss();
                                            //MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
                                        }
                                    });


                                }


                            }
                        });


                    } /*else
                        MessageBox.showAlert(getContext(), "Kayıt bulunamamıştır..", false);*/
                }
                get_list("", seciliKaynakId);
            }

            @Override
            public void onFailure(Call<List<Siparis>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }
        });


    }


    void get_list( String searchViewText, Long seciliKaynakId) {
        List<Siparis> siparisler = null;
        siparisler = db.siparisDao().getSiparisTelsimEdildiList(seciliKaynakId,searchviewText);
        siparisAdapter = new SiparisAdapter(getContext(), siparisler, true);
        siparisAdapter.notifyDataSetChanged();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(siparisAdapter);
        siparisAdapter.notifyDataSetChanged();


    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
            mContext = (Context) context;
        }
    }

    private void setupSearchView() {

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {

        }

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Search Results activity
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchviewText = newText;
                get_list( newText, seciliKaynakId);


                return false;
            }
        });
        search_view.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                get_list("", seciliKaynakId);
                searchviewText = "";
                return false;
            }
        });
    }




}