package com.notifyhub.notifyhub.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class ApiKeyAuthFilterTest {

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private Authentication runFilter(ApiKeyAuthFilter filter, MockHttpServletRequest req)
            throws ServletException, IOException {
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(req, new MockHttpServletResponse(), chain);
        assertThat(chain.getRequest()).isSameAs(req); // chain always proceeds
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Test
    void validHeaderAuthenticatesWithProducerRole() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("key1,key2");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(ApiKeyAuthFilter.HEADER, "key1");

        Authentication auth = runFilter(filter, req);

        assertThat(auth).isNotNull();
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_PRODUCER"));
    }

    @Test
    void secondRotationKeyAlsoWorks() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(" key1 , key2 ,,");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(ApiKeyAuthFilter.HEADER, "key2");

        assertThat(runFilter(filter, req)).isNotNull();
    }

    @Test
    void keyMayArriveAsQueryParameter() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("key1");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setParameter("apiKey", "key1");

        assertThat(runFilter(filter, req)).isNotNull();
    }

    @Test
    void wrongKeyIsNotAuthenticated() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("key1");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(ApiKeyAuthFilter.HEADER, "nope");

        assertThat(runFilter(filter, req)).isNull();
    }

    @Test
    void missingKeyIsNotAuthenticated() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("key1");

        assertThat(runFilter(filter, new MockHttpServletRequest())).isNull();
    }

    @Test
    void emptyConfigRejectsEveryKey() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(ApiKeyAuthFilter.HEADER, "anything");

        assertThat(runFilter(filter, req)).isNull();
    }
}
