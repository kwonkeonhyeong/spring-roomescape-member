package roomescape.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.controller.CheckMemberRoleInterceptor;
import roomescape.controller.LoginMemberArgumentResolver;
import roomescape.infrastructure.AuthorizationExtractor;
import roomescape.infrastructure.JwtTokenProvider;
import roomescape.service.MemberService;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final MemberService memberService;
    private final AuthorizationExtractor extractor;
    private final JwtTokenProvider jwtTokenProvider;

    public WebMvcConfiguration(MemberService memberService, AuthorizationExtractor authorizationExtractor, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.extractor = authorizationExtractor;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(memberService, extractor, jwtTokenProvider));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CheckMemberRoleInterceptor(jwtTokenProvider, extractor, memberService))
                .addPathPatterns("/admin/**");
    }
}
