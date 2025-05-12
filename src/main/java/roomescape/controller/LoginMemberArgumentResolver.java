package roomescape.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.common.exception.AuthorizationException;
import roomescape.domain.Member;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.infrastructure.AuthorizationExtractor;
import roomescape.infrastructure.JwtTokenProvider;
import roomescape.service.MemberService;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;
    private final AuthorizationExtractor extractor;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginMemberArgumentResolver(MemberService memberService, AuthorizationExtractor extractor, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.extractor = extractor;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMemberRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = extractor.extractToken(request);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AuthorizationException("인증 정보가 올바르지 않습니다.");
        }
        String id = jwtTokenProvider.getSub(token);
        Long memberId = Long.parseLong(id);
        Member member = memberService.findMemberById(memberId);
        return new LoginMemberRequest(member.getId(), member.getName());
    }
}
