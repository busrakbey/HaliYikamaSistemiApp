package com.example.haliyikamaapp.UI;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Permissions;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.Model.Entity.User;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.ToolLayer.RefrofitRestApi;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {

    private ImageView bookIconImageView, ilk_logo;
    private TextView bookITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    Button loginButton;
    EditText username_edittext, password_edittext;
    HaliYikamaDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_activity);
        initViews();
        ilk_logo.setVisibility(VISIBLE);
        db = HaliYikamaDatabase.getInstance(LoginActivity.this);


        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                new CountDownTimer(6000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        bookITextView.setVisibility(GONE);
                        loadingProgressBar.setVisibility(GONE);
                        rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorSplashText));
                        bookIconImageView.setImageResource(R.drawable.logo_hali);

                        startAnimation();
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
        };
        handler.postDelayed(r, 1500);


    }

    private void initViews() {
        bookIconImageView = (ImageView) findViewById(R.id.bookIconImageView);
        bookITextView = (TextView) findViewById(R.id.bookITextView);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        afterAnimationView = (RelativeLayout) findViewById(R.id.afterAnimationView);
        ilk_logo = (ImageView) findViewById(R.id.ilk_logo);
        loginButton = (Button) findViewById(R.id.loginButton);
        username_edittext = (EditText) findViewById(R.id.usernameEditText);
        password_edittext = (EditText) findViewById(R.id.passwordEditText);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  OrtakFunction.tokenControl(Log.this);

                if (username_edittext.getText().toString().trim().equalsIgnoreCase("") ||
                        password_edittext.getText().toString().trim().equalsIgnoreCase(""))
                    MessageBox.showAlert(LoginActivity.this, "Lütfen kullanıcı adı ve parolayı eksiksiz bir şekilde giriniz.", false);
                else {

                    if(InternetKontrol())
                    OrtakFunction.getTtoken(LoginActivity.this, username_edittext.getText().toString(), password_edittext.getText().toString());
                    else{
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                  /*  if (OrtakFunction.authorization != null)
                        getCurrentUserFromService();*/
                }
            }
        });
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ilk_logo.setVisibility(GONE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    String gelenUserList;

    public void getCurrentUserFromService() {

        RefrofitRestApi refrofitRestApi = OrtakFunction.refrofitRestApiForScalar();
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(LoginActivity.this);
        progressDoalog.setMessage("Lütfen bekleyiniz..");
        progressDoalog.setTitle("SİSTEM");
        progressDoalog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
        progressDoalog.show();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        final Gson gson = gsonBuilder.create();
        Call<String> call = refrofitRestApi.getCurrentUserList(OrtakFunction.authorization, OrtakFunction.tenantId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    progressDoalog.dismiss();
                    MessageBox.showAlert(LoginActivity.this, "Servisle bağlantı sırasında hata oluştu...", false);
                    return;
                }
                if (response.isSuccessful()) {
                    progressDoalog.dismiss();
                    gelenUserList = response.body();
                    if (gelenUserList != null) {
                        try {
                            List<User> gelenUser = Arrays.asList(gson.fromJson(gelenUserList, User.class));

                            Boolean userVarMi = false;
                            gelenUser.get(0).setPassword(password_edittext.getText().toString());
                            for (User item : db.userDao().getUserAll()) {
                                if (item.getId() == gelenUser.get(0).getId()) {
                                    gelenUser.get(0).setMid(item.getMid());

                                    db.userDao().updateUserList(gelenUser);
                                    userVarMi = true;
                                }
                            }
                            if (!userVarMi)
                                db.userDao().setUserList(gelenUser);

                            JSONObject object = new JSONObject(gelenUserList);
                            JSONArray permissinArray = new JSONArray(object.getString("permissions"));
                            db.permissionsDao().deletePermissionsAll();
                            for (int i = 0; i < permissinArray.length(); i++) {
                                JSONObject permissinObject = new JSONObject(permissinArray.get(i).toString());
                                List<Permissions> permissionsList = Arrays.asList(gson.fromJson(permissinObject.toString(), Permissions.class));
                                db.permissionsDao().setPermissionsList(permissionsList);


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Toast.makeText(LoginActivity.this, " " + "Giriş başarılı..", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);


                    } else
                        MessageBox.showAlert(LoginActivity.this, "Kayıt bulunamamıştır..", false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDoalog.dismiss();
                MessageBox.showAlert(LoginActivity.this, "Hata Oluştu.. " + t.getMessage(), false);
            }
        });

    }

    public boolean InternetKontrol() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null
                && manager.getActiveNetworkInfo().isAvailable()
                && manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}