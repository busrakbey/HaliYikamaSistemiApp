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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.haliyikamaapp.Adapter.GorevlerAdapter;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GorevlerimFragment2 extends Fragment {
    ProgressDialog progressDoalog;
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
    String dateFiltre, searchviewText;
    RadioGroup tarihRadioGrup;
    Boolean tarihBugundeMi = true;
    Kaynak secili_kaynak;
    String gorevNotu = "", secilenTeslimEtDurumu = "", girilenTahsilatTutari = "", gorevTamamlamaNotu = "";
    Boolean hesapKapatCheckbox = false;
    String tahsilEdilecekTuar = "";
    List<SiparisDetay> siparisDetayList = null;
    private SwipeRefreshLayout swipeRefreshLayout;


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
            getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void init_item(View view) {
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    ((MainActivity) getContext()).siparis_islemleri();
                    getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
                    get_list(durumList, searchviewText);
                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
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

                get_list(durumList, searchviewText);
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
                get_list(durumList, searchviewText);


            }


        });


        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView, false) {
            @Override
            public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Tamamla",

                        AppCompatResources.getDrawable(
                                getContext(),
                                android.R.drawable.ic_media_play
                        ),
                        Color.parseColor("#FF0000"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

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
                                        alert_dialog_gorev_tamamla("TeslimEt", gorevlerAdapter.getData().get(position).getTaskId());

                                        List<GorevFomBilgileri> formDetay = db.gorevFomBilgileriDao().getGorevId(gorevlerAdapter.getData().get(position).getTaskId());
                                        for (GorevFomBilgileri item : formDetay) {
                                            if (item.getName().equalsIgnoreCase("Tahsil Edilecek Tutar"))
                                                tahsilEdilecekTuar = item.getValue();
                                        }
                                    }

                                    if (gorevlerAdapter.getData().get(position).getTaskName().equalsIgnoreCase("TeslimAl")) {

                                        List<Gorevler> gorevlerList = db.gorevlerDao().getGorevForId(Long.valueOf(gorevlerAdapter.getData().get(position).getTaskId()));
                                        if (gorevlerList.size() > 0) {
                                            List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
                                            if (siparisList.size() > 0) {
                                                siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
                                                if (siparisDetayList.size() == 0)
                                                    siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());
                                            }
                                        }
                                        if (siparisDetayList == null || siparisDetayList.size() == 0) {
                                            MessageBox.showAlert(getContext(), "Görevi tamamlamak için ürün eklemeniz lazım.", false);
                                            return;
                                        } else

                                            alert_dialog_gorev_tamamla("TeslimAl", gorevlerAdapter.getData().get(position).getTaskId());


                                    }

                                }
                            }
                        }
                ));


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Konum",

                        AppCompatResources.getDrawable(
                                getContext(),
                                android.R.drawable.ic_menu_mylocation
                        ),
                        Color.parseColor("#FF9800"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                List<Musteri> musteriList = null;
                                musteriList = db.musteriDao().getMusteriForId(gorevlerAdapter.getData().get(position).getMusteriId());
                                if (musteriList.size() > 0) {

                                    if (musteriList.get(0).getxKoor() != null && musteriList.get(0).getyKoor() != null
                                            && Double.valueOf(musteriList.get(0).getxKoor()) > 0 && Double.valueOf(musteriList.get(0).getyKoor()) > 0) {
                                        String geoUri = "http://maps.google.com/maps?q=loc:" + Double.valueOf(musteriList.get(0).getyKoor()) + "," + Double.valueOf(musteriList.get(0).getxKoor()) + " (" + "" + ")";
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
                                        startActivity(intent);
                                    } else if (!musteriList.get(0).getAdres().equalsIgnoreCase("")) {
                                        String url = "http://maps.google.com/maps?daddr=" + musteriList.get(0).getAdres();
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Yazdır",

                        AppCompatResources.getDrawable(
                                getContext(),
                                android.R.drawable.ic_menu_gallery
                        ),
                        Color.parseColor("#44728A"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                if (gorevlerAdapter.getData().get(position).getSiparisId() != null) {
                                    Intent bluetooth = new Intent(getContext(), BluetoothActivity.class);
                                    bluetooth.putExtra("siparisId", gorevlerAdapter.getData().get(position).getSiparisId().toString());
                                    bluetooth.putExtra("subeAdi", gorevlerAdapter.getData().get(position).getSubeAdi());
                                    startActivity(bluetooth);
                                }
                            }
                        }
                ));


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Sms",

                        AppCompatResources.getDrawable(
                                getContext(),
                                android.R.drawable.ic_menu_edit
                        ),
                        Color.parseColor("#23A96E"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                if (!gorevlerAdapter.getData().get(position).getTelefonNumarasi().equalsIgnoreCase("")) {

                                  /*  SmsManager smgr = SmsManager.getDefault();
                                    smgr.sendTextMessage(gorevlerAdapter.getData().get(position).getTelefonNumarasi(),null,"Merhabalar..",null,null);
                                    Toast.makeText(getContext(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();*/


                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(Uri.parse("smsto:" + gorevlerAdapter.getData().get(position).getTelefonNumarasi())); // This ensures only SMS apps respond
                                    intent.putExtra("sms_body", "Merhabalar..");
                                    startActivity(intent);
                                }
                            }
                        }
                ));


                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Telefon",

                        AppCompatResources.getDrawable(
                                getContext(),
                                android.R.drawable.ic_menu_call
                        ),
                        Color.parseColor("#D8D8D8"), Color.parseColor("#FFFFFF"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                if (!gorevlerAdapter.getData().get(position).getTelefonNumarasi().equalsIgnoreCase("")) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel: " + gorevlerAdapter.getData().get(position).getTelefonNumarasi()));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.getApplicationContext().startActivity(intent);
                                } else {
                                    Cursor telefonun_rehberi = mContext.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                                    while (telefonun_rehberi.moveToNext()) {
                                        String isim = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                        String numara = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        if (isim.equalsIgnoreCase(gorevlerAdapter.getData().get(position).getMusteriAdi() + " " +
                                                gorevlerAdapter.getData().get(position).getMusteriSoyadi())) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                                            intent.setData(Uri.parse("tel: " + numara)); // Data with intent respective action on intent
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.getApplicationContext().startActivity(intent);
                                            telefonun_rehberi.close();

                                        }
                                    }
                                }
                            }
                        }
                ));

            }
        };


    }

    public void get_list(List<String> siparisDurumu, String searchViewText) {
        List<Gorevler> gorevlerList;


        if (durumList.size() == 0 || durumList.get(0).equalsIgnoreCase("")) {
            durumList.add("Teslim Alınacak");
            durumList.add("Teslime Hazır");
            durumList.add("Teslime Çıktı");
            durumList.add("Yıkanacak");
        }
        if (searchViewText == null)
            searchViewText = "";

        if ((siparisDurumu != null && siparisDurumu.size() > 0) || (searchViewText != null && !searchViewText.equalsIgnoreCase("")))
            if (tarihBugundeMi)
                gorevlerList = db.gorevlerDao().getQueryIleriTarih(searchViewText, siparisDurumu, dateFiltre);
            else
                gorevlerList = db.gorevlerDao().getGorevQueryPrameter(searchViewText, siparisDurumu, dateFiltre);

        else
            gorevlerList = db.gorevlerDao().getGorevAll();
        gorevlerAdapter = new GorevlerAdapter(getContext(), gorevlerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gorevlerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(gorevlerAdapter);


        gorevlerAdapter = new GorevlerAdapter(getContext(), gorevlerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gorevlerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(gorevlerAdapter);


    }


    String gelenGorevList = null;

    public void getGorevlerimFromService(Long kullaniciId) throws Exception {
        progressDoalog.show();
        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        JSONObject object = new JSONObject();


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
                    progressDoalog.dismiss();
                    gelenGorevList = response.body();
                    if (!gelenGorevList.equalsIgnoreCase("")) {
                        db.gorevlerDao().deleteGorevAll();

                        List<Gorevler> allGorevList = db.gorevlerDao().getGorevAll();
                        JSONObject gelenObject = null;
                        //db.gorevlerDao().deleteGorevAll();
                        Boolean yeniKayitMi = true;
                        Long gorevMid;
                        try {
                            gelenObject = new JSONObject(gelenGorevList);
                            JSONArray rowArray = new JSONArray(gelenObject.getString("rows"));
                            for (int i = 0; i < rowArray.length(); i++) {
                                JSONObject rowObject = new JSONObject(rowArray.getString(i));
                            /*   JSONObject processVariablesObject = new JSONObject(rowObject.getString("processVariables"));
                                rowObject.put("siparisId", processVariablesObject.get("siparisId"));
                                rowObject.put("musteriId", processVariablesObject.get("musteriId"));*/
                                List<Gorevler> gorevlerList = Arrays.asList(gson.fromJson(rowObject.toString(), Gorevler.class));
                                for (Gorevler item : allGorevList) {
                                    if (item.getTaskId() != null && item.getTaskId().toString().equalsIgnoreCase(rowObject.getString("taskId"))) {
                                        yeniKayitMi = false;
                                    }

                                    if (yeniKayitMi)
                                        db.gorevlerDao().setGorev(item);

                                    else {
                                        item.setMid(item.getMid());
                                        db.gorevlerDao().updateGorev(item);

                                    }
                                }
                                if (allGorevList != null && allGorevList.size() == 0)
                                    db.gorevlerDao().setGorevList(gorevlerList);

                              /*  JSONObject musteriObject = new JSONObject(processVariablesObject.getString("musteri"));
                                List<Musteri> gelenMusteriList = Arrays.asList(gson.fromJson(musteriObject.toString(), Musteri.class));

                                JSONObject siparisObject = new JSONObject(processVariablesObject.getString("siparis"));
                                List<Siparis> gelenSiparisList = Arrays.asList(gson.fromJson(siparisObject.toString(), Siparis.class));*/


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                get_list(durumList, searchviewText);
                                List<Gorevler> totalGorevList = db.gorevlerDao().getGorevAll();
                                for (Gorevler item : totalGorevList) {
                                    try {
                                        getFormBilgilerimList(item.getTaskId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });


                    } /*else
                        MessageBox.showAlert(getContext(), "Kayıt bulunamamıştır..", false);*/
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(getContext(), "Hata Oluştu.. " + t.getMessage(), false);
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
                    //   MessageBox.showAlert(mContext, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
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
                        //   db.siparisDao().updateSiparisProcessId(Long.valueOf(gelenObject.getString("processInstanceId")), item.get(0).getId());


                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             /*   if (gelenMusteriList.size() != kayitList.size())
                                    MessageBox.showAlert(MusteriKayitActivity.this, "Müşteri listesi senkron edilirken hata oluştu.", false);
                                else
                                    ();*/

                            }
                        });
                        get_list(null, null);

                    } else
                        MessageBox.showAlert(mContext, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(mContext, "Hata Oluştu.. " + t.getMessage(), false);
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

    @Override
    public void onResume() {
        super.onResume();

        //((MainActivity)getContext()).siparis_islemleri();
        //durumList = new ArrayList<String>();
        // get_list(null, null);
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
                get_list(durumList, newText);


                return false;
            }
        });
        search_view.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                get_list(durumList, "");
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


    public void gorevTamamlaPostService(final Long gorevId, final Long kaynakId, String taskName) throws Exception {
        progressDoalog.show();
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
            if (siparisDetayList != null && siparisDetayList.size() > 0)
                for (SiparisDetay item : siparisDetayList) {
                    JSONObject icObje = new JSONObject();
                    icObje.put("id", item.getId());
                    icObje.put("siparisId", item.getSiparisId());
                    icObje.put("urunId", item.getUrunId());
                    icObje.put("urunAdi", db.urunDao().getUrunForId(item.getUrunId()).get(0).getUrunAdi());
                    icObje.put("olcuBirimId", item.getOlcuBirimId());
                    icObje.put("olcuBirimAdi", db.olcuBirimDao().getOlcuBirimForId(item.getOlcuBirimId()).get(0).getOlcuBirimi());
                    icObje.put("birimFiyat", item.getBirimFiyat());
                    icObje.put("miktar", item.getMiktar());
                    icObje.put("toplamTutar", item.getBirimFiyat() * item.getMiktar());
                    icObje.put("musteriNotu", null);
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
                    /*if (urunArray != null && urunArray.length() > 0) {
                        db.siparisDao().updateSiparisDurumu(gorevId, "Yıkanacak");
                    }

                    if (gorevlerList.get(0).getName().equalsIgnoreCase("TeslimEt")){
                        db.siparisDao().updateSiparisDurumu(gorevId, )
                    }*/
                    MessageBox.showAlert(getContext(), "Görev başarıyla tamamlanmıştır..", false);
                    try {
                        getGorevlerimFromService(db.userDao().getUserAll().get(0).getId());
                    } catch (Exception e) {
                        e.printStackTrace();
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