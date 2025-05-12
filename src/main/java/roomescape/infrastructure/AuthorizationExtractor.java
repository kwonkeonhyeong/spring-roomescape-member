package roomescape.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public interface AuthorizationExtractor {
    String extractToken(HttpServletRequest request);
}
