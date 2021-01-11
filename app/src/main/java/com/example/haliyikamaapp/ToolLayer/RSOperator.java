package com.example.haliyikamaapp.ToolLayer;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


public class RSOperator {


    public <T> T convertJSONToEntity(String jsonStr, Type type, T entity) throws DefaultException {

        try {
            Gson gson = null;
            JsonSerializer<Date> ser = new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                        context) {
                    return src == null ? null : new JsonPrimitive(src.getTime());
                }
            };

            JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
                @Override
                public Date deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {


                    if (json == null)
                        return null;
                    else {
                        if (json.getAsString().length() == 10) {
                            Date date = new Date();
                            try {
                                date = new DateUtils().ConvertStringToDate(json.getAsJsonPrimitive().getAsString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return date;
                        } else if (OrtakFunction.isNumeric(json.getAsString())) {
                            return new Date(json.getAsJsonPrimitive().getAsLong());
                        } else {
                            Date date = new Date();
                            try {
                                String short_date_string = new DateUtils().ConvertLongStringToShortString(json.getAsJsonPrimitive().getAsString());
                                date = new DateUtils().ConvertStringToDate(short_date_string);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return date;
                        }
                    }
                    // return json == null ? null : new Date(json.getAsJsonPrimitive().getAsLong());
                }
            };


            gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, ser)
                    .registerTypeAdapter(Date.class, deser).create();
            entity = gson.fromJson(jsonStr, type);
            jsonStr = null;


        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new DefaultException(e.getMessage());

        } catch (com.google.gson.JsonParseException e) {
            e.printStackTrace();
            throw new DefaultException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DefaultException(e.getMessage());
        } finally {
            return entity;
        }
    }
}
