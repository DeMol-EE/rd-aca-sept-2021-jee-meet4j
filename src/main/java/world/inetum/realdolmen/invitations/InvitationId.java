package world.inetum.realdolmen.invitations;

import java.io.Serializable;
import java.util.Objects;

public class InvitationId implements Serializable {
    private Long person;
    private Long meeting;

    public Long getPersonId() {
        return person;
    }

    public void setPerson(Long person) {
        this.person = person;
    }

    public Long getMeeting() {
        return meeting;
    }

    public void setMeeting(Long meeting) {
        this.meeting = meeting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvitationId that = (InvitationId) o;
        return Objects.equals(person, that.person) && Objects.equals(meeting, that.meeting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, meeting);
    }
}
