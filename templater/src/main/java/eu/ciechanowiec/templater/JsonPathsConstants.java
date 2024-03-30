package eu.ciechanowiec.templater;

@SuppressWarnings("PMD.DataClass")
public final class JsonPathsConstants {

    public static final String TEMP_C = "temp_c";
    public static final String CONDITION = "condition";
    public static final String LOCATION = "location";
    public static final String TEMP = "temp";
    public static final String FORECAST = "forecast";
    public static final String FORECAST_DAY = "forecastday";
    public static final String HOUR = "hour";
    public static final String TIME = "time";
    public static final String CURRENT = "current";
    public static final String TEXT = "text";
    public static final String LAST_UPDATE = "last_updated";
    public static final String NAME = "name";
    public static final String COUNTRY = "country";
    public static final String LOCAL_TIME = "localtime";

    private JsonPathsConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
