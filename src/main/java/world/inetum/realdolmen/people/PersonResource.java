package world.inetum.realdolmen.people;

import world.inetum.realdolmen.invitations.Invitation;
import world.inetum.realdolmen.invitations.InvitationStatus;
import world.inetum.realdolmen.meetings.Meeting;
import world.inetum.realdolmen.meetings.MeetingDetailsResponse;
import world.inetum.realdolmen.meetings.MeetingMapper;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("people")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class PersonResource {

    static Logger logger = Logger.getLogger(PersonResource.class.getName());

    @PersistenceContext
    EntityManager entityManager;

    @EJB
    PasswordHasher passwordHasher;

    @GET
    public List<PersonDetailsResponse> getAll() {
        return entityManager
                .createQuery("select p from Person p", Person.class)
                .getResultStream()
                .map(PersonMapper::toDetails)
                .collect(Collectors.toList());
    }

    @POST
    public Response create(
            @Valid @NotNull PersonCreationRequest dto
    ) {
        Person person = new Person();
        person.setFirstName(dto.firstName);
        person.setLastName(dto.lastName);
        person.setEmail(dto.email);
        person.setPassword(passwordHasher.hash(dto.password));
        entityManager.persist(person);
        // To make this RESTful, we would need to return the URI to the
        // created resource, but... we don't have such an endpoint.
        // Also, we would have to push the persist method into an EJB
        // with a new transaction (now person.getId() returns null).
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("{personId}/meetings")
    public List<MeetingDetailsResponse> getMeetingsForPerson(
            @PathParam("personId") long personId,
            // requires a JAX-RS param converter
            @QueryParam("from") LocalDateTime start
    ) {
        return entityManager
                .createQuery(
                        "select m from Meeting m join m.invitations i where i.person.id = :pid and (:start is null or m.start >= :start) order by m.start ASC",
                        Meeting.class)
                .setParameter("pid", personId)
                .setParameter("start", start)
                .getResultStream()
                .map(MeetingMapper::toDetails)
                .collect(Collectors.toList());
    }

    @PUT
    @RolesAllowed("USER") // force that caller must be authenticated
    @Path("{personId}/meetings/{meetingId}")
    public void updateInvitation(
            @Context SecurityContext securityContext,
            @PathParam("personId") long personId,
            @PathParam("meetingId") long meetingId,
            InvitationStatus status
    ) {
        String accessedId = String.valueOf(personId);
        String actualId = securityContext.getUserPrincipal().getName();
        if (!accessedId.equals(actualId)) {
            logger.log(
                    Level.WARNING,
                    "Detected possible intrusion: user authenticated as {0} tried to access the invitation of user {1}",
                    new Object[]{actualId, accessedId}
            );
            // Silent failure (could also throw 404)
            throw new NotFoundException();
        }
        Invitation i = entityManager
                .createQuery(
                        "select i from Invitation i where i.person.id = ?1 and i.meeting.id = ?2",
                        Invitation.class)
                .setParameter(1, personId)
                .setParameter(2, meetingId)
                .getSingleResult();
        // ignore other fields (they should not be updatable)
        i.setStatus(status);
    }

    // not exposed as endpoint
    public Person findByEmailAndHashedPassword(String email, String pwHash) {
        return entityManager
                .createQuery(
                        "select p from Person p where p.email = ?1 and p.password = ?2",
                        Person.class)
                .setParameter(1, email)
                .setParameter(2, pwHash)
                .getSingleResult();
    }

}
