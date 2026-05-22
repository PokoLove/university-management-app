package kz.iitu.hello.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String FALLBACK_PATH = "/users";

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        if (request.getRequestURI().startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        String referer = request.getHeader("Referer");
        String targetPath = FALLBACK_PATH;

        if (referer != null && !referer.isBlank()) {
            try {
                URI refererUri = URI.create(referer);
                String path = refererUri.getPath();
                if (path != null && !path.isBlank() && isSafePath(path)) {
                    targetPath = path;
                }
            } catch (IllegalArgumentException ignored) {
                if (referer.startsWith("/") && !referer.startsWith("//") && isSafePath(referer)) {
                    targetPath = referer;
                }
            }
        }

        if (!targetPath.startsWith("/")) {
            targetPath = FALLBACK_PATH;
        }

        String redirectUrl = UriComponentsBuilder
                .fromPath(targetPath)
                .replaceQueryParam("error", "access_denied")
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private boolean isSafePath(String path) {
        return !path.contains("..");
    }
}
