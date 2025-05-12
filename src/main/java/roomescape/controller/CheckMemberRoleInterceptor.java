package roomescape.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.exception.AuthorizationException;
import roomescape.domain.MemberRole;
import roomescape.infrastructure.AuthorizationExtractor;
import roomescape.infrastructure.TokenProvider;
import roomescape.service.MemberService;

public class CheckMemberRoleInterceptor implements HandlerInterceptor {

    private final MemberService memberService;
    private final AuthorizationExtractor extractor;
    private final TokenProvider tokenProvider;

    public CheckMemberRoleInterceptor(TokenProvider tokenProvider, AuthorizationExtractor extractor, MemberService memberService) {
        this.tokenProvider = tokenProvider;
        this.extractor = extractor;
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = extractor.extractToken(request);
        if (!tokenProvider.validateToken(token)) {
            throw new AuthorizationException("인증 정보가 올바르지 않습니다.");
        }
        Long memberId = Long.parseLong(tokenProvider.getSub(token));
        String byKey = tokenProvider.getPayloadByKey(token, "role");
        MemberRole memberRole = MemberRole.valueOf(byKey);
        if (!memberService.isExistMemberById(memberId) || !memberRole.equals(MemberRole.ADMIN)) {
            response.setStatus(403);
            return false;
        }
        return true;
    }
}
