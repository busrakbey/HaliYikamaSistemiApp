package com.example.haliyikamaapp.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;

import io.reactivex.annotations.Nullable;

@SuppressLint("ValidFragment")
public class DialogConfigFragment extends DialogFragment {
    View m_view;
    EditText url;
    Button exitBtn, btnKAydet;
    Context context;
    String uls = null;
    public static final String serviceUrl = "SERVICEURL";


    public DialogConfigFragment(Context ctx) {
        context = ctx;
    }

    String urlStr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_view = inflater.inflate(R.layout.layout_dialog_config, container);
        setCancelable(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        lp.height = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getDialog().getWindow().setAttributes(lp);
        urlStr = "";
        init();
        return m_view;
    }

    public void init() {
        url = (EditText) m_view.findViewById(R.id.server_url_text);
        url.setText("http://");
     /*   if (uls != null)
            url.setText(uls);*/
        Get(m_view);

        btnKAydet = (Button) m_view.findViewById(R.id.kayit_button);
        btnKAydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if (url.getText().toString().trim().length() > 10) {
                  /**  urlStr = url.getText().toString();
                    listener.getServiceUrl(urlStr, 0);*/
                    Save(m_view);

                /*} else {
                    MessageBox.showAlert(context, "Url bilgisi girilmeli !", false);
                }*/
                dismiss();
                dismiss();

            }
        });
        exitBtn = (Button) m_view.findViewById(R.id.vazgec_button);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dismiss();
            }
        });

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dialog.cancel();
    }

    SharedPreferences sharedpreferences;
    public void Save(View view) {
        sharedpreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String url_ = url.getText().toString();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(serviceUrl, url_);
        editor.commit();
    }

    public void Get(View view) {
        url = (EditText) view.findViewById(R.id.server_url_text);
        sharedpreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedpreferences.contains(serviceUrl)) {
            url.setText(sharedpreferences.getString(serviceUrl, ""));
            if(url.getText().toString().equalsIgnoreCase(""))
                url.setText(OrtakFunction.serviceUrl);
        }

    }
}
