package com.geeker.lv.loadimg;

/**
 * Created by lv on 16-12-21.
 */
public class NewsData {
    private int code;
    private String msg;
    private News[] newslist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public News[] getNewslist() {
        return newslist;
    }

    public void setNewslist(News[] newslist) {
        this.newslist = newslist;
    }

    public NewsData() {

    }
}
