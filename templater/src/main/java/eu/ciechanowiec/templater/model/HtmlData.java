package eu.ciechanowiec.templater.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class HtmlData {

    private String htmlTemplate;
    private String defaultColor;
    private String smallIconSize;
    private String bigIconSize;
    private String mainIconSize;
    private String mainMarginTop;
    private String subMarginTop;
    private Map<String, WeatherCondition> conditionMappings;
}
