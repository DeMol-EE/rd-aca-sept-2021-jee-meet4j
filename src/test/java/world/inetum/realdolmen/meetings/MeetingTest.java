package world.inetum.realdolmen.meetings;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class MeetingTest {

    @Test(dataProvider = "intervals")
    public void testOverlaps(LocalDateTime start, LocalDateTime end, boolean shouldOverlap) {
        Meeting sut = new Meeting();
        sut.setStart(LocalDateTime.of(2021, 8, 23, 21, 0));
        sut.setDuration(Duration.ofMinutes(60));
        boolean overlaps = sut.overlapsWith(start, end);
        Assert.assertEquals(shouldOverlap, overlaps);
    }

    @DataProvider(name = "intervals")
    public Object[][] createIntervals() {
        return new Object[][]{
                {
                        LocalDateTime.of(2021, 8, 23, 19, 0),
                        LocalDateTime.of(2021, 8, 23, 20, 0),
                        false
                }, {
                        LocalDateTime.of(2021, 8, 23, 23, 0),
                        LocalDateTime.of(2021, 8, 23, 23, 30),
                        false
                }, {
                        LocalDateTime.of(2021, 8, 23, 20, 0),
                        LocalDateTime.of(2021, 8, 23, 21, 0),
                        false
                }, {
                        LocalDateTime.of(2021, 8, 23, 22, 0),
                        LocalDateTime.of(2021, 8, 23, 23, 0),
                        false
                }, {
                        LocalDateTime.of(2021, 8, 23, 20, 30),
                        LocalDateTime.of(2021, 8, 23, 21, 30),
                        true
                }, {
                        LocalDateTime.of(2021, 8, 23, 21, 30),
                        LocalDateTime.of(2021, 8, 23, 22, 30),
                        true
                }, {
                        LocalDateTime.of(2021, 8, 23, 21, 15),
                        LocalDateTime.of(2021, 8, 23, 21, 45),
                        true
                }, {
                        LocalDateTime.of(2021, 8, 23, 19, 45),
                        LocalDateTime.of(2021, 8, 23, 22, 15),
                        true
                }
        };
    }
}