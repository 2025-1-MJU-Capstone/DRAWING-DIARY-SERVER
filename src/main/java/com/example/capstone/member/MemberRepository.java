package com.example.capstone.member;

import com.example.capstone.auth.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByEmailAndSocialProvider(String email, SocialProvider provider);

    Optional<Member> findBySocialProviderAndSocialId(SocialProvider provider, String socialId);
}
