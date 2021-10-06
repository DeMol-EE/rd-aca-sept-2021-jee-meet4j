package world.inetum.realdolmen;

import org.testng.Assert;
import org.testng.annotations.Test;
import world.inetum.realdolmen.invitations.Invitation;
import world.inetum.realdolmen.invitations.InvitationStatus;
import world.inetum.realdolmen.meetings.Meeting;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

public class PlanningServiceTest {

    private final PlanningService sut = new PlanningService();

    @Test
    public void findsEarliestMomentNextDay() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 23), // Monday,
                LocalTime.of(20, 7)
        );
        Meeting proposal = sut.proposeMeetingFor(Collections.emptyList(), Duration.ofMinutes(60), begin);
        Assert.assertEquals(proposal.getStart(), LocalDateTime.of(2021, 8, 24, 9, 0));
    }

    @Test
    public void findsEarliestMomentAfterWeekend() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 28), // Saturday
                LocalTime.of(10, 7)
        );
        Meeting proposal = sut.proposeMeetingFor(Collections.emptyList(), Duration.ofMinutes(60), begin);
        Assert.assertEquals(proposal.getStart(), LocalDateTime.of(2021, 8, 30, 9, 0));
    }

    @Test
    public void treatsInvitationsAsNotAvailable() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 23), // Monday
                LocalTime.of(20, 7)
        );
        Meeting m = new Meeting();
        m.setStart(LocalDateTime.of(2021, 8, 24, 9, 0));
        m.setDuration(Duration.ofMinutes(60));
        Invitation i = new Invitation();
        i.setMeeting(m);
        Meeting proposal = sut.proposeMeetingFor(Collections.singletonList(i), Duration.ofMinutes(60), begin);
        Assert.assertEquals(proposal.getStart(), LocalDateTime.of(2021, 8, 24, 10, 0));
    }

    @Test
    public void treatsRejectedInvitationsAsAvailable() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 23), // Monday
                LocalTime.of(20, 7)
        );
        Meeting m = new Meeting();
        m.setStart(LocalDateTime.of(2021, 8, 24, 9, 0));
        m.setDuration(Duration.ofMinutes(60));
        Invitation i = new Invitation();
        i.setStatus(InvitationStatus.REJECTED);
        i.setMeeting(m);
        Meeting proposal = sut.proposeMeetingFor(Collections.singletonList(i), Duration.ofMinutes(60), begin);
        Assert.assertEquals(proposal.getStart(), LocalDateTime.of(2021, 8, 24, 9, 0));
    }
}