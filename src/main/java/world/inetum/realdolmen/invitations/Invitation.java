package world.inetum.realdolmen.invitations;

import world.inetum.realdolmen.meetings.Meeting;
import world.inetum.realdolmen.people.Person;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@IdClass(InvitationId.class)
public class Invitation {

    @Id
    @JoinColumn(name = "meeting_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Meeting meeting;

    @Id
    @JoinColumn(name = "person_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Person person;

    // note the default value here
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status")
    private InvitationStatus status = InvitationStatus.NO_RESPONSE;

    public Meeting getMeeting() {
        return meeting;
    }

    public Invitation setMeeting(Meeting meeting) {
        this.meeting = meeting;
        return this;
    }

    public Person getPerson() {
        return person;
    }

    public Invitation setPerson(Person person) {
        this.person = person;
        return this;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public Invitation setStatus(InvitationStatus status) {
        this.status = status;
        return this;
    }

    public boolean isAccepted() {
        return getStatus() == InvitationStatus.ACCEPTED;
    }

    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        return (getStatus() != InvitationStatus.REJECTED)
                && meeting.overlapsWith(start, end);
    }
}
