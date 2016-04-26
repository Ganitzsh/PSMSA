package io.ganich.sms;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SMSApp appContext;
    private List<Conversation> smses = new ArrayList<>();
    SmsPreviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rView = (RecyclerView) findViewById(R.id.conv_list);
        if (rView != null) {
            rView.addOnItemTouchListener(new RecyclerViewClickListener(this, new RecyclerViewClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Conversation clickedConv = smses.get(position);
                    Intent intent = new Intent(appContext, ConversationActivity.class);
                    intent.putExtra("thread_id", Integer.toString(clickedConv.threadId));
                    intent.putExtra("sender", clickedConv.firstName);
                    intent.putExtra("phone_number", clickedConv.phoneNumber);
                    startActivity(intent);
                }
            }));
        }

        adapter = new SmsPreviewAdapter(smses);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        handlePermissions();
        appContext = (SMSApp) getApplicationContext();
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.sms_list_main_layout);
        if (layout != null) {
//            layout.setPadding(0, appContext.getStatusBarHeight(), 0, appContext.getNavBarHeight());
            layout.setPadding(0, appContext.getStatusBarHeight() + 6, 0, 0);
        }
        getLoaderManager().initLoader(0, null, this);
        if (rView != null) {
            rView.setAdapter(adapter);
            rView.setLayoutManager(layoutManager);
        }
        adapter.notifyDataSetChanged();
    }

    private void handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                // WAIT lol
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.RECEIVE_MMS,
                        Manifest.permission.RECEIVE_SMS
                }, 0);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri message = null;
        String selection = null;
        String sort = null;
        String[] filter = null;
        String[] values = null;

        switch (id) {
            case 0:
                message = Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
                sort = "date DESC";
                break;
        }
        if (message == null) {
            return null;
        }
        return new CursorLoader(this, message, filter, selection, values, sort);
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        if (contactName != null) {
            return contactName;
        } else {
            return phoneNumber;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int total = data.getCount();
        List<Conversation> tmp = new ArrayList<>();

        switch (loader.getId()) {
            case 0:
                if (data.moveToFirst()) {
                    for (int j = 0; j < data.getColumnCount(); j++) {
//                        Log.d("DEBUG", data.getColumnName(j) + " = " + data.getString(j));
                    }
                    for (int i = 0; i < total; i++) {
                        String address = data.getString(data.getColumnIndex("address"));
//                        Log.d("DEBUG", getContactName(this, address));
                        Conversation conv = new Conversation();
                        conv.firstName = getContactName(this, address);
                        conv.lastText = data.getString(data.getColumnIndex("body"));
                        conv.read = data.getInt(data.getColumnIndex("read"));
                        conv.threadId = data.getInt(data.getColumnIndex("thread_id"));
                        conv.phoneNumber = data.getString(data.getColumnIndex("address"));
                        tmp.add(conv);
                        data.moveToNext();
                    }
                    smses.clear();
                    smses.addAll(tmp);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
