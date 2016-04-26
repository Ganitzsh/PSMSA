package io.ganich.sms;

import android.app.Application;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public SmsManager smsManager = SmsManager.getDefault();
    public ApplicationTest() {
        super(Application.class);
    }
}