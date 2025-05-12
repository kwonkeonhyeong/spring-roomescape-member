package roomescape.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.exception.AuthorizationException;
import roomescape.domain.MemberRole;
import roomescape.infrastructure.AuthorizationExtractor;
import roomescape.infrastructure.TokenProvider;

@Component
public class CheckMemberRoleInterceptor implements HandlerInterceptor {
    private final AuthorizationExtractor authorizationExtractor;
    private final TokenProvider tokenProvider;

    public CheckMemberRoleInterceptor(TokenProvider tokenProvider, AuthorizationExtractor authorizationExtractor) {
        this.authorizationExtractor = authorizationExtractor;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = authorizationExtractor.extractToken(request);
        if (!tokenProvider.validateToken(token)) {
            throw new AuthorizationException("인증 정보가 올바르지 않습니다.");
        }
        String byKey = tokenProvider.getPayloadByKey(token, "role");
        MemberRole memberRole = MemberRole.valueOf(byKey);
        if (!memberRole.equals(MemberRole.ADMIN)) {
            response.setStatus(403);
            return false;
        }
        return true;
    }
}
