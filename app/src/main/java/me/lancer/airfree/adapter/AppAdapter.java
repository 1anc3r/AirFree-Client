package me.lancer.airfree.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.lancer.distance.R;
import me.lancer.airfree.model.AppBean;

public class AppAdapter extends BaseAdapter {

	private List<AppBean> appList;
	private List<String> posList;
	protected LayoutInflater mInflater;

	public AppAdapter(Context context, List<AppBean> appList, List<String> posList) {
		this.appList = appList;
		this.posList = posList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return appList.size();
	}

	@Override
	public Object getItem(int position) {
		return appList.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;

		if(convertView == null){
			convertView = mInflater.inflate(R.layout.lv_app_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);

			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvTitle.setText(appList.get(position).getAppName());
		viewHolder.ivIcon.setImageDrawable(appList.get(position).getAppIcon());

		return convertView;
	}

	public static class ViewHolder{
		public TextView tvTitle;
		public ImageView ivIcon;
	}
}
