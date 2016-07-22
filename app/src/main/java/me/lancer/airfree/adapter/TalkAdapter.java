package me.lancer.airfree.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextVew;

import java.util.List;

import me.lancer.airfree.model.TalkBean;
import me.lancer.distance.R;

public class TalkAdapter extends BaseAdapter {

    private List<TalkBean> talkList;
    protected LayoutInflater mInflater;

    public TalkAdapter(Context context, List<TalkBean> talkList) {
        this.talkList = talkList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return talkList.size();
    }

    @Override
    public Object getItem(int position) {
        return talkList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TalkBean item = talkList.get(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_talk_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvLeftId = (TextView) convertView.findViewById(R.id.tv_left_id);
            viewHolder.tvRightId = (TextView) convertView.findViewById(R.id.tv_right_id);
            viewHolder.btvLeft = (BubbleTextVew) convertView.findViewById(R.id.btv_left);
            viewHolder.btvRight = (BubbleTextVew) convertView.findViewById(R.id.btv_right);
            viewHolder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
            viewHolder.tvRight = (TextView) convertView.findViewById(R.id.tv_right);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item.getType().equals("Q")) {
            viewHolder.btvLeft.setVisibility(View.VISIBLE);
            viewHolder.btvLeft.setText(item.getContent());
            viewHolder.tvLeftId.setVisibility(View.VISIBLE);
            viewHolder.tvLeftId.setText(item.getId());
            viewHolder.tvLeft.setVisibility(View.VISIBLE);
            viewHolder.btvRight.setVisibility(View.GONE);
            viewHolder.tvRightId.setVisibility(View.GONE);
            viewHolder.tvRight.setVisibility(View.GONE);
        } else if (item.getType().equals("A")) {
            viewHolder.btvRight.setVisibility(View.VISIBLE);
            viewHolder.btvRight.setText(item.getContent());
            viewHolder.tvRightId.setVisibility(View.VISIBLE);
            viewHolder.tvRightId.setText(item.getId());
            viewHolder.tvRight.setVisibility(View.VISIBLE);
            viewHolder.btvLeft.setVisibility(View.GONE);
            viewHolder.tvLeftId.setVisibility(View.GONE);
            viewHolder.tvLeft.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView tvLeftId;
        public TextView tvRightId;
        public BubbleTextVew btvLeft;
        public BubbleTextVew btvRight;
        public TextView tvLeft;
        public TextView tvRight;
    }
}