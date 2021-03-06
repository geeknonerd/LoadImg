package com.geeker.lv.loadimg;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NewAdapter.LoadingListener {

    private static final String NEWS_URL = "http://api.tianapi.com/guonei/?key=&num=50&page=";
    private final String TAG = getClass().getName();

    SwipeRefreshLayout srlContain;
    ListView lvView;
    NewAdapter adapter;
    int mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srlContain = (SwipeRefreshLayout) findViewById(R.id.srl_show);
        srlContain.setColorSchemeColors(0x99000066);
        srlContain.setOnRefreshListener(this);
        lvView = (ListView) findViewById(R.id.lv_show_news);
        mPage = 1;
        new LoadDataTask().execute(NEWS_URL + mPage++);
        adapter = new NewAdapter(this, null, lvView);
        adapter.setLoadingListener(this);
        lvView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        new LoadDataTask(){
            @Override
            protected Void doInBackground(String... params) {
                String s = loadData(params[0]);
//            Log.e(TAG, s);
                List<News> newses = strToJson(s);
                adapter.updateNewsList(newses);
                return null;
            }
        }.execute(NEWS_URL+1);
    }

    @Override
    public void onLoading() {
        new LoadDataTask().execute(NEWS_URL + mPage++);
    }

    private class LoadDataTask extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... params) {
            String s = loadData(params[0]);
//            Log.e(TAG, s);
            List<News> newses = strToJson(s);
            adapter.addNewsList(newses);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //            Log.e(TAG, ""+newses.size());
//            adapter.setNewsList(newses);
            if (srlContain!=null&&srlContain.isRefreshing()) {
                srlContain.setRefreshing(false);
            }
            adapter.loadFinish();
        }
    }

    private List<News> strToJson(String s) {
        NewsData newsData = new Gson().fromJson(s, NewsData.class);
        if (newsData!=null) return new ArrayList<>(Arrays.asList(newsData.getNewslist()));
        return null;
    }

    private String loadData(String s) {
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(s);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            byte[] buf = new byte[4096];
            String bs = null;
            while ((bs = br.readLine())!=null) {
                sb.append(bs);
            }
            is.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
