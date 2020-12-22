package com.example.haliyikamaapp.ToolLayer;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.haliyikamaapp.R;

public class CustomDialog extends AlertDialog.Builder {
    public CustomDialog(Context context, String mesg)
    {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View viewDialog = inflater.inflate(R.layout.mr_dialog_messagebox, null, false);

        // TextView titleTextView = (TextView)viewDialog.findViewById(R.id.title);
        //titleTextView.setText(title);
        TextView messageTextView = (TextView)viewDialog.findViewById(R.id.mr_dia_messagebox_messtxt);
        messageTextView.setText(mesg);

        this.setCancelable(false);
        this.setView(viewDialog);
    }
}
