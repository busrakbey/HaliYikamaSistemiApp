package com.example.haliyikamaapp.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Dao.AuthTokenDao;
import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.ToolLayer.MessageBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrtakFunction {
    public static List<AuthToken> tokenList = null;
    public static String serviceUrl = "http://35.204.214.240:80/haliBackend/";
    public static HaliYikamaDatabase db = null;
    public static String tokenName = null;

    public static void tokenControl(Context context) {
        db = HaliYikamaDatabase.getInstance(context);
        tokenList = db.authToken().getAuthTokenAll();
        if (tokenList.size() > 0)
            tokenName = tokenList.get(0).getAccess_token();
    }

    public static void getTtoken(final Context context) {
        String url = serviceUrl + "oauth/token";
        RequestQueue mQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final AuthToken AuthToken = new AuthToken();
                    AuthToken.setAccess_token(jsonObject.getString("access_token"));
                    AuthToken.setExpires_in(jsonObject.getString("expires_in"));
                    AuthToken.setRefresh_token(jsonObject.getString("refresh_token"));
                    AuthToken.setScope(jsonObject.getString("scope"));
                    AuthToken.setToken_type(jsonObject.getString("token_type"));
                    tokenName = jsonObject.getString("access_token");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            long yeniKayit = -1;
                            yeniKayit = db.authToken().setAuthToken(AuthToken);

                            final long finalyeniKayit = yeniKayit;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Integer.valueOf(String.valueOf(finalyeniKayit)) > 0) {
                                        Toast.makeText(context, "Sistem " + "Giriş başarılı..", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }
                    }).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof AuthFailureError) {
                            Toast.makeText(context, "Auth ERROR: " + error, Toast.LENGTH_SHORT).show();
                            Log.e("TAG", error.getMessage(), error);

                        } else {
                            Toast.makeText(context, "ERROR: " + error, Toast.LENGTH_SHORT).show();
                            Log.e("TAG", error.getMessage(), error);
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String username = "hy";
                String password = "hy-secret";
                String auth = username + ":" + password;
                byte[] data = auth.getBytes();
                String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", "Basic " + base64);
                headers.put("tenant-id", "test");
                return headers;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "password");
                params.put("username", "Admin");
                params.put("password", "123456");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(1000, 2, 1));
        mQueue.add(request);
    }

    public static void permission_control(Context context, Activity activity) {
        Boolean hasCallPermission = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED;
        if (!hasCallPermission)
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);


    }
}
