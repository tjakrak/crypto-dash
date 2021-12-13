package cs601.project4.server;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

// documentation of testing for spring: https://spring.io/guides/gs/testing-web/
@SpringBootTest
@AutoConfigureMockMvc
public class NoStayHomeAppMockTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * GET /event/create
     */
    @Test
    public void getEventCreateResponseStatusAndContentTest() throws Exception {
        this.mockMvc.perform(get("/event/create"))
                .andDo(print())
                .andExpect(status().is3xxRedirection()); // check if this page will give status 3xx and redirect to another page
    }

    /**
     * POST /event/create
     */
    @Test
    public void postEventCreateResponseStatusAndContentTest() throws Exception {
        this.mockMvc.perform(post("/event/create"))
                .andDo(print())
                .andExpect(status().is4xxClientError()); // check if response status is 4xx Not supported
    }

    /**
     * GET /event/{eventId}
     */
    @Test
    public void getEventDetailResponseStatusAndContentTest() throws Exception {
        this.mockMvc.perform(get("/event/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection()); // check if this page will give status 3xx and redirect to another page
    }

    /**
     * POST /event/{eventId}
     */
    @Test
    public void postEventDetailResponseStatusAndContentTest() throws Exception {
        this.mockMvc.perform(post("/event/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError()); // check if response status is 4xx Not supported
    }

    /**
     * GET /home
     */
    @Test
    public void getHomeResponseStatusTest() throws Exception {
        this.mockMvc.perform(get("/home"))
                .andDo(print())
                .andExpect(status().is3xxRedirection()); // check if this page will give status 3xx and redirect to another page

        //FOUND(302, HttpStatus.Series.REDIRECTION, "Found"),
    }

    /**
     * POST /home
     */
    @Test
    public void postHomeResponseStatusTest() throws Exception {
        this.mockMvc.perform(post("/home"))
                .andDo(print())
                .andExpect(status().is4xxClientError()); // check if response status is 4xx Not supported
    }


    /**
     * GET /login
     */
    @Test
    public void getLoginResponseStatusAndContentTest() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk()) // check if the http response is 200 ok
                .andExpect(content().string(containsString("Let's get the party started")));
    }

    /**
     * POST /login
     */
    @Test
    public void postLoginResponseStatusAndContentTest() throws Exception {
        this.mockMvc.perform(post("/login"))
                .andDo(print())
                .andExpect(status().is4xxClientError()); // check if response status is 4xx Not supported
    }

    /**
     * GET /
     */
    @Test
    public void getLandingPageResponseStatusTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is3xxRedirection()); // check if this page will give status 3xx and redirect to another page

        //FOUND(302, HttpStatus.Series.REDIRECTION, "Found"),
    }

    /**
     * GET /logout
     */
    @Test
    public void getLogoutResponseStatusTest() throws Exception {
        this.mockMvc.perform(get("/logout"))
                .andDo(print())
                .andExpect(status().is3xxRedirection()); // check if response status is 3xx redirection
    }

    /**
     * POST /logout
     */
    @Test
    public void postLogoutResponseStatusTest() throws Exception {
        this.mockMvc.perform(post("/logout"))
                .andDo(print())
                .andExpect(status().is4xxClientError()); // check if response status is 4xx Not supported
    }
}