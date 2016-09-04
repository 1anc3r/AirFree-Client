package me.lancer.airfree.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import me.lancer.airfree.bean.ImageViewBean;
import me.lancer.airfree.util.MusicUtil;
import me.lancer.distance.R;
import me.lancer.airfree.bean.MusicBean;

public class MusicAdapter extends BaseAdapter {

    private Context context;
    private List<String> posList;
    private List<String> searchList;
    private List<MusicBean> musicList;
    protected LayoutInflater mInflater;
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象

    private Handler mHandler;

    public MusicAdapter(Context context, List<MusicBean> musicList, List<String> posList, List<String> searchList, Handler mHandler) {
        this.context = context;
        this.musicList = musicList;
        this.searchList = searchList;
        this.posList = posList;
        this.mHandler = mHandler;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final String path = musicList.get(position).getPath();

        Log.e("IP & PORT", searchList.toString());

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvArtist0 = (TextView) convertView.findViewById(R.id.tv_artist0);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_music);
            viewHolder.ivAlbum = (ImageViewBean) convertView.findViewById(R.id.iv_album);
            viewHolder.ivAlbum.setOnMeasureListener(new ImageViewBean.OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.ivAlbum.setImageResource(R.drawable.ic_fm_icon_music);
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

        viewHolder.tvArtist0.setText(musicList.get(position).getArtist());
        viewHolder.tvTitle.setText(musicList.get(position).getTitle());
        Bitmap bitmap = MusicUtil.getArtwork(context, musicList.get(position).getId(), musicList.get(position).getAlbumId(), true, true);
        if (bitmap != null) {
            viewHolder.ivAlbum.setImageBitmap(bitmap);
        } else {
            viewHolder.ivAlbum.setImageResource(R.drawable.ic_fm_icon_music);
        }
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
                addAnimation(viewHolder.mCheckBox);
            }
        });
        viewHolder.mCheckBox.setChecked(posList.contains("" + position) ? true : false);
        viewHolder.mCheckBox.bringToFront();

        String title = musicList.get(position).getTitle();
        String artist = musicList.get(position).getArtist();
        if (searchList.size() > 0) {
            String keyword = searchList.get(0);
            if ((title != null && title.contains(keyword)) || (artist != null && artist.contains(keyword))) {
                ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                SpannableStringBuilder builder1 = new SpannableStringBuilder(title);
                int index1 = title.indexOf(keyword);
                if (index1 != -1) {
                    builder1.setSpan(span, index1, index1 + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                SpannableStringBuilder builder2 = new SpannableStringBuilder(artist);
                int index2 = artist.indexOf(keyword);
                if (index2 != -1) {
                    builder2.setSpan(span, index2, index2 + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                viewHolder.tvTitle.setText(builder1);
                viewHolder.tvArtist0.setText(builder2);
            } else {
                viewHolder.tvTitle.setText(title);
                viewHolder.tvArtist0.setText(artist);
            }
        }

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
        public ImageViewBean ivAlbum;
        public TextView tvArtist0;
        public TextView tvTitle;
        public CheckBox mCheckBox;
    }
}
