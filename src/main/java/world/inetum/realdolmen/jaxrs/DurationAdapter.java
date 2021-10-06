package world.inetum.realdolmen.jaxrs;

import javax.json.bind.adapter.JsonbAdapter;
import javax.ws.rs.ext.Provider;
import java.time.Duration;

@Provider
public class DurationAdapter implements JsonbAdapter<Duration, String> {
    @Override
    public String adaptToJson(Duration obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    @Override
    public Duration adaptFromJson(String obj) {
        if (obj == null) {
            return null;
        }
        return Duration.parse(obj);
    }
}
