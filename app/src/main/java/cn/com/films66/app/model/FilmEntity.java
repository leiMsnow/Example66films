package cn.com.films66.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.shuyu.core.uils.DateUtils;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class FilmEntity implements Parcelable {

    public int id;
    public String name;
    public String introduction;
    public int runtime;
    public String created_at;
    public String updated_at;
    public String cover_url;
    public String background_image_url;

    public FilmEntity() {
    }

    protected FilmEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        introduction = in.readString();
        runtime = in.readInt();
        created_at = in.readString();
        updated_at = in.readString();
        cover_url = in.readString();
        background_image_url = in.readString();
    }

    public static final Creator<FilmEntity> CREATOR = new Creator<FilmEntity>() {
        @Override
        public FilmEntity createFromParcel(Parcel in) {
            return new FilmEntity(in);
        }

        @Override
        public FilmEntity[] newArray(int size) {
            return new FilmEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(introduction);
        dest.writeInt(runtime);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(cover_url);
        dest.writeString(background_image_url);
    }

    public String getRuntime() {
        return DateUtils.formatTime(runtime * 60 * 1000);
    }
}
