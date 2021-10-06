package world.inetum.realdolmen;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.testng.annotations.BeforeMethod;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class IntegrationTestBase {

    private final static DataSource dataSource;

    static {
        // TODO: get datasource params from system props
        String jdbc = "jdbc:mysql://localhost:3306/meet4j";
        String username = "acaddemicts";
        String password = "acaddemicts";
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbc);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        dataSource = new HikariDataSource(hikariConfig);
    }

    @BeforeMethod
    public void clearDatabase() throws Exception {
        doInTx(con -> {
            con.prepareStatement("delete from invitations;").execute();
            con.prepareStatement("delete from meetings;").execute();
            con.prepareStatement("delete from persons;").execute();
        });
    }

    public void doInTx(InTx c) throws Exception {
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            c.apply(con);
            con.commit();
        }
    }

    protected void insertPerson(
            long id,
            String firstName,
            String lastName,
            String email,
            String password
    ) throws Exception {
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement(
                    "insert into persons (id, first_name, last_name, email, password) values (?, ?, ?, ?, ?)"
            );
            ps.setLong(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, password);
            ps.executeUpdate();
        });
    }

    protected void insertMeeting(
            long meetingId,
            List<Long> invitees
    ) throws Exception {
        doInTx(c -> {
            PreparedStatement ps = c.prepareStatement("insert into meetings (id, start, duration) values (?, ?, ?)");
            ps.setLong(1, meetingId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, Duration.ofHours(1).toMillis());
            ps.executeUpdate();
            for (Long invitee : invitees) {
                PreparedStatement ips = c.prepareStatement(
                        "insert into invitations (meeting_id, person_id, status) values (?, ?, ?)");
                ips.setLong(1, meetingId);
                ips.setLong(2, invitee);
                ips.setString(3, "NO_RESPONSE");
                ips.executeUpdate();
            }
        });
    }
}
