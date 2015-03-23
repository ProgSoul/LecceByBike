package progsoul.opendata.leccebybike.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ProgSoul on 08/03/2015.
 */
public class BikeSharingStation implements Parcelable {
    private String name;
    private boolean isOperative;
    private int freeBikes;
    private int availablePlaces;
    private String address;
    private double latitude;
    private double longitude;
    private String imageURL;

    public BikeSharingStation(String name, boolean isOperative, int freeBikes, String address, int availablePlaces, double latitude, double longitude, String imageURL) {
        this.name = name;
        this.isOperative = isOperative;
        this.freeBikes = freeBikes;
        this.address = address;
        this.availablePlaces = availablePlaces;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL;
    }

    public BikeSharingStation(Parcel parcel) {
        this.name = parcel.readString();
        this.isOperative = parcel.readByte() != 0;     //myBoolean == true if byte != 0
        this.freeBikes = parcel.readInt();
        this.availablePlaces = parcel.readInt();
        this.address = parcel.readString();
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
        this.imageURL = parcel.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOperative() {
        return isOperative;
    }

    public void setOperative(boolean isOperative) {
        this.isOperative = isOperative;
    }

    public int getFreeBikes() {
        return freeBikes;
    }

    public void setFreeBikes(int freeBikes) {
        this.freeBikes = freeBikes;
    }

    public int getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(int availablePlaces) {
        this.availablePlaces = availablePlaces;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isOperative ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeInt(freeBikes);
        dest.writeInt(availablePlaces);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(imageURL);
    }

    //method to recreate a catch from a Parcel
    public static Creator<BikeSharingStation> CREATOR = new Creator<BikeSharingStation>() {
        @Override
        public BikeSharingStation createFromParcel(Parcel source) {
            return new BikeSharingStation(source);
        }

        @Override
        public BikeSharingStation[] newArray(int size) {
            return new BikeSharingStation[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BikeSharingStation that = (BikeSharingStation) o;

        if (availablePlaces != that.availablePlaces) return false;
        if (freeBikes != that.freeBikes) return false;
        if (isOperative != that.isOperative) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (!address.equals(that.address)) return false;
        if (!imageURL.equals(that.imageURL)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }
}
