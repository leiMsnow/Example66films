package cn.com.films66.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import cn.com.films66.app.utils.TimeUtils;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class FilmEvents implements Parcelable {

    public static final int TYPE_FILM = 1;
    public static final int TYPE_PICTURE = 2;
    public static final int TYPE_WEB = 3;

    public int id;
    public int film_id;
    public int type;
    public String start_time;
    public String end_time;
    public String resources_url;

    protected FilmEvents(Parcel in) {
        id = in.readInt();
        film_id = in.readInt();
        type = in.readInt();
        start_time = in.readString();
        end_time = in.readString();
        resources_url = in.readString();
    }

    public static final Creator<FilmEvents> CREATOR = new Creator<FilmEvents>() {
        @Override
        public FilmEvents createFromParcel(Parcel in) {
            return new FilmEvents(in);
        }

        @Override
        public FilmEvents[] newArray(int size) {
            return new FilmEvents[size];
        }
    };

    public int getStartTime() {
        return TimeUtils.stringToTime(start_time);
    }

    public int getEndTime() {
        return TimeUtils.stringToTime(end_time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(film_id);
        dest.writeInt(type);
        dest.writeString(start_time);
        dest.writeString(end_time);
        dest.writeString(resources_url);
    }

}
