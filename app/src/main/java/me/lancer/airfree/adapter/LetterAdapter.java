package me.lancer.airfree.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.List;

import me.lancer.distance.R;
import me.lancer.airfree.model.LetterBean;

public class LetterAdapter extends BaseAdapter {

    private List<LetterBean> fileList;
    private List<String> posList;
    private Handler mHandler;
    protected LayoutInflater mInflater;

    public LetterAdapter(Context context, List<LetterBean> fileList, List<String> posList, Handler mHandler) {
        this.fileList = fileList;
        this.posList = posList;
        this.mHandler = mHandler;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_file_item, null);
            viewHolder = new ViewHolder();
            viewHolder.ivShow = (ImageView) convertView.findViewById(R.id.iv_show);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_music);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(fileList.get(position).getFileName());
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

        if (fileList.get(position).getFileName().endsWith(".png")
                || fileList.get(position).getFileName().endsWith(".jpg")
                || fileList.get(position).getFileName().endsWith(".psd")
                || fileList.get(position).getFileName().endsWith(".bmp")
                || fileList.get(position).getFileName().endsWith(".gif")
                || fileList.get(position).getFileName().endsWith(".jpeg")
                || fileList.get(position).getFileName().endsWith(".ico")
                || fileList.get(position).getFileName().endsWith(".tif")) {
            viewHolder.ivShow.setImageResource(R.drawable.ic_fm_icon_pic);
        } else if (fileList.get(position).getFileName().endsWith(".wav")
                || fileList.get(position).getFileName().endsWith(".mp3")
                || fileList.get(position).getFileName().endsWith(".m4a")
                || fileList.get(position).getFileName().endsWith(".mid")
                || fileList.get(position).getFileName().endsWith(".wma")
                || fileList.get(position).getFileName().endsWith(".ogg")) {
            viewHolder.ivShow.setImageResource(R.drawable.ic_fm_icon_music);
        } else if (fileList.get(position).getFileName().endsWith(".txt")
                || fileList.get(position).getFileName().endsWith(".pdf")
                || fileList.get(position).getFileName().endsWith(".docx")
                || fileList.get(position).getFileName().endsWith(".doc")
                || fileList.get(position).getFileName().endsWith(".pptx")
                || fileList.get(position).getFileName().endsWith(".xlsx")
                || fileList.get(position).getFileName().endsWith(".ppt")) {
            viewHolder.ivShow.setImageResource(R.drawable.ic_fm_icon_file);
        } else if (fileList.get(position).getFileName().endsWith(".mp4")
                || fileList.get(position).getFileName().endsWith(".avi")
                || fileList.get(position).getFileName().endsWith(".wmv")) {
            viewHolder.ivShow.setImageResource(R.drawable.ic_fm_icon_video);
        } else if (fileList.get(position).getFileName().endsWith(".zip")
                || fileList.get(position).getFileName().endsWith(".7z")
                || fileList.get(position).getFileName().endsWith(".cab")
                || fileList.get(position).getFileName().endsWith(".rar")
                || fileList.get(position).getFileName().endsWith(".rar")
                || fileList.get(position).getFileName().endsWith(".cab")) {
            viewHolder.ivShow.setImageResource(R.drawable.ic_fm_icon_zip);
        } else if (fileList.get(position).getFileName().endsWith(".exe")
                || fileList.get(position).getFileName().endsWith(".jar")
                || fileList.get(position).getFileName().endsWith(".dll")
                || fileList.get(position).getFileName().endsWith(".bat")
                || fileList.get(position).getFileName().endsWith(".vbs")
                || fileList.get(position).getFileName().endsWith(".xml")
                || fileList.get(position).getFileName().endsWith(".config")) {
            viewHolder.ivShow.setImageResource(R.drawable.fm_icon_default);
        } else {
            viewHolder.ivShow.setImageResource(R.drawable.ic_fm_icon_folder);
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
        public ImageView ivShow;
        public TextView tvName;
        public CheckBox mCheckBox;
    }
}
