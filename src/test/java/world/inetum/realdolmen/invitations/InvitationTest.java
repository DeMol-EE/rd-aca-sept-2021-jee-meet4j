package world.inetum.realdolmen.invitations;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import world.inetum.realdolmen.meetings.Meeting;

import java.time.Duration;
import java.time.LocalDateTime;

public class InvitationTest {

    @Test(dataProvider = "cases")
    public void testOverlapsWith(InvitationStatus status, boolean shouldOverlap) {
        Meeting m = new Meeting();
        m.setStart(LocalDateTime.of(2021, 8, 23, 10, 0));
        m.setDuration(Duration.ofMinutes(60));
        Invitation sut = new Invitation();
        sut.setStatus(status);
        sut.setMeeting(m);
        boolean overlaps = sut.overlapsWith(
                LocalDateTime.of(2021, 8, 23, 9, 30),
                LocalDateTime.of(2021, 8, 23, 10, 30)
        );
        Assert.assertEquals(shouldOverlap, overlaps);
    }

    @DataProvider(name = "cases")
    public Object[][] createCases() {
        return new Object[][]{
                {InvitationStatus.NO_RESPONSE, true},
                {InvitationStatus.ACCEPTED, true},
                {InvitationStatus.REJECTED, false}
        };
    }
}