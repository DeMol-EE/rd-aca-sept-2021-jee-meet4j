package world.inetum.realdolmen.meetings;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import world.inetum.realdolmen.RestIntegrationTestBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;

public class MeetingResourceIT extends RestIntegrationTestBase {

    @Test
    public void testGetProposal() throws Exception {
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "hello");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi");
        // act
        Response response = spec
                .queryParam("inv", new HashSet<>(Arrays.asList(1L, 2L)))
                .get("/meetings");
        // assert
        response.then()
                // can't really verify start without reimplementing the algorithm here...
                .body("start", matchesPattern("[0-9]{4}-[0-9]{2}-[0-9]{2}T[012][0-9](:[0-5][0-9]){2}"))
                .body("duration", equalTo("PT1H"))
                .body("inviteeIds", containsInAnyOrder(1, 2));
    }

    @Test
    public void testCreate() throws Exception {
        // arrange
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "hello");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi");
        LocalDateTime inTheFuture = LocalDateTime.of(
                LocalDate.of(3000, 1, 1), // wednesday
                LocalTime.of(9, 30));
        // act
        Response response = spec
                .body("{\"start\":\"" + inTheFuture + "\", \"duration\": \"PT1H\", \"inviteeIds\": [1, 2]}")
                .contentType(ContentType.JSON)
                .post("/meetings");
        // assert
        response.then()
                .statusCode(is(201));
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement("select id from meetings where start = ?");
            ps.setTimestamp(1, Timestamp.valueOf(inTheFuture));
            ResultSet rs = ps.executeQuery();
            // Verify there is a meeting
            Assert.assertTrue(rs.next());
            long meetingId = rs.getLong("id");
            // should also verify that invitations are created...
            PreparedStatement ips = c.prepareStatement(
                    "select person_id, status from invitations where meeting_id = ?");
            ips.setLong(1, meetingId);
            ResultSet irs = ips.executeQuery();
            Set<Long> ids = new HashSet<>();
            while (irs.next()) {
                ids.add(irs.getLong("person_id"));
                // verify that default status is NO_RESPONSE
                Assert.assertEquals(irs.getString("status"), "NO_RESPONSE");
            }
            Assert.assertEquals(ids, new HashSet<>(Arrays.asList(1L, 2L)));
        });
        // assert that mails were sent... not trivially done!
    }
}