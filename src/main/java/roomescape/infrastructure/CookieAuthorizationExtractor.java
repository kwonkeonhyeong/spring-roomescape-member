package roomescape.infrastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import roomescape.common.exception.AuthorizationException;

import java.util.Arrays;

@Component
@Qualifier("CookieAuthorizationExtractor")
public class CookieAuthorizationExtractor implements AuthorizationExtractor{
    @Override
    public String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new AuthorizationException("인증 정보를 찾을 수 없습니다."));
    }
}
