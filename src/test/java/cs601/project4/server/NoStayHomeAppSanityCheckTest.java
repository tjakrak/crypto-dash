package cs601.project4.server;

import cs601.project4.controller.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * This is a simple integration test where we just check that when the system under test is invoked it returns something
 * Smoke test / Sanity check for the server and each controller.
 * Sanity check test will fail if the application context cannot start.
 */
@SpringBootTest
public class NoStayHomeAppSanityCheckTest {
    private static final Logger LOGGER = LogManager.getLogger(NoStayHomeAppSanityCheckTest.class);

    @Autowired
    private EventCreateController eventCreateController;

    @Autowired
    private EventDetailsController eventDetailsController;

    @Autowired
    private HomeController homeController;

    @Autowired
    private LandingPageController landingPageController;

    @Autowired
    private LoginController loginController;

    @Autowired
    private LogoutController logoutController;

    @Autowired
    private SettingController settingController;

    @Autowired
    private TicketPurchaseController ticketPurchaseController;

    @Autowired
    private TicketTransferController ticketTransferController;

    @Test
    public void serverContextLoad() { // Sanity check ensuring server is running
        LOGGER.info("server is running...");
    }

    @Test
    public void eventCreateControllerContextLoad() { // Sanity check for EventCreateController
        assertThat(eventCreateController).isNotNull();
    }

    @Test
    public void eventDetailContextLoad() { // Sanity check for EventCreateController
        assertThat(eventDetailsController).isNotNull();
    }

    @Test
    public void homeControllerContextLoad() { // Sanity check for HomeController
        assertThat(homeController).isNotNull();
    }

    @Test
    public void landingPageControllerContextLoad() { // Sanity check for HomeController
        assertThat(landingPageController).isNotNull();
    }

    @Test
    public void loginControllerContextLoad() { // Sanity check for HomeController
        assertThat(loginController).isNotNull();
    }

    @Test
    public void logoutControllerContextLoad() { // Sanity check for HomeController
        assertThat(loginController).isNotNull();
    }

    @Test
    public void SettingControllerContextLoad() { // Sanity check for HomeController
        assertThat(settingController).isNotNull();
    }

    @Test
    public void TicketPurchaseControllerContextLoad() { // Sanity check for HomeController
        assertThat(ticketPurchaseController).isNotNull();
    }

    @Test
    public void TicketTransferControllerContextLoad() { // Sanity check for HomeController
        assertThat(ticketTransferController).isNotNull();
    }

}
