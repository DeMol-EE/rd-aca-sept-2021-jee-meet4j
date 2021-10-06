package world.inetum.realdolmen.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;

// auto-apply: register as global converter for all Duration fields
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Duration duration) {
        if (duration == null) {
            return null;
        }
        return (int) (duration.getSeconds() / 60);
    }

    @Override
    public Duration convertToEntityAttribute(Integer s) {
        if (s == null) {
            return null;
        }
        return Duration.ofMinutes(s);
    }
}
