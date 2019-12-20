package com.physi.beam.monitor.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceData implements Parcelable {

    private String no, number, location;

    public DeviceData(String no, String number, String location){
        this.no = no;
        this.number = number;
        this.location = location;
    }

    protected DeviceData(Parcel in) {
        no = in.readString();
        number = in.readString();
        location = in.readString();
    }

    public static final Creator<DeviceData> CREATOR = new Creator<DeviceData>() {
        @Override
        public DeviceData createFromParcel(Parcel in) {
            return new DeviceData(in);
        }

        @Override
        public DeviceData[] newArray(int size) {
            return new DeviceData[size];
        }
    };

    public String getLocation() {
        return location;
    }

    public String getNumber() {
        return number;
    }

    public String getNo() {
        return no;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(no);
        dest.writeString(number);
        dest.writeString(location);
    }
}
