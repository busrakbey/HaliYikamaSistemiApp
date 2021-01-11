package com.example.haliyikamaapp.ToolLayer;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by omeryildirim on 9.09.2015.
 */
public class DefaultException extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public DefaultException()
    {
        super();
    }

    public DefaultException(String message)
    {
        super(message);
    }


    public void alertUser(Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("HATA !");
        dialog.setMessage(this.toString());
        dialog.setNeutralButton("Tamam", null);
        dialog.create().show();
    }

}
