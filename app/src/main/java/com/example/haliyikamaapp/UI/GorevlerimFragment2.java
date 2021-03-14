package com.example.haliyikamaapp.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.haliyikamaapp.Adapter.GorevlerAdapter;
import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.GorevFomBilgileri;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.MultiSelectionSpinner;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.example.haliyikamaapp.ToolLayer.SwipeHelper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GorevlerimFragment2 extends Fragment {
    ProgressDialog progressDoalog, pd;
    RefrofitRestApi refrofitRestApi;
    HaliYikamaDatabase db;
    Activity mActivity;
    Context mContext;
    RecyclerView recyclerView;
    GorevlerAdapter gorevlerAdapter;
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
    List<SiparisDetay> siparisDetayListGorevTamamlama = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    int position;
    Long seciliKaynakId = null;
    ImageView barcode_scanner;
    Long okunanSiparisId = null;
    Gorevler urunEklendiktenSonraGorev= null;
    Button gorev_kaynak_secimi;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gorevlerim2i, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_item(view);
        try {
            List<Kaynak> kaynakList = db.kaynakDao().getkaynakAll();
            seciliKaynakId = null;
            for (Kaynak item : kaynakList)
                if (item.getSecilenKaynakMi() != null && item.getSecilenKaynakMi() == true)
                    seciliKaynakId = item.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }


        get_list(null, "", seciliKaynakId);

     /*   ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

                try {
                    getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, 10, TimeUnit.SECONDS);*/

        final Handler handler = new Handler();
        final int delay = 120000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                System.out.println("myHandler: here!"); // Do your work here
                try {
                    getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);


       /* if (urunEklendiktenSonraGorev != null) {
            List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(urunEklendiktenSonraGorev.getSiparisId());
            if (siparisList.size() > 0) {
                siparisDetayListGorevTamamlama = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());
                if (siparisDetayListGorevTamamlama != null && siparisDetayListGorevTamamlama.size() > 0)
                    postSiparisListFromService(siparisList.get(0), siparisList.get(0).getMid());
            }
        }*/


    }





    void siparis_islemleri() {
        for (Siparis item : db.siparisDao().getSiparisAll()) {
            if (item.getMusteriId() == null && db.musteriDao().getMusteriForMid(item.getMusteriMid()).size() > 0) {
                Musteri musteri = db.musteriDao().getMusteriForMid(item.getMusteriMid()).get(0);
                db.siparisDao().updateSiparisMusteriId(item.getMid(), musteri.getId());
                item.setMusteriId(musteri.getId());
            }
            item.setMustId(null);
            item.setSenkronEdildi(null);
            item.setMusteriMid(null);
            postSiparisListFromService(item, item.getMid());
        }
        getSiparisListFromService();


        try {
            getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            okunanSiparisId = Long.valueOf(scanningResult.getContents());

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://34.91.29.223/siparisiniz/" + okunanSiparisId));
            startActivity(browserIntent);
        } else
            Toast.makeText(getContext(), "Sipariş bulunamamıştır.", Toast.LENGTH_SHORT).show();


    }

    void init_item(View view) {
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(getContext());
        pd = new ProgressDialog(getContext());

        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("Sistem");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        db = HaliYikamaDatabase.getInstance(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.musterigorevlerim_recyclerview);
        userButton = (ImageView) view.findViewById(R.id.toolbar_right_subtitle2);
        filtre_tarih_bugun = (RadioButton) view.findViewById(R.id.filtre_tarih_bugun);
        filtre_tarih_yarin = (RadioButton) view.findViewById(R.id.filtre_tarih_yarin);
        durumList = new ArrayList<String>();
        if (durumList.size() == 0 || durumList.get(0).equalsIgnoreCase("")) {
            durumList.add("Teslim Alınacak");
            durumList.add("Teslime Hazır");
            durumList.add("Teslime Çıktı");
            durumList.add("Yıkanacak");
        }
        tarihRadioGrup = (RadioGroup) view.findViewById(R.id.tarih_radio_group);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh_layout);
        barcode_scanner = (ImageView) view.findViewById(R.id.barcode_scanner);
        gorev_kaynak_secimi = (Button) view.findViewById(R.id.gorev_kaynak_secimi);

        barcode_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator scanIntegrator = new IntentIntegrator(mActivity);
                scanIntegrator.initiateScan();
            }
        });

        gorev_kaynak_secimi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), KaynakActivity.class);
                i.putExtra("gorevdenMi" , true);
                startActivity(i);
            }
        });


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


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pd = ProgressDialog.show(getContext(), "Sistem",
                        "Tüm bilgiler güncellenmektedir.Bu işlem yaklaşık 5 sn. sürmektedir. \nLütfen bekleyiniz.", true);
                pd.setCancelable(false);
                pd.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
                pd.show();
                try {
                    siparis_islemleri();
                    ((MainActivity) getContext()).getServisler();
                    ((MainActivity) getContext()).getMusteriListFromService();
                    get_list(durumList, "", seciliKaynakId);

                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        pd.dismiss();

                    }
                }, 7000);


            }
        });


        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.colorAccent, R.color.colorPrimaryDark);

        tarihRadioGrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (filtre_tarih_bugun.isChecked())
                    tarihBugundeMi = true;
                if (filtre_tarih_yarin.isChecked())
                    tarihBugundeMi = false;

                get_list(durumList, searchviewText, seciliKaynakId);
            }
        });


        if (filtre_tarih_bugun.isChecked())

            dateFiltre = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());


        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Sistem");
                builder.setMessage("Çıkış yapılacaktır. Devam etmek istiyor musunuz?");
                builder.setNegativeButton("Hayır", null);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.userDao().deleteUserAll();
                        db.sIlceDao().deleteIlceAll();
                        db.sIlDao().deleteIlAll();
                        db.musteriDao().deleteMusteriAll();
                        db.siparisDao().deleteSiparisAll();
                        db.gorevlerDao().deleteGorevAll();
                        db.gorevFomBilgileriDao().deleteGorevAll();
                        db.subeDao().deleteSubeAll();
                        db.urunDao().deleteUrunAll();
                        db.urunFiyatDao().deleteUrunFiyatAll();
                        db.urunSubeDao().deleteUrunSubeAll();
                        db.olcuBirimDao().deleteOlcuBirimAll();
                        db.sTenantDao().deleteSTenantAll();
                        db.permissionsDao().deletePermissionsAll();
                        db.siparisDetayDao().deleteSiparisDetayAll();
                        db.bolgeDao().deleteBolgeAll();
                        db.musteriTuruDao().deleteMusteriTuruAll();
                        db.authToken().deleteTokenAll();
                        db.kaynakDao().deletekaynakAll();
                        db.hesapDao().deleteHesapAll();
                        db.userPermissionsDao().deleteUserAll();
                        db.smsDao().deleteSmsAll();
                        getActivity().finish();
                        Intent a = new Intent(getActivity(), LoginActivity.class);
                        startActivity(a);
                        // System.exit(0);


                    }
                });
                builder.show();

            }
        });
        spinner = (MultiSelectionSpinner) view.findViewById(R.id.spinner_gorev_durum);
        search_view = (SearchView) view.findViewById(R.id.search_view);
        search_view.setFocusable(true);
        search_view.setIconified(false);
        search_view.clearFocus();
        setupSearchView();


        List<String> list = new ArrayList<String>();

        list.add("Teslim Alınacaklar");
        list.add("Yıkanacaklar");
        list.add("Araca Yüklenecekler");
        list.add("Teslim Edilecekler");
        spinner.setItems(list);
        spinner.setTitle("Sipariş Durumu");


        spinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices, MultiSelectionSpinner spinner) {

            }

            @Override
            public void selectedStrings(List<String> strings, MultiSelectionSpinner spinner) {


                durumList = new ArrayList<String>();
                durumList = strings;
                get_list(durumList, searchviewText, seciliKaynakId);


            }


        });


        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView, false) {
            @Override
            public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                position = viewHolder.getAdapterPosition();


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Tamamla", null,

                       /* AppCompatResources.getDrawable(
                                getContext(),
                                R.drawable.check
                        ),*/
                        Color.parseColor("#FF9800"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {

                                if (gorevlerAdapter.getData() != null) {
                                    if (gorevlerAdapter.getData().get(position).getTaskName().equalsIgnoreCase("Yikama")) {
                                        try {
                                            gorevTamamlaPostService(gorevlerAdapter.getData().get(position).getTaskId(), null, "Yikama");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (gorevlerAdapter.getData().get(position).getTaskName().equalsIgnoreCase("Araca Yükle")) {
                                        alert_dialog_gorev_tamamla("Araca Yükle", gorevlerAdapter.getData().get(position).getTaskId());
                                    }

                                    if (gorevlerAdapter.getData().get(position).getTaskName().equalsIgnoreCase("TeslimEt")) {


                                        List<GorevFomBilgileri> formDetay = db.gorevFomBilgileriDao().getGorevId(gorevlerAdapter.getData().get(position).getTaskId());
                                        for (GorevFomBilgileri item : formDetay) {
                                            if (item.getName().equalsIgnoreCase("Tahsil Edilecek Tutar"))
                                                tahsilEdilecekTuar = item.getValue();
                                        }

                                        alert_dialog_gorev_tamamla("TeslimEt", gorevlerAdapter.getData().get(position).getTaskId());
                                    }

                                    if (gorevlerAdapter.getData().get(position).getTaskName().equalsIgnoreCase("TeslimAl")) {
                                        siparisDetayListGorevTamamlama = null;
                                        urunEklendiktenSonraGorev = gorevlerAdapter.getData().get(pos);
                                        List<Gorevler> gorevlerList = db.gorevlerDao().getGorevForId(Long.valueOf(gorevlerAdapter.getData().get(position).getTaskId()));
                                        if (gorevlerList.size() > 0) {
                                            List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
                                            if (siparisList.size() > 0) {
                                                siparisDetayListGorevTamamlama = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
                                                if (siparisDetayListGorevTamamlama.size() > 0) {
                                                    alert_dialog_gorev_tamamla("TeslimAl", gorevlerAdapter.getData().get(position).getTaskId());

                                                } else {
                                                    siparisDetayListGorevTamamlama = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());
                                                    if (siparisDetayListGorevTamamlama != null && siparisDetayListGorevTamamlama.size() > 0)
                                                        postSiparisListFromService(siparisList.get(0), siparisList.get(0).getMid());
                                                    else {
                                                       // MessageBox.showAlert(getContext(), "Görevi tamamlamak için ürün eklemeniz lazım.", false);
                                                        Toast.makeText(mContext,"Görevi tamamlamak için ürün eklemeniz lazım.",Toast.LENGTH_LONG).show();
                                                        Intent i = new Intent(mContext, SiparisDetayActivity.class);
                                                        // i.putExtra("gelenPage", "sipariş");
                                                        i.putExtra("siparisId", String.valueOf(gorevlerAdapter.getData().get(position).getSiparisId()));
                                                        i.putExtra("subeMid", siparisList.get(0).getSubeMid() != null ? siparisList.get(0).getSubeMid().toString() : null);
                                                        i.putExtra("subeId", siparisList.get(0).getSubeId() != null ? siparisList.get(0).getSubeId().toString() : null);
                                                        i.putExtra("siparisMid", siparisList.size() > 0 ? siparisList.get(0).getMid().toString() : null);
                                                        i.putExtra("gorevId", gorevlerAdapter.getData().get(position).getTaskId());
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        mContext.getApplicationContext().startActivity(i);
                                                        return;

                                                    }

                                                }


                                            }

                                      /*else

                                            alert_dialog_gorev_tamamla("TeslimAl", gorevlerAdapter.getData().get(position).getTaskId());*/

                                        }


                                    }

                                }
                            }
                        }
                ));


            }
        };


    }

    public void get_list(List<String> siparisDurumu, String searchViewText, Long seciliKaynakId) {
        List<Gorevler> gorevlerList;

        // durumList = new ArrayList<String>();

        if (durumList.size() == 0 || durumList.get(0).equalsIgnoreCase("")) {
            durumList.add("Teslim Alınacak");
            durumList.add("Teslime Hazır");
            durumList.add("Teslime Çıktı");
            durumList.add("Yıkanacak");
        }
        // gorevlerList = db.gorevlerDao().getGorevAllForKaynakId(seciliKaynakId);


        if (searchViewText == null)
            searchViewText = "";
        if ((siparisDurumu != null && siparisDurumu.size() > 0) || (searchViewText != null && !searchViewText.equalsIgnoreCase("")))

            gorevlerList = db.gorevlerDao().getquery(searchViewText, siparisDurumu, seciliKaynakId);

           /* if (tarihBugundeMi)
                gorevlerList = db.gorevlerDao().getQueryIleriTarihTeslimAlmaTarihi(searchViewText, siparisDurumu, dateFiltre, seciliKaynakId);
            else
                gorevlerList = db.gorevlerDao().getGorevQueryPrameterTeslimAlmaTarihi(searchViewText, siparisDurumu, dateFiltre, seciliKaynakId);*/

        else
            //  gorevlerList = db.gorevlerDao().getQueryIleriTarihTeslimAlmaTarihi(searchViewText, siparisDurumu, dateFiltre, seciliKaynakId);
            gorevlerList = db.gorevlerDao().getGorevAllForKaynakId(seciliKaynakId);
        gorevlerAdapter = new GorevlerAdapter(getContext(), gorevlerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gorevlerAdapter);
        gorevlerAdapter.notifyDataSetChanged();
        recyclerView.invalidate();


    }


    String gelenGorevList = null;

    public void getGorevlerimFromService(Long kullaniciId) {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        JSONObject object = new JSONObject();

        try {
            JSONObject object2 = new JSONObject();
            object2.put("musteriler", new JSONArray());
            object2.put("subeler", new JSONArray());
            object2.put("kaynaklar", new JSONArray());
            object2.put("durum", new JSONArray());
            object2.put("ilkTarih", null);
            object2.put("sonTarih", null);
            object.put("model", object2);

            JSONObject object1 = new JSONObject();
            object1.put("pageSize", 99999);
            object1.put("pageNumber", 0);
            object1.put("sortField", "siparis_tarihi");
            object1.put("sortOrder", -1);

            object.put("pageParams", object1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<String> call = refrofitRestApi.getKullaniciGorevList("hy/process/userTasks", OrtakFunction.authorization, OrtakFunction.tenantId, object.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {

                    progressDoalog.dismiss();
                    //  MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {

                    gelenGorevList = response.body();
                    if (!gelenGorevList.equalsIgnoreCase("")) {
                        db.gorevlerDao().deleteGorevAll();

                        List<Gorevler> allGorevList = db.gorevlerDao().getGorevAll();
                        JSONObject gelenObject = null;
                        try {

                            gelenObject = new JSONObject(gelenGorevList);
                            JSONArray rowArray = new JSONArray(gelenObject.getString("rows"));
                            for (int i = 0; i < rowArray.length(); i++) {
                                Boolean yeniKayitMi = true;
                                Boolean kayitSilinecekMi = true;
                                JSONObject rowObject = new JSONObject(rowArray.getString(i));
                                List<Gorevler> gelenGorevList = Arrays.asList(gson.fromJson(rowObject.toString(), Gorevler.class));
                                if (allGorevList != null && allGorevList.size() == 0)
                                    db.gorevlerDao().setGorevList(gelenGorevList);
                                else {
                                    for (Gorevler gelenGorev : gelenGorevList) {
                                        for (Gorevler item : allGorevList) {
                                            kayitSilinecekMi = true;

                                            if (item.getSiparisId() != null && item.getSiparisId().toString().equalsIgnoreCase(gelenGorev.getSiparisId().toString())) {
                                                db.gorevlerDao().deletedGorevForSiparisId(gelenGorev.getSiparisId());
                                                yeniKayitMi = false;
                                                db.gorevlerDao().setGorev(gelenGorev);
                                            }
                                            if (item.getTaskId() == gelenGorev.getTaskId())
                                                kayitSilinecekMi = false;

                                        }
                                        if (yeniKayitMi)
                                            db.gorevlerDao().setGorev(gelenGorev);
                                        if (kayitSilinecekMi)
                                            db.gorevlerDao().deletedGorevForTaskId(gelenGorev.getTaskId());
                                    }

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        get_list(durumList, "", seciliKaynakId);
                        if (gorevTamamlandiMi) {
                            MessageBox.showAlert(getContext(), "Görev başarıyla tamamlanmıştır..", false);
                            // Toast.makeText(getContext(), "Görev başarıyla tamamlanmıştır.", Toast.LENGTH_LONG).show();
                            gorevTamamlandiMi = false;
                        }

                        progressDoalog.dismiss();
                        get_list(durumList, "", seciliKaynakId);

                        List<Gorevler> totalGorevList = db.gorevlerDao().getGorevAll();
                        for (Gorevler item : totalGorevList) {
                            try {
                                getFormBilgilerimList(item.getTaskId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    String gelenFormList = null;

    public void getFormBilgilerimList(final Long gorevId) throws Exception {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();

        Call<String> call = refrofitRestApi.getGorevTamamlamakIcinGerekliFormList("fw/process/taskFormData/" + gorevId, OrtakFunction.authorization,
                OrtakFunction.tenantId, "application/json", "application/json");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    return;
                }
                if (response.isSuccessful()) {
                    gelenFormList = response.body();
                    if (!gelenFormList.equalsIgnoreCase("")) {
                        JSONArray gelenArray = null;
                        db.gorevFomBilgileriDao().deletedGorevForGorevId(gorevId);
                        try {
                            gelenArray = new JSONArray(gelenFormList);
                            for (int i = 0; i < gelenArray.length(); i++) {
                                JSONObject rowObject = new JSONObject(gelenArray.getString(i));
                                rowObject.put("gorevId", gorevId);
                                if (!rowObject.getString("formValues").equalsIgnoreCase("null"))
                                    rowObject.put("formValues", rowObject.getString("formValues"));
                                List<GorevFomBilgileri> gorevlerList = Arrays.asList(gson.fromJson(rowObject.toString(), GorevFomBilgileri.class));
                                db.gorevFomBilgileriDao().setGorevList(gorevlerList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDoalog.dismiss();
                        get_list(durumList, searchviewText, seciliKaynakId);


                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(mContext, "Hata Oluştu.. " + t.getMessage(), false);
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

    public  void urunEkleGorevTamamla(){
        if (urunEklendiktenSonraGorev != null) {
            List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(urunEklendiktenSonraGorev.getSiparisId());
            if (siparisList.size() > 0) {
                siparisDetayListGorevTamamlama = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());
                if (siparisDetayListGorevTamamlama != null && siparisDetayListGorevTamamlama.size() > 0)
                    postSiparisListFromService(siparisList.get(0), siparisList.get(0).getMid());
            }
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
                get_list(durumList, newText, seciliKaynakId);


                return false;
            }
        });
        search_view.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                get_list(durumList, "", seciliKaynakId);
                searchviewText = "";
                return false;
            }
        });
    }


    void alert_dialog_gorev_tamamla(String taskName, final Long taskId) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        final View mView = getLayoutInflater().inflate(R.layout.not_popup_window, null);
        final EditText call_message = (EditText) mView.findViewById(R.id.call_message);
        final Button vazgec_button = (Button) mView.findViewById(R.id.not_vazgec_button);
        final Button kaydet_button = (Button) mView.findViewById(R.id.not_kaydet_button);
        final TextView tittle = (TextView) mView.findViewById(R.id.call_tittle);
        final EditText tahsilatTutari = (EditText) mView.findViewById(R.id.gorev_tahsilat_tutari);
        final EditText gorev_tahsil_edilen_tutar = (EditText) mView.findViewById(R.id.gorev_tahsil_edilen_tutar);


        LinearLayout not_linear = (LinearLayout) mView.findViewById(R.id.gorev_not);
        LinearLayout spinenr_linerar = (LinearLayout) mView.findViewById(R.id.gorev_teslim_et_linear);
        final Spinner spinner_teslim_edildi_mi = (Spinner) mView.findViewById(R.id.gorev_tamamla_spinner);
        final CheckBox gorev_hesap_kapat = (CheckBox) mView.findViewById(R.id.gorev_hesap_kapat);
        final EditText gorev_teslim_et_notlar = (EditText) mView.findViewById(R.id.gorev_teslim_edilecek_not);
        final LinearLayout gorev_araca_yukle_linear = (LinearLayout) mView.findViewById(R.id.gorev_araca_yukle_linear);
        final Spinner gorev_kaynak_spinner = (Spinner) mView.findViewById(R.id.gorev_kaynak_spinner);


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        tittle.setText("Görev Tamamla");


        if (taskName.equalsIgnoreCase("Araca Yükle")) {
            dialog.show();
            secili_kaynak = null;
            List<Kaynak> kaynakList = new ArrayList<Kaynak>();
            final List<String> kaynakListString = new ArrayList<String>();
            gorev_araca_yukle_linear.setVisibility(View.VISIBLE);
            kaynakList.add(null);
            kaynakList = db.kaynakDao().getkaynakAllForArac();
            kaynakListString.add("Kaynak");
            for (Kaynak item : kaynakList) {
                kaynakListString.add(item.getKaynakAdi());
            }

            ArrayAdapter<String> dataAdapter_kaynak = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, kaynakListString);
            dataAdapter_kaynak.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            gorev_kaynak_spinner.setAdapter(dataAdapter_kaynak);
            gorev_kaynak_spinner.setSelection(0);

            final List<Kaynak> finalKaynakList = kaynakList;
            gorev_kaynak_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        String valInfo = kaynakListString.get(position);
                        if (valInfo != null) {
                            secili_kaynak = finalKaynakList.get(position - 1);
                        }
                    } else {
                        secili_kaynak = null;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            kaydet_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    secili_kaynak.getId();

                    try {
                        gorevTamamlaPostService(taskId, secili_kaynak.getId(), "Araca Yükle");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    secili_kaynak = null;
                }


            });

        }


        if (taskName.equalsIgnoreCase("TeslimEt")) {
            spinenr_linerar.setVisibility(View.VISIBLE);
            dialog.show();

            gorevNotu = call_message.getText().toString();

            List<String> list2 = new ArrayList<String>();
            list2.add("Teslim Edildi Mi?");
            list2.add("Evet");
            list2.add("Hayır");

            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, list2);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_teslim_edildi_mi.setAdapter(dataAdapter2);

            if (tahsilEdilecekTuar != null)
                gorev_tahsil_edilen_tutar.setText(tahsilEdilecekTuar);

            kaydet_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        if (spinner_teslim_edildi_mi.getSelectedItemPosition() == 1)
                            secilenTeslimEtDurumu = "Evet";
                        if (spinner_teslim_edildi_mi.getSelectedItemPosition() == 2)
                            secilenTeslimEtDurumu = "Hayır";


                        girilenTahsilatTutari = tahsilatTutari.getText().toString();
                        hesapKapatCheckbox = gorev_hesap_kapat.isChecked() == true ? true : false;
                        gorevTamamlamaNotu = gorev_teslim_et_notlar.getText().toString();

                        if (spinner_teslim_edildi_mi.getSelectedItemPosition() == 0) {
                            MessageBox.showAlert(getContext(), "Teslimat durumunu seçmeden görev tamamlanamaz.", false);
                            return;
                        } else if (tahsilatTutari.getText().toString().equalsIgnoreCase("") || tahsilatTutari.getText().toString().equalsIgnoreCase("0")) {
                            MessageBox.showAlert(getContext(), "Tahsilat tutarını girmeden görev tamamlanamaz.", false);
                            return;
                        } else
                            gorevTamamlaPostService(taskId, null, "TeslimEt");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }


            });

        }


        if (taskName.equalsIgnoreCase("TeslimAl")) {
            not_linear.setVisibility(View.VISIBLE);
            dialog.show();


            kaydet_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        gorevNotu = call_message.getText().toString();
                        gorevTamamlaPostService(taskId, null, "TeslimAl");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }


            });

        }


        vazgec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


    public Boolean gorevTamamlandiMi = false;

    public void gorevTamamlaPostService(final Long gorevId, final Long kaynakId, String taskName) throws Exception {
        progressDoalog.show();
        gorevTamamlandiMi = false;
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        JSONObject disObje = new JSONObject();
        final JSONArray urunArray = new JSONArray();

        if (taskName.equalsIgnoreCase("TeslimEt")) {
            disObje.put("tahsilatTutari", !girilenTahsilatTutari.equalsIgnoreCase("") ? Double.valueOf(girilenTahsilatTutari) : 0);
            disObje.put("teslimEdildi", secilenTeslimEtDurumu);
            disObje.put("tahsilEdilecekTutar", tahsilEdilecekTuar != null ? Double.valueOf(tahsilEdilecekTuar) : 0);
            disObje.put("hesabiKapat", hesapKapatCheckbox == false ? "" : true);
            disObje.put("teslimatNotu", gorevTamamlamaNotu);

        }

        if (taskName.equalsIgnoreCase("TeslimAl")) {
            disObje.put("notlar", gorevNotu);
            if (siparisDetayListGorevTamamlama != null && siparisDetayListGorevTamamlama.size() > 0)
                for (SiparisDetay item : siparisDetayListGorevTamamlama) {
                    JSONObject icObje = new JSONObject();
                    icObje.put("id", item.getId());
                    icObje.put("siparisId", item.getSiparisId());
                    icObje.put("urunId", item.getUrunId());
                    icObje.put("urunAdi", item.getUrunId() != null ? db.urunDao().getUrunForId(item.getUrunId()).get(0).getUrunAdi() : null);
                    icObje.put("olcuBirimId", item.getOlcuBirimId());
                    icObje.put("olcuBirimAdi", item.getOlcuBirimId() != null ? db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).get(0).getOlcuBirimi() : null);
                    icObje.put("birimFiyat", item.getBirimFiyat());
                    icObje.put("miktar", item.getMiktar());
                    icObje.put("toplamTutar", item.getBirimFiyat() * item.getMiktar());
                    icObje.put("musteriNotu", db.siparisDao().getSiparisForSiparisId(item.getSiparisId()).get(0).getAciklama());
                    urunArray.put(icObje);
                }
            disObje.put("urunListesi", urunArray);
        }

        if (secili_kaynak != null || kaynakId != null) {
            disObje.put("kaynak", secili_kaynak.getId() != null ? secili_kaynak.getId() : null);
        }


        Call<String> call = refrofitRestApi.postGorevTamamla("fw/process/completeTask/" + gorevId, OrtakFunction.authorization, OrtakFunction.tenantId, disObje.toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                secili_kaynak = null;
                if (!response.isSuccessful()) {

                    progressDoalog.dismiss();
                    //  MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    get_list(durumList, "", seciliKaynakId);


                    getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
                    gorevTamamlandiMi = true;
                    //   MessageBox.showAlert(getContext(), "Görev başarıyla tamamlanmıştır..", false);


                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                //   MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
            }

        });
    }


    Siparis gelenSiparis;

    public void postSiparisListFromService(final Siparis siparis, final Long siparisMid) {
        progressDoalog.show();
        siparis.setMid(null);
        siparis.setBarkod(null);
        siparis.setSubeMid(null);
        siparis.setSiparisTarihi(siparis.getSiparisTarihi() + " 00:00");
        siparis.setKaynakMid(null);
        siparis.setSenkronEdildi(null);
        siparis.setMusteriMid(null);
        siparis.setMustId(null);
        Call<Siparis> call;
        if (siparis.getId() == null)
            call = refrofitRestApi.postSiparis(OrtakFunction.authorization, OrtakFunction.tenantId, siparis);
        else
            call = refrofitRestApi.putSiparis("hy/siparis/" + siparis.getId(), OrtakFunction.authorization, OrtakFunction.tenantId, siparis);
        call.enqueue(new Callback<Siparis>() {
            @Override
            public void onResponse(Call<Siparis> call, Response<Siparis> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
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
                                List<SiparisDetay> siparisdetayList = db.siparisDetayDao().getSiparisDetayForMustId(siparisMid);

                                List<SiparisDetay> senkronEdilecekler = new ArrayList<>();

                                for (SiparisDetay item : siparisdetayList) {
                                    if (item.getSenkronEdildi() == null || item.getSenkronEdildi() == false)
                                        senkronEdilecekler.add(item);
                                }

                                if (senkronEdilecekler != null && senkronEdilecekler.size() > 0) {
                                    List<Siparis> gidecekSiparis = db.siparisDao().getSiparisForSiparisId(gelenSiparis.getId());
                                    db.siparisDetayDao().updateSiparisId(siparisMid, gelenSiparis.getId());
                                    postSiparisDetayListFromService(senkronEdilecekler, gidecekSiparis);

                                } else {

                                    if (gelenSiparis.processInstanceId == null) {
                                        postSiparisSureciBaslatService(gelenSiparis);
                                    }

                                }


                            }
                        });
                    }
                }


            }

            @Override
            public void onFailure(Call<Siparis> call, Throwable t) {
                progressDoalog.dismiss();
            }
        });
    }


    String gelenSiparisDetayList = null;

    public void postSiparisDetayListFromService(final List<SiparisDetay> siparisDetayList, final List<Siparis> gelenSiparis) {
        final ProgressDialog progressDoalog = new ProgressDialog((Activity) getContext());
        progressDoalog.show();
        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        final List<Long> midList = new ArrayList<>();
        JsonArray datas = new JsonArray();
        for (SiparisDetay item : siparisDetayList) {
            JsonObject object = new JsonObject();
            object.addProperty("id", item.getId());
            object.addProperty("siparisId", gelenSiparis.get(0).getId());
            object.addProperty("urunId", item.getUrunId());
            if (item.getUrunId() != null && db.urunDao().getUrunForId(item.getUrunId()).size() > 0)
                object.addProperty("urunAdi", db.urunDao().getUrunForId(item.getUrunId()).get(0).getUrunAdi());
            else
                object.addProperty("urunAdi", " ");

            if (item.getOlcuBirimId() != null && db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).size() > 0)
                object.addProperty("olcuBirimAdi", db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).get(0).getOlcuBirimi());
            else
                object.addProperty("olcuBirimAdi", " ");

            object.addProperty("olcuBirimId", item.getOlcuBirimId());
            object.addProperty("birimFiyat", item.getBirimFiyat());
            object.addProperty("miktar", item.getMiktar());
            if (item.getMiktar() != null && item.getBirimFiyat() != null)
                object.addProperty("toplamTutar", item.getMiktar() * item.getBirimFiyat());
            else
                object.addProperty("toplamTutar", 0);

            object.addProperty("musteriNotu", gelenSiparis.get(0).getAciklama());


            midList.add(item.getMid());
            //  object.addProperty("musteriNotu", "");
            datas.add(object);
        }


        Call<String> call = refrofitRestApi.postSiparisDetay(OrtakFunction.authorization, OrtakFunction.tenantId, datas.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();

                    // MessageBox.showAlert(mActivity, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    gelenSiparisDetayList = response.body();
                    if (gelenSiparisDetayList != null) {

                        progressDoalog.dismiss();

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // db.siparisDetayDao().updateSiparisDetayQuery(gelenSiparis.get(0).getMid(), true);


                                final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
                                Call<List<SiparisDetay>> call = refrofitRestApi.getSiparisDetayList("hy/siparis/siparisUrunler/" + gelenSiparis.get(0).getId(), OrtakFunction.authorization, OrtakFunction.tenantId);
                                call.enqueue(new Callback<List<SiparisDetay>>() {
                                    @Override
                                    public void onResponse(Call<List<SiparisDetay>> call, Response<List<SiparisDetay>> response) {
                                        if (!response.isSuccessful()) {
                                            progressDoalog.dismiss();
                                            //MessageBox.showAlert(mActivity, "Servisle bağlantı sırasında hata oluştu...", false);
                                            return;
                                        }
                                        if (response.isSuccessful()) {
                                            db.siparisDetayDao().deletedSiparisDetayForSiparisId(gelenSiparis.get(0).getId());

                                            for (SiparisDetay item : response.body()) {
                                                item.setSiparisMid(gelenSiparis.get(0).getMid());
                                                item.setMustId(gelenSiparis.get(0).getMid());
                                                item.setSenkronEdildi(true);
                                                db.siparisDetayDao().setSiparisDetay(item);

                                            }


                                            progressDoalog.dismiss();

                                            if (siparisDetayListGorevTamamlama != null && siparisDetayListGorevTamamlama.size() > 0) {

                                                siparisDetayListGorevTamamlama = db.siparisDetayDao().getSiparisDetayForSiparisId(gelenSiparis.get(0).getId());

                                                if (siparisDetayListGorevTamamlama.size() > 0)
                                                    alert_dialog_gorev_tamamla("TeslimAl", gorevlerAdapter.getData().get(position).getTaskId());


                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<SiparisDetay>> call, Throwable t) {
                                        progressDoalog.dismiss();
                                        //  MessageBox.showAlert(mActivity, "Hata Oluştu.. " + t.getMessage(), false);
                                    }
                                });


                            }
                        });
                    }
                }
                if (gelenSiparis != null && gelenSiparis.size() > 0 && gelenSiparis.get(0).processInstanceId == null) {
                    postSiparisSureciBaslatService(gelenSiparis.get(0));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                //  MessageBox.showAlert(mActivity, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    String gelenProcessId = null;

    public void postSiparisSureciBaslatService(final Siparis item) {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        JsonObject object = new JsonObject();
        object.addProperty("subeId", item.getSubeId());
        object.addProperty("musteriId", item.getMusteriId());
        if (item.getTeslimAlinacak() != null)
            object.addProperty("teslimAlinacak", item.getTeslimAlinacak() == true ? "Evet" : "Hayır");
        else
            object.addProperty("teslimAlinacak", "Hayır");
        object.addProperty("siparisId", item.getId());


        Call<String> call = refrofitRestApi.startSiparisSureci(OrtakFunction.authorization, OrtakFunction.tenantId, object.toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();

                    // MessageBox.showAlert(mActivity, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenProcessId = response.body();
                    if (!gelenProcessId.equalsIgnoreCase("")) {
                        JSONObject gelenObject = null;
                        try {
                            gelenObject = new JSONObject(gelenProcessId);
                            db.siparisDao().updateSiparisProcessId(Long.valueOf(gelenObject.getString("processInstanceId")), item.getId());
                            get_list(durumList, "", seciliKaynakId);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                //  MessageBox.showAlert(mActivity, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }


    List<Siparis> gelenSiparisList;
    List<SiparisDetay> gelenSiparisDetayList2;

    void getSiparisListFromService() {
        final RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog.show();
        Call<List<Siparis>> call = refrofitRestApi.getSiparisList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Siparis>>() {
            @Override
            public void onResponse(Call<List<Siparis>> call, Response<List<Siparis>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    // MessageBox.showAlert(mActivity, "Servisle bağlantı sırasında hata oluştu...", false);
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
                                    item.setSenkronEdildi(true);
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
                                                //  MessageBox.showAlert(mActivity, "Servisle bağlantı sırasında hata oluştu...", false);
                                                return;
                                            }
                                            if (response.isSuccessful()) {
                                                progressDoalog.dismiss();
                                                gelenSiparisDetayList2 = response.body();

                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        for (SiparisDetay i : gelenSiparisDetayList2) {
                                                            i.setSenkronEdildi(true);
                                                            List<Siparis> siparis = db.siparisDao().getSiparisForSiparisId(item.getId());
                                                            List<SiparisDetay> urunVarMi = db.siparisDetayDao().getSiparisDetayForId(i.getId());
                                                            if (siparis != null && siparis.size() > 0) {
                                                                i.setMustId(siparis.get(0).getMid());
                                                                i.setSiparisMid(siparis.get(0).getMid());
                                                            }
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
                                            // MessageBox.showAlert(mActivity, "Hata Oluştu.. " + t.getMessage(), false);
                                        }
                                    });


                                }


                            }
                        });
                    } /*else
                       MessageBox.showAlert(mActivity, "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<List<Siparis>> call, Throwable t) {
                progressDoalog.dismiss();
                // MessageBox.showAlert(mActivity, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });
    }


}