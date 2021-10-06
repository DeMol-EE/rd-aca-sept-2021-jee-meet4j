package world.inetum.realdolmen.jaxrs;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDateTime;

public class LocalDateTimeParamConverter implements ParamConverter<LocalDateTime> {
    @Override
    public LocalDateTime fromString(String value) {
        if (value == null) {
            return null;
        }
        return LocalDateTime.parse(value);
    }

    @Override
    public String toString(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
