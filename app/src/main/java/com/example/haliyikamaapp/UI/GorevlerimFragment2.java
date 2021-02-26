package com.example.haliyikamaapp.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.GorevlerAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.GorevFomBilgileri;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.MultiSelectionSpinner;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.example.haliyikamaapp.ToolLayer.SwipeHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

        spinner = (MultiSelectionSpinner) view.findViewById(R.id.spinner_gorev_durum);
        search_view = (SearchView) view.findViewById(R.id.search_view);
            search_view.setFocusable(true);
        search_view.setIconified(false);
        search_view.clearFocus();

        List<String> list = new ArrayList<String>();
        list.add("Teslim Alınacaklar");
        list.add("Yıkanacaklar");
        list.add("Araca Yüklenecekler");
        list.add("Teslim Edilecekler");
        spinner.setItems(list);
        spinner.setTitle("Sipariş Durumu");



        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Tamamla",
                        R.drawable.check,
                        Color.parseColor("#FF3C30"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                            }
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Konum",
                        R.drawable.location,
                        Color.parseColor("#FF9502"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnTransfer
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Telefon",
                        R.drawable.telephone,
                        Color.parseColor("#C7C7CB"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                final int position = viewHolder.getAdapterPosition();

                                if (gorevlerAdapter.getData().get(position).getTelefonNumarasi().equalsIgnoreCase("")) {
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

    void get_list() {
        List<Gorevler> gorevlerList = db.gorevlerDao().getGorevAll();
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
                    MessageBox.showAlert(getContext(), "Servisle bağlantı sırasında hata oluştu...", false);
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

                                get_list();
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
                    MessageBox.showAlert(mContext, "Servisle bağlantı sırasında hata oluştu...", false);
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
                                    get_list();*/

                            }
                        });


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
        get_list();
    }


}