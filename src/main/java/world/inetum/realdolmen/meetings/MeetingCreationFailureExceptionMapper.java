package world.inetum.realdolmen.meetings;

import world.inetum.realdolmen.invitations.EmailBean;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class MeetingCreationFailureExceptionMapper implements ExceptionMapper<MeetingCreationFailureException> {
    @Override
    public Response toResponse(MeetingCreationFailureException exception) {
        List<EmailBean.EmailFailureException> exceptions = exception.getCauses();
        return Response.status(Response.Status.BAD_GATEWAY)
                .entity(
                        "Failed to send email to: " + exceptions
                                .stream()
                                .map(e -> e.getEmail() + " (status: " + e.getStatus() + ")")
                                .collect(Collectors.joining())
                ).build();
    }
}
