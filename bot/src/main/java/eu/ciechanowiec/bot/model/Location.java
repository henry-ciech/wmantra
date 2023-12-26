package eu.ciechanowiec.bot.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor
public final class Location {

    @SerializedName("location")
    private LocationDetails locationDetails;

    @Getter
    @AllArgsConstructor
    public static class LocationDetails {
        @SerializedName("name")
        private final String city;

        @SerializedName("country")
        private final String country;

        @SerializedName("lon")
        private final double longitude;

        @SerializedName("lat")
        private final double latitude;
    }

    public String country() {
        return locationDetails.getCountry();
    }

    public String city() {
        return locationDetails.getCity();
    }

    @Override
    public String toString() {
        if (locationDetails != null) {
            return "LocationData[" +
                    "city=" + locationDetails.city + ", " +
                    "country=" + locationDetails.country + ", " +
                    "longitude=" + locationDetails.longitude + ", " +
                    "latitude=" + locationDetails.latitude + ']';
        } else {
            return "LocationData[No location data]";
        }
    }
}
