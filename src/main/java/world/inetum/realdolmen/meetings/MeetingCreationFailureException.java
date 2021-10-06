package world.inetum.realdolmen.meetings;

import world.inetum.realdolmen.invitations.EmailBean;

import java.util.List;

public class MeetingCreationFailureException extends Exception {

    private final List<EmailBean.EmailFailureException> causes;

    public MeetingCreationFailureException(
            List<EmailBean.EmailFailureException> causes
    ) {
        this.causes = causes;
    }

    public List<EmailBean.EmailFailureException> getCauses() {
        return causes;
    }
}
