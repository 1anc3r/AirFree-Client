package me.lancer.airfree.adapter;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextVew;

import java.util.List;

import me.lancer.airfree.bean.ProblemBean;
import me.lancer.distance.R;

public class ProblemAdapter extends BaseAdapter {

    private List<ProblemBean> problemList;
    protected LayoutInflater mInflater;

    public ProblemAdapter(Context context, List<ProblemBean> problemList) {
        this.problemList = problemList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return problemList.size();
    }

    @Override
    public Object getItem(int position) {
        return problemList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ProblemBean item = problemList.get(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_problem_item, null);
            viewHolder = new ViewHolder();
            viewHolder.btvLeft = (BubbleTextVew) convertView.findViewById(R.id.btv_left);
            viewHolder.btvLeft.setAutoLinkMask(Linkify.ALL);
            viewHolder.btvLeft.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.btvLeft.setTextIsSelectable(true);
            viewHolder.btvRight = (BubbleTextVew) convertView.findViewById(R.id.btv_right);
            viewHolder.btvRight.setAutoLinkMask(Linkify.ALL);
            viewHolder.btvRight.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.btvRight.setTextIsSelectable(true);
            viewHolder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
            viewHolder.tvRight = (TextView) convertView.findViewById(R.id.tv_right);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item.getType().equals("Q")) {
            viewHolder.btvLeft.setVisibility(View.VISIBLE);
            viewHolder.btvLeft.setText(item.getContent());
            viewHolder.tvLeft.setVisibility(View.VISIBLE);
            viewHolder.btvRight.setVisibility(View.GONE);
            viewHolder.tvRight.setVisibility(View.GONE);
        } else if (item.getType().equals("A")) {
            viewHolder.btvRight.setVisibility(View.VISIBLE);
            viewHolder.btvRight.setText(item.getContent());
            viewHolder.tvRight.setVisibility(View.VISIBLE);
            viewHolder.btvLeft.setVisibility(View.GONE);
            viewHolder.tvLeft.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {
        public BubbleTextVew btvLeft;
        public BubbleTextVew btvRight;
        public TextView tvLeft;
        public TextView tvRight;
    }
}