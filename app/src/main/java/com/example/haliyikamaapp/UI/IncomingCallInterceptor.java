package com.example.haliyikamaapp.UI;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/*created by busrakbey - 30.12.2020*/

public class IncomingCallInterceptor extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String msg = "Phone state changed to " + state;

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            msg += ". Incoming number is " + incomingNumber;

            // TODO This would be a good place to "Do something when the phone rings" ;-)


            if (incomingNumber != null) {
                Intent intent1 = new Intent();
                intent1.putExtra("number", incomingNumber);
                intent1.setClassName(context.getPackageName(), MainActivity.class.getName());
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
              //  Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            }
        }
    }
}