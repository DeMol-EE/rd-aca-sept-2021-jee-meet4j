package world.inetum.realdolmen;

import world.inetum.realdolmen.invitations.Invitation;
import world.inetum.realdolmen.meetings.Meeting;

import javax.ejb.Stateless;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Stateless
public class PlanningService {

    public Meeting proposeMeetingFor(List<Invitation> invitations, Duration duration, LocalDateTime now) {
        // earliest proposal should be the next day
        LocalDateTime moment = now.plusDays(1).withHour(9).truncatedTo(ChronoUnit.HOURS);
        LocalDateTime threshold = now.plusWeeks(2);
        do {
            // iterate over slots until a "legal" candidate is found (on workday + in work hours)
            while (isWeekend(moment.getDayOfWeek()) || !isWorkHour(moment.getHour())) {
                moment = moment.plus(duration);
            }
            // see if we haven't gone too far yet
            if (moment.isAfter(threshold)) {
                return null;
            }
            LocalDateTime start = moment;
            LocalDateTime end = moment.plus(duration);
            boolean slotIsFree = invitations.stream()
                    .noneMatch(inv -> inv.overlapsWith(start, end));
            if (slotIsFree) {
                Meeting proposal = new Meeting();
                proposal.setDuration(duration); // 1h
                proposal.setStart(start);
                // do no set invitations, this is postponed until the create method
                return proposal;
            } else {
                moment = moment.plus(duration);
            }
        } while (moment.isBefore(threshold));
        // in case no slot was found
        return null;
    }

    private boolean isWeekend(DayOfWeek dow) {
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    private boolean isWorkHour(int hour) {
        return hour >= 9 && hour <= 15;
    }
}
