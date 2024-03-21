package eu.ciechanowiec.bot.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationDetails {

    @SerializedName("name")
    private final String city;
    @SerializedName("country")
    private final String country;
    @SerializedName("lon")
    private final double longitude;
    @SerializedName("lat")
    private final double latitude;
}
