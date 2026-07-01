package com.notifyhub.notifyhub.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates callers by a shared API key. Producers send it in the
 * {@code X-API-Key} header; the SSE stream (EventSource can't set headers)
 * may pass it as the {@code apiKey} query parameter. Comparison is
 * constant-time and multiple keys are supported for rotation.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-API-Key";
    private static final String QUERY_PARAM = "apiKey";

    private final List<String> validKeys;

    public ApiKeyAuthFilter(@Value("${notifyhub.security.api-key:}") String configured) {
        this.validKeys = Arrays.stream(configured.split(","))
                .map(String::trim)
                .filter(k -> !k.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String presented = request.getHeader(HEADER);
        if (presented == null) {
            presented = request.getParameter(QUERY_PARAM);
        }

        if (presented != null && matches(presented)) {
            var auth = new UsernamePasswordAuthenticationToken(
                    "api-client", null, AuthorityUtils.createAuthorityList("ROLE_PRODUCER"));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // No/invalid key → context stays empty; the entry point returns 401
        // for anything that requires authentication.
        chain.doFilter(request, response);
    }

    private boolean matches(String presented) {
        byte[] p = presented.getBytes(StandardCharsets.UTF_8);
        boolean ok = false;
        for (String key : validKeys) {
            // Compare against every key without short-circuiting on the first match.
            ok |= MessageDigest.isEqual(p, key.getBytes(StandardCharsets.UTF_8));
        }
        return ok;
    }
}
