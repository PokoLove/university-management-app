package kz.iitu.hello.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAccessDeniedHandlerTest {

    private final CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();

    @Test
    void handle_withSafeReferer_redirectsToRefererPath() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://localhost:8080/students");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, null);

        assertThat(response.getRedirectedUrl()).contains("/students");
    }

    @Test
    void handle_withPathTraversalInReferer_redirectsToFallback() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://localhost:8080/../secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, null);

        assertThat(response.getRedirectedUrl()).contains("/users");
    }

    @Test
    void handle_withNoReferer_redirectsToFallback() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, null);

        assertThat(response.getRedirectedUrl()).contains("/users");
    }
}
