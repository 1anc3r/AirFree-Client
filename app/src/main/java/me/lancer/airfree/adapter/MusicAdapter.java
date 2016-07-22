package me.lancer.airfree.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import me.lancer.distance.R;
import me.lancer.airfree.model.MusicBean;

public class MusicAdapter extends BaseAdapter {

    private Context context;
    private List<MusicBean> mp3List;
    private List<String> posList;
    private Handler mHandler;
    protected LayoutInflater mInflater;

    public MusicAdapter(Context context, List<MusicBean> mp3List, List<String> posList, Handler mHandler) {
        this.context = context;
        this.mp3List = mp3List;
        this.posList = posList;
        this.mHandler = mHandler;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mp3List.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3List.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final String path = mp3List.get(position).getPath();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvArtist0 = (TextView) convertView.findViewById(R.id.tv_artist0);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_music);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri mUri = Uri.parse("file://" + file.getPath());
                intent.setDataAndType(mUri, "audio/MP3");
                context.startActivity(intent);
            }
        });

        viewHolder.tvArtist0.setText(mp3List.get(position).getArtist());
        viewHolder.tvTitle.setText(mp3List.get(position).getTitle());
        viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.obj = "" + position;
                mHandler.sendMessage(msg);
            }
        });
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //如果是未选中的CheckBox,则添加动画
                addAnimation(viewHolder.mCheckBox);
            }
        });
        viewHolder.mCheckBox.setChecked(posList.contains("" + position) ? true : false);
        viewHolder.mCheckBox.bringToFront();

        return convertView;
    }

    private void addAnimation(View view) {
        float[] vaules = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.5f, 0.6f, 0.65f, 0.7f, 0.8f, 0.7f, 0.65f, 0.6f, 0.5f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    public static class ViewHolder {
        public TextView tvArtist0;
        public TextView tvTitle;
        public CheckBox mCheckBox;
    }
}
