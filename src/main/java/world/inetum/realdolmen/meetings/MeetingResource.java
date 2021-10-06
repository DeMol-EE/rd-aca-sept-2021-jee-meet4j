package world.inetum.realdolmen.meetings;

import world.inetum.realdolmen.invitations.EmailBean;
import world.inetum.realdolmen.PlanningService;
import world.inetum.realdolmen.invitations.Invitation;
import world.inetum.realdolmen.people.Person;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("meetings")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class MeetingResource {

    @PersistenceContext
    EntityManager entityManager;

    @EJB
    PlanningService planningService;

    @EJB
    EmailBean emailBean;

    @GET
    @Path("{meetingId}")
    public MeetingDetailsResponse getDetailsById(@PathParam("meetingId") long id) {
        Meeting m = entityManager.find(Meeting.class, id);
        return MeetingMapper.toDetails(m);
    }

    @GET
    // If authenticated: add current user id to invitee ids?
    public MeetingProposalResponse getProposal(
            @QueryParam("duration") @DefaultValue("60") int duration,
            @QueryParam("inv") @NotNull @Size(min = 2) List<Long> inviteeIds
    ) {
        // Fetching here keeps the service "clean" (POJO without entitymanager or persistence annotations).
        // Getting invitations instead of persons is more memory efficient
        // (otherwise all meetings for each person would be fetched, which could include a long history)
        List<Invitation> invitations = entityManager
                .createQuery(
                        "select i from Invitation i where i.person.id in :ids and i.meeting.start >= :now",
                        Invitation.class)
                .setParameter("ids", inviteeIds)
                .setParameter("now", LocalDateTime.now())
                .getResultList();
        Meeting meetingProposal = planningService.proposeMeetingFor(
                invitations,
                Duration.ofMinutes(duration),
                LocalDateTime.now()
        );
        return MeetingMapper.toProposal(meetingProposal, inviteeIds);
    }

    @POST
    // Require authentication and put current user as meeting host?
    public Response create(
            @Valid @NotNull MeetingCreationRequest mcr
    ) throws MeetingCreationFailureException {
        Meeting meeting = new Meeting();
        meeting.setStart(mcr.start);
        meeting.setDuration(mcr.duration);
        // validate that each invitee exists by translating it to its managed entity
        meeting.setInvitations(mcr.inviteeIds
                .stream()
                .map(personId -> entityManager.getReference(Person.class, personId))
                .map(person -> new Invitation()
                        .setMeeting(meeting)
                        .setPerson(person))
                .collect(Collectors.toList()));
        entityManager.persist(meeting);
        // send invitation emails
        List<EmailBean.EmailFailureException> exceptions = new ArrayList<>();
        for (Invitation inv : meeting.getInvitations()) {
            try {
                emailBean.mailInvitation(
                        inv.getPerson().getEmail(),
                        inv.getMeeting().getStart()
                );
            } catch (EmailBean.EmailFailureException e) {
                exceptions.add(e);
            }
        }
        if (!exceptions.isEmpty()) {
            throw new MeetingCreationFailureException(exceptions);
            // use specific exception + mapper?
        }
        // should check what default status code is for POST
        return Response.status(Response.Status.CREATED).build();
    }
}
