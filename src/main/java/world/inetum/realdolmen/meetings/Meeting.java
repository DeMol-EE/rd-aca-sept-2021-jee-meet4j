package world.inetum.realdolmen.meetings;

import world.inetum.realdolmen.invitations.Invitation;
import world.inetum.realdolmen.jpa.Auditable;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meetings")
public class Meeting extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @FutureOrPresent
    @NotNull
    @Column(name = "start")
    private LocalDateTime start;

    @NotNull
    @Column(name = "duration")
    private Duration duration;

    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Size(min = 2)
    @NotNull
    private List<Invitation> invitations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        LocalDateTime meetingStart = this.start;
        LocalDateTime meetingEnd = this.start.plus(duration);
        return end.isAfter(meetingStart) && start.isBefore(meetingEnd);
    }
}
