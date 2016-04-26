package io.ganich.sms;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ganitzsh on 4/11/16.
 */
public class ConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SMSApp appContext;

    List<Messages> stash = new ArrayList<>();
    List<Message> rawStash = new ArrayList<>();
    String threadId;
    String senderName;
    String phoneNumber;

    EditText textContent;
    LinearLayoutManager layoutManager;

    ConversationAdapter adapter = new ConversationAdapter(rawStash);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        textContent = (EditText) findViewById(R.id.text_content);

        appContext = (SMSApp) getApplicationContext();
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.sms_conv_main_layout);
        if (layout != null) {
            layout.setPadding(0, appContext.getStatusBarHeight() + 6, 0, 0);
        }
        senderName = getIntent().getExtras().getString("sender");
        threadId = getIntent().getExtras().getString("thread_id");
        Log.d("Debug", threadId);
        phoneNumber = getIntent().getExtras().getString("phone_number");
        getLoaderManager().initLoader(0, null, this);
        Log.d("CONVDEBUG", "id = " + threadId);

        TextView title = (TextView) findViewById(R.id.conv_title);
        if (title != null) {
            title.setText(getResources().getString(R.string.title_conv, senderName));
        }
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView rv = (RecyclerView) findViewById(R.id.conv_rv);
        if (rv != null) {
            rv.setAdapter(adapter);
            rv.setLayoutManager(layoutManager);
        }
        adapter.notifyDataSetChanged();

        ImageButton b_send = (ImageButton) findViewById(R.id.send_button);
        if (b_send != null) {
            b_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!textContent.getText().toString().isEmpty()) {
                        Message lastMsg = rawStash.get(rawStash.size() - 1);
                        Message newMsg = new Message();

                        newMsg.setBody(textContent.getText().toString());
                        Log.d("Debug last", lastMsg.getBody());
                        Log.d("Debug last", Boolean.toString(lastMsg.getFirst()));
                        newMsg.setFirst(!lastMsg.getSender().equals("You"));
                        newMsg.setYou(true);
                        newMsg.setSender("You");
                        rawStash.add(newMsg);
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(rawStash.size() - 1);
                        try {
                            appContext.getSmsManager().sendTextMessage(phoneNumber, null, textContent.getText().toString(), null, null);
                            textContent.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
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
                Log.d("Debug", "content://mms-sms/conversations/" + threadId);
//                message = Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
                message = Uri.parse("content://mms-sms/conversations/" + threadId);
                sort = "datetime(date, 'unixepoch') ASC";
//                selection = "thread_id = ?";
//                values = new String[]{threadId};
                filter = new String[]{"_id", "ct_t", "date", "body", "service_center", "_data", "datetime(date, 'unixepoch') as date"};
                break;
            case 1:
                String mmsId = args.getString("mms_id");
                message = Uri.parse("content://mms");
//                sort = "";
                selection = "_id = ?";
                values = new String[]{mmsId};
                break;
        }
        if (message == null) {
            return null;
        }
        return new CursorLoader(this, message, filter, selection, values, sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int total = data.getCount();
        List<Message> tmpStash = new ArrayList<>();

        if (data.moveToFirst()) {
            Boolean currentIsYou;
            Boolean first = true;
            switch (loader.getId()) {
                case 0:
                    for (int i = 0; i < total; i++) {
                        Message msg = new Message();
                        String number = Integer.toString(i);

                        currentIsYou = (data.getString(data.getColumnIndex("service_center")) == null);
                        Log.d("Debug", "Message #" + number + " is " + ((first) ? "first" : "not first") + " and you " + ((currentIsYou) ? "did" : "didn't") + " sent it. Type: " + data.getString(data.getColumnIndex("ct_t")));
                        Log.d("Debug", "Date: " + data.getString(data.getColumnIndex("date")));
                        if (data.getString(data.getColumnIndex("ct_t")) != null
                                && data.getString(data.getColumnIndex("ct_t")).equals("application/vnd.wap.multipart.related")) {
                            // MMS
                            Log.d("Debug", "MMS!");
                            Bundle bundle = new Bundle();
                            bundle.putString("mms_id", data.getString(data.getColumnIndex("_id")));
                            bundle.putSerializable("msg", msg);
                            msg.setBody("<MMS HERE>");
                            msg.setFirst(first);
                            msg.setYou(currentIsYou);
                            msg.setSender((msg.getYou()) ? "You" : senderName);
                            tmpStash.add(msg);
                            getLoaderManager().restartLoader(1, bundle, this);
                        } else {
                            // SMS
                            Log.d("Debug", "SMS!");
                            String body = data.getString(data.getColumnIndex("body"));
                            msg.setBody(body);
                            msg.setFirst(first);
                            msg.setYou(currentIsYou);
                            msg.setSender((msg.getYou()) ? "You" : senderName);
                            tmpStash.add(msg);
                        }
                        if (data.moveToNext()) {
                            Boolean nextIsYou = (data.getString(data.getColumnIndex("service_center")) == null);
                            first = ((currentIsYou && !nextIsYou) || (!currentIsYou && nextIsYou));
                        }
                    }
                    Log.d("DEBUG", "Total messages: " + Integer.toString(tmpStash.size()));
                    rawStash.clear();
                    rawStash.addAll(tmpStash);
                    adapter.notifyDataSetChanged();
                    layoutManager.scrollToPosition(rawStash.size() - 1);
                    break;
                case 1:

                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
