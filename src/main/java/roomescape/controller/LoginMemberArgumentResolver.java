package roomescape.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.infrastructure.AuthorizationExtractor;
import roomescape.infrastructure.TokenProvider;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthorizationExtractor authorizationExtractor;
    private final TokenProvider tokenProvider;

    public LoginMemberArgumentResolver(
            AuthorizationExtractor authorizationExtractor,
            TokenProvider tokenProvider
    ) {
        this.authorizationExtractor = authorizationExtractor;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMemberRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = authorizationExtractor.extractToken(request);
        String id = tokenProvider.getSub(token);
        Long memberId = Long.parseLong(id);
        String name = tokenProvider.getPayloadByKey(token, "name");
        return new LoginMemberRequest(memberId, name);
    }
}
