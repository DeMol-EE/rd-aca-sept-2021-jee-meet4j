package world.inetum.realdolmen.jaxrs;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import world.inetum.realdolmen.jaxrs.DurationAdapter;

import java.time.Duration;

public class DurationAdapterTest {

    private DurationAdapter sut;

    @BeforeMethod
    public void setUp() {
        sut = new DurationAdapter();
    }

    @Test(dataProvider = "cases")
    public void testAdaptToJson(String expected, Duration input) {
        String actual = sut.adaptToJson(input);
        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "cases")
    public void testAdaptFromJson(String input, Duration expected) {
        Duration actual = sut.adaptFromJson(input);
        Assert.assertEquals(actual, expected);
    }

    @DataProvider
    public Object[][] cases() {
        return new Object[][]{
                {"PT30M", Duration.ofMinutes(30)},
                {"PT15M", Duration.ofMinutes(15)},
                {"PT1H20M", Duration.ofMinutes(80)}
        };
    }
}