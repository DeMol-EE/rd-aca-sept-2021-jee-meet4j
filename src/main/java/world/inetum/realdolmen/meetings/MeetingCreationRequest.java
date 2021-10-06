package world.inetum.realdolmen.meetings;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class MeetingCreationRequest {
    @NotNull
    @FutureOrPresent
    public LocalDateTime start;

    @NotNull
    public Duration duration;

    @NotNull
    @Size(min = 2)
    public List<Long> inviteeIds;
}
