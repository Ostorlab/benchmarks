package com.example.myapplication3;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataParcelable implements Parcelable {
    public static final Parcelable.Creator<DataParcelable> CREATOR = new Parcelable.Creator<>() {
        @Override
        public DataParcelable[] newArray(int i) {
            return new DataParcelable[i];
        }

        @Override
        public DataParcelable createFromParcel(Parcel parcel) {
            return new DataParcelable(parcel);
        }
    };

    private static final Gson GSON = new GsonBuilder().create();

    public Object data;

    public DataParcelable(Object data) {
        this.data = data;
    }

    private DataParcelable(Parcel parcel) {
        try {
            Class clazz = Class.forName(parcel.readString());
            data = GSON.fromJson(parcel.readString(), clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data.getClass().getCanonicalName());
        parcel.writeString(GSON.toJson(data));
    }
}