package world.inetum.realdolmen;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;

public abstract class RestIntegrationTestBase extends IntegrationTestBase {
    protected RequestSpecification spec;

    @BeforeMethod
    public void setUpRestAssured() {
        // Made available by the liberty maven plugin. See:
        // https://github.com/OpenLiberty/ci.maven/blob/main/docs/dev.md#system-properties-for-integration-tests
        String rawPort = System.getProperty("liberty.http.port", "8080");
        int port = Integer.parseInt(rawPort);
        spec = RestAssured.given()
                .basePath("/meet4j/rest")
                .port(port);
    }
}
