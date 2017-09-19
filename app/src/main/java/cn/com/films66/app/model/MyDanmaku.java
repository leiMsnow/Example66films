package cn.com.films66.app.model;

/**
 * Created by Azure on 2017/9/16.
 */

public class MyDanmaku {
    public int time;
    public String content;

    @Override
    public String toString() {
        return "MyDanmaku{" +
                "time=" + time +
                ", content='" + content + '\'' +
                '}';
    }
}
