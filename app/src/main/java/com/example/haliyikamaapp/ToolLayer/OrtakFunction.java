package com.example.haliyikamaapp.ToolLayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.haliyikamaapp.Adapter.MyDateTypeAdapter;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Dao.AuthTokenDao;
import com.example.haliyikamaapp.Model.Entity.AuthToken;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.UI.LoginActivity;
import com.example.haliyikamaapp.UI.MainActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class OrtakFunction {
    public static List<AuthToken> tokenList = null;
    public static String serviceUrl = "http://35.204.214.240:80/haliBackend/";
    public static HaliYikamaDatabase db = null;
    public static String authorization = null;
    public static String tenantId = "test";

    public static void tokenControl(Context context) {
        db = HaliYikamaDatabase.getInstance(context);
        tokenList = db.authToken().getAuthTokenAll();
        if (tokenList.size() > 0)
            authorization = "Bearer " + tokenList.get(0).getAccess_token();
    }

    public static TelephonyManager tm;
    public static String imei;

    public static ProgressDialog dialog;

    @SuppressLint("WrongConstant")
    public static void getTtoken(final Context context, final String username, final String password, final String tenantId) {
        db = HaliYikamaDatabase.getInstance(context);
        String url = serviceUrl + "oauth/token";

        dialog = new ProgressDialog(context);
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager != null) {
                    try {
                        imei = telephonyManager.getImei();
                    } catch (Exception e) {
                        e.printStackTrace();
                        imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                }
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1010);
            }
        } else {
            if (ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager != null) {
                    imei = telephonyManager.getDeviceId();
                }
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1010);
            }
        }


        RequestQueue mQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response.toString());
                dialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final AuthToken AuthToken = new AuthToken();
                    AuthToken.setAccess_token(jsonObject.getString("access_token"));
                    AuthToken.setExpires_in(jsonObject.getString("expires_in"));
                    AuthToken.setRefresh_token(jsonObject.getString("refresh_token"));
                    AuthToken.setScope(jsonObject.getString("scope"));
                    AuthToken.setToken_type(jsonObject.getString("token_type"));
                    authorization = "Bearer " + jsonObject.getString("access_token");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            long yeniKayit = -1;
                            db.authToken().deleteTokenAll();
                            yeniKayit = db.authToken().setAuthToken(AuthToken);

                            final long finalyeniKayit = yeniKayit;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Integer.valueOf(String.valueOf(finalyeniKayit)) > 0) {
                                        tokenControl(context);
                                        if (authorization != null)
                                            ((LoginActivity) context).getCurrentUserFromService(tenantId);


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
                        dialog.dismiss();
                        if (error instanceof AuthFailureError) {
                            //Toast.makeText(context, "Kullanıcı adı veya parola hatalı ! " , Toast.LENGTH_SHORT).show();
                            MessageBox.showAlert(context, "Kullanıcı adı veya parola hatalı !", false);

                            Log.e("TAG", error.getMessage(), error);

                        } else {
                            // Toast.makeText(context, "Kullanıcı adı veya parola hatalı ! " , Toast.LENGTH_SHORT).show();
                            MessageBox.showAlert(context, "Kullanıcı adı veya parola hatalı !", false);

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
                headers.put("tenant-id", tenantId);
                headers.put("Connection", "keep-alive");


                //   headers.put("imei-number ", imei);


                return headers;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "password");
                params.put("username", username);
                params.put("password", password);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(1000, 2, 1));
        mQueue.add(request);
        dialog.show();

    }


    public static void permission_control(Context context, Activity activity) {


        Boolean hasCallPermission =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                        /*  && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED-*/ &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;

        //   ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1010);


        if (!hasCallPermission) {


            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CALL_LOG,
                            //  Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 123);
        }


    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }


    public static RefrofitRestApi refrofitRestApiSetting() {
        String url = OrtakFunction.serviceUrl;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MyDateTypeAdapter())
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(4, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();


        RefrofitRestApi refrofitRestApi = retrofit.create(RefrofitRestApi.class);
        return refrofitRestApi;

    }

    public static RefrofitRestApi refrofitRestApiForScalar() {
        String url = OrtakFunction.serviceUrl;
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(4, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();


        RefrofitRestApi refrofitRestApi = retrofit.create(RefrofitRestApi.class);
        return refrofitRestApi;

    }


    public static void GetLocation(Activity activity, Context context) throws IOException {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 200);

            return;
        } else {


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("", "onLocationChanged: " + location.getLongitude() + " , " + location.getLatitude());

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    Log.d("", "onStatusChanged: " + s);

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(bestProvider);

                if (location == null) {
                  /*  Toast.makeText(context, "GPS signal not found",
                            Toast.LENGTH_LONG).show();*/
                    return;
                }
                if (location != null) {
                    Log.e("location", "location--" + location);
                    Log.e("latitude at beginning",
                            "@@@@@@@@@@@@@@@" + location.getLatitude());
                    // onLocationChanged(location);
                }


                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                Log.d("", "GetLocation: address " + address + " city " + city + " state " + state + " country " + country + " postalCode " + postalCode + " knownName " + knownName);
            }
        }
    }


}
