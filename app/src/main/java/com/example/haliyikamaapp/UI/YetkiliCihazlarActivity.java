package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Adapter.SwipeToDeleteCallback;
import com.example.haliyikamaapp.Adapter.UrunAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Permissions;
import com.example.haliyikamaapp.Model.Entity.S_IL;
import com.example.haliyikamaapp.Model.Entity.S_ILCE;
import com.example.haliyikamaapp.Model.Entity.S_User;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.UrunFiyat;
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.Model.Entity.User;
import com.example.haliyikamaapp.Model.Entity.UserPermissions;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YetkiliCihazlarActivity extends AppCompatActivity {
    Toolbar toolbar;
    HaliYikamaDatabase db;
    EditText kullanici_edittext, kullanici_adi_edittext, kullanici_soyadi_edittext;
    Spinner yetkili_kullanicilar_spinner;
    ProgressDialog progressDoalog;
    RefrofitRestApi refrofitRestApi;
    List<S_User> userList;
    List<String> userListString;
    int selected_user_index = 0;
    S_User secili_user;
    View addView;
    LinearLayout switch_linear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.yetkili_cihazlar_activity);
        init_item();
        initToolBar();
        get_list();
        getUserList();
        // dynamicSwitchLayout(null);

    }

    public void initToolBar() {
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.left);
            TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Yetki Tanımlama");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    void init_item() {
        db = HaliYikamaDatabase.getInstance(YetkiliCihazlarActivity.this);
        kullanici_edittext = (EditText) findViewById(R.id.kullanici);
        kullanici_adi_edittext = (EditText) findViewById(R.id.kullanici_adi);
        kullanici_soyadi_edittext = (EditText) findViewById(R.id.kullanici_soyadi);
        yetkili_kullanicilar_spinner = (Spinner) findViewById(R.id.kayitli_cihaz_spinner);
        refrofitRestApi = OrtakFunction.refrofitRestApiSetting();
        progressDoalog = new ProgressDialog(YetkiliCihazlarActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        switch_linear = (LinearLayout) findViewById(R.id.switch_linear);

        userList = new ArrayList<S_User>();
        userListString = new ArrayList<String>();
    }

    public void get_list() {
        final List<Urun> urunler = db.urunDao().getUrunAll();
    }

    List<S_User> gelenUserList;

    void getUserList() {
        progressDoalog.show();
        Call<List<S_User>> call = refrofitRestApi.getUserList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<S_User>>() {
            @Override
            public void onResponse(Call<List<S_User>> call, Response<List<S_User>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(YetkiliCihazlarActivity.this, "Ürün bilgileri alınırken hata oluştu...", false);
                    userSpinnerList();
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUserList = response.body();
                    if (gelenUserList != null && gelenUserList.size() > 0) {
                        for (S_User item : gelenUserList) {
                            List<S_User> urunVarMi = db.sUserDao().getUserForId(item.getId());
                            if (urunVarMi.size() > 0) {
                                item.setMid(urunVarMi.get(0).getMid());
                                db.sUserDao().updateUser(item);
                            } else
                                db.sUserDao().setUser(item);
                        }
                        YetkiliCihazlarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userSpinnerList();
                                gerPermissionList();

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<S_User>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(YetkiliCihazlarActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                userSpinnerList();
            }
        });
    }


    List<Permissions> gelenPermissionList;

    void gerPermissionList() {
        progressDoalog.show();
        Call<List<Permissions>> call = refrofitRestApi.geTPermissionList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<List<Permissions>>() {
            @Override
            public void onResponse(Call<List<Permissions>> call, Response<List<Permissions>> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(YetkiliCihazlarActivity.this, "Ürün bilgileri alınırken hata oluştu...", false);
                    userSpinnerList();
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenPermissionList = response.body();
                    if (gelenPermissionList != null && gelenPermissionList.size() > 0) {
                        for (Permissions item : gelenPermissionList) {
                            List<Permissions> urunVarMi = db.permissionsDao().getPermissionsForId(item.getId());
                            if (urunVarMi.size() > 0) {
                                item.setMid(urunVarMi.get(0).getMid());
                                db.permissionsDao().updatePermissions(item);
                            } else
                                db.permissionsDao().setPermissions(item);
                        }

                        YetkiliCihazlarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                db.userPermissionsDao().deleteUserAll();
                                for (Permissions item : db.permissionsDao().getPermissionsAll()) {
                                    userPermission(item.getId());
                                }


                            }
                        });


                    }

                }
            }

            @Override
            public void onFailure(Call<List<Permissions>> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(YetkiliCihazlarActivity.this, "Hata Oluştu.. " + t.getMessage(), false);

            }
        });

    }

    List<S_User> userPermissionList;

    void userPermission(final Long permissionId) {
        YetkiliCihazlarActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Call<List<S_User>> call = refrofitRestApi.getUsersPermission("fw/permission/findUsersByPermission/" + permissionId, OrtakFunction.authorization, OrtakFunction.tenantId);
                call.enqueue(new Callback<List<S_User>>() {
                    @Override
                    public void onResponse(Call<List<S_User>> call, Response<List<S_User>> response) {
                        if (!response.isSuccessful()) {
                            progressDoalog.dismiss();
                            MessageBox.showAlert(YetkiliCihazlarActivity.this, "Ürün bilgileri alınırken hata oluştu...", false);
                            userSpinnerList();
                            return;
                        }
                        if (response.isSuccessful()) {
                            progressDoalog.dismiss();
                            userPermissionList = response.body();
                            if (userPermissionList != null && userPermissionList.size() > 0) {
                                for (S_User item : userPermissionList) {
                                    UserPermissions a = new UserPermissions();
                                    a.setPermissionId(permissionId);
                                    a.setUserId(item.getId());
                                    db.userPermissionsDao().setUser(a);

                                }
                                YetkiliCihazlarActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<S_User>> call, Throwable t) {
                        progressDoalog.dismiss();
                        MessageBox.showAlert(YetkiliCihazlarActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
                        userSpinnerList();

                    }
                });


            }
        });
    }

    void userSpinnerList() {
        userList.add(null);
        userList = db.sUserDao().getUserAll();
        userListString.add("Kullanıcılar");

        for (S_User item : userList) {
            userListString.add(item.getName() + " " + item.getSurname());
        }

        ArrayAdapter<String> dataAdapter_il = new ArrayAdapter<String>(YetkiliCihazlarActivity.this, android.R.layout.simple_spinner_item, userListString);
        dataAdapter_il.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        yetkili_kullanicilar_spinner.setAdapter(dataAdapter_il);
        yetkili_kullanicilar_spinner.setSelection(0);

        yetkili_kullanicilar_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String valInfo = userListString.get(position);
                    if (valInfo != null) {
                        selected_user_index = position;

                        secili_user = userList.get(selected_user_index - 1);

                        if (secili_user != null) {
                            kullanici_adi_edittext.setText(secili_user.getName());
                            kullanici_soyadi_edittext.setText(secili_user.getSurname());
                            kullanici_edittext.setText(secili_user.getUserName());
                            dynamicSwitchLayout(secili_user.getId());
                        }


                    }
                } else {
                    secili_user = null;
                    selected_user_index = 0;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void dynamicSwitchLayout(Long userId) {
        switch_linear.removeAllViews();
        List<Permissions> allList = db.permissionsDao().getPermissionsAll();
        List<UserPermissions> allPermissionList = db.userPermissionsDao().getUserAll();

        for (Permissions permissions : allList) {
            if ( permissions.getId() != null && permissions.getParentId() != null) {
                Switch myButton = new Switch(this);
                myButton.setText(db.permissionsDao().getPermissionsForParentId(permissions.getParentId()).get(0).getName() + " " + permissions.getName());
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                switch_linear.addView(myButton, lp);
                for(UserPermissions item : allPermissionList){
                    if (item.getUserId() != null && userId != null && item.getUserId().toString().equalsIgnoreCase(String.valueOf(userId))) {
                        myButton.setChecked(true);
                    }
                }
            }
        }



    }


}


