package com.example.haliyikamaapp.Adapter;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.provider.Settings.System.DATE_FORMAT;

public class MyDateTypeAdapter implements JsonDeserializer<Date> {

    private final String TAG = MyDateTypeAdapter.class.getSimpleName();

    @Override
    public Date deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String date = element.getAsString();

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date returnDate = null;
        try {
            returnDate = formatter.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, "Date parser exception:", e);
            returnDate = null;
        }
        return returnDate;
    }


    static class DateSerializer implements JsonSerializer<Date> {

        private final String TAG = DateSerializer.class.getSimpleName();

        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            formatter.setTimeZone(TimeZone.getDefault());
            String dateFormatAsString = formatter.format(date);
            return new JsonPrimitive(dateFormatAsString);
        }

    }
}