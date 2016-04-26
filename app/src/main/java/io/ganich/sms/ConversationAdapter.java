package io.ganich.sms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ganitzsh on 4/11/16.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<Message> values;
    private float circleWidth;

    public ConversationAdapter(List<Message> objects) {
        values = objects;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_message;
        private TextView tv_firstLetter;
        private ImageView iv_you;
        private View v_bar;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_message = (TextView) itemView.findViewById(R.id.conv_message);
            tv_firstLetter = (TextView) itemView.findViewById(R.id.conv_circle);
            v_bar = itemView.findViewById(R.id.conv_bar);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        // TODO: Use a map as well
        switch (viewType) {
            case MsgType.OUT_FIRST:
                layoutId = R.layout.conv_out_first;
                break;
            case MsgType.OUT:
                layoutId = R.layout.conv_out;
                break;
            case MsgType.IN_FIRST:
                layoutId = R.layout.conv_in_first;
                break;
            case MsgType.IN:
                layoutId = R.layout.conv_in;
                break;
            default:
                layoutId = R.layout.conv_in;
                break;
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message tmp = values.get(position);

        if (holder.tv_firstLetter != null) {
            String first = Character.toString(tmp.getSender().charAt(0));
            holder.tv_firstLetter.setText(first);
        }
        holder.tv_message.setText(tmp.getBody());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class MsgType {
        public static final int IN_FIRST = 0;
        public static final int IN = 1;
        public static final int OUT_FIRST = 2;
        public static final int OUT = 3;
    }

    @Override
    public int getItemViewType(int position) {
        Message tmp = values.get(position);

        // TODO: Use a map to do this
        if (!tmp.getYou() && tmp.getFirst()) {
            return MsgType.IN_FIRST;
        } else if (!tmp.getYou() && !tmp.getFirst()) {
            return MsgType.IN;
        } else if (tmp.getYou() && tmp.getFirst()) {
            return MsgType.OUT_FIRST;
        } else {
            return MsgType.OUT;
        }
    }
}
