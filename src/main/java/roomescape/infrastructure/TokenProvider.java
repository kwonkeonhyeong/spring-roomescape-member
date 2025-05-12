package roomescape.infrastructure;

import org.springframework.stereotype.Component;
import roomescape.domain.Member;

@Component
public interface TokenProvider {
    String createToken(Member member);

    String getSub(String token);

    String getPayloadByKey(String token, String key);

    boolean validateToken(String token);
}
