package roomescape.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import roomescape.common.exception.AuthorizationException;
import roomescape.common.exception.NotFoundMemberException;
import roomescape.domain.Member;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.MemberLoginCheckResponse;
import roomescape.infrastructure.JwtTokenProvider;
import roomescape.infrastructure.TokenProvider;
import roomescape.repository.impl.JdbcMemberRepository;

@Service
public class AuthService {
    private final TokenProvider tokenProvider;
    private final JdbcMemberRepository jdbcMemberRepository;

    public AuthService(
            @Qualifier("JwtTokenProvider") TokenProvider tokenProvider, JdbcMemberRepository jdbcMemberRepository) {
        this.tokenProvider = tokenProvider;
        this.jdbcMemberRepository = jdbcMemberRepository;
    }

    public String tokenLogin(LoginRequest request) {
        Member member = jdbcMemberRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthorizationException("인증되지 않은 유저 정보입니다."));
        if (checkInvalidLogin(member, request)) {
            throw new AuthorizationException("인증되지 않은 유저 정보입니다.");
        }
        return tokenProvider.createToken(member);
    }

    public boolean checkInvalidLogin(Member member, LoginRequest request) {
        return member.checkInvalidLogin(request.email(), request.password());
    }

    public MemberLoginCheckResponse findMemberById(LoginMemberRequest request) {
        Member member = jdbcMemberRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundMemberException("존재하지 않는 유저 정보입니다."));
        return MemberLoginCheckResponse.from(member);
    }
}
