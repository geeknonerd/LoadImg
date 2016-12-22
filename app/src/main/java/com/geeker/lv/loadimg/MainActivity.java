package com.geeker.lv.loadimg;

import android.net.Uri;
import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity {

    private static final String NEWS_URL = "";
    private final String TAG = getClass().getName();

    ListView lvView;
    NewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvView = (ListView) findViewById(R.id.lv_show_news);
        new LoadDataTask().execute(NEWS_URL+"&page=1");
        new LoadDataTask().execute(NEWS_URL+"&page=2");
        new LoadDataTask().execute(NEWS_URL+"&page=3");
        new LoadDataTask().execute(NEWS_URL+"&page=4");
        new LoadDataTask().execute(NEWS_URL+"&page=5");
        new LoadDataTask().execute(NEWS_URL+"&page=6");
        new LoadDataTask().execute(NEWS_URL+"&page=7");
        new LoadDataTask().execute(NEWS_URL+"&page=8");
        new LoadDataTask().execute(NEWS_URL+"&page=9");
        new LoadDataTask().execute(NEWS_URL+"&page=10");
        adapter = new NewAdapter(this, null, lvView);
        lvView.setAdapter(adapter);
    }

    private class LoadDataTask extends AsyncTask<String,Void,List<News>>{

        @Override
        protected void onPostExecute(List<News> newses) {
//            Log.e(TAG, ""+newses.size());
            List<News> oriNews = adapter.getNewsList();
            if (oriNews==null){
                oriNews = newses;
            }else {
                oriNews.addAll(newses);
            }
            adapter.setNewsList(oriNews);
        }

        @Override
        protected List<News> doInBackground(String... params) {
            String s = loadData(params[0]);
//            Log.e(TAG, s);
            return strToJson(s);
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
