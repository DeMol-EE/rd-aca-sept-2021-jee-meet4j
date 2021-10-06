package world.inetum.realdolmen.invitations;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Stateless
public class EmailBean {
    // could write something with CDI to inject config values here...

    private final String url = "http://localhost:8081/hello-world/email";
    private final String username = "RDM";
    private final String password = "P@ssw0rd";

    // Make sure that failures to send an email don't roll back our transaction
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void mailInvitation(String email, LocalDateTime start) throws EmailFailureException {
        Response response = ClientBuilder
                .newClient()
                .target(url)
                .queryParam("to", email)
                .request()
                .header("Authorization", "Basic " + getToken())
                .buildPost(Entity.entity(
                        "You are invited to attend a meeting at " + start.toString(),
                        MediaType.TEXT_PLAIN_TYPE))
                .invoke();
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new EmailFailureException(
                    response.getStatus(),
                    email);
        }
    }

    private String getToken() {
        String rawToken = username+":"+password;
        return Base64
                .getEncoder()
                .encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
    }

    public static class EmailFailureException extends Exception {
        private final int status;
        private final String email;

        public EmailFailureException(int status, String email) {
            this.status = status;
            this.email = email;
        }

        public int getStatus() {
            return status;
        }

        public String getEmail() {
            return email;
        }

    }
}
