package world.inetum.realdolmen.people;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import world.inetum.realdolmen.RestIntegrationTestBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;

public class PersonResourceIT extends RestIntegrationTestBase {

    @Test
    void create() throws Exception {
        String json = "{\"firstName\": \"Robin\", \"lastName\": \"De Mol\", \"email\": \"robin.demol@realdolmen.com\", \"password\": \"hi\"}";
        // act
        Response response = spec
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/people");
        // assert
        response.then()
                .statusCode(is(201));
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement(
                    "select password from persons where first_name = ? and last_name = ? and email = ?"
            );
            ps.setString(1, "Robin");
            ps.setString(2, "De Mol");
            ps.setString(3, "robin.demol@realdolmen.com");
            ResultSet rs = ps.executeQuery();
            Assert.assertTrue(rs.next());
            // verify that password is not stored plain text
            Assert.assertNotEquals("hi", rs.getString("password"));
        });
    }


    @Test
    void updateInvitation() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, Arrays.asList(1L, 2L));
        // act
        Response response = spec
                .auth()
                .preemptive()
                .basic("brecht.g@realdolmen.com", "sdasdad")
                .pathParam("pid", 1L)
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(204));
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 1L);
            ps.setString(3, "ACCEPTED");
            ResultSet rs = ps.executeQuery();
            Assert.assertTrue(rs.next());
        });
    }

    // Too edgy for IT, better to rework to a unit test
    @Test
    void canNotUpdateInvitationWithoutCredentials() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, Arrays.asList(1L, 2L));
        // act
        Response response = spec
                // no auth
                .pathParam("pid", 1L)
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(401));
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 1L);
            ps.setString(3, "ACCEPTED");
            ResultSet rs = ps.executeQuery();
            Assert.assertFalse(rs.next());
        });
    }

    // Too edgy for IT, better to rework to a unit test
    @Test
    void canNotUpdateInvitationWithWrongCredentials() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, Arrays.asList(1L, 2L));
        // act
        Response response = spec
                .auth()
                .preemptive()
                .basic("brecht.g@realdolmen.com", "wrong-password")
                .pathParam("pid", 1L)
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(401));
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 1L);
            ps.setString(3, "ACCEPTED");
            ResultSet rs = ps.executeQuery();
            Assert.assertFalse(rs.next());
        });
    }

    // Too edgy for IT, better to rework to a unit test
    @Test
    void canNotUpdateInvitationOfOtherUser() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, Arrays.asList(1L, 2L));
        // act
        Response response = spec
                .auth()
                .preemptive()
                .basic("brecht.g@realdolmen.com", "sdasdad")
                .pathParam("pid", 2L) // incorrect person id!
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(404));
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 2L);
            ps.setString(3, "ACCEPTED");
            ResultSet rs = ps.executeQuery();
            Assert.assertFalse(rs.next());
        });
    }
}