package progsoul.opendata.leccebybike.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by ProgSoul on 26/03/2015.
 */
public class CyclePath implements Parcelable {
    private String name;
    private String description;
    private double[] latitudes;
    private double[] longitudes;
    private Features features;
    private Details details;
    private float distanceFromMyLocation;

    public CyclePath(String name, String description, double[] latitudes, double[] longitudes, Features features, Details details) {
        this.name = name;
        this.description = description;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.features = features;
        this.details = details;
    }

    public CyclePath(Parcel parcel) {
        this.name = parcel.readString();
        this.description = parcel.readString();

        int latitudesLenght = parcel.readInt();
        this.latitudes = new double[latitudesLenght];
        parcel.readDoubleArray(this.latitudes);

        int longitudesLenght = parcel.readInt();
        this.longitudes = new double[longitudesLenght];
        parcel.readDoubleArray(this.longitudes);

        this.features = parcel.readParcelable(Features.class.getClassLoader());
        this.details = parcel.readParcelable(Details.class.getClassLoader());
    }

    public CyclePath() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double[] getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(double[] latitudes) {
        this.latitudes = latitudes;
    }

    public double[] getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(double[] longitudes) {
        this.longitudes = longitudes;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public float getDistanceFromMyLocation() {
        return distanceFromMyLocation;
    }

    public void setDistanceFromMyLocation(float distanceFromMyLocation) {
        this.distanceFromMyLocation = distanceFromMyLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(latitudes.length);
        dest.writeDoubleArray(latitudes);
        dest.writeInt(longitudes.length);
        dest.writeDoubleArray(longitudes);
        dest.writeParcelable(features, flags);
        dest.writeParcelable(details, flags);
    }

    //method to recreate a catch from a Parcel
    public static Creator<CyclePath> CREATOR = new Creator<CyclePath>() {
        @Override
        public CyclePath createFromParcel(Parcel source) {
            return new CyclePath(source);
        }

        @Override
        public CyclePath[] newArray(int size) {
            return new CyclePath[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CyclePath)) return false;

        CyclePath cyclePath = (CyclePath) o;

        if (description != null ? !description.equals(cyclePath.description) : cyclePath.description != null)
            return false;
        if (details != null ? !details.equals(cyclePath.details) : cyclePath.details != null)
            return false;
        if (features != null ? !features.equals(cyclePath.features) : cyclePath.features != null)
            return false;
        if (!Arrays.equals(latitudes, cyclePath.latitudes)) return false;
        if (!Arrays.equals(longitudes, cyclePath.longitudes)) return false;
        if (name != null ? !name.equals(cyclePath.name) : cyclePath.name != null) return false;

        return true;
    }

    public static class Features implements Parcelable {
        private String distance;
        private boolean isSuitableForChildren;
        private boolean isSuitableForSkaters;
        private TYPE type;
        private ROAD_SURFACE roadSurface;

        public Features(String distance, boolean isSuitableForChildren, boolean isSuitableForSkaters, TYPE type, ROAD_SURFACE roadSurface) {
            this.distance = distance;
            this.isSuitableForChildren = isSuitableForChildren;
            this.isSuitableForSkaters = isSuitableForSkaters;
            this.type = type;
            this.roadSurface = roadSurface;
        }

        public Features(Parcel parcel) {
            this.distance = parcel.readString();
            this.isSuitableForChildren = parcel.readByte() != 0;     //myBoolean == true if byte != 0
            this.isSuitableForSkaters = parcel.readByte() != 0;     //myBoolean == true if byte != 0
            this.type = TYPE.values()[parcel.readInt()];
            this.roadSurface = ROAD_SURFACE.values()[parcel.readInt()];
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public boolean isSuitableForChildren() {
            return isSuitableForChildren;
        }

        public void setSuitableForChildren(boolean isSuitableForChildren) {
            this.isSuitableForChildren = isSuitableForChildren;
        }

        public boolean isSuitableForSkaters() {
            return isSuitableForSkaters;
        }

        public void setSuitableForSkaters(boolean isSuitableForSkaters) {
            this.isSuitableForSkaters = isSuitableForSkaters;
        }

        public TYPE getType() {
            return type;
        }

        public void setType(TYPE type) {
            this.type = type;
        }

        public ROAD_SURFACE getRoadSurface() {
            return roadSurface;
        }

        public void setRoadSurface(ROAD_SURFACE roadSurface) {
            this.roadSurface = roadSurface;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(distance);
            dest.writeByte((byte) (isSuitableForChildren ? 1 : 0));     //if myBoolean == true, byte == 1
            dest.writeByte((byte) (isSuitableForSkaters ? 1 : 0));     //if myBoolean == true, byte == 1
            dest.writeInt(type.ordinal());
            dest.writeInt(roadSurface.ordinal());
        }

        //method to recreate a catch from a Parcel
        public static Creator<Features> CREATOR = new Creator<Features>() {
            @Override
            public Features createFromParcel(Parcel source) {
                return new Features(source);
            }

            @Override
            public Features[] newArray(int size) {
                return new Features[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Features)) return false;

            Features features = (Features) o;

            if (isSuitableForChildren != features.isSuitableForChildren) return false;
            if (isSuitableForSkaters != features.isSuitableForSkaters) return false;
            if (distance != null ? !distance.equals(features.distance) : features.distance != null)
                return false;
            if (roadSurface != features.roadSurface) return false;
            if (type != features.type) return false;

            return true;
        }
    }

    public static class Details implements Parcelable{
        private String averageSlope;
        private String maxSlope;
        private String trackDensity;
        private String difference;
        private String ascentDifference;
        private String descentDifference;

        public Details(String averageSlope, String maxSlope, String trackDensity, String difference, String ascentDifference, String descentDifference) {
            this.averageSlope = averageSlope;
            this.maxSlope = maxSlope;
            this.trackDensity = trackDensity;
            this.difference = difference;
            this.ascentDifference = ascentDifference;
            this.descentDifference = descentDifference;
        }

        public Details(Parcel parcel) {
            this.averageSlope = parcel.readString();
            this.maxSlope = parcel.readString();
            this.trackDensity = parcel.readString();
            this.difference = parcel.readString();
            this.ascentDifference = parcel.readString();
            this.descentDifference = parcel.readString();
        }

        public String getAverageSlope() {
            return averageSlope;
        }

        public void setAverageSlope(String averageSlope) {
            this.averageSlope = averageSlope;
        }

        public String getMaxSlope() {
            return maxSlope;
        }

        public void setMaxSlope(String maxSlope) {
            this.maxSlope = maxSlope;
        }

        public String getTrackDensity() {
            return trackDensity;
        }

        public void setTrackDensity(String trackDensity) {
            this.trackDensity = trackDensity;
        }

        public String getDifference() {
            return difference;
        }

        public void setDifference(String difference) {
            this.difference = difference;
        }

        public String getAscentDifference() {
            return ascentDifference;
        }

        public void setAscentDifference(String ascentDifference) {
            this.ascentDifference = ascentDifference;
        }

        public String getDescentDifference() {
            return descentDifference;
        }

        public void setDescentDifference(String descentDifference) {
            this.descentDifference = descentDifference;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(averageSlope);
            dest.writeString(maxSlope);
            dest.writeString(trackDensity);
            dest.writeString(difference);
            dest.writeString(ascentDifference);
            dest.writeString(descentDifference);
        }

        public static Creator<Details> CREATOR = new Creator<Details>() {
            @Override
            public Details createFromParcel(Parcel source) {
                return new Details(source);
            }

            @Override
            public Details[] newArray(int size) {
                return new Details[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Details)) return false;

            Details details = (Details) o;

            if (ascentDifference != null ? !ascentDifference.equals(details.ascentDifference) : details.ascentDifference != null)
                return false;
            if (averageSlope != null ? !averageSlope.equals(details.averageSlope) : details.averageSlope != null)
                return false;
            if (descentDifference != null ? !descentDifference.equals(details.descentDifference) : details.descentDifference != null)
                return false;
            if (difference != null ? !difference.equals(details.difference) : details.difference != null)
                return false;
            if (maxSlope != null ? !maxSlope.equals(details.maxSlope) : details.maxSlope != null)
                return false;
            if (trackDensity != null ? !trackDensity.equals(details.trackDensity) : details.trackDensity != null)
                return false;

            return true;
        }
    }

    public enum TYPE {
        STRADA, CICLOSTRADA, SENTIERO, CICLABILE, CICLOPEDONALE
    }

    public enum ROAD_SURFACE {
        ASFALTO, MISTO
    }

}
