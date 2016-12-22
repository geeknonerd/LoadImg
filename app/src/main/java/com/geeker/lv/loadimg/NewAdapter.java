package com.geeker.lv.loadimg;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lv on 16-12-21.
 */
public class NewAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private final String TAG = getClass().getName();
    private List<News> mNewsList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int start;
    private int end;
    private boolean isFirst;
    private ListView mListView;

    public NewAdapter(Context context, List<News> newsList, ListView listView) {
        mInflater = LayoutInflater.from(context);
        mNewsList = newsList;
        mImageLoader = new ImageLoader();
        mListView = listView;
        listView.setOnScrollListener(this);
        isFirst = true;
    }

    @Override
    public int getCount() {
        return mNewsList==null?0:mNewsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        News news = mNewsList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_new_item, parent, false);
            vh = new ViewHolder();
            vh.iv = (ImageView) convertView.findViewById(R.id.iv_new_item);
            vh.tv = (TextView) convertView.findViewById(R.id.tv_new_item);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        String purl = news.getPicUrl();
        Bitmap bm = mImageLoader.getCache(purl);
        if (bm!=null){
            vh.iv.setImageBitmap(bm);
        }else {
            vh.iv.setImageResource(R.mipmap.ic_launcher);
            vh.iv.setTag(purl);
        }
        vh.tv.setText(position + news.getTitle());
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        Log.e(TAG, "onScrollStateChanged:" + scrollState);
        if (scrollState == SCROLL_STATE_IDLE) {
            loadImg(start,end);
        }else {
            cancelAllTasks();
        }
    }

    private void loadImg(int _start,int _end) {
        Log.e(TAG, "start:"+start + ","+end);
        for (int i=_start;i<_end;i++) {
            String purl = mNewsList.get(i).getPicUrl();
            ImageView _iv = (ImageView) mListView.findViewWithTag(purl);
            if (_iv!=null)
                mImageLoader.load(_iv,purl);
        }
    }

    private void cancelAllTasks() {
        mImageLoader.cancelAllTasks();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        Log.e(TAG, "onScroll");
        start = firstVisibleItem;
        end = firstVisibleItem + visibleItemCount;
        if (isFirst && visibleItemCount > 0) {
            loadImg(start,end);
            isFirst = false;
        }
    }

    private class ViewHolder{
        ImageView iv;
        TextView tv;
    }

    public void setNewsList(List<News> list) {
        mNewsList = list;
        notifyDataSetChanged();
    }

    public List<News> getNewsList() {
        return mNewsList;
    }

}
