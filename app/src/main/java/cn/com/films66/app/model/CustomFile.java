package cn.com.films66.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class CustomFile implements Parcelable{

    public int play_offset_ms;
    public String title;
    public String acrid;
    public String audio_id;

    protected CustomFile(Parcel in) {
        play_offset_ms = in.readInt();
        title = in.readString();
        acrid = in.readString();
        audio_id = in.readString();
    }

    public CustomFile() {
    }

    public static final Creator<CustomFile> CREATOR = new Creator<CustomFile>() {
        @Override
        public CustomFile createFromParcel(Parcel in) {
            return new CustomFile(in);
        }

        @Override
        public CustomFile[] newArray(int size) {
            return new CustomFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(play_offset_ms);
        dest.writeString(title);
        dest.writeString(acrid);
        dest.writeString(audio_id);
    }

    @Override
    public String toString() {
        return "CustomFile{" +
                "play_offset_ms=" + play_offset_ms +
                ", title='" + title + '\'' +
                ", acrid='" + acrid + '\'' +
                ", audio_id='" + audio_id + '\'' +
                '}';
    }
}
