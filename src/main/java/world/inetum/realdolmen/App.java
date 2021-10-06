package world.inetum.realdolmen;

import javax.annotation.security.DeclareRoles;
import javax.annotation.sql.DataSourceDefinition;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
@DataSourceDefinition(
        name = "java:app/ds/meet4j",
        className = "com.mysql.cj.jdbc.Driver",
        url = "jdbc:mysql://localhost:3306/meet4j",
        user = "acaddemicts",
        password = "acaddemicts",
        properties = {
                "useSSL=false",
                "allowPublicKeyRetrieval=true",
        }
)
@BasicAuthenticationMechanismDefinition
@DeclareRoles("USER")
@ApplicationScoped
public class App extends Application {
}
