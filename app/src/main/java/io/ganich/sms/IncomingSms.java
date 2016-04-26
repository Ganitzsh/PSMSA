package io.ganich.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ganitzsh on 4/15/16.
 */
public class IncomingSms extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        String action = intent.getAction();

        Log.d("Debug", "Received something");
        try {

            if (bundle != null) {

                Log.d("Debug", bundle.toString());
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                final String format = (String) bundle.get("format");

                if (action.equals(Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION)) {
                    // If MMS
                } else if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                    // If SMS
                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                    }
                }
            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }
}
