package com.stephen.curry.memesou.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.stephen.curry.memesou.R;
import com.stephen.curry.memesou.bean.SortModel;

public class MyLvAdapter extends BaseAdapter implements SectionIndexer{
	private List<SortModel> list = null;
	private Context mContext;
	public boolean isListViewIdle=true;

	//缓存实现
	private static final long DISK_CACHE_SIZE=1024*1024*50;//50M缓存空间
	private boolean mIsDiskCacheCreated=false;

	private LruCache<String,Bitmap> mMemoryCache;//内存缓存
	private DiskLruCache mDiskCache;//磁盘缓存

	public MyLvAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;

		//初始化缓存设置
		int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
		int cacheSize=maxMemory/8;
		mMemoryCache=new LruCache<String,Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes()*value.getHeight()/1024;
			}
		};

		//初始化磁盘缓存
		File diskCacheDir=getDiskCacheDir(mContext,"memesou");//获取缓存路径
		if (!diskCacheDir.exists()){
			diskCacheDir.mkdirs();
		}
		if (getUsableSpace(diskCacheDir)>DISK_CACHE_SIZE){
			try {
				mDiskCache=DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
				mIsDiskCacheCreated=true;
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	public File getDiskCacheDir(Context context,String uniqueName){
		boolean externalAvailable= Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		final String cachePath;
		if (externalAvailable){
			cachePath=context.getExternalCacheDir().getPath();
		}else {
			cachePath=context.getCacheDir().getPath();
		}
		return new File(cachePath+File.separator+uniqueName);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private long getUsableSpace(File path){
		if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.GINGERBREAD){
			return path.getUsableSpace();
		}

		final StatFs statFs=new StatFs(path.getPath());
		return (long)statFs.getBlockSize()*(long)statFs.getAvailableBlocks();
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_city_selecter, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.ivSrc=(ImageView)view.findViewById(R.id.item_iv);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		viewHolder.ivSrc.setImageResource(list.get(position).getSrcId());

		//只有在列表静止的时候，我们再加载图片；
		if (isListViewIdle){
			//加载图片操作


		}

		return view;
	}


	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView ivSrc;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	//获取顶部textview的文字
	public String getHeaderText(int position){
		if (position<0||position>=list.size()){
			return "热门城市";
		}
		return list.get(position).getSortLetters();
	}
	
	@Override
	public Object[] getSections() {
		return null;
	}
}