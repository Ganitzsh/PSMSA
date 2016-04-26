package io.ganich.sms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ganitzsh on 4/10/16.
 */
public class SmsPreviewAdapter extends RecyclerView.Adapter<SmsPreviewAdapter.ViewHolder> {

    private List<Conversation> values;

    public SmsPreviewAdapter(List<Conversation> objects) {
        values = objects;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_firstName;
        private TextView tv_firstLetter;
        private TextView tv_preview;
        private TextView tv_asterisk;
        private View v_bar;
        private LinearLayout ll_notFound;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_firstName = (TextView) itemView.findViewById(R.id.sms_list_first_name);
            tv_firstLetter = (TextView) itemView.findViewById(R.id.sms_list_first_letter_text);
            tv_preview = (TextView) itemView.findViewById(R.id.sms_list_preview);
            tv_asterisk = (TextView) itemView.findViewById(R.id.asterisk);
            ll_notFound = (LinearLayout) itemView.findViewById(R.id.not_found);
            v_bar = itemView.findViewById(R.id.bar);
            tv_asterisk.setVisibility(View.GONE);
            tv_firstLetter.setVisibility(View.INVISIBLE);
            ll_notFound.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversation tmp = values.get(position);

        holder.tv_firstLetter.setVisibility(View.INVISIBLE);
        holder.ll_notFound.setVisibility(View.INVISIBLE);

        holder.tv_firstName.setText(tmp.firstName);
        holder.tv_preview.setText(tmp.lastText);
        if (tmp.read == 0) {
            holder.tv_asterisk.setVisibility(View.VISIBLE);
        }
        if (Character.isLetter(tmp.firstName.charAt(0))) {
            holder.tv_firstLetter.setText(Character.toString(tmp.firstName.charAt(0)));
            holder.tv_firstLetter.setVisibility(View.VISIBLE);
        } else {
            holder.ll_notFound.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}
