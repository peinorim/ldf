package com.paocorp.defunessounds.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LDFSound implements Parcelable {

    private int id;
    private String name;
    private String res;
    private int isFavorite;

    public LDFSound() {
    }

    public LDFSound(int id, String name, String res, int isFavorite) {
        this.id = id;
        this.name = name;
        this.res = res;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public int isFavorite() {
        return isFavorite;
    }

    public void setFavorite(int favorite) {
        isFavorite = favorite;
    }

    public static final Creator<LDFSound> CREATOR = new Creator<LDFSound>() {
        @Override
        public LDFSound createFromParcel(Parcel in) {
            return new LDFSound(in);
        }

        @Override
        public LDFSound[] newArray(int size) {
            return new LDFSound[size];
        }
    };

    private LDFSound(Parcel in) {
        id = in.readInt();
        name = in.readString();
        res = in.readString();
        isFavorite = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(res);
        parcel.writeInt(isFavorite);
    }
}
