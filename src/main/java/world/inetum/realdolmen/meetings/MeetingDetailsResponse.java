package world.inetum.realdolmen.meetings;

import world.inetum.realdolmen.invitations.InvitationStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class MeetingDetailsResponse {

    private final LocalDateTime start;
    private final Duration duration;
    private final Map<String, InvitationStatus> invitations;

    public MeetingDetailsResponse(
            LocalDateTime start,
            Duration duration,
            Map<String, InvitationStatus> invitations
    ) {
        this.start = start;
        this.duration = duration;
        this.invitations = invitations;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public Duration getDuration() {
        return duration;
    }

    public Map<String, InvitationStatus> getInvitations() {
        return invitations;
    }
}
