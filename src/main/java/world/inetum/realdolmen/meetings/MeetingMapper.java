package world.inetum.realdolmen.meetings;

import world.inetum.realdolmen.invitations.Invitation;
import world.inetum.realdolmen.invitations.InvitationStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MeetingMapper {
    public static MeetingDetailsResponse toDetails(Meeting meeting) {
        if (meeting == null) {
            return null;
        }
        Map<String, InvitationStatus> invitations = meeting.getInvitations()
                .stream()
                .collect(Collectors.toMap(
                        inv -> inv.getPerson().getFullName(),
                        Invitation::getStatus));
        return new MeetingDetailsResponse(
                meeting.getStart(),
                meeting.getDuration(),
                invitations);
    }

    public static MeetingProposalResponse toProposal(
            Meeting meeting,
            List<Long> inviteeIds
    ) {
        if (meeting == null) {
            return null;
        }
        return new MeetingProposalResponse(
                meeting.getStart(),
                meeting.getDuration(),
                inviteeIds
        );
    }
}
