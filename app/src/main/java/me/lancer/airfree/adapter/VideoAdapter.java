package me.lancer.airfree.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.util.List;

import me.lancer.airfree.bean.ImageViewBean;
import me.lancer.airfree.bean.VideoBean;
import me.lancer.airfree.util.NativeImageLoader;
import me.lancer.distance.R;

public class VideoAdapter extends BaseAdapter {

    private Context context;
    private List<VideoBean> list;
    private List<String> posList;
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    private GridView mGridView;
    private Handler mHandler;
    protected LayoutInflater mInflater;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public VideoAdapter(Context context, List<VideoBean> list, List<String> posList, GridView mGridView, Handler mHandler) {
        this.context = context;
        this.list = list;
        this.posList = posList;
        this.mGridView = mGridView;
        this.mHandler = mHandler;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final String path = list.get(position).getVideoPath();
        VideoBean item = list.get(position);
        String ipath = item.getThumbPath();
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gv_group_item, null);
            viewHolder.mImageView = (ImageViewBean) convertView.findViewById(R.id.iv_group);
            viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.tv_group_title);
            viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.tv_group_count);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_group);
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);

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

            //用来监听ImageView的宽和高
            viewHolder.mImageView.setOnMeasureListener(new ImageViewBean.OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.ic_pictures_no);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri mUri = Uri.parse("file://" + file.getPath());
                intent.setDataAndType(mUri, "video/mp4");
                context.startActivity(intent);
            }
        });

        viewHolder.mTextViewTitle.setText(item.getVideoName());
        viewHolder.mTextViewCounts.setVisibility(View.GONE);
        //给ImageView设置路径Tag,这是异步加载图片的小技巧
        viewHolder.mImageView.setTag(ipath);

        //利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(ipath, mPoint, new NativeImageLoader.NativeImageCallBack() {

            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                if (bitmap != null && mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });

        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.ic_pictures_no);
        }
//			viewHolder.mImageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(
//                    path, MediaStore.Video.Thumbnails.MICRO_KIND));

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
        public ImageViewBean mImageView;
        public TextView mTextViewTitle;
        public TextView mTextViewCounts;
        public CheckBox mCheckBox;
    }
}
