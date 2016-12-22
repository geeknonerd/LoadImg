package com.geeker.lv.loadimg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.ArraySet;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lv on 16-12-21.
 */
public class ImageLoader{

    private final String TAG = getClass().getName();
    private LruCache<String, Bitmap> mLruCache;
    private Set<BitmapLoaderTask> mTask;

    public ImageLoader() {
        int size = (int) (Runtime.getRuntime().maxMemory()/4);
        mLruCache = new LruCache<String,Bitmap>(size){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mTask = new HashSet<>();
    }

    public void load(ImageView _iv, String _url) {
        Bitmap bm = getCache(_url);
        if (bm == null) {
//            Log.e(TAG, "WebLoad");
            BitmapLoaderTask task = new BitmapLoaderTask(_iv, _url);
            task.execute();
            if (mTask!= null)
                mTask.add(task);
        }else {
          if(_iv!=null && _url.equals(_iv.getTag()))
              _iv.setImageBitmap(bm);
        }
    }

    public void cancelAllTasks() {
        if (mTask==null || mTask.size()<=0) return;
        for (BitmapLoaderTask task : mTask) {
            task.cancel(false);
        }
    }

    private Bitmap webLoad(String _url) {
        Bitmap bitmap = null;
        try {
            InputStream is = null;
            URL url = new URL(_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getCache(String _url){
        return mLruCache.get(_url);
    }

    private void putCache(String _url,Bitmap _bm){
        if (_url!=null && _bm!=null)
            mLruCache.put(_url, _bm);
    }

    private class BitmapLoaderTask extends AsyncTask<Void, Void, Bitmap> {

        private ImageView iv;
        private String mUrl;

        BitmapLoaderTask(ImageView iv, String mUrl) {
            this.iv = iv;
            this.mUrl = mUrl;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = webLoad(mUrl);
            if (bitmap != null) {
                putCache(mUrl,bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onCancelled() {
//            Log.e(TAG, "onCancelled");
            super.onCancelled();
            mTask.remove(this);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (iv!=null && mUrl.equals(iv.getTag()) && bitmap != null)
//                Log.e(TAG, "load bitmap");
                iv.setImageBitmap(bitmap);
            mTask.remove(this);
        }
    }

}
