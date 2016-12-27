package com.geeker.lv.loadimg;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout lgContain;
    private volatile boolean isLoading;
    private int mScrollState;

    public NewAdapter(Context context, List<News> newsList, ListView listView) {
        mInflater = LayoutInflater.from(context);
        mNewsList = newsList;
        mImageLoader = new ImageLoader();
        mListView = listView;
        listView.setOnScrollListener(this);
        isFirst = true;
        lgContain = (LinearLayout) mInflater.inflate(R.layout.lg_load_view, null, false);
        isLoading = false;
    }

    @Override
    public int getCount() {
        return getNewsList()==null?0:getNewsList().size();
    }

    @Override
    public Object getItem(int position) {
        return getNewsList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        News news = getNewsList().get(position);
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
        Log.e(TAG, "onScrollStateChanged:" + scrollState);
//        mScrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE) {
            loadImg(start,end);
        }else {
            cancelAllTasks();
        }
    }

    private void loadImg(int _start,int _end) {
        int tmp = getNewsList().size();
        _end = _end>tmp?tmp:_end;
//        Log.e(TAG, "start:"+start + ","+end+"--"+_start+","+_end);
        for (int i=_start;i<_end;i++) {
            String purl = getNewsList().get(i).getPicUrl();
            ImageView _iv = (ImageView) mListView.findViewWithTag(purl);
            if (_iv!=null && purl!=null)
                mImageLoader.load(_iv,purl);
        }
    }

    private void cancelAllTasks() {
        mImageLoader.cancelAllTasks();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.e(TAG, "onScroll totalCount:" + totalItemCount +",firstVisibleItem:" + firstVisibleItem
                + ",visibleItemCount:" + visibleItemCount);
        start = firstVisibleItem;
        end = firstVisibleItem + visibleItemCount;
//        Log.e(TAG, "onScroll start:end-"+start+":"+end+",isLoading:"+isLoading+",State:"+mScrollState);
        if (isFirst && visibleItemCount > 0) {
            loadImg(start,end);
            isFirst = false;
        }else if(!isLoading && end>0 && end >= totalItemCount){
            Log.e(TAG, "Loading");
            isLoading = true;
            mListView.addFooterView(lgContain);
            mListView.smoothScrollByOffset(50);
            mListener.onLoading();
        }

    }



    private class ViewHolder{
        ImageView iv;
        TextView tv;
    }

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public synchronized void setNewsList(List<News> list) {
        mNewsList = list;
        notifyDataSetChanged();
    }

    public synchronized void addNewsList(List<News> newses) {
//        Log.e(TAG, "addNessList");
        if (mNewsList==null) {
            mNewsList = newses;
        }else {
            mNewsList.addAll(newses);
        }
        notifyUpdate();
    }

    public void notifyUpdate() {
        Log.e(TAG, "NessList Num:" + getNewsList().size());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public synchronized void updateNewsList(List<News> newses) {
//        Log.e(TAG, "updateNessList");
        mNewsList = newses;
        notifyUpdate();
    }

    public synchronized List<News> getNewsList() {
        return mNewsList;
    }

    public void loadFinish() {
        if (isLoading){
            mListView.removeFooterView(lgContain);
            isLoading = false;
        }
    }

    private LoadingListener mListener = null;

    public void setLoadingListener(LoadingListener listener) {
        mListener = listener;
    }

    public interface LoadingListener{
        public void onLoading();
    }

}
