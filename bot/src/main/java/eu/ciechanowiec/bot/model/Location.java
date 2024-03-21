package eu.ciechanowiec.bot.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public final class Location {

    private static final String COMMA = ", ";
    @SerializedName("location")
    private LocationDetails locationDetails;

    public String country() {
        return locationDetails.getCountry();
    }

    public String city() {
        return locationDetails.getCity();
    }

    @Override
    @SuppressWarnings("Regexp")
    public String toString() {
        if (locationDetails != null) {
            return "LocationData["
                    + "city=" + locationDetails.getCity() + COMMA
                    + "country=" + locationDetails.getCountry() + COMMA
                    + "longitude=" + locationDetails.getLongitude() + COMMA
                    + "latitude=" + locationDetails.getLatitude() + ']';
        } else {
            return "LocationData[No location data]";
        }
    }
}
