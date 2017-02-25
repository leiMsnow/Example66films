package cn.com.films66.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class CustomFileEntity implements Parcelable{

    public int play_offset_ms;
    public String title;
    public String acrid;
    public String audio_id;

    protected CustomFileEntity(Parcel in) {
        play_offset_ms = in.readInt();
        title = in.readString();
        acrid = in.readString();
        audio_id = in.readString();
    }

    public static final Creator<CustomFileEntity> CREATOR = new Creator<CustomFileEntity>() {
        @Override
        public CustomFileEntity createFromParcel(Parcel in) {
            return new CustomFileEntity(in);
        }

        @Override
        public CustomFileEntity[] newArray(int size) {
            return new CustomFileEntity[size];
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
}
