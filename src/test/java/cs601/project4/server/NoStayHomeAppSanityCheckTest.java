package cs601.project4.server;

import cs601.project4.controller.EventCreateController;
import cs601.project4.controller.HomeController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NoStayHomeAppSanityCheckTest {
    private static final Logger LOGGER = LogManager.getLogger(NoStayHomeAppSanityCheckTest.class);

    @Autowired
    private EventCreateController eventCreateController;

    @Autowired
    private HomeController homeController;

    @Test
    public void contextLoads() { // Sanity check ensuring server is running
        LOGGER.info("server is running...");
    }

    @Test
    public void homeController() { // Sanity check for HomeController
        assertThat(homeController).isNotNull();
    }

    @Test
    public void eventCreateController() { // Sanity check for EventCreateController
        assertThat(eventCreateController).isNotNull();
    }





}
